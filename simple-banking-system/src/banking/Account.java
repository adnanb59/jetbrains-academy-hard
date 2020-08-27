package banking;

public class Account {
    private String account, pin;
    private long balance;


    public Account (String acct, String pin) {
        this.account = acct;
        this.pin = pin;
        this.balance = 0;
    }

    public String getCardNumber() {
        return this.account;
    }

    public String getPin() {
        return this.pin;
    }

    public long getBalance() {
        return this.balance;
    }

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

    public long deposit(long amount) {
        balance += amount;
        return balance;
    }

    public boolean setPin(String old, String newPin) {
        if (old.equals(this.pin)) this.pin = newPin;
        else return false;
        return true;
    }
}