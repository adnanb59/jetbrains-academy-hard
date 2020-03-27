package budget.util;

public enum ItemType {
    FOOD("Food"),
    CLOTHES("Clothes"),
    ENTERTAINMENT("Entertainment"),
    OTHER("Other"),
    ALL("All");

    private String type;

    ItemType(String type) {
        this.type = type;
    }

    /** String representation of enum
    *
    * @return String representation of ItemType
    */
    public String toString() {
        return this.type;
    }
}
