import recognition.*;

import java.util.Scanner;

public class Runner {
    public static String readGrid(Scanner in) {
        int lines = 0, ROWS = 5, COLS = 3;
        StringBuilder grid = new StringBuilder();

        System.out.println("Input grid:");
        while (lines < ROWS) {
            String curr = in.next();
            if (curr.matches(String.format("[X\\_]{%d}", COLS))) System.err.println("Invalid row entry");
            else grid.append(curr).append("\n");
            ++lines;
        }

        return grid.toString().trim();
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Recognizer r = new Recognizer(new Network());
        boolean canRunPrompt = true;

        while (canRunPrompt) {
            System.out.println("1. Learn the network\n2. Guess a number");
            System.out.print("Your choice: ");
            String option = in.nextLine();

            try {
                switch (Integer.parseInt(option)) {
                    case 1:
                        System.out.println(r.learn() ? "Done! Saved to the file." : "Error saving to file.");
                        break;
                    case 2:
                        String grid = readGrid(in);
                        int result = r.guess(grid);
                        System.out.println("This number is " + result);
                        break;
                    case 0:
                        canRunPrompt = false;
                        break;
                    default:
                        System.err.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter valid option.");
            } finally {
                if (!in.hasNext()) canRunPrompt = false;
            }
        }

        in.close();
    }
}
