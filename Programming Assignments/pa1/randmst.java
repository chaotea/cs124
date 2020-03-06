package src;

import java.util.Random;

public class randmst {
	public static void main(String[] args) {
		// extract arguments from the command line
		if (args.length < 4) {
			System.out.println("Missing arguments!");
		} else {
			int numOfPoints = Integer.parseInt(args[1]);
			int numOfTrials = Integer.parseInt(args[2]);
			int dim = Integer.parseInt(args[3]);
			double weight = 0.0;
			for (int i = 0; i < numOfTrials; i++) {
				Graph g = new Graph(numOfPoints, dim);
				Prim p = new Prim(g);
				weight += p.weight();
			}
			double average = weight / (double) numOfTrials;
			System.out.println(average + " " + numOfPoints + " " + numOfTrials + " " + dim);
		}
	}
}

class Edge {
	private int v;
	private int w;
	private double weight;

	public Edge(int v, int w, double weight) {
		this.v = v;
		this.w = w;
		this.weight = weight;
	}

	public int v() {
		return v;
	}

	public int w() {
		return w;
	}

	public double weight() {
		return weight;
	}

	public boolean compare(Edge e) {
		if (this.weight >= e.weight()) {
			return true;
		} else {
			return false;
		}
	}
}

class Graph {
	int v;
	double[][] points; // data structure for storing points
	int dim;
	Random rand;

	public Graph(int v, int n) {
		this.v = v;
		this.dim = n;
		points = new double[v][n];
		rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < v; i++) {
			for (int j = 0; j < n; j++) {
				points[i][j] = rand.nextDouble();
			}
		}
	}

	public Graph(int v) {
		this.v = v;
	}

	public int numVertices() {
		return this.v;
	}
}

class IndexMinPQ {
	private int maxN;		// Capacity of Priority Queue
	private int n;			// Current elements in Priority Queue
	private int[] pq;		// forward mapping(heap)
	private int[] qp;		// inverse mapping of pq - qp[pq[i]] = pq[qp[i]] = i
	private double[] keys;	// keys[i] = weight of edges

	public IndexMinPQ(int maxN) {
		// initialize variables
		this.maxN = maxN;
		n = 0;
		keys = new double[maxN + 1];
		pq = new int[maxN + 1];
		qp = new int[maxN + 1];
		for (int i = 0; i <= maxN; i++)
			qp[i] = -1;
	}

	public boolean isEmpty() {
		return n == 0;
	}

	public boolean contains(int i) {
		// if the key is not contained in the heap, then the inverse mapping is -1.
		return qp[i] != -1;
	}

	public int size() {
		return n;
	}

	public void insert(int i, double key) {
		// add the element to the end, and compare to its parent, if greater, then switch.
		n++;
		qp[i] = n;
		pq[n] = i;
		keys[i] = key;
		up(n);
	}

	public int minIndex() {
		// return the element at the begining of the heap
		return pq[1];
	}

	public double minKey() {
		// the weight of the smallest element
		return keys[pq[1]];
	}

	public int delMin() {
		// exchange the smallest element with the last element in the heap, and heapify the heap again.
		int min = pq[1];
		exch(1, n--);
		down(1);
		assert min == pq[n + 1];
		qp[min] = -1; // delete
		keys[min] = -1;
		pq[n + 1] = -1;
		return min;
	}

	public double keyOf(int i) {
		return keys[i];
	}

	public void changeKey(int i, double key) {
		// change the key, reconstruct the heap
		keys[i] = key;
		up(qp[i]);
		down(qp[i]);
	}

	private boolean greater(int i, int j) {
		return keys[pq[i]] > (keys[pq[j]]);
	}

	private void exch(int i, int j) {
		int swap = pq[i];
		pq[i] = pq[j];
		pq[j] = swap;
		qp[pq[i]] = i;
		qp[pq[j]] = j;
	}

	private void up(int k) {
		// compare with its parents
		while (k > 1 && greater(k / 2, k)) {
			exch(k, k / 2);
			k = k / 2;
		}
	}

	private void down(int k) {
		// check the node with its children nodes
		while (2 * k <= n) {
			int j = 2 * k;
			if (j < n && greater(j, j + 1))
				j++;
			if (!greater(k, j))
				break;
			exch(k, j);
			k = j;
		}
	}
}

class Prim {
	private Edge[] edgeTo;		// shortest edge from vertex
	private double[] distTo;	// distTo[i] = edgeTo[i].weight()
	private boolean[] marked;	// true if v on tree
	private IndexMinPQ pq;		// eligible crossing edges

	public Prim(Graph g) {
		// initialize variables
		edgeTo = new Edge[g.numVertices()];
		distTo = new double[g.numVertices()];
		marked = new boolean[g.numVertices()];
		pq = new IndexMinPQ(g.numVertices());
		// set up the distance to each node to be infinity
		for (int v = 0; v < g.numVertices(); v++) {
			distTo[v] = Double.POSITIVE_INFINITY;
		}

		distTo[0] = 0.0;
		edgeTo[0] = new Edge(0, 0, 0.0);
		pq.insert(0, 0.0); // initialize pq with 0, weight 0
		while (!pq.isEmpty()) {
			visit(g, pq.delMin());
		}

	}

	public double euclideanDistance(double[] a, double[] b) {
		// function for calculating euclidean distance
		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + Math.pow((a[i] - b[i]), 2.0);
		}
		return Math.sqrt(sum);
	}

	private void visit(Graph g, int v) {
		// add v to tree and update datastructures
		marked[v] = true; // mark the node as visited
		for (int i = 0; i < g.numVertices(); i++) {
			int w = i;
			if (marked[w]) { // if already visited, then ignore
				continue;
			}
			double distance;
			// calculating the distance from v to w
			if (g.dim == 1) {
				distance = g.rand.nextDouble();
			} else {
				distance = euclideanDistance(g.points[w], g.points[v]);
			}
			if (distance < distTo[w]) {
				// if distance is smaller than recorded distance, then e is new best connection from tree to w
				edgeTo[w] = new Edge(v, w, distance);
				distTo[w] = distance;
				if (pq.contains(w)) {
					pq.changeKey(w, distTo[w]);
				} else {
					pq.insert(w, distTo[w]);
				}
			}
		}
	}

	public double weight() {
		// calculating the total weight of the MST
		double weight = 0.0;
		for (int i = 1; i < edgeTo.length; i++) {
			weight = weight + edgeTo[i].weight();
		}
		return weight;
	}
}