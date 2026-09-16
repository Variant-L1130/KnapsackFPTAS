# KnapsackFPTAS

Comparing exact, greedy, and FPTAS (Fully Polynomial-Time Approximation Scheme)
solutions for the 0/1 knapsack problem.

## Structure

```
KnapsackFPTAS/
│
├── src/knapsack/
│   ├── Item.java
│   ├── ExactKnapsack.java
│   ├── GreedyKnapsack.java
│   ├── FPTAS.java
│   ├── InstanceGenerator.java
│   └── Experiment.java
│
├── results/
├── plots/
└── README.md
```

- `results/` – CSV output from experiments
- `plots/` – generated comparison plots