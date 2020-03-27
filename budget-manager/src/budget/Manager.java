package budget;

import budget.util.ItemType;
import java.util.*;
import java.util.stream.*;

public class Manager {
    private Double income;
    private Map<ItemType, List<Item>> list;
    private Map<ItemType, Double> totals;

    /** Manager constructor **/
    public Manager() {
        income = 0.0;
        list = new HashMap<>();
        totals = new TreeMap<>();
        for (ItemType type : ItemType.values()) {
            if (type.equals(ItemType.ALL)) continue;
            totals.put(type, 0.0);
            list.put(type, new ArrayList<>());
        }
    }

    /** Get the types of purchases that exist in the manager
    *
    * @return A collection of the types of purchases in the manager
    */
    public List<ItemType> getItemTypes() {
        return Arrays.stream(ItemType.values()).collect(Collectors.toList());
    }

    /** Get the current balance in the manager.
    *
    * @return the balance of the manager
    */
    public double getBalance() {
        return this.income;
    }

    /** Get total of a category of purchases (or the total of all the purchases)
    *
    * @param it - Type of purchase in the manager
    * @return The total sum for a specific category of purchases
    */
    public double getTotal(ItemType it) {
        if (it.equals(ItemType.ALL)) return totals.values().stream().reduce(0.0, Double::sum);
        else return totals.getOrDefault(it, 0.0);
    }

    /** Take the categories of purchases and sort them in descending order by their total sums.
    *   Return the sorted result.
    *
    * @return Sorted collection of categories of purchases by their total sum
    */
    public Collection<String> getSortedTotals() {
        return totals.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .map(entry -> entry.getKey() + " - $" + String.format("%.2f", entry.getValue()))
                                .collect(Collectors.toList());
    }


    /** Private method returning a stream of all the items in the manager
    *
    * @return All the purchases in the manager as a stream (to be modified later)
    */
    private Stream<Item> _getAllPurchases() {
        return list.values().stream().flatMap(Collection::stream);
    }

    /** Collection of all the purchases in the manager, which can be sorted based on the parameter passed in.
    *
    * @param sorted - parameter specifying whether or not the purchases should be sorted by their cost
    * @return The list of all purchases in the manager
    */
    public Collection<String> getAllPurchases(boolean sorted) {
        Stream<Item> intermediate = _getAllPurchases();
        if (sorted) intermediate = intermediate.sorted(Comparator.comparingDouble(Item::getCost).reversed());
        return intermediate.map(Item::toString).collect(Collectors.toList());
    }

    /** Create an item that will be added to the manager under a specific type of purchase.
    *   The type, name and price of the purchase are passed in through the parameters.
    *   Update the income by reducing the amount of the cost as well.
    *
    * @param target - type of purchase for this item
    * @param name - name of item
    * @param price - price of item
    */
    public void addPurchaseItem(ItemType target, String name, double price) {
        Item item = new Item(name, price);
        list.get(target).add(item);
        totals.put(target, totals.get(target) + price);
        income -= price;
    }

    /** Add the specified income to the manager to increase balance for purchases.
    *
    * @param income - Income to add to the manager
    * @return Whether or not the income was added to the manager
    */
    public boolean addIncome(Double income) {
        if (income < 0 || income*100 - Math.round(income*100) > 0.0) return false;
        this.income += income;
        return true;
    }

    /** Set the income of the manager to the specified value.
    *
    * @param income - the income to set the manager to
    */
    public void setIncome(Double income) {
        if (income < 0 || income*100 - Math.round(income*100) > 0.0) System.out.println("Invalid dollar amount!");
        else this.income = income;
    }

    /** Get a list of purchases for a specific category of purchase.
    *   Depending on the sorted parameter, the list can be sorted or unsorted.
    *
    * @param it - the category of purchase being queried for
    * @param sorted - parameter specifying whether or not the list is sorted
    * @return The collection of purchases for a specific category (or all)
    */
    public Collection<String> getTypeOfPurchases(ItemType it, boolean sorted) {
        if (it.equals(ItemType.ALL)) return getAllPurchases(sorted);

        List<Item> intermediate = list.get(it);
        if (sorted) intermediate.sort(Comparator.comparingDouble(Item::getCost).reversed());
        return intermediate.stream().map(Item::toString).collect(Collectors.toList());
    }
}
