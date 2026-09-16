package knapsack;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class InstanceGenerator {


    private final Random random;


    public InstanceGenerator(long seed) {
        this.random = new Random(seed);
    }


    public KnapsackInstance generateRandomInstance(
            int n,
            int maxWeight,
            int maxValue,
            double capacityRatio) {


        List<Item> items = new ArrayList<>();


        int totalWeight = 0;


        for (int i = 1; i <= n; i++) {


            int weight =
                    1 + random.nextInt(maxWeight);


            int value =
                    1 + random.nextInt(maxValue);


            items.add(
                    new Item(i, weight, value)
            );


            totalWeight += weight;
        }


        int capacity =
                (int) (capacityRatio * totalWeight);


        return new KnapsackInstance(
                items,
                capacity
        );
    }


    // Strongly correlated: value = weight + offset.
    // All items have nearly identical value/weight ratios
    // (≈ 1 + offset/weight), which is the regime where
    // density-greedy tie-breaking can go wrong.
    public KnapsackInstance generateCorrelatedInstance(
            int n,
            int maxWeight,
            double capacityRatio,
            int offset) {


        List<Item> items = new ArrayList<>();


        int totalWeight = 0;


        for (int i = 1; i <= n; i++) {


            int weight =
                    1 + random.nextInt(maxWeight);


            int value = weight + offset;


            items.add(
                    new Item(i, weight, value)
            );


            totalWeight += weight;
        }


        int capacity =
                (int) (capacityRatio * totalWeight);


        return new KnapsackInstance(
                items,
                capacity
        );
    }
}