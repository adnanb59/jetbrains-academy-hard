package banking;

public class Bank {
    private final int IIN;
    private final int account_length = 9;
    public final int ACCOUNT_NUMBER_LENGTH = 16;
    public final int PIN_LENGTH = 4;
    private BackendService b;

    /**
    * Bank constructor
    * 
    * @param IIN - Institution identification number
    * @param b - DB connection
    */
    public Bank(int IIN, BackendService b) {
        this.IIN = IIN;
        this.b = b;
    }

    /**
    * Check whether or not account number passed in passes Luhn's algorithm.
    * 
    * @param acctNum - account number to check
    * @return Whether or not check has passed
    */
    private boolean checkLuhnsAlgo(long acctNum) {
        long lastDigit = acctNum % 10;
        acctNum /= 10;
        return (checkSum(acctNum, false) + lastDigit) % 10 == 0;
    }

    /**
    * Calculate check sum of value w.r.t. Luhn's algorithm. 
    * If value has fixed length, regardless of # of digits for value,
    * check sum will be calculated against <account_length> digits
    * 
    * @param val - value to calculate check sum for
    * @param hasFixedLength - if check sum has fixed length of digits to calculate for
    * @return check sum of value
    */
    private long checkSum(long val, boolean hasFixedLength) {
        long sum = 0;
        int digits = (int) Math.floor(Math.log10(val)) + 1;
        // Calculate whether or not first digit of algorithm (last digit of number) has an odd index
        // It is checked against account_length # of digits if fixed length, o/w the number's # of digits
        boolean odd = hasFixedLength ? account_length % 2 != 0 : digits % 2 != 0;
        // Go through digits and calculate check sum
        // If on odd index, multiply value by 2, then subtract by 9 if result is double digits 
        while (val > 0) {
            if (odd) {
                long tmp = (val % 10)*2; // the last digit is the relevant value per iteration
                if (tmp > 9) tmp -= 9;
                sum += tmp;
            } else sum += val % 10;
            odd = !odd;
            val /= 10;
        }

        return sum;
    }

    /**
    * Calculate the final digit for account number so it passes Luhn's algorithm.
    * 
    * @param acct - account number
    * @return last check sum digit for account number to pass Luhn's algo
    */
    private long calculateCheckSum(long acct) {
        // calculate check sums of IIN and account number then calculate final digit
        long checksum = checkSum(IIN, false) + checkSum(acct, true);
        return checksum % 10 == 0 ? 0 : 10 - (checksum%10);
    }

    /**
    * Issue a new account for a user (this gets stored in the DB as well)
    * 
    * @return Account object representing account created
    */
    public Account issueAccount() {
        String accountNumber;
        long acctNum;
        // PIN gets filled with 0's to make 4 digits (if random # is less than 4 digits)
        String pin = String.format("%0" + PIN_LENGTH + "d", (int) (Math.random()*Math.pow(10, PIN_LENGTH)));
        do {
            // Calculate core account # and formatted account number
            // Then check if account exists already
            acctNum = (long) (Math.random()*Math.pow(10, account_length));
            accountNumber = IIN + "" + String.format("%0" + account_length + "d%d", acctNum, calculateCheckSum(acctNum));
        } while (b.findAccount(accountNumber, null) != -1);

        // Add new account to DB
        return b.addAccount(acctNum, accountNumber, pin);
    }

    /**
    * Check if account (with passed in credentials) is valid (meaning that it exists)
    * 
    * @param accNum - account number
    * @param pin - account pin
    * @return Account object if it exists, o/w null
    */
    public Account findAccount(String accNum, String pin) {
        long accountNumber = Long.parseLong(accNum);
        // If account number passes Luhn's algo & if account's IIN belongs to this bank
        if (checkLuhnsAlgo(accountNumber) && accountNumber / ((long) Math.pow(10, account_length+1)) == IIN) {
            long balance;
            // if balance != -1 (meaning account has been found), create Account object and return it
            if ((balance=(b.findAccount(accNum, pin))) != -1) {
                Account a = new Account(accNum, pin);
                a.deposit(balance);
                return a;
            }
        }
        // account hasn't been found
        return null;
    }

    /**
    * Add funds specified to account passed in
    * 
    * @param acct - Account to add funds into
    * @param income - amount of funds to add
    * @return Whether or not addition of funds is successful
    */
    public boolean addFunds(Account acct, long income) {
        // attempt to update funds in DB, if successful then update Account object
        if (b.updateFundsToAccount(acct.getCardNumber(), income)) {
            acct.deposit(income);
            return true;
        }
        return false;
    }

    /**
    * Check receiver's card to see if it's suitable to transfer funds.
    * 
    * @param acct - sender's account
    * @param card - receiver's account number
    * @return 0 if successful, 1 if card == acct's card, 2 if receiver's card doesn't pass Luhn's algo
    *         3 if receiver's account does not exist
    */
    public int checkCardForTransfer(Account acct, String card) {
        if (card.equals(acct.getCardNumber())) return 1;
        else if (!checkLuhnsAlgo(Long.parseLong(card))) return 2;
        else if (b.findAccount(card, null) == -1) return 3;
        else return 0;
    }

    /**
    * Attempt to transfer <transfer> amount of funds from sender (acct) to receiver (card).
    * 
    * @param acct - sender's account
    * @param card - receiver's account
    * @param transfer - funds to transfer
    * @return Whether or not transfer is successful
    */
    public boolean transferFunds(Account acct, String card, long transfer) {
        // If transfer is successful in BE, update sender's interface (Account object)
        if (b.transferFunds(acct.getCardNumber(), card, transfer)) {
            acct.withdraw(transfer);
            return true;
        }
        return false;
    }

    /**
     * Delete account of user passed in from the records in the DB.
     * 
     * @param acct - account representing user to delete
     * @return Whether deletion of successful
     */
    public boolean closeAccount(Account acct) {
        return b.deleteAccount(acct.getCardNumber(), acct.getPin());
    }
}