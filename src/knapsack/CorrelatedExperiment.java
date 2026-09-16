package knapsack;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class CorrelatedExperiment {


    public static void main(String[] args) {


        // ------------------------------------
        // Configuration
        // ------------------------------------


        int[] sizes = {
                20, 50, 100, 250
        };


        int instancesPerSize = 30;


        double[] epsilons = {
                0.50, 0.20, 0.10, 0.05, 0.01
        };


        int maxWeight = 100;
        int offset = 1;                 // value = weight + offset
        double capacityRatio = 0.4;


        long seed = 42;


        String outFile = "correlated_results.csv";


        try (PrintWriter out =
                     new PrintWriter(
                             new FileWriter(outFile))) {


            out.println(
                    "n,instance,algorithm,epsilon,"
                    + "optimal_value,value,weight,"
                    + "ratio,error_percent,runtime_ms"
            );


            InstanceGenerator generator =
                    new InstanceGenerator(seed);


            System.out.println(
                    "Correlated instances: "
                    + "value = weight + " + offset
                    + ", capacity ratio = "
                    + capacityRatio
            );


            for (int n : sizes) {


                System.out.println(
                        "n = " + n + " ..."
                );


                for (int instanceNumber = 1;
                     instanceNumber <= instancesPerSize;
                     instanceNumber++) {


                    KnapsackInstance instance =
                            generator.generateCorrelatedInstance(
                                    n,
                                    maxWeight,
                                    capacityRatio,
                                    offset
                            );


                    List<Item> items =
                            instance.getItems();


                    int capacity =
                            instance.getCapacity();


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


                    out.flush();
                }
            }


            System.out.println(
                    "Done. Saved to " + outFile
            );


        } catch (IOException e) {


            e.printStackTrace();
        }
    }
}