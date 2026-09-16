package knapsack;


import java.util.ArrayList;
import java.util.List;


public class ExactKnapsack {


    public static class Result {
        private final int totalValue;
        private final int totalWeight;
        private final List<Item> selectedItems;


        public Result(int totalValue, int totalWeight, List<Item> selectedItems) {
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
            this.selectedItems = selectedItems;
        }


        public int getTotalValue() {
            return totalValue;
        }


        public int getTotalWeight() {
            return totalWeight;
        }


        public List<Item> getSelectedItems() {
            return selectedItems;
        }
    }


    public static Result solve(List<Item> items, int capacity) {


        int n = items.size();


        // dp[i][w] = maximum value using first i items
        // with capacity w
        int[][] dp = new int[n + 1][capacity + 1];


        for (int i = 1; i <= n; i++) {


            Item item = items.get(i - 1);


            for (int w = 0; w <= capacity; w++) {


                // Don't take the item
                dp[i][w] = dp[i - 1][w];


                // Take the item if it fits
                if (item.getWeight() <= w) {
                    int valueWithItem =
                            dp[i - 1][w - item.getWeight()]
                            + item.getValue();


                    dp[i][w] =
                            Math.max(dp[i][w], valueWithItem);
                }
            }
        }


        // Reconstruct solution
        List<Item> selectedItems = new ArrayList<>();


        int w = capacity;


        for (int i = n; i >= 1; i--) {


            if (dp[i][w] != dp[i - 1][w]) {


                Item item = items.get(i - 1);


                selectedItems.add(item);


                w -= item.getWeight();
            }
        }


        int totalValue = dp[n][capacity];


        int totalWeight = 0;


        for (Item item : selectedItems) {
            totalWeight += item.getWeight();
        }


        return new Result(
                totalValue,
                totalWeight,
                selectedItems
        );
    }
}