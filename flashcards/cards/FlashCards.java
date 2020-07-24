package cards;

import java.io.*;
import java.util.*;

public class FlashCards {
    // flashcards is a collection for the flash cards and their respective definitions
    // attempts tracks attempts at getting the definition of a flash card correct (incorrect answers increments count)
    private Map<String, String> flashcards;
    private Map<String, Integer> attempts;
    // Stats for the hardest card
    private Set<String> hardest;
    private Integer hard_count;

    /**
    * Constructor for FlashCards
    */
    public FlashCards() {
        this.flashcards = new HashMap<String, String>();
        this.attempts = new HashMap<String, Integer>();
        this.hard_count = 0;
        this.hardest = new HashSet<String>();
    }

    /**
    * Add a flash card to the collection
    *
    * @param term - The phrase for the flash card
    * @param defn - The definition of the flash card
    */
    public void addCard(String term, String defn) {
        this.flashcards.put(term, defn);
        this.attempts.put(term, 0);
    }

    /**
    * Check if the definition provided by the user matches up with the phrase
    * of the flash card in question
    *
    * @param term - The phrase for the flash card in question
    * @param defn - A user provided answer for the definition
    * @return Whether or not the definition matches the definition of
    */
    public boolean isDefinitionCorrect(String term, String defn) {
        return defn.equalsIgnoreCase(this.flashcards.get(term));
    }

    /**
    * Check if the term provided is a valid flash card
    *
    * @param term - Phrase of a potential flash card
    * @return Whether or not the flash card exists
    */
    public boolean doesCardExist(String term) {
        return this.flashcards.containsKey(term);
    }

    /**
    * Find the existing flash card (if it exists), given the definition
    *
    * @param defn - A possible answer to a flash card
    * @return If the flash card exists, return the term, otherwise null
    * */
    public String getTermFromDefinition(String defn) {
        Optional<String> check = this.flashcards.keySet().stream().filter(e -> defn.equals(flashcards.get(e))).findFirst();
        return check.isPresent() ? check.get() : null;
    }

    /**
    * Check if the definition provided belongs to a flash card
    *
    * @param defn - A prospective definition for a flash card
    * @return Whether or not the definition exists
    */
    public boolean doesDefinitionExist(String defn) {
        return this.flashcards.containsValue(defn);
    }

    /**
    * Get the definition of a flash card (if it exists) given a potential term
    *
    * @param term - A term for a potential flash card
    * @return Get the definition of a flash card or null if it doesn't exist
    */
    public String getDefinition(String term) {
        return this.flashcards.getOrDefault(term, null);
    }

    /**
    * Delete a flash card from the collection (if it exists)
    *
    * @param term - Term for the flash card to delete
    * @return Whether or not the flash card exists
    */
    public boolean removeCard(String term) {
        boolean ret = this.flashcards.containsKey(term);
        this.flashcards.remove(term);
        this.attempts.remove(term);
        // If the card you removed was the hardest one, reset values
        this.hardest.remove(term);
        if (hardest.size() == 0) hard_count = 0;
        return ret;
    }

    /**
    * Take the cards in the collection and export them into the file specified by the user
    *
    * @param fileName - Name of file to export flash card data to
    * @return Status message of operation
    */
    public String exportCards(String fileName) {
        int numSaved = 0;
        StringBuilder sb = new StringBuilder();
        // Go through cards in collection and save them to the file
        try {
            FileWriter fw = new FileWriter(fileName);
            for (Map.Entry<String, String> e : flashcards.entrySet()) {
                fw.write(e.getKey() + "\n" + e.getValue() + "\n" + this.attempts.get(e.getKey()) + "\n");
                numSaved++;
            }
            fw.close();
        } catch(IOException e) {
            System.err.println(e.getMessage());
            sb.append(e.getMessage() + "\n");
        } finally {
            System.out.println(numSaved + " cards have been saved.\n");
            sb.append(numSaved + " cards have been saved.\n");
        }
        return sb.toString();
    }

    /**
    * Read file specified by user and add cards to the collection
    *
    * @param fileName - Name of file to import flash card data from
    * @return Status message of operation
    */
    public String importCards(String fileName) {
        File file = new File(fileName);
        int numLoaded = 0;
        StringBuilder sb = new StringBuilder();
        try (Scanner in = new Scanner(file)) {
            while (in.hasNextLine()) {
                String term = in.nextLine();
                String defn = in.nextLine();
                Integer count = in.nextInt();
                in.nextLine();
                flashcards.put(term, defn);
                attempts.put(term, count);
                // Checking if the attempt count for the current card being imported
                // is greater than the value recorded in the stats.
                if (count >= this.hard_count) {
                    // Add the card to hardest cards collection and update values
                    this.hardest.add(term);
                    if (count > this.hard_count) this.hardest.retainAll(Set.of(term));
                    this.hard_count = count;
                }
                numLoaded++;
            }
            System.out.println(numLoaded + " cards have been loaded.\n");
            sb.append(numLoaded + " cards have been loaded.\n");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.\n");
            sb.append("File not found.\n");
        }
        return sb.toString();
    }

    /**
    * Get all the cards that have been the hardest for the user to answer
    *
    * @return All the cards that have the most incorrect attempts at answering
    */
    public Set<String> getHardest() {
        return this.hardest;
    }

    /**
    * Get the highest number of attempts at answering a flash card where the user
    * still hasn't got the correct definition
    *
    * @return The highest number of attempts at answering a flash card
    */
    public Integer getHardCount() {
        return this.hard_count;
    }

    /**
    * Reset the stats of all the cards in the collection
    */
    public void resetCards() {
        for (String k :  flashcards.keySet()) {
            attempts.put(k, 0);
        }
        hardest.clear();
        hard_count = 0;
    }

    /**
    * Get a random set of cards (the amount specified by user)
    *
    * @param length - The amount of cards to get from the collection
    * @return The set of cards from the collection
    */
    public List<String> getCards(int length) {
        // If the number specified is larger than the collection size then return the whole set
        List<String> ret = new ArrayList<String>(flashcards.keySet());
        return (length <= flashcards.size()) ? ret.subList(0, length) : ret;
    }

    /**
    * Update the stats for the flash card defined by term.
    * Update the card's attempts stat based on if the user got it correct.
    *
    * @param term - Term for the flash card to update
    * @param isCorrect - Whether or not the user got it right
    */
    public void updateTermStats(String term, boolean isCorrect) {
        if (isCorrect) {
            hardest.remove(term);
            attempts.put(term, 0);
        } else {
            int t = attempts.get(term) + 1;
            attempts.put(term, t);
            if (t >= hard_count) {
                this.hardest.add(term);
                if (t > hard_count) this.hardest.retainAll(Set.of(term));
                hard_count = t;
            }
        }
    }
}