import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

public class Runner {
    /**
     *
     * @param characters
     * @param words
     * @param sentences
     * @return
     */
    public static double getARI(int characters, int words, int sentences) {
        return (4.71*characters/words) + (0.5*words/sentences) - 21.43;
    }

    /**
     *
     * @param words
     * @param sentences
     * @param syllables
     * @return
     */
    public static double getFKR(int words, int sentences, int syllables) {
        return (0.39*words/sentences) + (11.8*syllables/words) - 15.59;
    }

    /**
     *
     * @param polysyllables
     * @param sentences
     * @return
     */
    public static double getSMOG(int polysyllables, int sentences) {
        return 1.043*Math.sqrt(polysyllables*30.0/sentences) + 3.1291;
    }

    /**
     *
     * @param L
     * @param S
     * @return
     */
    public static double getCLI(double L, double S) { // characters*100/words, sentences*100/words
        return 0.0588*L - 0.296*S - 15.8;
    }

    /**
     *
     * @param measure
     * @return
     */
    public static int mapMeasureToAge(double measure) {
        int rounded_measure = (int) Math.round(measure);
        int res = rounded_measure + 4 + (rounded_measure <= 3 ? 0 : 1);
        return res <= 18 ? res : 24;
    }

    /**
     *
     * @param characters
     * @param words
     * @param sentences
     * @param syllables
     * @param polysyllables
     * @return
     */
    public static String getResult(int characters, int words, int sentences, int syllables, int polysyllables) {
        return "\nWords: " + words + "\n" +
                "Sentences: " + sentences + "\n" +
                "Characters: " + characters + "\n" +
                "Syllables: " + syllables + "\n" +
                "Polysyllables: " + polysyllables + "\n";
    }

    /**
     *
     * @param choice
     * @param characters
     * @param words
     * @param sentences
     * @param syllables
     * @param polysyllables
     * @return
     */
    public static String getMeasures(int choice, int characters, int words, int sentences, int syllables, int polysyllables) {
        double avg_age = 0;
        int age;
        StringBuilder sb = new StringBuilder();
        if ((choice & 1) != 0) {
            double ari = getARI(characters, words, sentences);
            age = mapMeasureToAge(ari);
            sb.append("Automated Readability Index: ").append(String.format("%.2f", ari)).append(" (about ")
                    .append(age).append(" year olds).\n");
            avg_age += age;
        }
        if ((choice & 2) != 0) {
            double fk = getFKR(words, sentences, syllables);
            age = mapMeasureToAge(fk);
            sb.append("Flesch–Kincaid readability tests: ").append(String.format("%.2f", fk)).append(" (about ")
                    .append(age).append(" year olds).\n");
            avg_age += age;
        }
        if ((choice & 4) != 0) {
            double gg = getSMOG(polysyllables, sentences);
            age = mapMeasureToAge(gg);
            sb.append("Simple Measure of Gobbledygook: ").append(String.format("%.2f", gg)).append(" (about ")
                    .append(age).append(" year olds).\n");
            avg_age += age;
        }
        if ((choice & 8) != 0) {
            double cli = getCLI(characters*100.0/words, sentences*100.0/words);
            age = mapMeasureToAge(cli);
            sb.append("Coleman–Liau index: ").append(String.format("%.2f", cli)).append(" (about ")
                    .append(age).append(" year olds).\n");
            avg_age += age;
        }
        sb.append("\nThis text should be understood in average by ").append(avg_age / (choice == 15 ? 4 : 1))
                .append(" year olds.");

        return sb.toString();
    }

    public static void main(String[] args) {
        File f = new File(args[0]);
        //
        try (Scanner in = new Scanner(f); Scanner input = new Scanner(System.in)) {
            Pattern p = Pattern.compile("[aeiouy]{1,2}");
            Matcher m;
            int characters = 0, words = 0, sentences_count = 0, syllables = 0, polysyllables = 0;

            //
            System.out.println("The text is:");
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                System.out.println(line);
                //
                String[] sentences = line.split("\\s*([.?!])\\s*");
                sentences_count += sentences.length;
                for (String sentence : sentences) {
                    String[] words_in_sentence = sentence.split("\\s+");
                    for (String word : words_in_sentence) {
                        //
                        int vowels = 0;
                        m = p.matcher(word);
                        while (m.find()) {
                            vowels++;
                            if (m.end() == word.length() && word.charAt(m.end()-1) == 'e') {
                                vowels--;
                            }
                        }
                        if (word.charAt(word.length()-1) == 'e') vowels--;
                        if (vowels <= 0) vowels = 1;
                        characters += word.length();
                        syllables += vowels;
                        if (vowels > 2) polysyllables++;
                    }
                    words += words_in_sentence.length;
                    characters += 1;
                }
                if (!in.hasNextLine() && line.charAt(line.length()-1) != '.') characters -= 1;
            }

            //
            if (characters > 0) {
                polysyllables += 1;
                System.out.println(getResult(characters, words, sentences_count, syllables, polysyllables));

                //
                int check_choice = 0;
                while (check_choice == 0) {
                    System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
                    String choice = input.nextLine();
                    check_choice = choice.equals("all") ? 15 : ((choice.equals("ARI") ? 1 : 0) +
                            (choice.equals("FK") ? 2 : 0) +
                            (choice.equals("SMOG") ? 4 : 0) +
                            (choice.equals("CL") ? 8 : 0));
                    System.out.println();
                }

                System.out.println(getMeasures(check_choice, characters, words, sentences_count, syllables, polysyllables));
            }
        } catch (FileNotFoundException e) {
            e.getStackTrace();
        }
    }
}