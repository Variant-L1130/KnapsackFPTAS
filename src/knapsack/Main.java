package knapsack;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class Main {


    public static void main(String[] args) {


        // ------------------------------------
        // Experiment configuration
        // ------------------------------------


        int[] sizes = {
                20, 50, 100, 250
        };


        int instancesPerSize = 30;


        double[] epsilons = {
                0.50, 0.20, 0.10, 0.05, 0.01
        };


        int maxWeight = 100;
        int maxValue = 100;
        double capacityRatio = 0.4;


        long seed = 42;


        // ------------------------------------
        // Create output file
        // ------------------------------------


        try (PrintWriter out =
                     new PrintWriter(
                             new FileWriter("results.csv"))) {


            // CSV header
            out.println(
                    "n,instance,algorithm,epsilon,"
                    + "optimal_value,value,weight,"
                    + "ratio,error_percent,runtime_ms"
            );


            InstanceGenerator generator =
                    new InstanceGenerator(seed);


            // ------------------------------------
            // Run experiments
            // ------------------------------------


            for (int n : sizes) {


                System.out.println(
                        "Running experiments for n = " + n
                );


                for (int instanceNumber = 1;
                     instanceNumber <= instancesPerSize;
                     instanceNumber++) {


                    KnapsackInstance instance =
                            generator.generateRandomInstance(
                                    n,
                                    maxWeight,
                                    maxValue,
                                    capacityRatio
                            );


                    List<Item> items =
                            instance.getItems();


                    int capacity =
                            instance.getCapacity();


                    // --------------------------------
                    // Exact DP
                    // --------------------------------


                    long t0 = System.nanoTime();


                    ExactKnapsack.Result exact =
                            ExactKnapsack.solve(
                                    items,
                                    capacity
                            );


                    long t1 = System.nanoTime();


                    double exactMs =
                            (t1 - t0) / 1_000_000.0;


                    int optimalValue =
                            exact.getTotalValue();


                    // Save exact result
                    out.printf(
                            "%d,%d,EXACT,NA,%d,%d,%d,%.6f,%.6f,%.6f%n",
                            n,
                            instanceNumber,
                            optimalValue,
                            optimalValue,
                            exact.getTotalWeight(),
                            1.0,
                            0.0,
                            exactMs
                    );


                    // --------------------------------
                    // Greedy
                    // --------------------------------


                    t0 = System.nanoTime();


                    ExactKnapsack.Result greedy =
                            GreedyKnapsack.solve(
                                    items,
                                    capacity
                            );


                    t1 = System.nanoTime();


                    double greedyMs =
                            (t1 - t0) / 1_000_000.0;


                    double greedyRatio =
                            (double) greedy.getTotalValue()
                            / optimalValue;


                    double greedyErrorPct =
                            100.0
                            * (optimalValue
                            - greedy.getTotalValue())
                            / optimalValue;


                    out.printf(
                            "%d,%d,GREEDY,NA,%d,%d,%d,%.6f,%.6f,%.6f%n",
                            n,
                            instanceNumber,
                            optimalValue,
                            greedy.getTotalValue(),
                            greedy.getTotalWeight(),
                            greedyRatio,
                            greedyErrorPct,
                            greedyMs
                    );


                    // --------------------------------
                    // FPTAS
                    // --------------------------------


                    for (double epsilon : epsilons) {


                        t0 = System.nanoTime();


                        ExactKnapsack.Result result =
                                FPTAS.solve(
                                        items,
                                        capacity,
                                        epsilon
                                );


                        t1 = System.nanoTime();


                        double fptasMs =
                                (t1 - t0)
                                / 1_000_000.0;


                        double ratio =
                                (double)
                                result.getTotalValue()
                                / optimalValue;


                        double errorPct =
                                100.0
                                * (optimalValue
                                - result.getTotalValue())
                                / optimalValue;


                        out.printf(
                                "%d,%d,FPTAS,%.2f,%d,%d,%d,%.6f,%.6f,%.6f%n",
                                n,
                                instanceNumber,
                                epsilon,
                                optimalValue,
                                result.getTotalValue(),
                                result.getTotalWeight(),
                                ratio,
                                errorPct,
                                fptasMs
                        );
                    }


                    // Make sure results are written immediately
                    out.flush();
                }
            }


            System.out.println();
            System.out.println(
                    "Experiment completed."
            );


            System.out.println(
                    "Results saved to results.csv"
            );


        } catch (IOException e) {


            System.err.println(
                    "Could not write results.csv"
            );


            e.printStackTrace();
        }
    }
}