package knapsack;


import java.util.List;


public class KnapsackInstance {


    private final List<Item> items;
    private final int capacity;


    public KnapsackInstance(
            List<Item> items,
            int capacity) {


        this.items = items;
        this.capacity = capacity;
    }


    public List<Item> getItems() {
        return items;
    }


    public int getCapacity() {
        return capacity;
    }
}