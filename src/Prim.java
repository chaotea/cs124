package src;

import java.util.ArrayList;

public class Prim{
	private Edge[] edgeTo; // shortest edge from vertex
	private double[] distTo; // distTo[i] = edgeTo[i].weight()
	private boolean[] marked; // true if v on tree
	private IndexMinPQ pq; // eligible crossing edges
	private ArrayList<Edge> mst;

	public Prim(Graph g) {
		edgeTo = new Edge[g.numVertices()];
		distTo = new double[g.numVertices()];
		marked = new boolean[g.numVertices()];
		pq = new IndexMinPQ(g.numVertices());
		ArrayList<Edge> mst = new ArrayList<Edge>();

		for(int v = 0; v < g.numVertices(); v++) {
			distTo[v] = Double.POSITIVE_INFINITY;
		}

		distTo[0] = 0.0;
		pq.insert(0, 0.0); // initialize pq with 0, weight 0
		while(!pq.isEmpty()) {
			visit(g, pq.extractMin());
		}

		for (int v = 1; v < edgeTo.length; v++) {
			mst.add(edgeTo[v]);
		}
	}

	private void visit(Graph g, int v) { // Add v to tree and update datastructures
		marked[v] = true;
		for(Edge e : g.adj(v)) {
			int w = e.w();
			if (marked[w]) {
				continue; // v-w edge is not in MST
			}
			if (e.weight() < distTo[w]) { // e is new best connection from tree to w
				edgeTo[w] = e;
				distTo[w] = e.weight();
				if (pq.contains(w)) {
					pq.change(w, distTo[w]);
				} else {
					pq.insert(w, distTo[w]);
				}
			}
		}
	}

	public ArrayList<Edge> edges() {
		return mst;
	}

	public double weight() {
		double weight = 0.0;
		for (Edge e : mst) {
			weight += weight;
		}
		return weight;
	}
}