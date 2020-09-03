import java.util.Scanner;

import banking.*;

public class Runner {
    // MAIN METHOD
    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("-fileName")) {
            System.out.println("Usage: java Runner -fileName file_name");
            System.exit(1);
        }

        // For MySQL JDBC to work properly
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Set up DB connection for persistence
        BackendService bes = BackendService.initConnection(args[1]);
        if (bes == null) {
            System.out.println("Failure to establish connection to DB");
            System.exit(1);
        }

        boolean isRunningProgram = true;
        Scanner in = new Scanner(System.in);
        Bank b = new Bank(400000, bes);

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
                        System.out.println(newAcct.getPin());
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
                            if (!accessAccount(acct, b, in)) isRunningProgram = false;
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
            if (isRunningProgram) System.out.println();
        }

        System.out.println("Bye!");
        bes.closeConnection();
    }

    /**
    * Public method where logged in user can access their account and do transactions.
    * 
    * @param acct - Account of user logged in
    * @param b - The Bank
    * @param in - Scanner for user input
    * @return Status of user exit (log out or straight exit)
    */
    private static boolean accessAccount(Account acct, Bank b, Scanner in) {
        boolean isRunningProgram = true;
        boolean hasResolution = false;
        while (isRunningProgram) {
            System.out.println("1. Balance\n2. Add income\n3. Do Transfer\n4. Close account\n5. Log out\n0. Exit");
            try {
                int option = Integer.parseInt(in.nextLine().trim());
                System.out.println();
                switch (option) {
                    case 1:
                        System.out.println("Balance: " + acct.getBalance());
                        break;
                    case 2:
                        System.out.println("Enter income:");
                        long income = Long.parseLong(in.nextLine().trim());
                        System.out.println("Income was" + (b.addFunds(acct, income) ? "" : " not") + " added!");
                        break;
                    case 3:
                        System.out.println("Transfer");
                        System.out.println("Enter card number:");
                        String card = in.nextLine().trim();
                        int check_card = b.checkCardForTransfer(acct, card);
                        if (check_card == 1) System.out.println("You can't transfer money to the same account!");
                        else if (check_card == 2) System.out.println("Probably you made mistake in the card number. Please try again!");
                        else if (check_card == 3) System.out.println("Such a card does not exist.");
                        else {
                            System.out.println("Enter how much money you want to transfer:");
                            long transfer = Long.parseLong(in.nextLine().trim());
                            if (transfer > acct.getBalance()) System.out.println("Not enough money!");
                            else if (b.transferFunds(acct, card, transfer)) System.out.println("Success!");
                            else System.out.println("Failure!");
                        }
                        break;
                    case 4:
                        if (b.closeAccount(acct)) {
                            System.out.println("This account has been closed");
                            acct = null;
                            isRunningProgram = false;
                            hasResolution = true;
                        } else System.out.println("This account has not been closed");
                        break;
                    case 5:
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
            if (isRunningProgram) System.out.println();
        }

        return hasResolution;
    }
}