package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 *  Coursera, ALGORITHM Part II, week 1, Undirected Graphs - Breadth-First Search
 *  <p></p>
 *  Implements {@code Breadth-First Search} to find paths in an undirected graph with
 *  the fewest number of edges from the source {@code s} given.
 *  <p>
 *  <i>Performance</i>:
 *  <p>
 *  ・ For any vertex v reachable from s, BFS computes the shortest path from s to v (no path from s to v has fewer edges).
 *  <p>
 *  ・ BFS marks all the vertices connected to {@code s} in time proportional to the sum of their degrees.
 *  If the graph is connected, this sum is the sum of the degrees of all the vertices, or 2E.
 *  <p></p>
 *  Algorithm design:
 * <p>
 *     It is based on maintaining a queue of all vertices that have been marked but whose adjacency lists
 *     have not been checked. Put the source vertex on the queue, then perform the following steps until the queue is empty:
 *  <p>
 *    ・ Take the next vertex v from the queue and mark it.
 *  <p>
 *    ・ Put onto the queue all unmarked vertices that are adjacent to {@code v}.
 *  <p>
 *    In BFS, explores the vertices in order of their distance from the source: using a (FIFO) queue,
 *    choose, of the paths yet to be explored, the one that was least recently encountered.
 * <p></p>
 *    Test client:
 * <p>
 *   - reads graph from input stream and build bfs object based on undirected graph and given source vertex; <p>
 *   - finds Center(s) of an undirected graph. Center is a vertex such that its maximum distance from any other vertex is minimized.
 * <p></p>
 *  <i>Input data</i> - undirected graph {@code G}, vertex {@code s};
 * <p></p>
 *  <i> Data structure</i>
 *  <p>
 *  ・ {@code boolean[] marked} - to mark visited vertices
 *  <p>
 *  ・ {@code int[] edgeTo} - a parent-link representation of a tree rooted at {@code s}, which
 *                            defines the shortest paths from {@code s} to every vertex that is connected to {@code s}.
 *  <p>
 *  ・ {@code int[] distTo} - length of a path from s to v (number of edges)
 * <p></p>
 *  @author Serhii Yeshchenko
 *  (based on R. Sedgewick, K. Wayne, ALGORITHM fourth ed, Princeton University)
 *  Date: Feb'2024
 */
public class BFSgraphCenter {
    private boolean[] marked; //marked[v] = true if v connected to s (Is a shortest path to this vertex known?)
    private int[] edgeTo;     //edgeTo[v] = last vertex on known path from source to v
    private int[] distTo;     // dist[v] = length of a path from s to v (number of edges)
    private final int s;      // source vertex
    private final int V;      // number of vertices of the graph
    private int count;       // number of connected vertices to a given source

    public BFSgraphCenter(GraphUndirected G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        distTo = new int[G.V()];
        this.s = s;
        this.V = G.V();

        bfs(G, s);
    }

    /**
     * Find paths in a graph with the fewest number of edges from the source {@code s} given.
     * Method marks all vertices connected to {@code s}
     * @param G undirected graph
     * @param s source vertex
     */
    private void bfs(GraphUndirected G, int s) {
        Queue<Integer> que = new Queue<>();
        marked[s] = true; // mark the source
        que.enqueue(s);   // and put it on the queue.
        count++;          // count source as first vertex from numbers of connected vertices
        distTo[s] = 0;

        while (!que.isEmpty()) {
            int v = que.dequeue();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    count++;       // increment number of connected vertices
                    marked[w] = true;
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    que.enqueue(w);
                }
            }
        }
    }

    /**
     * Is there a path from s to v?
     * @param v vertex
     * @return <i>true</i> - s connected to v, <i>false</i> - no path from s to v
     */
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    /**
     * Find a path from source vertex <i>s</i> to <i>v</i> (if one exists) in time proportional to its length,
     *  with the property that no other such path from <i>s</i> to <i>v</i> has fewer edges.
     * @param v vertex
     * @return path from s to v; null if no such path
     */
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;

        Stack<Integer> path = new Stack<>();
        for (int x = v; x != s; x = edgeTo[x])
            path.push(x);
        path.push(s);
        return path;
    }

    /**
     *  How many vertices are connected to source?
     *  Helps to define whether the graph is connected (the graph
     *  is connected iif the search marked all of its vertices).
     * @return number of connected vertices to a given source
     */
    public int getCount() {
        return count;
    }

    /**
     * Return string representation of the BFS data structure
     * @return the sequence of vertices connected to a given <i>source</i> vertex with
     * corresponding data of  <i>marked[v]</i>,<i>edgeTo[v]</i> and  <i>distTo[v]</i>, separated by 'newline'
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int v = 0; v < this.V; v++) {
            if (marked[v])
                sb.append("v:").append(v).append(" marked:T").append(" edgeTo:").append(edgeTo[v]).append(" distTo s:").append(distTo[v]).append("\n\r");
            else
                sb.append("v:").append(v).append(" marked:F").append(" edgeTo:- ").append(" distTo s:-").append("\n\r");
        }
        return sb.toString();
    }
    /**
     * Unit tests the {@code BFSgraphCenter} data type. <p></p>
     * - Reads graph from input stream.  <p>
     * - Builds {@code bfs} object based on undirected graph and source vertex. <p>
     * - Prints a path from the source {@code s} to each vertex connected to it. <p>
     * - Finds Center(s) of an undirected graph.
     *           Center is a vertex such that its maximum distance from any other vertex is minimized. <p>
     *
     *  <i>Algorithm design</i>: <p>
     *           	For any vertex v reachable from s, Breadth-First Search computes the shortest path
     *           	from s to v (no path from s to v has fewer edges). In the connected graph,
     *           	one have to build  BFS object based on undirected graph and source which to be
     *           	chosen among all vertices one by one. Source which has the minimum sum of distances
     *           	to all other vertices is Center of the graph (or Centers, in case of many with equal value of sum),
     *           	as their maximum distance from any other vertex is minimized .<p>
     *           Performance - linear-time
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        // reads graph from input stream
        In in = new In(args[0]);
        GraphUndirected G = new GraphUndirected(in);

        // builds bfs object based on undirected graph and source vertex and
        // return string representation of the BFS data structure
        int s = 6;
        BFSgraphCenter bfs = new BFSgraphCenter(G, s);
        StdOut.print("Built BFS data structure of the ");
        if (bfs.getCount() < G.V()) StdOut.println("NOT connected graph with source " + s + ": ");
        else StdOut.println("connected graph with source " + s + ": ");
        StdOut.println(bfs.toString());

        // Prints a path from the source s to each vertex connected to it
        if (bfs.getCount() < G.V()) StdOut.print("Path in the SUBgraph from the given source " + s + " to ");
        else StdOut.print("Path in the connected graph from the given source " + s + " to ");
        StdOut.println();
        for (int v = 0; v < G.V(); v++) {
            if (bfs.hasPathTo(v)) {
                StdOut.print("                                                      " + v + ": ");
                for (int w : bfs.pathTo(v)) {
                    if (w == s) StdOut.print(w);
                    else StdOut.print("-" + w);
                }
                StdOut.println();
            }
        }

        // Finds Center(s) of an undirected graph.
        Queue<Integer> Centers = new Queue<>(); // collection of centers
        int[] distSum = new int[G.V()]; // array, keeps for each vertex (as a source) minimum sum of distances to all other vertices
        int minDist = 0;                // keeps a minimum sum of distances to all other vertices
        boolean connected = false;      // is graph connected?- there is a path from every vertex to every other vertex
        for (int v = 0; v < G.V(); v++) {
            bfs = new BFSgraphCenter(G, v);
            for (int w = 0; w < bfs.V; w++) {
                distSum[v] = distSum[v] + bfs.distTo[w]; // for given source and built bfs object calculate a sum of distances to all other vertices
            }
            if (v == 0) {
                minDist = distSum[v];
                if (bfs.getCount() == G.V()) connected = true; // check whether graph is connected (just with first source)
            }
            else if (distSum[v] <= minDist) {
                if (distSum[v] == minDist) {
                    Centers.enqueue(v);
                } else {
                    Centers = new Queue<>();
                    Centers.enqueue(v);
                    minDist = distSum[v];
                }
            }
        }
        if (connected) StdOut.println("Center(s) of connected graph: ");
        else StdOut.println("Center(s) of connected component (graph is NOT connected): ");
        StdOut.println(Centers.toString());
     }
}
