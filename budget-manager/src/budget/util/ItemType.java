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

    @Override
    public String toString() {
        return this.type;
    }
}
