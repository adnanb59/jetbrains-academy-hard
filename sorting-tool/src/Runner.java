import java.io.*;
import java.util.*;
import sorting.*;
import sorting.util.*;

public class Runner {
    /** Sorting Tool Runner **/
    public static void main(String[] args) {
        SortType st = SortType.NATURAL;
        ToolType tt = ToolType.WORD;
        String inFile = null, outFile = null;
        boolean hasError = false;

        // PROCESS COMMAND LINE ARGUMENTS
        for (int i = 0; i < args.length; i++) {
            /* For each valid flag, check if valid option was passed in (if passed in at all).
               There is also the possibility of no option passed (catch as well). */
            if (args[i].equals("-dataType")) {
                if (i+1 == args.length || args[i+1].startsWith("-")) {
                    System.err.println("No data type defined!");
                    hasError = true;
                } else if (args[i+1].matches("(word|line|long)")) {
                    tt = args[i+1].equals("word") ? tt : ToolType.getType(args[i+1]);
                } else {
                    System.err.println("\"" + args[i+1] + "\" is an invalid parameter for -dataType flag");
                    hasError = true;
                }
            } else if (args[i].equals("-sortingType")) {
                if (i+1 == args.length || args[i+1].startsWith("-")) {
                    System.err.println("No sorting type defined!");
                    hasError = true;
                } else if (args[i+1].matches("(natural|byCount)")) {
                    st = args[i+1].equals("natural") ? st : SortType.BY_COUNT;
                } else {
                    System.err.println("\"" + args[i+1] + "\" is an invalid parameter for -sortingType flag");
                    hasError = true;
                }
            } else if (args[i].equals("-inputFile")) {
                if (i+1 == args.length || args[i+1].startsWith("-")) {
                    System.err.println("No input file defined!");
                    hasError = true;
                } else {
                    inFile = args[i+1];
                }
            } else if (args[i].equals("-outputFile")) {
                if (i+1 == args.length || args[i+1].startsWith("-")) {
                    System.err.println("No output file defined!");
                    hasError = true;
                } else {
                    outFile = args[i+1];
                }
            } else if (args[i].startsWith("-")) { // invalid flag entered
                System.err.println(String.format("\"%s\" isn't a valid parameter. It's skipped.", args[i]));
                hasError = true;
            }
        }

        if (hasError) {
            System.err.println("java SortingTool [-sortingType natural|byCount] [-dataType word|long|line] " +
                    "[-inputFile <filename>] [-outputFile <filename>]");
            System.exit(0);
        }

        /* The command line arguments have been processed, now get the user input to
           create the data collection as well as the sorting tool */
        try {
            Scanner in = inFile == null ? new Scanner(System.in) : new Scanner(new File(inFile));

            Tool t = new Tool(tt, st);
            String res;
            /* First, collect the user entries into a collection then pass them into the
               tool to sort and store the formatted result string into the variable */
            if (tt == ToolType.LONG) {
                List<Integer> input_values = new ArrayList<>();
                while (in.hasNext()) {
                    try {
                        Integer i = Integer.parseInt(in.next());
                        input_values.add(i);
                    } catch (NumberFormatException e) {
                        System.err.println(String.format("\"%s\" isn't a long. It's skipped.", e.getCause()));
                    }
                }
                res = t.act(input_values); // MAIN ENTRY POINT TO SORTING TOOL
            } else {
                List<String> input_values = new ArrayList<>();
                while (tt == ToolType.WORD && in.hasNext() || tt == ToolType.LINE && in.hasNextLine()) {
                    if (tt == ToolType.WORD) input_values.add(in.next());
                    else input_values.add(in.nextLine());
                }
                res = t.act(input_values);
            }
            in.close();

            // Output result (file or console depending on whether one was passed in)
            if (outFile == null) System.out.println(res);
            else {
                FileWriter fw = new FileWriter(new File(outFile));
                fw.write(res);
                fw.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getCause() + " isn't a valid file.");
        } catch (IOException e) {
            System.err.println("Something went wrong when writing to file.");
            e.printStackTrace();
        }
    }
}