import budget.util.Action;
import budget.util.ItemType;
import budget.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
    static List<ItemType> itemTypes;

    private static int promptUserForPurchases(Scanner in, Action action) {
        StringBuilder prompt = new StringBuilder("Choose the type of purchase:\n");
        int options, result = -1;

        for (options = 0; options < itemTypes.size(); options++) {
            if (options == itemTypes.size()-1 && !action.equals(Action.LIST)) continue;
            prompt.append(options+1).append(") ").append(itemTypes.get(options)).append("\n");
        }
        if (!action.equals(Action.SORT)) {
            options += action.equals(Action.LIST) ? 1 : 0;
            prompt.append(options).append(") ").append("Back\n");
        }

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

        return result;
    }

    public static void listPurchases(Scanner in, Manager m) {
        boolean isNotBack = true;
        while (isNotBack) {
            int res = promptUserForPurchases(in, Action.LIST);
            if (res == itemTypes.size() + 1) isNotBack = false;
            else {
                ItemType it = itemTypes.get(res-1);
                display(m.getTypeOfPurchases(it, false), m.getTotal(it), it);
            }

            if (isNotBack) System.out.println();
        }
    }

    public static void addPurchases(Scanner in, Manager m) {
        boolean isNotBack = true;
        while (isNotBack) {
            int res = promptUserForPurchases(in, Action.ADD);
            if (res == itemTypes.size()) isNotBack = false;
            else {
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

    public static void sortPurchases(Scanner in, Manager m) {
        boolean isNotBack = true;
        while (isNotBack) {
            System.out.println("How do you want to sort?\n1) Sort all purchases\n2) Sort by type\n3) Sort certain " +
                    "type\n4) Back");
            try {
                int option = Integer.parseInt(in.next());
                in.nextLine();
                System.out.println();
                if (option < 1 || option > 4) System.out.println("Please enter a valid option!");
                else {
                    switch(option) {
                        case 1:
                            display(m.getTypeOfPurchases(ItemType.ALL, true), m.getTotal(ItemType.ALL), ItemType.ALL);
                            break;
                        case 2:
                            System.out.println("Types: ");
                            Collection<String> totals = m.getSortedTotals();
                            for (String total : totals) {
                                System.out.println(total);
                            }
                            System.out.println(String.format("Total sum: $%.2f", m.getTotal(ItemType.ALL)));
                            break;
                        case 3:
                            ItemType it = itemTypes.get(promptUserForPurchases(in, Action.SORT) - 1);
                            display(m.getTypeOfPurchases(it, true), m.getTotal(it), it);
                            break;
                        case 4:
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

    private static void display(Collection<String> colln, Double d, ItemType typeForTotal) {
        System.out.println(typeForTotal.toString() + ":");
        for (String elem : colln) {
            System.out.println(elem);
        }
        if (colln.size() == 0) System.out.println("Purchase list is empty!");
        System.out.println(String.format("Total: $%.2f", d));
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Manager m = new Manager();
        itemTypes = m.getItemTypes();
        boolean exit = false;
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