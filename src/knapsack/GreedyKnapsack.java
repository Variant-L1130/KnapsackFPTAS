package knapsack;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class GreedyKnapsack {


    public static ExactKnapsack.Result solve(
            List<Item> items,
            int capacity) {


        // Make a copy so that we don't change the original list
        List<Item> sortedItems = new ArrayList<>(items);


        // Sort by value/weight ratio, highest first
        sortedItems.sort(
                Comparator.comparingDouble(Item::getRatio)
                          .reversed()
        );


        List<Item> selectedItems = new ArrayList<>();


        int totalWeight = 0;
        int totalValue = 0;


        for (Item item : sortedItems) {


            // Take the item if it fits
            if (totalWeight + item.getWeight() <= capacity) {


                selectedItems.add(item);


                totalWeight += item.getWeight();
                totalValue += item.getValue();
            }
        }


        return new ExactKnapsack.Result(
                totalValue,
                totalWeight,
                selectedItems
        );
    }
}