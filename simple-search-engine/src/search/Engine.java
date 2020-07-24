package search;

import java.util.*;
import java.util.stream.*;

public class Engine {
    private final Data data;

    /***
    * Engine object constructor
    *
    * @param d - Data access object
    */
    public Engine(Data d) {
        this.data = d;
    }

    /***
    * Try to add the passed in person to the database collection.
    *
    * @param person - Person to add to collection
    * @return Whether or not the addition was successful
    */
    public boolean addPerson(String person) {
        boolean res;
        if (res = validatePerson(person)) {
            data.addPersonToStructures(person);
        }
        return res;
    }

    /***
    * Check to see whether or not the potential person can be added to the database.
    * First by checking to see if it matches the format of [first last email(Optional)], then
    * by checking to see if it exists in the collection.
    *
    * @param potential - Person that can be potentially added to collection
    * @return Whether or not person can be added to collection
    */
    private boolean validatePerson(String potential) {
        String[] entry = potential.trim().split("\\s+"); // split person by spaces

        // Check name contains only alphabetic characters
        if (!entry[0].matches("[a-zA-Z]+") || !entry[1].matches("[a-zA-Z]+")) return false;

        // Check if email is in correct format (if it is passed in)
        if (entry.length == 3 && !entry[2].matches("[a-zA-Z0-9_\\-.]+@[a-z]+\\.(com|ca)")) return false;

        return !data.doesPersonExist(potential); // Check if it is already in collection
    }

    /***
    * Given a search query, find all the users in the collection that match
    * any part of the query.
    *
    * @param query - Search query to check for in collection
    * @return Any user that matches some part of the search query
    */
    public List<String> findPersonAny(String query) {
        return data.getPeople(data.findPeopleInIndex(query,true));
    }

    /***
    * Given a search query, find all the users in the collection that do not match any part of the query.
    *
    * @param query - Search query to check for in collection
    * @return All the users that do not match any parts of the search query
    */
    public List<String> findPersonNone(String query) {
        // Make a set of all the possible indices and take the difference with those that match
        // parts of the search query
        Set<Integer> result = IntStream.range(0, data.length()).boxed().collect(Collectors.toSet());
        result.removeAll(data.findPeopleInIndex(query, true));
        return data.getPeople(result);
    }

    /***
    * Given a search query, find people from the collection that match all the terms in the query.
    *
    * @param query - Search query to check for in collection
    * @return Users that match all parts of the query
    */
    public List<String> findPersonAll(String query) {
        return data.getPeople(data.findPeopleInIndex(query,false));
    }

    /***
    * Get all the people from the database.
    *
    * @return All the people from the database
    */
    public List<String> getPeople() {
        return data.getPeople();
    }
}