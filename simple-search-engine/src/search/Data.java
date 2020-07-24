package search;

import java.util.*;

public class Data {
    private List<String> people;
    private Map<String, Set<Integer>> invertedIndexStructure;

    /***
    * Data object constructor
    */
    public Data() {
        people = new ArrayList<>();
        invertedIndexStructure = new LinkedHashMap<>();
    }

    /***
    * Collect indices in inverted index data structure that contain parts of person passed in.
    *
    * @param query - Search query passed in (consists of one or more people)
    * @param areAllElementsKept - Flag to catch whether or not to retain indices found (set operation - intersection)
    * @return Indices in data structure that match up to search terms
    */
    public Set<Integer> findPeopleInIndex(String query, boolean areAllElementsKept) {
        Set<Integer> peopleOfInterest = new HashSet<>();

        // Go through search terms
        for (String term : query.split("\\s+")) {
            // Per term, check data structure to see if it exists
            for (String k : invertedIndexStructure.keySet()) {
                // If it exists in the structure, choose to keep all
                // the indices or those that are already in the result set
                if (k.toLowerCase().equals(term.toLowerCase())) {
                    if (areAllElementsKept) peopleOfInterest.addAll(invertedIndexStructure.get(k));
                    else {
                        if (peopleOfInterest.isEmpty()) peopleOfInterest.addAll(invertedIndexStructure.get(k));
                        else peopleOfInterest.retainAll(invertedIndexStructure.get(k));
                    }

                }
            }
        }
        return peopleOfInterest;
    }

    /***
    * Add person passed in to the data collections (index and collection)
    *
    * @param person Person to add to the collection (and to update the index structure with)
    */
    public void addPersonToStructures(String person) {
        people.add(person);
        for (String p : person.split("\\s+")) {
            if (!invertedIndexStructure.containsKey(p)) {
                invertedIndexStructure.put(p, new LinkedHashSet<>(Collections.singletonList(people.size() - 1)));
            } else {
                invertedIndexStructure.get(p).add(people.size()-1);
            }
        }
    }

    /***
    * Take the person passed in and check if it already exists in the collection.
    *
    * @param person - Person to check against database
    * @return Whether or not person exists in the database
    */
    public boolean doesPersonExist(String person) {
        for (String p : person.split("\\s+")) {
            if (!invertedIndexStructure.containsKey(p)) return false;
        }
        return true;
    }

    /***
    * Get size of database collection
    *
    * @return Size of collection
    */
    public int length() {
        return this.people.size();
    }

    /***
    * Get people in database that match up to people in database.
    *
    * @param indices - Indices that matched up to previous search terms
    * @return People in collection that match up to indices
    */
    public List<String> getPeople(Set<Integer> indices) {
        List<String> ret = new ArrayList<>();
        for (Integer i : indices) {
            ret.add(people.get(i));
        }
        return ret;
    }

    /***
    * Get all the people in the database.
    *
    * @return The list of people in the collection
    */
    public List<String> getPeople() {
        return people;
    }
}
