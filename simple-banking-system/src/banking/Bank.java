package banking;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final int IIN;
    private final int account_length = 9;
    private Map<Long, Account> accounts;
    public final int ACCOUNT_NUMBER_LENGTH = 16;
    public final int PIN_LENGTH = 4;

    public Bank(int IIN) {
        accounts = new HashMap<>();
        this.IIN = IIN;
    }

    private boolean checkLuhnsAlgo(long acctNum) {
        long lastDigit = acctNum % 10;
        acctNum /= 10;
        //System.out.println(checkSum(acctNum, false) + lastDigit);
        //System.out.println(lastDigit);
        return (checkSum(acctNum, false) + lastDigit) % 10 == 0;
    }

    private long checkSum(long val, boolean hasFixedLength) {
        long sum = 0;
        int digits = (int) Math.floor(Math.log10(val)) + 1;
        boolean odd = hasFixedLength ? account_length % 2 != 0 : digits % 2 != 0;
        // calc digits to go through, not just val cause of
        while (val > 0) {
            //System.out.println(val);
            //System.out.println("ODD: " + odd);
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
        //System.out.println(checksum);
        //System.out.println(checksum % 10 == 0 ? 0 : 10 - (checksum%10));
        return checksum % 10 == 0 ? 0 : 10 - (checksum%10);
    }

    public Account issueAccount() {
        long acctNum;
        while (accounts.containsKey((acctNum=((long) (Math.random()*Math.pow(10, account_length))))));
        Account a = new Account(IIN, acctNum, calculateCheckSum(acctNum));
        accounts.put(acctNum, a);
        return a;
    }

    public Account findAccount(String accNum, String pin) {
        long accountNumber = Long.parseLong(accNum);
        int accountPin = Integer.parseInt(pin);
        //System.out.println(checkLuhnsAlgo(acctNumber));
        //System.out.println(acctNumber / ((long) Math.pow(10, account_length+1)));
        //System.out.println((acctNumber/10) % ((long) Math.pow(10, account_length)));
        if (checkLuhnsAlgo(accountNumber)) {
            if (accountNumber / ((long) Math.pow(10, account_length+1)) == IIN) {
                Account acct = accounts.get((accountNumber/10) % ((long) Math.pow(10, account_length)));
                if (acct != null && acct.getPin() == accountPin) return acct;
            }
        }
        return null;
    }
}
