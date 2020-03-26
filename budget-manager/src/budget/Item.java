package budget;

public class Item {
    private String label;
    private double cost;

    public Item(String label, double cost) {
        this.label = label;
        this.cost = cost;
    }

    public double getCost() { return this.cost; }

    public String toString() {
        return label + " $" + String.format("%.2f", cost);
    }
}
