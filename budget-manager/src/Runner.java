import java.io.*;
import java.util.*;
import java.util.regex.*;

import budget.util.*;
import budget.*;

public class Runner {
    static List<ItemType> itemTypes;

    /** Given an action that the user wants to do, prompt the user to choose an option
    *   for the type of purchase they want
    *
    * @param in - Scanner for user input
    * @param action - Action that the user wants to do (list/sort/add)
    * @return The number (from the menu) that the user selected
    */
    private static int promptUserForPurchases(Scanner in, Action action) {
        StringBuilder prompt = new StringBuilder("Choose the type of purchase:\n");
        int options, result = -1;

        // Create the menu using the possible purchase types
        for (options = 0; options < itemTypes.size(); options++) {
            if (options == itemTypes.size()-1 && !action.equals(Action.LIST)) continue;
            prompt.append(options+1).append(") ").append(itemTypes.get(options)).append("\n");
        }
        // Add an option for going back (if required)
        if (!action.equals(Action.SORT)) {
            options += action.equals(Action.LIST) ? 1 : 0;
            prompt.append(options).append(") ").append("Back\n");
        }

        // Display the prompt and ask user for input, keep asking till a valid option is given
        while (result == -1) {
            System.out.print(prompt.toString());
            try {
                int option = Integer.parseInt(in.next());
                in.nextLine();
                if (option < 1 || option > options) System.out.println("Please enter a valid option!");
                else result = option;
            } catch (NumberFormatException e) {
                in.nextLine();
                System.out.println("Please enter valid numbers!");
            }
            System.out.println();
        }

        return result;
    }

    /** List the purchases for a specific category (or all purchases)
    *
    * @param in - Scanner for user input
    * @param m - Budget Manager application
    */
    public static void listPurchases(Scanner in, Manager m) {
        boolean isNotBack = true;

        // While user has not prompted to go back
        while (isNotBack) {
            int res = promptUserForPurchases(in, Action.LIST);
            if (res == itemTypes.size() + 1) isNotBack = false;
            else {
                // Upon user selecting a specific type of purchase, get the list and display it
                ItemType it = itemTypes.get(res-1);
                display(m.getTypeOfPurchases(it, false), m.getTotal(it), it);
            }

            if (isNotBack) System.out.println();
        }
    }

    /** Prompt user to add a purchase to the budget manager
    *
    * @param in - Scanner for user input
    * @param m - Budget Manager application
    */
    public static void addPurchases(Scanner in, Manager m) {
        boolean isNotBack = true;

        // While user has not prompted to go back
        while (isNotBack) {
            int res = promptUserForPurchases(in, Action.ADD); // Prompt user for a purchase type
            if (res == itemTypes.size()) isNotBack = false;
            else {
                // Add a purchase to the manager
                ItemType it = itemTypes.get(res-1);
                System.out.println("Enter purchase name:");
                String name = in.nextLine();
                System.out.println("Enter its price:");
                String price = in.next();
                while (price.isEmpty() || !price.matches("\\d*(\\.\\d{0,2})?")) {
                    System.out.println("Please enter valid dollar amount");
                    price = in.next();
                }
                m.addPurchaseItem(it, name, Double.parseDouble(price));
                System.out.println("Purchase was added!");
            }

            if (isNotBack) System.out.println();
        }
    }

    /** Prompt user to sort purchases by descending order of price.
    *   A user can sort all the purchases, sort the categories of purchases or purchases for a category
    *
    * @param in - Scanner for user input
    * @param m - Budget Manager application
    */
    public static void sortPurchases(Scanner in, Manager m) {
        boolean isNotBack = true;

        // While user has not prompted to go back
        while (isNotBack) {
            System.out.println("How do you want to sort?\n1) Sort all purchases\n2) Sort by type\n3) Sort certain " +
                               "type\n4) Back");
            // Read user option for type of sorting user wants to do
            try {
                int option = Integer.parseInt(in.next());
                in.nextLine();
                System.out.println();
                if (option < 1 || option > 4) System.out.println("Please enter a valid option!");
                else {
                    // Valid options
                    switch(option) {
                        case 1: // Sort all the purchases
                            display(m.getTypeOfPurchases(ItemType.ALL, true), m.getTotal(ItemType.ALL), ItemType.ALL);
                            break;
                        case 2: // Sort the totals of each category
                            System.out.println("Types: ");
                            Collection<String> totals = m.getSortedTotals();
                            for (String total : totals) {
                                System.out.println(total);
                            }
                            System.out.println(String.format("Total sum: $%.2f", m.getTotal(ItemType.ALL)));
                            break;
                        case 3: // Sort the purchases of a specific category
                            ItemType it = itemTypes.get(promptUserForPurchases(in, Action.SORT) - 1);
                            display(m.getTypeOfPurchases(it, true), m.getTotal(it), it);
                            break;
                        case 4: // Back
                            isNotBack = false;
                            break;
                        default:
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                in.nextLine();
                System.out.println("Please enter valid numbers!");
            }

            if (isNotBack) System.out.println();
        }
    }

    /** Display a list of purchases and their total sum (or a prompt displaying empty if there are no purchases)
    *
    * @param purchases - list of purchases to display
    * @param total - total to display
    * @param typeForTotal - type of purchases that are being displayed
    */
    private static void display(Collection<String> purchases, Double total, ItemType typeForTotal) {
        System.out.println(typeForTotal.toString() + ":");
        for (String purchase : purchases) {
            System.out.println(purchase);
        }
        if (purchases.size() == 0) System.out.println("Purchase list is empty!");
        else System.out.println(String.format("Total sum: $%.2f", total));
    }

    /** Application Runner **/
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Manager m = new Manager();
        boolean exit = false;
        itemTypes = m.getItemTypes();

        // Main menu, prompt user for an action
        while (!exit) {
            System.out.println("Choose your action:\n1) Add income\n2) Add purchase\n" +
                    "3) Show list of purchases\n4) Balance\n5) Save\n6) Load\n7) Analyze (Sort)\n0) Exit");
            try {
                int action = Integer.parseInt(in.next());
                System.out.println();
                in.nextLine();
                switch (action) {
                    case 1:
                        System.out.println("Enter income:");
                        Double income = Double.parseDouble(in.next());
                        if (m.addIncome(income)) System.out.println("Income was added!");
                        else System.out.println("Error adding income!");
                        break;
                    case 2:
                        addPurchases(in, m);
                        break;
                    case 3:
                        listPurchases(in, m);
                        break;
                    case 4:
                        System.out.println(String.format("Balance: $%.2f", m.getBalance()));
                        break;
                    case 5:
                        // Go through purchases and write them to the file
                        File wf = new File("purchases.txt");
                        FileWriter fw = new FileWriter(wf);
                        for (ItemType it : itemTypes) {
                            if (it.equals(ItemType.ALL)) continue;
                            Collection<String> purchases = m.getTypeOfPurchases(it, false);
                            fw.write(String.format("%s -- %d\n", it, purchases.size()));
                            for (String purchase : purchases) {
                                fw.write(purchase + "\n");
                            }
                            fw.write(String.format("Total: $%.2f\n", m.getTotal(it)));
                        }
                        fw.write(String.format("Balance: $%.2f", m.getBalance()));
                        fw.close();
                        System.out.println("Purchases were saved!");
                        break;
                    case 6:
                        // Read file and add items to manager
                        File rf = new File("purchases.txt");
                        Scanner fr = new Scanner(rf);
                        while (fr.hasNextLine()) {
                            String line = fr.nextLine();
                            if (line.contains("Balance: $")) {
                                m.setIncome(Double.parseDouble(line.split(": \\$")[1]));
                            } else {
                                String[] curr = line.split("\\s--\\s");
                                int amount = Integer.parseInt(curr[1]);
                                ItemType it = ItemType.valueOf(curr[0].toUpperCase());
                                for (int i = 0; i < amount; i++) {
                                    Pattern p = Pattern.compile("^(.+)\\s+\\$([0-9]*(\\.[0-9]{0,2})?)$");
                                    Matcher map = p.matcher(fr.nextLine());
                                    if (map.matches()) {
                                        m.addPurchaseItem(it, map.group(1), Double.parseDouble(map.group(2)));
                                    }
                                }
                                fr.nextLine();
                            }
                        }
                        fr.close();
                        System.out.println("Purchases were loaded!");
                        break;
                    case 7:
                        sortPurchases(in, m);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error writing to file!");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("Please enter numbers");
            }
            if (!exit) System.out.println();
        }

        in.close();
        System.out.println("Bye!");
    }
}