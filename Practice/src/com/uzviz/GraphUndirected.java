package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 *  Undirected Graph based on adjacency-list graph representation.
 *  <p>
 *  This Graph implementation maintains a vertex-indexed array of
 *  lists of integers. Every edge appears twice: if an edge connects v and w,
 *  then w appears in v’s list and v appears in w’s list.
 *  The second constructor reads a graph from an input stream,
 *  in the format V followed by E followed by a list of pairs of int
 *  values between 0 and V-1.
 *  <p>
 * (based on "ALGORITHM" fourth ed. R. Sedgewick & K. Wayne,
 *  Princeton University )
 *  <p></p>
 *  @author Serhii Yeshchenko
 *  <p>
 *  Date: Dec'2023
 */

public class GraphUndirected
{
    private final int V; // number of vertices
    private int E; // number of edges
    private Bag<Integer>[] adj; // adjacency lists (using Bag data type)

    /**
     * Initializes an empty graph with {@code V} vertices and 0 edges.
     * param V the number of vertices

     * Maintain vertex-indexed array of lists.
     * @param V number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public GraphUndirected(int V)
    {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");

        this.V = V;
        this.E = 0;

        adj = (Bag<Integer>[]) new Bag[V];  // Create a vertex-indexed array of  lists of integers
        for (int v = 0; v < V; v++)         // Initialize all lists to empty.
            adj[v] = new Bag<Integer>();
    }

    /**
     * Initializes a graph from the specified input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices, with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     */
    public GraphUndirected(In in)
    {
        this(in.readInt());   // Read V and construct this graph.
        this.E = in.readInt(); // Read E.
        if (E < 0) throw new IllegalArgumentException("number of edges in a Graph must be nonnegative");

        while (!in.isEmpty())
        { // Read a vertex, read another vertex, and add edge connecting them.
            addEdge(in.readInt(), in.readInt());
        }

    }
    /**
     * Initializes a new graph that is a deep copy of {@code G}.
     *
     * @param  G the graph to copy
     */
    public GraphUndirected(GraphUndirected G) {
        this(G.V());
        this.E = G.E();
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<Integer> reverse = new Stack<Integer>();
            for (int w : G.adj[v]) {
                reverse.push(w);
            }
            for (int w : reverse) {
                adj[v].add(w);
            }
        }
    }

    /**
     * Add edge v-w (parallel edges and self-loops allowed)
     * @param v edge vertex
     * @param w edge vertex
     * @throws IllegalArgumentException unless both {@code 0 <= v < V} and {@code 0 <= w < V}
     */
    public void addEdge(int v, int w)
    {
        validateVertex(v);
        validateVertex(w);
        adj[v].add(w);  // Add w to v’s list.
        adj[w].add(v);  // Add v to w’s list.
    }
    /**
     * Iterator for vertices adjacent to v
     * @param v vertice
     * @return all vertices adjacent to v
     */
    public Iterable<Integer> adj(int v)
    {  return adj[v];  }

    /**
     *
     * @return the number of vertices
     */
    public int V()
    {
        return V;
    }
    /**
     *
     * @return the number of edges
     */
    public int E()
    {
        return E;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }


    /**
     * Return string representation of the Graph
     * @return the sequence of edges in the Graph, separated by 'newline'
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        // print out each edge (twice)
        sb = sb.append(this.V()).append(" vertices, ").append(this.E()).append( " edges\n");
        for(int v = 0; v < this.V(); v++) {
            sb.append(v).append(" : ");
            for (int w: this.adj(v))
                sb.append("-").append(w);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Unit tests the {@code GraphUndirected} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args)
    {
        // read graph from input stream
        In in = new In(args[0]);
        GraphUndirected G = new GraphUndirected(in);

        // print out each edge (twice)
        StdOut.println("String representation of the Graph- print out each edge (twice)");
        StdOut.println(G.toString());
    }
}

