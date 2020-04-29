import csv
import math
import random
import sys
import time

# -------------------
# Heap implementation
# -------------------
def max_heapify(array, i):
    try:
        left = array[2 * i + 1]
    except:
        left = None
    try:
        right = array[2 * i + 2]
    except:
        right = None
    if left and left > array[i]:
        max_index = 2 * i + 1
    else:
        max_index = i
    if right and right > array[max_index]:
        max_index = 2 * i + 2
    if max_index != i:
        array[i], array[max_index] = array[max_index], array[i]
        max_heapify(array, max_index)


def build_heap(array):
    for i in range(len(array) // 2 - 1, -1, -1):
        max_heapify(array, i)


def extract_max(array):
    root = array[0]
    array[0] = array[-1]
    del array[-1]
    max_heapify(array, 0)
    return root


def insert(array, value):
    index = len(array)
    array.append(value)
    while index != 0 and array[(index - 1) // 2] < array[index]:
        array[(index - 1) // 2], array[index] = array[index], array[(index - 1) // 2]
        index = (index - 1) // 2

# ----------
# Algorithms
# ----------
def karmarkar_karp(nums):
    build_heap(nums)
    while len(nums) > 1:
        max1 = extract_max(nums)
        max2 = extract_max(nums)
        insert(nums, max1 - max2)
    return nums[0]


def repeated_random(nums, max_iter=25000, seed=time.time()):
    random.seed(seed)
    soln = [random.randrange(-1, 2, 2) for i in range(len(nums))]
    r = residue(nums, soln)
    for i in range(max_iter):
        soln[:] = [random.randrange(-1, 2, 2) for i in range(len(nums))]
        if residue(nums, soln) < r:
            r = residue(nums, soln)
    return r


def hill_climbing(nums, max_iter=25000, seed=time.time()):
    random.seed(seed)
    soln = [random.randrange(-1, 2, 2) for i in range(len(nums))]
    r = residue(nums, soln)
    for i in range(max_iter):
        indices = random.sample(range(len(nums)), 2)
        soln[indices[0]] *= -1
        if indices[0] < indices[1]:  # corresponds to probability 1/2 that we swap second index as well
            soln[indices[1]] *= -1
        if residue(nums, soln) < r:
            r = residue(nums, soln)
    return r


def annealing(nums, max_iter=25000, seed=time.time()):
    random.seed(seed)
    soln = [random.randrange(-1, 2, 2) for i in range(len(nums))]
    r = r_best = residue(nums, soln)
    for i in range(max_iter):
        indices = random.sample(range(len(nums)), 2)
        soln[indices[0]] *= -1
        if indices[0] < indices[1]:  # corresponds to probability 1/2 that we swap second index as well
            soln[indices[1]] *= -1
        if residue(nums, soln) < r:
            r = residue(nums, soln)
        elif random.random() < math.exp(-(residue(nums, soln) - r) / ((10 ** 10) * (0.8 ** ((i + 1) // 300)))):
            r = residue(nums, soln)
        if r < r_best:
            r_best = r
    return r_best


def p_repeated_random(nums, max_iter=25000, seed=time.time()):
    random.seed(seed)
    partition = [random.randrange(len(nums)) for i in range(len(nums))]
    new_nums = [0] * len(nums)
    for j, index in enumerate(partition):
        new_nums[index] += nums[j]
    r = karmarkar_karp(new_nums)
    for k in range(max_iter):
        partition[:] = [random.randrange(len(nums)) for i in range(len(nums))]
        new_nums[:] = [0] * len(nums)
        for j, index in enumerate(partition):
            new_nums[index] += nums[j]
        r_new = karmarkar_karp(new_nums)
        if r_new < r:
            r = r_new
    return r


def p_hill_climbing(nums, max_iter=25000, seed=time.time()):
    random.seed(seed)
    partition = [random.randrange(len(nums)) for i in range(len(nums))]
    new_nums = [0] * len(nums)
    for j, index in enumerate(partition):
        new_nums[index] += nums[j]
    r = karmarkar_karp(new_nums)
    for k in range(max_iter):
        changed_index = random.randrange(len(nums))
        new_value = random.randrange(len(nums))
        while partition[changed_index] == new_value:
            new_value = random.randrange(len(nums))
        partition[changed_index] = new_value
        new_nums[:] = [0] * len(nums)
        for j, index in enumerate(partition):
            new_nums[index] += nums[j]
        r_new = karmarkar_karp(new_nums)
        if r_new < r:
            r = r_new
    return r


def p_annealing(nums, max_iter=25000, seed=time.time()):
    random.seed(seed)
    partition = [random.randrange(len(nums)) for i in range(len(nums))]
    new_nums = [0] * len(nums)
    for j, index in enumerate(partition):
        new_nums[index] += nums[j]
    r = r_best = karmarkar_karp(new_nums)
    for k in range(max_iter):
        changed_index = random.randrange(len(nums))
        new_value = random.randrange(len(nums))
        while partition[changed_index] == new_value:
            new_value = random.randrange(len(nums))
        partition[changed_index] = new_value
        new_nums[:] = [0] * len(nums)
        for j, index in enumerate(partition):
            new_nums[index] += nums[j]
        r_new = karmarkar_karp(new_nums)
        if r_new < r:
            r = r_new
        elif random.random() < math.exp(-(r_new - r) / ((10 ** 10) * (0.8 ** ((k + 1) // 300)))):
            r = r_new
        if r < r_best:
            r_best = r
    return r_best


def residue(array, solution):
    return abs(sum(array[i] * solution[i] for i in range(len(array))))


def main():
    if len(sys.argv) != 4:
        print("Usage: partition.py [flag] [algorithm] [inputfile]")
        sys.exit(1)

    if sys.argv[1] == "0":
        f = open(sys.argv[3], "r")
        nums = [int(line) for line in f]

        if sys.argv[2] == "0":
            print(karmarkar_karp(nums))
        elif sys.argv[2] == "1":
            print(repeated_random(nums))
        elif sys.argv[2] == "2":
            print(hill_climbing(nums))
        elif sys.argv[2] == "3":
            print(annealing(nums))
        elif sys.argv[2] == "11":
            print(p_repeated_random(nums))
        elif sys.argv[2] == "12":
            print(p_hill_climbing(nums))
        elif sys.argv[2] == "13":
            print(p_annealing(nums))
    else:
        algs = {
            "karmarkar_karp": dict(method=karmarkar_karp),
            "repeated_random": dict(method=repeated_random),
            "hill_climbing": dict(method=hill_climbing),
            "simulated_annealing": dict(method=annealing),
            "p_repeated_random": dict(method=p_repeated_random),
            "p_hill_climbing": dict(method=p_hill_climbing),
            "p_simulated_annealing": dict(method=p_annealing)
        }
        max_iter = 25000
        seed = time.time()
        trials = 3
        trial_nums = [[random.randrange(1, 10 ** 12 + 1)
                       for i in range(100)] for j in range(trials)]

        for alg_name, alg in algs.items():
            alg_method = alg["method"]
            alg["residues"] = []
            start_time = time.time()

            for i in range(trials):
                nums = trial_nums[i]
                r = alg_method(nums[:]) if alg_name == "karmarkar_karp" else alg_method(
                    nums, max_iter, seed)
                alg["residues"].append(r)

            end_time = time.time()
            alg["time"] = end_time - start_time
            print(f"Done with {alg_name}")

        print(f"\nNumber of trials: {trials}")
        with open("data.csv", "w") as f:
            w = csv.writer(f)
            w.writerow(["algorithm", "total_time", "avg_residue"] +
                       [f"trial{i+1:02}" for i in range(trials)])
            for alg_name, alg in algs.items():
                residues = alg["residues"]
                alg_time = alg["time"]
                avg_res = sum(residues) / len(residues)
                w.writerow([alg_name, alg_time, avg_res] + residues)
                print(f"-----{alg_name}-----\navg residue: {avg_res} | total time: {alg_time}\n{residues}")


if __name__ == "__main__":
    main()
