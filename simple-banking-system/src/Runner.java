import java.util.Scanner;

import banking.*;

public class Runner {
    public static void main(String[] args) {
        boolean isRunningProgram = true;
        Scanner in = new Scanner(System.in);
        Bank b = new Bank(400000);

        while (isRunningProgram) {
            try {
                System.out.println("1. Create an account\n2. Log into account\n0. Exit");
                int option = Integer.parseInt(in.nextLine().trim());
                System.out.println();
                switch (option) {
                    case 1:
                        Account newAcct = b.issueAccount();
                        System.out.println("Your card has been created");
                        System.out.println("Your card number:");
                        System.out.println(newAcct.getCardNumber());
                        System.out.println("Your card PIN");
                        System.out.printf("%04d\n", newAcct.getPin());
                        break;
                    case 2:
                        System.out.println("Enter your card number:");
                        String acctNum = in.nextLine().trim();
                        System.out.println("Enter your PIN:");
                        String pin = in.nextLine().trim();
                        Account acct = null;
                        if (acctNum.length() == b.ACCOUNT_NUMBER_LENGTH &&
                            acctNum.matches(String.format("[0-9]{%d}", b.ACCOUNT_NUMBER_LENGTH)) &&
                            pin.length() == b.PIN_LENGTH && pin.matches(String.format("[0-9]{%d}", b.PIN_LENGTH))) {
                            acct = b.findAccount(acctNum, pin);
                        }
                        if (acct != null) {
                            System.out.println("\nYou have successfully logged in!");
                            if (!accessAccount(acct, in)) isRunningProgram = false;
                        }
                        else System.out.println("\nWrong card number or PIN!");
                        break;
                    case 0:
                        isRunningProgram = false;
                        break;
                    default:
                        System.out.println("Incorrect option. Try again!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter valid input.");
            }
            System.out.println();
        }

        System.out.println("Bye!");
    }

    private static boolean accessAccount(Account acct, Scanner in) {
        boolean isRunningProgram = true;
        boolean hasResolution = false;
        while (isRunningProgram) {
            System.out.println("1. Balance\n2. Log out\n0. Exit");
            try {
                int option = Integer.parseInt(in.nextLine().trim());
                System.out.println();
                switch (option) {
                    case 1:
                        System.out.println("Balance: " + acct.getBalance());
                        break;
                    case 2:
                        isRunningProgram = false;
                        hasResolution = true;
                        System.out.print("You have successfully logged out!");
                        break;
                    case 0:
                        isRunningProgram = false;
                        break;
                    default:
                        System.out.println("Incorrect option. Try again!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter valid input.");
            }
            System.out.println();
        }

        return hasResolution;
    }
}