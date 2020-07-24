import java.io.*;
import java.util.*;
import cards.*;

public class Runner {
    static final String MENU_PROMPT = "Input the action (add, remove, import, export, ask, exit," +
                                      " log, hardest card, reset stats):";

    public static void main(String[] args) {
        // -- PRE-PROCESS COMMAND LINE ARGUMENTS --
        boolean hasError = false;
        String importFile = null, exportFile = null;
        for (int i = 0; i < args.length; i += 2) {
            // for -export/-import, error occurs if there is no argument with the flag or
            // the flag was filled previously
            switch(args[i]) {
                case "-export":
                    hasError = i >= args.length - 1 || exportFile != null;
                    if (!hasError) exportFile = args[i+1];
                    break;
                case "-import":
                    hasError = i >= args.length - 1 || importFile != null;
                    if (!hasError) importFile = args[i+1];
                    break;
                default:
                    hasError = true;
                    break;
            }
        }

        // If there was an error in pre-processing, don't go through with the program
        if (hasError) {
            System.out.println("Error. Correct format: java Runner [-import <String>] [-export <String>]");
            System.exit(0);
        }

        // -- INITIALIZE VARIABLES --
        Scanner in = new Scanner(System.in);
        FlashCards fc = new FlashCards();
        List<String> log = new ArrayList<>();
        StringBuilder action = new StringBuilder();
        boolean exit = false;
        String prompt;

        // If there was an import file specified, do an import first
        if (importFile != null) action.append(fc.importCards(importFile));

        // -- PROCESS USER INPUT --
        // Run user prompt cycle
        while (!exit) {
            System.out.println(MENU_PROMPT);
            action.append(MENU_PROMPT + "\n");
            switch(in.nextLine()) {
                case "add":
                    // Get term for a new flash card from user
                    System.out.println("The card:");
                    prompt = in.nextLine();
                    action.append("add\nThe card:\n" + prompt + "\n");
                    // Check if flash card proposed by user already exists
                    if (!fc.doesCardExist(prompt)) { // if it hasn't, prompt for definition
                        System.out.println("The definition of the card:");
                        String defn = in.nextLine();
                        action.append("The definition of the card:\n" + defn + "\n");
                        // Similarly, check if definition has been used before by user
                        if (!fc.doesDefinitionExist(defn)) {
                            fc.addCard(prompt, defn); // it hasn't; add the card to collection and let user know
                            System.out.println("The pair (\"" + prompt + "\":\"" + defn + "\") has been added.\n");
                            action.append("The pair (\"" + prompt + "\":\"" + defn + "\") has been added.\n\n");
                        } else {
                            System.out.println("The definition \"" + defn + "\" already exists.\n");
                            action.append("The definition \"" + defn + "\" already exists.\n\n");
                        }
                    } else {
                        System.out.println("The card \"" + prompt + "\" already exists.\n");
                        action.append("The card \"" + prompt + "\" already exists.\n\n");
                    }
                    break;
                case "remove":
                    System.out.println("The card:");
                    prompt = in.nextLine();
                    // Try to remove card and procure error/success response as a result to display
                    String result = fc.removeCard(prompt) ? "The card has been removed.\n" :
                                                            "Can't remove \"" + prompt + "\": there is no such card.\n";
                    System.out.println(result);
                    action.append("remove\nThe card:\n" + prompt + "\n" + result + "\n");
                    break;
                case "import":
                    System.out.println("File name:");
                    prompt = in.nextLine();
                    action.append("import\nFile name:\n" + prompt + "\n" + fc.importCards(prompt));
                    break;
                case "export":
                    System.out.println("File name:");
                    prompt = in.nextLine();
                    action.append("export\nFile name:\n" + prompt + "\n" + fc.exportCards(prompt));
                    break;
                case "ask":
                    System.out.println("How many times to ask?");
                    prompt = in.next();
                    in.nextLine();
                    action.append("ask\nHow many times to ask?\n" + prompt + "\n");
                    try {  // Take number entered by user for # of times to ask and check if it is a valid integer
                        Integer n = Integer.parseInt(prompt);
                        if (n <= 0) {
                            System.out.println("Enter a number above one.");
                            action.append("Enter a number above one.\n");
                        } else {
                            List<String> cards = fc.getCards(n); // it's a valid number, get the cards
                            // Once you get the cards (and there are cards), go through them
                            // If number of times to ask exceeds number of cards, cycle back to beginning of set
                            if (cards.size() > 0) {
                                for (int i = 0; i < n; i++) {
                                    // ask for definition of card
                                    System.out.println("Print the definition of \"" +
                                                       cards.get(i % cards.size()) + "\":");
                                    prompt = in.nextLine();
                                    action.append("Print the definition of \"" +
                                                  cards.get(i % cards.size()) + "\":\n" + prompt + "\n");
                                    // Check definition, update card stats accordingly and display result message
                                    if (fc.isDefinitionCorrect(cards.get(i % cards.size()), prompt)) {
                                        fc.updateTermStats(cards.get(i % cards.size()), true);
                                        System.out.println("Correct answer\n");
                                        action.append("Correct answer\n\n");
                                    } else {
                                        fc.updateTermStats(cards.get(i % cards.size()), false);
                                        System.out.print("Wrong answer. (The correct one is \"" +
                                                         fc.getDefinition(cards.get(i % cards.size())) + "\"");
                                        action.append("Wrong answer. (The correct one is \"" +
                                                      fc.getDefinition(cards.get(i % cards.size())) + "\"");
                                        // Check if definition provided exists for another card & inform user if so
                                        prompt = fc.getTermFromDefinition(prompt);
                                        if (prompt != null) {
                                            System.out.print(", you've just written the definition of \"" +
                                                             prompt + "\"");
                                            action.append(", you've just written the definition of \"" +
                                                          prompt + "\"");
                                        }
                                        System.out.println(").\n");
                                        action.append(").\n");
                                    }
                                }
                            } else {
                                System.out.println("No cards.\n");
                                action.append("No cards.\n\n");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Need to enter a valid number\n");
                        action.append("Need to enter a valid number\n\n");
                    }
                    break;
                case "exit":
                    exit = true;
                    break;
                case "log":
                    // Get log's file name from user
                    System.out.println("File name:");
                    prompt = in.nextLine();
                    action.append("log\nFile name:\n" + prompt + "\n");
                    try { // Try to open a file for writing, go through log and write to file
                        FileWriter fw = new FileWriter(prompt);
                        int tmp = 0;
                        for (String l : log) {
                            fw.write(tmp == log.size()-1 ? l.trim() : l);
                            tmp++;
                        }
                        System.out.println("The log has been saved.\n");
                        action.append("The log has been saved.\n\n");
                        fw.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        action.append(e.getMessage() + "\n");
                    }
                    break;
                case "hardest card":
                    // First check if there are "hardest cards"
                    Set<String> hardCards = fc.getHardest();
                    if (hardCards.size() == 0) {
                        System.out.println("There are no cards with errors.\n");
                        action.append("hardest card\nThere are no cards with errors.\n");
                    } else { // There are hardest cards
                        // Display cards that are the hardest
                        System.out.print("The hardest card" + (hardCards.size() > 1 ? "s are " : " is "));
                        action.append("The hardest card" + (hardCards.size() > 1 ? "s are " : " is "));
                        int temp = 0;
                        for (String card : hardCards) {
                            System.out.print("\"" + card + "\"");
                            action.append("\"" + card + "\"");
                            if (temp < hardCards.size() - 1) {
                                System.out.print(", ");
                                action.append(", ");
                            }
                            temp++;
                        }
                        // Display the number of failures
                        System.out.println(". You have " + fc.getHardCount() + " error" +
                                           (hardCards.size() > 1 ? "s" : "") + " answering it.\n");
                        action.append(". You have " + fc.getHardCount() + " error" +
                                      (hardCards.size() > 1 ? "s" : "") + " answering it.\n\n");
                    }
                    break;
                case "reset stats":
                    fc.resetCards();
                    System.out.println("Card statistics has been reset.\n");
                    action.append("reset stats\nCard statistics has been reset.\n\n");
                    break;
                default:
                    System.out.println("Incorrect input. Try again.\n");
                    action.append("Incorrect input. Try again.\n\n");
                    break;
            }
            // After one user action from prompt, append log with value from StringBuilder
            // and clear it for the next round
            log.add(action.toString());
            action.setLength(0);
        }

        System.out.print("Bye bye!");

        if (exportFile != null) { // If export file was specified, export collection to file at end of program
            System.out.println();
            fc.exportCards(exportFile);
        }
    }
}