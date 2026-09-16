package knapsack;


import java.util.ArrayList;
import java.util.List;


public class FPTAS {


    public static ExactKnapsack.Result solve(
            List<Item> items,
            int capacity,
            double epsilon) {


        int n = items.size();


        // ------------------------------------
        // Step 1: Find maximum item value
        // ------------------------------------


        int maxValue = 0;


        for (Item item : items) {
            maxValue = Math.max(maxValue, item.getValue());
        }


        if (n == 0 || maxValue == 0) {
            return new ExactKnapsack.Result(
                    0,
                    0,
                    new ArrayList<>()
            );
        }


        // ------------------------------------
        // Step 2: Calculate scaling factor K
        // ------------------------------------


        double K = (epsilon * maxValue) / n;


        // ------------------------------------
        // Step 3: Calculate scaled values
        // ------------------------------------


        int[] scaledValues = new int[n];


        int totalScaledValue = 0;


        for (int i = 0; i < n; i++) {


            scaledValues[i] =
                    (int) Math.floor(
                            items.get(i).getValue() / K
                    );


            totalScaledValue += scaledValues[i];
        }


        // ------------------------------------
        // Step 4: Value-based DP
        // ------------------------------------
        //
        // dp[i][v] = minimum weight required
        // to achieve scaled value v
        // using the first i items.
        //
        // ------------------------------------


        int INF = Integer.MAX_VALUE / 2;


        int[][] dp = new int[n + 1][totalScaledValue + 1];


        for (int i = 0; i <= n; i++) {
            for (int v = 0; v <= totalScaledValue; v++) {
                dp[i][v] = INF;
            }
        }


        // Achieving value 0 requires weight 0.
        for (int i = 0; i <= n; i++) {
            dp[i][0] = 0;
        }


        // ------------------------------------
        // Fill DP table
        // ------------------------------------


        for (int i = 1; i <= n; i++) {


            int scaledValue = scaledValues[i - 1];
            int weight = items.get(i - 1).getWeight();


            for (int v = 0; v <= totalScaledValue; v++) {


                // Don't take item i
                dp[i][v] = dp[i - 1][v];


                // Take item i
                if (v >= scaledValue &&
                        dp[i - 1][v - scaledValue] != INF) {


                    int newWeight =
                            dp[i - 1][v - scaledValue] + weight;


                    dp[i][v] =
                            Math.min(
                                    dp[i][v],
                                    newWeight
                            );
                }
            }
        }


        // ------------------------------------
        // Step 5: Find best feasible scaled value
        // ------------------------------------


        int bestScaledValue = 0;


        for (int v = 0; v <= totalScaledValue; v++) {


            if (dp[n][v] <= capacity) {
                bestScaledValue = v;
            }
        }


        // ------------------------------------
        // Step 6: Reconstruct solution
        // ------------------------------------


        List<Item> selectedItems = new ArrayList<>();


        int currentValue = bestScaledValue;


        for (int i = n; i >= 1; i--) {


            // If value differs from previous row,
            // item i was selected.
            if (dp[i][currentValue] != dp[i - 1][currentValue]) {


                Item item = items.get(i - 1);


                selectedItems.add(item);


                currentValue -= scaledValues[i - 1];
            }
        }


        // ------------------------------------
        // Step 7: Calculate ORIGINAL value
        // ------------------------------------


        int originalValue = 0;
        int originalWeight = 0;


        for (Item item : selectedItems) {


            originalValue += item.getValue();
            originalWeight += item.getWeight();
        }


        if (originalWeight > capacity) {
            throw new IllegalStateException(
                    "FPTAS produced infeasible solution: weight="
                    + originalWeight
                    + ", capacity="
                    + capacity
            );
        }


        return new ExactKnapsack.Result(
                originalValue,
                originalWeight,
                selectedItems
        );
    }
}