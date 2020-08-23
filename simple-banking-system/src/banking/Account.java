package banking;

public class Account {
    private final int BankIdentifier;
    private final long accountNumber;
    private int PIN;
    private final int checkSum;
    private long balance;

    public Account(int bi, long acct, long checkSum) {
        this.BankIdentifier = bi;
        this.accountNumber = acct;
        this.PIN = (int) (Math.random() * 10000);
        this.checkSum = (int) checkSum;
        this.balance = 0;

    }

    public String getCardNumber() {
        return this.BankIdentifier + "" + String.format("%09d", this.accountNumber) + this.checkSum;
    }

    public long getPin() {
        return this.PIN;
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

    public boolean setPin(int old, int newPin) {
        if (old == this.PIN) this.PIN = newPin;
        else return false;
        return true;
    }
}