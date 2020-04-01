package src;

import java.io.*;
import java.math.*;
import java.util.*;

public class strassen {
	// Standard matrix addition
	public static int[][] add(int[][] A, int[][] B) {
		int dim = A.length;
		int[][] C = new int[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				C[i][j] = A[i][j] + B[i][j];
			}
		}
		return C;
	}

	// Standard matrix subtraction
	public static int[][] sub(int[][] A, int[][] B) {
		int dim = A.length;
		int[][] C = new int[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				C[i][j] = A[i][j] - B[i][j];
			}
		}
		return C;
	}

	// Block matrix addition; avoids storing block matrices
	public static int[][] addBlock(int[][] A, int[][] B, int i1, int j1, int i2, int j2) {
		int dim = A.length;
		int half = dim / 2;
		int[][] C = new int[half][half];
		i1 = i1 - 1;
		i2 = i2 - 1;
		j1 = j1 - 1;
		j2 = j2 - 1;
		for (int i = 0; i < half; i++) {
			for (int j = 0; j < half; j++) {
				C[i][j] = A[i + half * i1][j + half * j1] + B[i + half * i2][j + half * j2];
			}
		}
		return C;
	}

	// Block matrix subtraction, avoids storing block matrices
	public static int[][] subBlock(int[][] A, int[][] B, int i1, int j1, int i2, int j2) {
		int dim = A.length;
		int half = dim / 2;
		int[][] C = new int[half][half];
		i1 = i1 - 1;
		i2 = i2 - 1;
		j1 = j1 - 1;
		j2 = j2 - 1;
		for (int i = 0; i < half; i++) {
			for (int j = 0; j < half; j++) {
				C[i][j] = A[i + half * i1][j + half * j1] - B[i + half * i2][j + half * j2];
			}
		}
		return C;
	}

	// Conventional matrix multiplication
	public static int[][] conMul(int[][] A, int[][] B) {
		int[][] C = new int[A.length][B[0].length];
		for (int k = 0; k < B.length; k++) {
			for (int i = 0; i < A.length; i++) {
				for (int j = 0; j < B[0].length; j++) {
					C[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return C;

	}

	// Set up the value of matrix based on block
	public static void setBlock(int[][] A, int[][] B, int i, int j, int dim) {
		int half = dim / 2;
		i = i - 1;
		j = j - 1;
		for (int hor = 0; hor < half; hor++) {
			for (int ver = 0; ver < half; ver++) {
				A[hor + half * i][ver + half * j] = B[hor][ver];
			}
		}
	}

	// Strassen's Algorithm helper function for even dimensions
	public static int[][] strassenAux(int[][] A, int[][] B, int dim, int crossover) {
		// Initialize and populate the block matrices needed for the 7 multiplications
		int half = dim / 2;
		int[][] a11 = new int[half][half];
		int[][] a22 = new int[half][half];
		int[][] b11 = new int[half][half];
		int[][] b22 = new int[half][half];
		for (int i = 0; i < half; i++) {
			for (int j = 0; j < half; j++) {
				a11[i][j] = A[i][j];
				b11[i][j] = B[i][j];
				a22[i][j] = A[i + half][j + half];
				b22[i][j] = B[i + half][j + half];
			}
		}

		// 7 multiplications
		int[][] P = straMul(addBlock(A, A, 1, 1, 2, 2), addBlock(B, B, 1, 1, 2, 2), half, crossover);
		int[][] Q = straMul(addBlock(A, A, 2, 1, 2, 2), b11, half, crossover);
		int[][] R = straMul(a11, subBlock(B, B, 1, 2, 2, 2), half, crossover);
		int[][] S = straMul(a22, subBlock(B, B, 2, 1, 1, 1), half, crossover);
		int[][] T = straMul(addBlock(A, A, 1, 1, 1, 2), b22, half, crossover);
		int[][] U = straMul(subBlock(A, A, 2, 1, 1, 1), addBlock(B, B, 1, 1, 1, 2), half, crossover);
		int[][] V = straMul(subBlock(A, A, 1, 2, 2, 2), addBlock(B, B, 2, 1, 2, 2), half, crossover);

		// Initialize and populate the blocks of C using the 7 products
		int[][] C = new int[dim][dim];
		setBlock(C, add(sub(add(P, S), T), V), 1, 1, dim);
		setBlock(C, add(R, T), 1, 2, dim);
		setBlock(C, add(Q, S), 2, 1, dim);
		setBlock(C, add(sub(add(P, R), Q), U), 2, 2, dim);
		return C;
	}

	public static int[][] straMul(int[][] A, int[][] B, int dim, int crossover) {
		// If the dimension is less than the crossover point, switch to conventional algorithm
		if (dim <= crossover) {
			return conMul(A, B);
		} else {
			if (dim % 2 == 0) {
				return strassenAux(A, B, dim, crossover);
			}
			// If the dimension is odd, pad the matrices with an extra row and column of 0's
			// so that the dimension is even for Strassen
			else {
				int[][] enlargedA = new int[dim + 1][dim + 1];
				int[][] enlargedB = new int[dim + 1][dim + 1];
				for (int i = 0; i < dim; i++) {
					for (int j = 0; j < dim; j++) {
						enlargedA[i][j] = A[i][j];
						enlargedB[i][j] = B[i][j];
					}
				}
				int[][] enlargedC = strassenAux(enlargedA, enlargedB, dim + 1, crossover);

				// Remove the padding and return the matrix product with the original dimension
				int[][] C = new int[dim][dim];
				for (int i = 0; i < dim; i++) {
					for (int j = 0; j < dim; j++) {
						C[i][j] = enlargedC[i][j];
					}
				}
				return C;
			}
		}
	}

	public static String toString(int[][] A) {
		String s = "";
		for (int[] row : A) {
			for (int element : row) {
				s = s + element + " ";
			}
			s = s + "\n";
		}
		return s;
	}

	public static void testRun(int base, int exp) {
		try {
			FileWriter csvWriter = new FileWriter("data" + base + ".csv");
			csvWriter.write("dim,cross,time\n");

			// Get runtime and optimal crossover point for powers of base up to exp
			for (int d = 1; d <= exp; d++) {
				int dim = (int) Math.pow(base, d);
				System.out.println("-----TESTING DIMENSION: " + dim + "-----");

				// Generate matrices with random integer entries between -9 and 9
				Random rand = new Random(System.currentTimeMillis());
				int[][] A = new int[dim][dim];
				int[][] B = new int[dim][dim];
				for (int i = 0; i < dim; i++) {
					for (int j = 0; j < dim; j++) {
						A[i][j] = -9 + rand.nextInt(19);
						B[i][j] = -9 + rand.nextInt(19);
					}
				}

				// Determine the crossover point with the fastest runtime
				int crossover = 1;
				while (crossover <= dim / 2) {
					long totalTime = 0;
					for (int k = 0; k < 5; k++) {
						long startTime = System.currentTimeMillis();
						int[][] C = straMul(A, B, dim, crossover);
						long endTime = System.currentTimeMillis();
						totalTime += endTime - startTime;
					}
					long averageTime = totalTime / 5;
					csvWriter.write(dim + "," + crossover + "," + averageTime + "\n");
					System.out.println("dim: " + dim + " crossover: " + crossover + " avg time: " + averageTime);
					crossover++;
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public static void countTriangles(int dim, double p, int crossover) {
		Random rand = new Random(System.currentTimeMillis());

		int[][] A = new int[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				if (rand.nextDouble() < p) {
					A[i][j] = 1;
				}
			}
		}

		int[][] A2 = straMul(A, A, dim, crossover);
		int[][] A3 = straMul(A2, A, dim, crossover);

		int diagonalSum = 0;
		for (int i = 0; i < dim; i++) {
			diagonalSum += A3[i][i];
		}

		System.out.println("Num triangles for " + dim + "x" + dim + " matrix with p = " + p + ": " + diagonalSum / 6);
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 3) {
			System.out.println("Missing arguments!");
			System.exit(0);
		}

		int dim = Integer.parseInt(args[1]);
		String inputFile = args[2];
		int[][] A = new int[dim][dim];
		int[][] B = new int[dim][dim];

		try {
			Scanner sc = new Scanner(new File(inputFile));

			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					A[i][j] = sc.nextInt();
				}
			}
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					B[i][j] = sc.nextInt();
				}
			}

			int[][] C = straMul(A, B, dim, 32);

			for (int i = 0; i < dim; i++) {
				System.out.println(C[i][i]);
			}
		} catch (FileNotFoundException fn) {
			fn.printStackTrace();
		}

		// Experimental analysis of cross-over point:
		// testRun(2, 10);
		// testRun(3, 6);
		// testRun(5, 4);
		// testRun(10, 3);

		// Count triangles:
		// Optimal crossover for 1024x1024 matrix is 194 (from experimental analysis)
		// countTriangles(1024, 0.01, 194);
		// countTriangles(1024, 0.02, 194);
		// countTriangles(1024, 0.03, 194);
		// countTriangles(1024, 0.04, 194);
		// countTriangles(1024, 0.05, 194);
	}
}
