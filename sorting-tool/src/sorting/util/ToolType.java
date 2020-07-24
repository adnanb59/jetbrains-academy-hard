package sorting.util;

public enum ToolType {
    WORD("word"),
    LINE("line"),
    LONG("number");

    private String repr;

    /** Enum constructor
    *
    * @param repr - String representation of ToolType
    */
    ToolType(String repr) {
        this.repr = repr;
    }

    /** Get the correct enum based on the string passed in (just checking if string passed in
    *  matches any of the string representations of the enums).
    *
    * @param value - string passed in to check
    * @return Associated ToolType (or null if there's no match)
    */
    public static ToolType getType(String value) {
        switch (value) {
            case "word":
                return WORD;
            case "line":
                return LINE;
            case "long":
                return LONG;
            default:
                return null;
        }
    }

    public String toString() {
        return repr;
    }
}