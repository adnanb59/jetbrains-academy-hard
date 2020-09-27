import java.util.Scanner;

import minesweeper.*;

public class Runner {
    public static void play(Scanner in, Game m) {
        while (!m.isCompleted()) {
            boolean validMove = false;
            while (!validMove) {
                System.out.print(m);
                System.out.print("Set/unset mines marks or claim a cell as free: ");
                // VALID ENTRY: column row "mine"/"free"
                String[] coordinates = in.nextLine().trim().split("\\s+");
                if (coordinates.length != 3) System.out.println("Invalid number of parameters passed.");
                else {
                    if (!(coordinates[2].equals("free") || coordinates[2].equals("mine"))) System.out.println("Invalid option.");
                    else {
                        try {
                            validMove = m.makeMove(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), coordinates[2]);
                            if (!validMove) System.out.println("Invalid move.");
                        } catch (NumberFormatException e) {
                            System.out.println("Pass in numbers!");
                        } finally {
                            System.out.println();
                        }
                    }
                }
            }
        }
        System.out.print(m);
        if (m.wonTheGame()) System.out.println("Congratulations! You found all mines!");
        else System.out.println("You stepped on a mine and failed!");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            int size = 0;
            while (size < 1) {
                System.out.print("How large do you want the grid? (NxN cells, enter one number): ");
                size = Integer.parseInt(in.nextLine().trim());
            }
            int mines = 0;
            while (mines < 1 || mines >= size*size) {
                System.out.print("How many mines do you want on the field? ");
                mines = Integer.parseInt(in.nextLine().trim());
            }
            System.out.println();
            Game game = new Game(size, mines);
            play(in, game);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
