import maze.*;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

public class Runner {
    /**
     * Read a maze from file passed in and create new Maze object
     *
     * @param fileName - file to read
     * @return Maze representing file data (or null if incorrect format)
     */
    public static Maze read(String fileName) {
        Maze m = null;
        try (Scanner fileReader = new Scanner(new File(fileName))) {
            int size = Integer.parseInt(fileReader.nextLine().trim());
            if (size < 3) {
                System.out.println("Cannot load the maze. It has an invalid format");
            } else {
                boolean isValid = true, hasEntrance = false, hasExit = false;
                HashSet<Integer> values = new HashSet<>();

                while (fileReader.hasNext() && isValid) {
                    int value = Integer.parseInt(fileReader.next());
                    // Check if value of free cell falls in range of maze with size <size>
                    if (value >= 0 && value < size*size) values.add(value);
                    else isValid = false;

                    // Check if more than one entrance and exit has been provided
                    if (value % size == 0) {
                        if (hasEntrance) isValid = false;
                        else hasEntrance = true;
                    } else if (value % size == size-1) {
                        if (hasExit) isValid = false;
                        else hasExit = true;
                    }
                }

                if (isValid) isValid = hasEntrance && hasExit; // confirmation that one entrance and exit has been provided

                if (!isValid) System.out.println("Cannot load the maze. It has an invalid format");
                else m = new Maze(size, size, values);
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file " + fileName + " does not exist");
        } catch (NumberFormatException e) {
            System.out.println("Cannot load the maze. It has an invalid format");
        }

        return m;
    }

    /**
    * Encode maze into file (specified by file name) for later use
    *
    * @param fileName - file to write to
    * @param m - maze to encode into file
    */
    public static void write(String fileName, Maze m) {
        if (m == null) return;

        try (FileWriter fw = new FileWriter(new File(fileName))) {
            // Print maze size (it's a square maze, so one side is printed)
            // Followed by the maze's path
            fw.write(m.getWidth() + "\n");
            for (Integer v : m.getPath()) {
                fw.write(" " + v);
            }
            fw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean runningProgram = true;
        Maze m = null;

        while (runningProgram) {
            System.out.println("=== Menu ===\n1. Generate a new maze\n2. Load a maze");
            if (m != null) System.out.println("3. Save the maze\n4. Display the maze\n5. Find the escape");
            System.out.println("0. Exit");

            try {
                int option = Integer.parseInt(in.nextLine().trim());
                String fileName;
                switch (option) {
                    case 1:
                        System.out.println("Enter the size of a new maze");
                        int size = Integer.parseInt(in.nextLine().trim());
                        m = new Maze(size, size);
                        System.out.println(m);
                        break;
                    case 2:
                        fileName = in.nextLine().trim();
                        m = read(fileName);
                        break;
                    case 3:
                        if (m == null) System.out.println("Incorrect option. Please try again");
                        else {
                            fileName = in.nextLine().trim();
                            write(fileName, m);
                        }
                        break;
                    case 4:
                        if (m == null) System.out.println("Incorrect option. Please try again");
                        else System.out.println(m);
                        break;
                    case 5:
                        if (m == null) System.out.println("Incorrect option. Please try again");
                        else System.out.println(m.showEscape());
                        break;
                    case 0:
                        runningProgram = false;
                        break;
                    default:
                        System.out.println("Incorrect option. Please try again");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect option. Please try again");
            }
            System.out.println();
        }

        System.out.println("Bye!");
    }
}