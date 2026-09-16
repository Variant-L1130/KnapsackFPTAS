import pandas as pd
import matplotlib.pyplot as plt
import os


# ----------------------------------------
# Load data
# ----------------------------------------


df = pd.read_csv("results.csv")


print("\nDataset shape:")
print(df.shape)


print("\nAlgorithms:")
print(df["algorithm"].value_counts())


print("\nProblem sizes:")
print(sorted(df["n"].unique()))


# ----------------------------------------
# Greedy summary
# ----------------------------------------


greedy = df[df["algorithm"] == "GREEDY"]


print("\n===== GREEDY SUMMARY =====")


greedy_summary = (
    greedy
    .groupby("n")
    .agg(
        mean_ratio=("ratio", "mean"),
        min_ratio=("ratio", "min"),
        mean_error=("error_percent", "mean"),
        mean_runtime=("runtime_ms", "mean")
    )
    .reset_index()
)


print(greedy_summary.to_string(index=False))


# ----------------------------------------
# FPTAS summary
# ----------------------------------------


fptas = df[df["algorithm"] == "FPTAS"].copy()


print("\n===== FPTAS SUMMARY =====")


fptas_summary = (
    fptas
    .groupby(["n", "epsilon"])
    .agg(
        mean_ratio=("ratio", "mean"),
        min_ratio=("ratio", "min"),
        mean_error=("error_percent", "mean"),
        mean_runtime=("runtime_ms", "mean")
    )
    .reset_index()
)


print(fptas_summary.to_string(index=False))


# ----------------------------------------
# Create figures directory
# ----------------------------------------


os.makedirs("analysis/figures", exist_ok=True)


# ----------------------------------------
# Figure 1:
# FPTAS approximation ratio vs epsilon
# ----------------------------------------


plt.figure(figsize=(8, 5))


for n in sorted(fptas["n"].unique()):


    subset = (
        fptas[fptas["n"] == n]
        .groupby("epsilon")["ratio"]
        .mean()
        .reset_index()
    )


    plt.plot(
        subset["epsilon"],
        subset["ratio"],
        marker="o",
        label=f"n={n}"
    )


plt.xlabel("Epsilon")
plt.ylabel("Mean approximation ratio")
plt.title("FPTAS Accuracy vs Epsilon")
plt.legend()
plt.grid(True, alpha=0.3)


plt.savefig(
    "analysis/figures/fptas_accuracy_vs_epsilon.png",
    dpi=200,
    bbox_inches="tight"
)


plt.close()


# ----------------------------------------
# Figure 2:
# FPTAS runtime vs epsilon
# ----------------------------------------


plt.figure(figsize=(8, 5))


for n in sorted(fptas["n"].unique()):


    subset = (
        fptas[fptas["n"] == n]
        .groupby("epsilon")["runtime_ms"]
        .mean()
        .reset_index()
    )


    plt.plot(
        subset["epsilon"],
        subset["runtime_ms"],
        marker="o",
        label=f"n={n}"
    )


plt.xlabel("Epsilon")
plt.ylabel("Mean runtime (ms)")
plt.title("FPTAS Runtime vs Epsilon")
plt.legend()
plt.grid(True, alpha=0.3)


plt.savefig(
    "analysis/figures/fptas_runtime_vs_epsilon.png",
    dpi=200,
    bbox_inches="tight"
)


plt.close()


# ----------------------------------------
# Figure 3:
# Runtime vs n
# ----------------------------------------


plt.figure(figsize=(8, 5))


for algorithm in ["EXACT", "GREEDY"]:


    subset = (
        df[df["algorithm"] == algorithm]
        .groupby("n")["runtime_ms"]
        .mean()
        .reset_index()
    )


    plt.plot(
        subset["n"],
        subset["runtime_ms"],
        marker="o",
        label=algorithm
    )


# FPTAS with epsilon = 0.10
subset = (
    fptas[fptas["epsilon"] == 0.10]
    .groupby("n")["runtime_ms"]
    .mean()
    .reset_index()
)


plt.plot(
    subset["n"],
    subset["runtime_ms"],
    marker="o",
    label="FPTAS ε=0.10"
)


plt.xlabel("Number of items (n)")
plt.ylabel("Mean runtime (ms)")
plt.title("Runtime Scaling")
plt.legend()
plt.grid(True, alpha=0.3)


plt.savefig(
    "analysis/figures/runtime_vs_n.png",
    dpi=200,
    bbox_inches="tight"
)


plt.close()


# ----------------------------------------
# Correlated-instance analysis (extension)
# ----------------------------------------
# Reads correlated_results.csv if present.


import os


if os.path.exists("correlated_results.csv"):


    cdf = pd.read_csv("correlated_results.csv")


    cg = cdf[cdf["algorithm"] == "GREEDY"]
    cf = cdf[cdf["algorithm"] == "FPTAS"]


    print("\n===== GREEDY on CORRELATED =====")


    print(
        cg.groupby("n")["ratio"]
        .agg(["mean", "min", "count"])
        .round(4)
        .to_string()
    )


    print("\n===== FPTAS on CORRELATED =====")


    print(
        cf.groupby(["n", "epsilon"])["ratio"]
        .mean()
        .round(4)
        .unstack()
        .to_string()
    )


    # Figure 4: Greedy vs FPTAS on correlated instances
    plt.figure(figsize=(8, 5))


    for name, sub in [
            ("GREEDY", cg),
            ("FPTAS ε=0.01", cf[cf["epsilon"] == 0.01])]:


        plt.plot(
            sub.groupby("n")["ratio"].mean().index,
            sub.groupby("n")["ratio"].mean().values,
            marker="o",
            label=name
        )


        plt.plot(
            sub.groupby("n")["ratio"].min().index,
            sub.groupby("n")["ratio"].min().values,
            marker="x",
            linestyle="--",
            label=name + " (min)"
        )


    plt.axhline(
        1.0, color="black", linewidth=0.8
    )


    plt.xlabel("Number of items (n)")
    plt.ylabel("Approximation ratio (value / OPT)")
    plt.title(
        "Correlated instances (value = weight + 1):\n"
        "Greedy vs FPTAS"
    )


    plt.legend()
    plt.grid(True, alpha=0.3)


    plt.savefig(
        "analysis/figures/greedy_vs_fptas.png",
        dpi=200,
        bbox_inches="tight"
    )


    plt.close()


print("\nFigures saved to analysis/figures/")