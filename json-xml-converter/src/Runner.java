package converter;

import converter.tools.Formatter;
import converter.tools.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Runner {
    public static void main(String[] args) {
        File f = new File("test.txt");
        try (Scanner in = new Scanner(f)) {
            StringBuilder sb = new StringBuilder();
            while (in.hasNextLine()) {
                sb.append(in.nextLine());
            }
            String content = sb.toString().trim();
            if (content.length() < 2) {
                System.err.println("ERROR");
                System.exit(0);
            }

            Formatter fc = null;
            switch (content.charAt(0)) {
                case '{':
                    fc = new JSONFormatter();
                    break;
                case '<':
                    fc = new XMLFormatter();
                    break;
                default:
                    System.err.println("ERROR");
                    System.exit(0);
            }
            String result = fc.process(content);
            System.out.println(result);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR");
        }
    }
}
