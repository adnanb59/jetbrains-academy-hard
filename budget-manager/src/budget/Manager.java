package budget;

import budget.util.ItemType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Manager {
    private Double income;
    private Map<ItemType, List<Item>> list;
    private Map<ItemType, Double> totals;

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

    public double getBalance() {
        return this.income;
    }

    public double getTotal(ItemType it) {
        if (it.equals(ItemType.ALL)) return totals.values().stream().reduce(0.0, Double::sum);
        else return totals.getOrDefault(it, 0.0);
    }

    public Collection<String> getSortedTotals() {
        return totals.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(entry -> entry.getKey() + " - $" + String.format("%.2f", entry.getValue())).collect(Collectors.toList());
    }


    private Stream<Item> _getAllPurchases() {
        return list.values().stream().flatMap(Collection::stream);
    }

    public Collection<String> getAllPurchases(boolean sorted) {
        Stream<Item> intermediate = _getAllPurchases();
        if (sorted) intermediate = intermediate.sorted(Comparator.comparingDouble(Item::getCost).reversed());
        return intermediate.map(Item::toString).collect(Collectors.toList());
    }


    public List<ItemType> getItemTypes() {
        return Arrays.stream(ItemType.values()).collect(Collectors.toList());
    }


    public void addPurchaseItem(ItemType target, String name, double price) {
        Item item = new Item(name, price);
        list.get(target).add(item);
        totals.put(target, totals.get(target) + price);
        income -= price;
    }

    public boolean addIncome(Double income) {
        if (income < 0 || income*100 - Math.round(income*100) > 0.0) return false;
        this.income += income;
        return true;
    }

    public void setIncome(Double income) {
        if (income < 0 || income*100 - Math.round(income*100) > 0.0) System.out.println("Invalid dollar amount!");
        else this.income = income;
    }

    public Collection<String> getTypeOfPurchases(ItemType it, boolean sorted) {
        if (it.equals(ItemType.ALL)) return getAllPurchases(sorted);

        List<Item> intermediate = list.get(it);
        if (sorted) intermediate.sort(Comparator.comparingDouble(Item::getCost).reversed());
        return intermediate.stream().map(Item::toString).collect(Collectors.toList());
    }
}
