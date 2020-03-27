package budget;

public class Item {
    private String label;
    private double cost;

    /** Item constructor
    *
    * @param label - Name of item being purchased
    * @param cost - Cost of item
    */
    public Item(String label, double cost) {
        this.label = label;
        this.cost = cost;
    }

    /** Return the cost of the current item
    *
    * @return the cost of the item
    */
    public double getCost() { return this.cost; }

    /** String representation of Item
    *
    * @return "label $price"
    */
    public String toString() {
        return label + " $" + String.format("%.2f", cost);
    }
}
