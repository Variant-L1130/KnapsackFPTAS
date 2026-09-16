package knapsack;


import java.util.List;


public class FailAnalysis {


    public static void main(String[] args) {


        int n = 20;
        int maxWeight = 100;
        int offset = 1;
        double capacityRatio = 0.4;
        long seed = 42;


        int targetInstance = 14;


        InstanceGenerator generator =
                new InstanceGenerator(seed);


        KnapsackInstance instance = null;


        for (int i = 1; i <= targetInstance; i++) {
            instance = generator.generateCorrelatedInstance(
                    n, maxWeight, capacityRatio, offset
            );
        }


        List<Item> items = instance.getItems();
        int capacity = instance.getCapacity();


        System.out.println("Worst greedy instance: n=" + n
                + ", capacity=" + capacity);


        System.out.println("Range of value/weight ratios:");
        double minRatio = Double.MAX_VALUE;
        double maxRatio = 0;
        for (Item item : items) {
            minRatio = Math.min(minRatio, item.getRatio());
            maxRatio = Math.max(maxRatio, item.getRatio());
        }
        System.out.printf("min=%.4f max=%.4f spread=%.4f%n%n",
                minRatio, maxRatio, maxRatio - minRatio);


        ExactKnapsack.Result exact =
                ExactKnapsack.solve(items, capacity);


        ExactKnapsack.Result greedy =
                GreedyKnapsack.solve(items, capacity);


        System.out.println("EXACT: value=" + exact.getTotalValue()
                + " weight=" + exact.getTotalWeight()
                + " items=" + exact.getSelectedItems().size());


        System.out.println("GREEDY: value=" + greedy.getTotalValue()
                + " weight=" + greedy.getTotalWeight()
                + " items=" + greedy.getSelectedItems().size());


        int slack = capacity - greedy.getTotalWeight();
        System.out.println("Capacity left unused by greedy: "
                + slack);


        System.out.println("\nGreedy selected items (ratio | w | v):");
        for (Item item : greedy.getSelectedItems()) {
            System.out.printf("id=%d ratio=%.4f weight=%d value=%d%n",
                    item.getId(), item.getRatio(),
                    item.getWeight(), item.getValue());
        }


        System.out.println("\nExact selected items (ratio | w | v):");
        for (Item item : exact.getSelectedItems()) {
            System.out.printf("id=%d ratio=%.4f weight=%d value=%d%n",
                    item.getId(), item.getRatio(),
                    item.getWeight(), item.getValue());
        }


        System.out.println("\nSkipped-by-greedy items lighter than slack (could have fit):");
        int fit = 0;
        for (Item item : items) {
            if (item.getWeight() <= slack) {
                fit++;
                if (fit <= 5) {
                    System.out.printf("id=%d ratio=%.4f weight=%d value=%d%n",
                            item.getId(), item.getRatio(),
                            item.getWeight(), item.getValue());
                }
            }
        }
        System.out.println("Count of items weighing <= slack: " + fit);
    }
}