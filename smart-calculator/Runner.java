import calculator.*;
import java.util.*;

public class Runner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SmartCalculator sc = new SmartCalculator();
        boolean isFinished = false;

        // -- PROCESS USER ARGUMENTS --
        while (!isFinished) {
            String input = scanner.nextLine().trim();
            switch(input) {
                case "/exit":
                    isFinished = true;
                    break;
                case "/help":
                    System.out.println("The program calculates the operation of numbers");
                    break;
                default:
                    if (input.matches("^/(.*)")) System.out.println("Unknown command");
                    else sc.process(input); // Process expression
                    break;
            }
        }
        scanner.close();
        System.out.print("Bye!");
    }
}
