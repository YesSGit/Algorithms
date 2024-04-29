package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

/**
 *  Undirected Graphs - Depth-First Search
 *  <p></p>
 *  Class implements {@code Depth-First Search} to find paths in an undirected graph without using recursion. <p>
 *  <i>Performance</i>: DFS marks all vertices connected to {@code s} in time proportional to the sum of their degrees.
 *  <p></p>
 *  Algorithm design: <p>
 *      Mark each visited vertex (and keep track of edge taken to visit it). <p>
 *      Return (retrace steps) when no unvisited options.  <p>
 * <i>Input data</i> - undirected graph {@code G}, source vertex {@code s}; <p>
 * <i> Data structure</i> <p>
 * ・ {@code boolean[] marked} - to mark visited vertices <p>
 * ・ {@code int[] edgeTo} - to keep tree of paths <p>
 * ・ {@code int[] distTo} - to keep length of a path from source <p>
 * ・ {@code final int s} - source vertex <p>
 * ・ {@code int farthestV} - vertex to which is the longest simple path from {@code s} <p>
 * ・ {@code final int V} - number of vertices of the graph <p>
 *     Also supports <i>methods</i> <p>
 * ・ {@code boolean hasPathTo(int v)} - is there a path from s to v? <p>
 * ・ {@code Iterable<Integer> pathTo(int v)} - find a path from source vertex <i>s</i> to <i>v</i> (if one exists) in time proportional to its length; <p>
 * ・ {@code int degree(GraphUndirected G, int v)} - compute the degree of v; <p>
 * ・ {@code int maxDegree(GraphUndirected G)} - compute maximum degree of the Graph; <p>
 * ・ {@code int getFarthestV()} - get the farthest vertex <i>v</i> - vertex to which is the longest simple path from source <i>s</i>; <p>
 * ・ {@code Iterable<Integer> getMaxPath()} - get the longest simple path from source vertex <i>s</i> to <i>v</i> (the farthest vertex from <i>s</i>),
 * in time proportional to its length; <p>
 * ・ {@code String toString()} - return string representation of the DFS data structure
 * <p></p>
 *    Test client: <p>
 *    - read graph from input stream and build dfs object based on undirected graph and given source vertex; <p>
 *    - prints a path from the source s to each vertex connected to it; <p>
 *    - prints the longest simple path from source s to v (the farthest vertex from s); <p>
 *    - find and print the longest simple path in the graph (in the subgraph, if it is NOT connected);
 * <p></p>
 *  @author Serhii Yeshchenko
 *  Date: Feb'2023
 */

public class DFSdiameter {
    private boolean[] marked; //marked[v] = true if v connected to s (Has dfs() been called for this vertex?)
    private int[] edgeTo;     //edgeTo[v] = last vertex on path from s to v
    private int[] distTo;     // distTo[v] = length of a path from s to v (number of edges)
    private int farthestV;    // vertex to which is the longest simple path from s
    private final int s;      // source vertex
    private final int V;      // number of vertices of the graph
    private int count;        // number of connected vertices to a given source


    public DFSdiameter(GraphUndirected G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        distTo = new int[G.V()];
        this.s = s;
        this.V = G.V();

        dfs(G, s); // find vertices connected to s
    }

    /**
     * Find all vertices connected to s and a corresponding path (vertices reachable from s).
     * Based on non-recursive Depth-First Search (using stack).
     * @param G undirected graph
     * @param s source vertex
     */
    private void dfs(GraphUndirected G, int s) {
        Stack<Integer> stack = new Stack<>();
        Iterator<Integer> iter; // Iterator over a collection of vertices
        int v;
        int maxPath = 0; // number of edges from source (length of path from s to v)

        // To ensure processing of all vertices adjacent to the source,
        // push into stack "s" in quantity of degree of s-1.
        for (int i = 0; i < degree(G, s) -1 ; i++) {
            stack.push(s);
        }
        marked[s] = true;
        distTo[s] = maxPath;
        count++;  // count source as first vertex from numbers of connected vertices
        while (!stack.isEmpty()) {
            v = stack.pop();
            iter = G.adj(v).iterator();
            while (iter.hasNext()) {
                int w = iter.next();
                if (!marked[w]) {
                    count++;  // increment number of connected vertices
                    stack.push(w);
                    marked[w] = true;
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    if (maxPath < distTo[w]) {
                        maxPath = distTo[w];
                        farthestV = w;
                    }
                    v = w;
                    iter = G.adj(w).iterator();
                }
            }
        }
     }

    /**
     * Get the farthest vertex <i>v</i>  - vertex to which is the longest simple path from source <i>s</i>
     * @return the farthest vertex <i>v</i> from <i>s</i>
     */
     public int getFarthestV() {
        return farthestV;
     }

    /**
     * Get the longest simple path from source vertex <i>s</i> to <i>v</i> (the farthest vertex from <i>s</i>)
     * @return the longest simple path from s
     */
     public Iterable<Integer> getMaxPath() {
        return pathTo(farthestV);
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
     * Find a path from source vertex <i>s</i> to <i>v</i> (if one exists) in time proportional to its length
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
     * Return string representation of the DFS data structure
     * @return the sequence of vertices connected to a given source vertex with
     * corresponding data of  <i>marked[v]</i>, <i>edgeTo[v]</i> and  <i>distTo[v]</i>, separated by 'newline'
     */
    public String toString() {
       StringBuilder sb = new StringBuilder();
        if (getCount() < this.V) sb.append("Graph is NOT connected.").append("\n\r");
        else sb.append("Graph is connected.").append("\n\r");
        for(int v = 0; v < this.V; v++) {
            if (marked[v])
                sb.append("v:").append(v).append(" marked: T").append(" edgeTo:").append(edgeTo[v]).append(" distTo s:").append(distTo[v]).append("\n\r");
            else
                sb.append("v:").append(v).append(" marked: F").append(" edgeTo: - ").append(" distTo s: -").append("\n\r");
        }
        return sb.toString();
    }

    /**
     * Compute the degree of v
     * @param G initialized graph
     * @param v vertice
     * @return degree of v
     */
    public int degree(GraphUndirected G, int v) {
        int degree = 0;
        for (int w : G.adj(v)) degree ++;
        return degree;
    }

    /**
     * Compute maximum degree of the Graph
     * @param G initialized graph
     * @return maximum degree of the Graph
     */
    public int maxDegree(GraphUndirected G) {
        int maxDegree = 0;
        for (int v = 0; v < G.V(); v++)
            if (degree(G, v) > maxDegree) maxDegree = degree(G, v);
        return maxDegree;
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
     * Unit tests the {@code DFSdiameter} data type. <p></p>
     * - Reads graph from input stream.  <p>
     * - Builds {@code dfs} object based on undirected graph and source vertex. <p>
     * - Prints a path from the source {@code s} to each vertex connected to it. <p>
     * - Prints the longest simple path from source s to v (the farthest vertex from s) <p>
     * - Find the longest simple path <p>
     * ・ in the graph, if it is connected <p>
     * ・ in the subgraph, if it is NOT connected    <p>
     *           Performance - linear-time
     * @param args the command-line arguments
     */
    public static void main (String[] args) {
        // read graph from input stream
        In in = new In(args[0]);
        GraphUndirected G = new GraphUndirected(in);

        // build dfs object based on undirected graph and source vertex
        int s = 0;
        DFSdiameter dfs = new DFSdiameter(G, s);
        StdOut.println(dfs.toString());

        // Prints a path from the source s to each vertex connected to it
        if (dfs.getCount() < G.V()) StdOut.print("Path in the SUBgraph from the given source " + s + " to ");
        else StdOut.print("Path in the connected graph from the given source " + s + " to ");
        StdOut.println();
        for (int v = 0; v < G.V(); v++) {
            if (dfs.hasPathTo(v)) {
                StdOut.print("                                                      " + v + ": ");
                for (int w : dfs.pathTo(v)) {
                    if (w == s) StdOut.print(w);
                    else StdOut.print("-" + w);
                }
                StdOut.println();
            }
        }

        // Prints the longest simple path from source s to v (the farthest vertex from s)
        StdOut.println();
        if (dfs.getCount() < G.V()) StdOut.print("The longest simple path in the SUBgraph ");
        else StdOut.print("The longest simple path in the connected graph ");
        StdOut.println("from the given source " +
                s + " to vertex " + dfs.getFarthestV() + ": " + dfs.distTo[dfs.getFarthestV()] + " edges");
        for (int w : dfs.getMaxPath()) {
            if (w == s) StdOut.print(w);
            else StdOut.print("-" + w);
        }
        StdOut.println();

        /*
          Find the longest simple path
           - in the graph, if it is connected
           - in the subgraph, if it is NOT connected
         */
        int maxPath = 0;               // number of edges from source (length of path from s to v)
        int sourceOfMaxPath = 0;
        int farthestV = 0;             // vertex to which is the longest simple path from source
        Iterator<Integer> iter = null; // Iterator over a collection of vertices which is the longest simple path in the graph
        for (int v = 0; v < G.V(); v++) {
            dfs = new DFSdiameter(G, v);
            if (maxPath < dfs.distTo[dfs.getFarthestV()]) {
                maxPath = dfs.distTo[dfs.getFarthestV()];
                sourceOfMaxPath = v;
                iter = dfs.getMaxPath().iterator();
                farthestV = dfs.getFarthestV();
            }
        }
        // Print the longest simple path in the graph
        int v;
        if (iter != null) {
            StdOut.println();
            if (dfs.getCount() < G.V()) StdOut.print("The longest simple path in the SUBgraph:");
            else StdOut.print("The longest simple path in the connected graph:");
            StdOut.println(" from source " +
                    sourceOfMaxPath + " to vertex " + farthestV + " - " + maxPath + " edges");
            while (iter.hasNext()) {
                v = iter.next();
                if ( v == sourceOfMaxPath) StdOut.print(v);
                else StdOut.print("-" + v);
            }
        }
    }
}