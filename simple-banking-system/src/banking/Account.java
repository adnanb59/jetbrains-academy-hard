package banking;

public class Account {
    private String account, pin;
    private long balance;

    /**
    * Account constructor
    * 
    * @param acct - account (card) number
    * @param pin - account pin
    */
    public Account (String acct, String pin) {
        this.account = acct;
        this.pin = pin;
        this.balance = 0;
    }

    /**
    * Get card number for account
    * 
    * @return account number
    */
    public String getCardNumber() {
        return this.account;
    }

    /**
    * Get PIN for account
    * 
    * @return account pin
    */
    public String getPin() {
        return this.pin;
    }

    /**
    * Get balance of account
    * 
    * @return account balance
    */
    public long getBalance() {
        return this.balance;
    }

    /**
    * Withdraw money from account, either for withdrawal or transfer
    * 
    * @param amount - Amount to withdraw
    * @return Amount transferred
    */
    public long withdraw(long amount) {
        if (amount > balance) {
            long tmp = balance;
            balance = 0;
            return tmp;
        } else {
            balance -= amount;
            return amount;
        }
    }

    /**
    * Deposit money into account
    * 
    * @param amount - Amount to deposit
    * @return New balance
    */
    public long deposit(long amount) {
        balance += amount;
        return balance;
    }
}