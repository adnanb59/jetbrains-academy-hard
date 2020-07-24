import java.io.*;
import java.util.*;
import search.*;

public class Runner {

    /***
    * Run the menu for user to interact with the search engine
    *
    * @param in - Scanner for user input
    * @param e - Search engine
    */
    public static void run(Scanner in, Engine e) {
        boolean runPrompt = true;
        while (runPrompt) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            switch(Integer.parseInt(in.nextLine())) {
                case 1:
                    int option = 0;
                    while (option == 0) {
                        System.out.println("Select a matching strategy: ALL, ANY, NONE");
                        String selection = in.nextLine();
                        option = (selection.equals("ALL") ? 1 :
                                selection.equals("ANY") ? 2 :
                                        selection.equals("NONE") ? 3 : 0);
                    }
                    System.out.println("Enter a name or email to search all suitable people.");
                    List<String> res;
                    // Independent of option, pass next user input as search query
                    if (option == 1) res = e.findPersonAll(in.nextLine().toLowerCase());
                    else if (option == 2) res = e.findPersonAny(in.nextLine().toLowerCase());
                    else res = e.findPersonNone(in.nextLine().toLowerCase());

                    if (res.size() != 0) {
                        System.out.println("Found people:");
                        for (String matchingPerson : res) {
                            System.out.println(matchingPerson);
                        }
                    } else {
                        System.out.println("No matching people found.");
                    }
                    break;
                case 2:
                    System.out.println("=== List of people ===");
                    for (String person : e.getPeople()) {
                        System.out.println(person);
                    }
                    break;
                case 0:
                    runPrompt = false;
                    break;
                default:
                    System.err.println("Incorrect option! Try again.");
                    break;
            }
        }
        System.out.println("Bye!");
    }

    /***
    * Main method (prompt user for people to store in database or read them from file).
    *
    * @param args - if they exist (--data [filename])
    */
    public static void main(String[] args) {
        // Check arguments to see if --data flag and filename are given
        boolean isDataPassed = false;
        String dataFile = null;
        for (String arg : args) {
            if (isDataPassed) {
                if (dataFile == null) dataFile = arg;
            } else if (arg.equals("--data")) {
                isDataPassed = true;
            }
        }

        Engine e = new Engine(new Data());
        // If file was passed in, go through it and add people to collection
        // Otherwise, prompt user to add people to the collection
        // Afterwards, run the main program
        if (dataFile != null) {
            File f = new File(dataFile);
            try (Scanner in = new Scanner(f)) {
                while (in.hasNextLine()) {
                    String person = in.nextLine();
                    e.addPerson(person);
                }
                in.close();
                run(new Scanner(System.in), e);
            } catch (IOException err) {
                err.printStackTrace();
            }
        } else {
            System.out.println("Enter the number of people:");
            try (Scanner in = new Scanner(System.in)) {
                int numPeople = Integer.parseInt(in.nextLine());
                System.out.println("Enter all people:");
                for (int i = 0; i < numPeople;) {
                    String person = in.nextLine();
                    if (e.addPerson(person)) {
                        i++;
                    } else {
                        System.out.println("Incorrect format for user, try again!");
                    }
                }
                run(in, e);
            } catch (NumberFormatException err) {
                err.printStackTrace();
            }
        }

    }
}
