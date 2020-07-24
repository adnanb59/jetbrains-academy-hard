package sorting.util;

public enum SortType {
    NATURAL,
    BY_COUNT;

    /** Get the correct enum based on the string passed in (just checking if string passed in
    *  matches any of the string representations of the enums).
    *  It's not used here but it's good to keep to make it extensible
    *
    * @param value - string passed in to check
    * @return Associated SortType (or null if there's no match)
    */
    public static SortType getType(String value) {
        switch (value) {
            case "natural":
                return NATURAL;
            case "byCount":
                return BY_COUNT;
            default:
                return null;
        }
    }
}