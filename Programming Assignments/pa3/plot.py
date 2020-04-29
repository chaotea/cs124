import matplotlib.pyplot as plt
import numpy as np
import csv


def plot():
    with open("data1.csv", newline="") as f:
        reader = csv.reader(f)
        data = list(reader)
    for lst in data[1:]:
        lst[2:] = [int(num) for num in lst[2:]]

    print(np.median(data[1][2:]))
    print(np.median(data[2][2:]))
    print(np.median(data[3][2:]))
    print(np.median(data[4][2:]))
    print(np.median(data[5][2:]))
    print(np.median(data[6][2:]))
    print(np.median(data[7][2:]))

    # # karmarkar-karp
    binsize = 200000
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize), [i for i in range(0, numbins * binsize + 1, binsize)])
    plt.hist(data[1][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Karmarkar-Karp")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[1][0]))

    # # repeated-random
    binsize = 200000000
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize),
               ["0"] + ["{:.1e}".format(i) for i in range(binsize, numbins * binsize + 1, binsize)])
    plt.hist(data[2][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Repeated Random")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[2][0]))

    # # hill-climbing
    binsize = 150000000
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize),
               ["0"] + ["{:.2e}".format(i) for i in range(binsize, numbins * binsize + 1, binsize)])
    plt.hist(data[3][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Hill Climbing")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[3][0]))

    # # simulated-annealing
    binsize = 150000000
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize),
               ["0"] + ["{:.2e}".format(i) for i in range(binsize, numbins * binsize + 1, binsize)])
    plt.hist(data[4][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Simulated Annealing")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[4][0]))

    # # pre-partition random
    binsize = 100
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize), [i for i in range(0, numbins * binsize + 1, binsize)])
    plt.hist(data[5][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Prepartitioned Repeated Random")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[5][0]))

    # # pre-partition hill
    binsize = 100
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize), [i for i in range(0, numbins * binsize + 1, binsize)])
    plt.hist(data[6][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Prepartitioned Hill Climbing")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[6][0]))

    # # pre-partition annealing
    binsize = 100
    numbins = 8
    plt.figure(figsize=(16*0.7, 9*0.7))
    bins = [binsize * i for i in range(numbins + 1)]
    plt.xticks(np.arange(0, numbins * binsize + 1, binsize), [i for i in range(0, numbins * binsize + 1, binsize)])
    plt.hist(data[7][2:], bins=bins, color="w", edgecolor="black")
    plt.title("Prepartitioned Simulated Annealing")
    plt.xlabel("Residue")
    plt.ylabel("Frequency")
    plt.savefig("plots-new/{}.png".format(data[7][0]))


plot()
