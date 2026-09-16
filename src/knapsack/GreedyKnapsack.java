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


        // Best single item that fits alone.
        // Taking max(greedy fill, best single item) is what gives
        // the greedy heuristic its provable 2-approximation bound;
        // without it, worst-case performance is unbounded.
        Item bestSingle = null;
        int bestSingleValue = 0;


        for (Item item : items) {


            if (item.getWeight() <= capacity
                    && item.getValue() >= bestSingleValue) {


                bestSingleValue = item.getValue();
                bestSingle = item;
            }
        }


        if (bestSingle != null
                && bestSingleValue > totalValue) {


            List<Item> bestSingleList =
                    new ArrayList<>();
            bestSingleList.add(bestSingle);


            return new ExactKnapsack.Result(
                    bestSingleValue,
                    bestSingle.getWeight(),
                    bestSingleList
            );
        }


        return new ExactKnapsack.Result(
                totalValue,
                totalWeight,
                selectedItems
        );
    }
}