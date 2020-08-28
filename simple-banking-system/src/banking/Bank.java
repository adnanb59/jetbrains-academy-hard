package banking;

public class Bank {
    private final int IIN;
    private final int account_length = 9;
    public final int ACCOUNT_NUMBER_LENGTH = 16;
    public final int PIN_LENGTH = 4;
    private BackendService b;

    public Bank(int IIN, BackendService b) {
        this.IIN = IIN;
        this.b = b;
    }

    private boolean checkLuhnsAlgo(long acctNum) {
        long lastDigit = acctNum % 10;
        acctNum /= 10;
        return (checkSum(acctNum, false) + lastDigit) % 10 == 0;
    }

    private long checkSum(long val, boolean hasFixedLength) {
        long sum = 0;
        int digits = (int) Math.floor(Math.log10(val)) + 1;
        boolean odd = hasFixedLength ? account_length % 2 != 0 : digits % 2 != 0;
        // calc digits to go through, not just val cause of
        while (val > 0) {
            if (odd) {
                long tmp = (val % 10)*2;
                if (tmp > 9) tmp -= 9;
                sum += tmp;
            } else sum += val % 10;
            //System.out.println(sum);
            odd = !odd;
            val /= 10;
        }

        return sum;
    }

    private long calculateCheckSum(long acct) {
        long checksum = checkSum(IIN, false) + checkSum(acct, true); // breaks with leading zeroes
        return checksum % 10 == 0 ? 0 : 10 - (checksum%10);
    }

    public Account issueAccount() {
        String accountNumber;
        long acctNum;
        String pin = String.format("%0" + PIN_LENGTH + "d", (int) (Math.random()*Math.pow(10, PIN_LENGTH)));
        do {
            acctNum = (long) (Math.random()*Math.pow(10, account_length));
            accountNumber = IIN + "" + String.format("%0" + account_length + "d%d", acctNum, calculateCheckSum(acctNum));
        } while (b.findAccount(accountNumber, null) != -1);

        return b.addAccount(acctNum, accountNumber, pin);
    }

    public Account findAccount(String accNum, String pin) {
        long accountNumber = Long.parseLong(accNum);
        if (checkLuhnsAlgo(accountNumber) && accountNumber / ((long) Math.pow(10, account_length+1)) == IIN) {
            long balance;
            if ((balance=(b.findAccount(accNum, pin))) != -1) {
                Account a = new Account(accNum, pin);
                a.deposit(balance);
                return a;
            }
        }
        return null;
    }

    public boolean addFunds(Account acct, long income) {
        if (b.updateFundsToAccount(acct.getCardNumber(), income)) {
            acct.deposit(income);
            return true;
        }
        return false;
    }

    public int checkCardForTransfer(Account acct, String card) {
        if (card.equals(acct.getCardNumber())) return 1;
        else if (!checkLuhnsAlgo(Long.parseLong(card))) return 2;
        else if (b.findAccount(card, null) == -1) return 3;
        else return 0;
    }

    public boolean transferFunds(Account acct, String card, long transfer) {
        if (b.transferFunds(acct.getCardNumber(), card, transfer)) {
            acct.withdraw(transfer);
            return true;
        }
        return false;
    }

    public boolean closeAccount(Account acct) {
        return b.deleteAccount(acct.getCardNumber(), acct.getPin());
    }
}