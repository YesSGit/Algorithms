package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 *  Class implements non-recursive {@code Depth-First Search} solution to detect whether a given
 *  undirected graph has Eulerian tour (<i>cycle</i> or <i>path</i>), and if so, find such a tour
 *  (assumes no self-loops or parallel edges). <p>
 *  Eulerian tour - a path (not necessarily simple) that uses every edge in the graph exactly ones.
 *  An Eulerian cycle is Eulerian path that is a cycle.
 *   <p></p>
 *  <i>Algorithm design:</i> <p>
 *  use {@code Depth-First Search} and piece together the cycles were discovered. <p>
 *
 * 	Preprocess  graph to detect whether it's connected and <p>
 * 	- every vertex has even degree (prerequisites to find Euler cycle). <p>
 * 	- or there are only two vertices with odd degree and there is an edge between them,- if no,
 * 	add the extra edge for preprocessing graph and remove it once the Euler path is identified (prerequisites for Euler path). <p>
 * 	In case of <i>Euler cycle</i>,- a source can be any vertex of the graph, in <i>Euler path</i> - one of the two vertices with odd degree. <p>
 * 	All isolated vertices (vertex with no adjacent vertices) to be identified and avoided during preprocessing and thus
 * 	doesn't be part of Eulerian tour (<i>cycle</i> or <i>path</i>).
 *  <p></p>
 *  <i>  dfs solution:</i> <p>
 *  	Initialize {@code stack}, add source {@code v}	<p>
 * 	    Iterate in cycle while  {@code stack} is not empty <p>
 * 		   Check if {@code v's  degree == 0} - add this vertex to the Eulerian tour and remove {@code v} from {@code stack}. <p>
 * 		   If {@code v's degree > 0}, take the next adjacent vertex  {@code w} (from the top of stack-based data structure
 * 		   which ensure DeepFirstSearch approach), <p>
 * 		   Delete edge  {@code v-w}, add {@code w} to the  {@code stack}.
 *  <p></p>
 *  <i>Performance</i>:  linear-time O(E + V). To implement <i>Deleting an edge</i> method (and disallow parallel edges):
 *                       an extra space for a clone of a graph vertex-indexed array of lists of integers
 *                       and amortized time performance penalty to be involved.
 *  <p></p>
 * <i>Input data</i> - undirected graph {@code G}; <p>
 * <i> Data structure</i> <p>
 * ・ {@code boolean[] marked} - to mark visited vertices <p>
 * ・ {@code boolean[] isolated} - is this vertex isolated? <p>
 * ・ {@code boolean isConnected} - is graph connected? <p>
 * ・ {@code boolean pathEuler} - true if the graph is such that the Eulerian path is not a cycle. <p>
 * ・ {@code Stack<Integer> tourEuler} - list of vertices which represents an Euler path.
 *  <p></p>
 *  Dependencies: Graph.java Stack.java Queue.java In.java StdOut.java
 * <p></p>
 *  @author Serhii Yeshchenko
 *  Date: Apr'2024
 */
public class EulerTourNonRecursive {
    private final boolean[] marked;      // marked[v] = true if v connected to s (Has dfs() been called for this vertex?)
    private final boolean[] isolated;    // isolated[v] =  true if v is an isolated vertices of a graph
    private boolean isConnected;      // is graph connected?
    private boolean pathEuler;        // true if the graph is such that the Eulerian path is not a cycle
    private int countEdges;           // number of connected edges,- equal to number of graph's edges if it is connected
    private Stack<Integer>[] adjV;    // vertex-indexed array of  lists of adjacent vertices - to track removing edges from graph
    private Stack<Integer> tourEuler; // list of vertices which represents an Euler path.

    public EulerTourNonRecursive(GraphUndirected G) {
        marked = new boolean[G.V()];
        isolated = new boolean[G.V()];
        tourEuler = new Stack<>();

        /* Check prerequisites to find an Euler path/cycle:
         * Whether every vertex has an even degree?
         * Eulerian path exists iif the number of vertices with odd degrees is two
         * Identify all isolated vertices.
         */
        boolean evenDegree = true;
        boolean missingEdge = false;
        pathEuler = false;
        countEdges = 0;
        int oddDegreeV1 = -1;
        int oddDegreeV2 = -1;
        for (int v = 0; v < G.V(); v++) {
            if (degree(G, v) != 0) {         // avoid isolated vertex
                if (degree(G, v) % 2 != 0) { // identify vertex with odd degree
                    evenDegree = false;
                    // an Eulerian path exists iif the number of vertices with odd degrees is two
                    if (oddDegreeV1 == -1) oddDegreeV1 = v;
                    else if (oddDegreeV2 == -1 ) {
                        oddDegreeV2 = v;
                        pathEuler = true;
                    } else if (pathEuler) pathEuler = false; // there are more than two vertices with odd degrees
                }
            }
            else {
                isolated[v] = true; // mark isolated vertex
            }
        }
        StdOut.println();

        /*  If the graph is such that the Eulerian path is not a cycle, then add the missing edge (if it doesn't exist),
            find the Eulerian cycle, then remove the extra edge (if it's been added).
         */
        int s; // source vertex for dfs method: = vertex with odd degree - for Eulerian path; =0 - for Eulerian cycle.
        if (pathEuler) {
            missingEdge = true;
            for (Integer i : G.adj(oddDegreeV1)) {
                if (i == oddDegreeV2) {
                    missingEdge = false;         // edge between two vertices w odd degrees exists
                    break;
                }
            }
            if (missingEdge) G.addEdge(oddDegreeV1, oddDegreeV2); // add the missing edge (if it doesn't exist)
            s = oddDegreeV1;
            evenDegree = true;
        } else {
            s = 0;
        }

        // Create a vertex-indexed array of  lists of adjacent vertices - to track removing edges from graph
        adjV = (Stack<Integer>[]) new Stack[G.V()];
        for (int v = 0; v < G.V(); v++) {
            adjV[v] = new Stack<Integer>();  // Initialize all lists to empty.
            // reverse so that adjacency list is in same order as original
            Stack<Integer> reverse = new Stack<Integer>();
            for (int w : G.adj(v)) {
                reverse.push(w);
            }
            for (int w : reverse) {
                adjV[v].push(w);
            }
        }

        if (evenDegree) {
            int count = 0;
            while (count++ < G.V())
            {
                if (!isolated[s]) {     // avoid isolated vertices
                    if (!marked[s]) {
                        countEdges = 0; // reset - in case graph has connected components (is not connected)
                        dfs(G, s);
                    }
                }
                s = ++s % G.V();        // looping source vertex index inside range of graph vertices
            }
            countEdges  = (missingEdge) ? countEdges - 1 : countEdges;  // subtract added missing edge from total number of connected edges

            if (countEdges  != G.E()) {
                isConnected = false;
                StdOut.println("Graph is NOT connected,- no Eulerian path.");
            } else {
                isConnected = true;
                if (pathEuler && missingEdge) tourEuler.pop(); // for Eulerian path - remove the extra edge if it's been added.
            }
        } else StdOut.println("More than two vertices with odd degrees,- no Eulerian path.");
    }

    private void dfs(GraphUndirected G, int v) {
        Stack<Integer> stack = new Stack<>();
        stack.push(v);
        marked[v] = true;

        while (!stack.isEmpty()) {
            int V = stack.pop();
            stack.push(V);
            if (adjV[V].size() == 0) {
                tourEuler.push(V);
                stack.pop();
                }
            else {
                // remove the edge that passed once:
                int w = adjV[V].pop(); // remove w from V's adjacency list and
                marked[w] = true;
                countEdges++;

                // reverse so that adjacency list is in same order as original
                Stack<Integer> reverse = new Stack<Integer>();
                for (int q : adjV[w]) {
                    if (q != V) reverse.push(q);  // remove V from w's adjacency list
                }
                while (!adjV[w].isEmpty()) adjV[w].pop();
                for (int q : reverse) {
                    adjV[w].push(q);
                }
                stack.push(w);
                }
        }
    }

    /**
     * Returns true if the graph {@code G} has an Euler path (between two vertices with odd degree).
     *
     * @return {@code true} if the graph has an Euler path; {@code false} otherwise
     */
    public boolean hasEulerPath() {
        return isConnected && pathEuler;
    }

    /**
     * Returns true if the graph {@code G} has an Euler cycle (all vertices have even degree).
     *
     * @return {@code true} if the graph has an Euler cycle; {@code false} otherwise
     */
    public boolean hasEulerCycle() {
        return isConnected && !pathEuler;
    }

    /**
     * Returns an Euler tour (path or cycle) in the graph {@code G}.
     * @return an Euler tour (path or cycle) if the graph {@code G} has such path,
     *         and {@code null} otherwise
     */
    public Iterable<Integer> getEulerTour() {
        if (isConnected && !tourEuler.isEmpty()) return tourEuler;
        else return null;
    }

    /**
     * Returns a list of isolated vertices in the graph {@code G}.
     * @return a list of isolated vertices in the graph if the graph {@code G} has such vertices,
     *         and {@code null} otherwise
     */
    public Iterable<Integer> getIsolatedV() {
        Queue<Integer> isolatedVs = new Queue<>();
        for (int v = 0; v < isolated.length; v++) {
            if (isolated[v]) isolatedVs.enqueue(v);
        }
        if (!isolatedVs.isEmpty()) return isolatedVs;
        else return null;
    }

    /**
     * Return string representation of Euler tour (path or cycle) in the graph.
     * @return the list of vertices which represents an Euler path or cycle.
     * The exact details of the representation are unspecified and subject
     * to change, but the following may be regarded as typical:
     * "Graph's isolated vertices: 6 8 <i>(if identified)</i>
     *  Eulerian path between two vertices with odd degree: 3 4 7 0 5 4 2 1 0 2"
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterable<Integer> list = this.getIsolatedV();
        if (list != null) {
            sb.append("Graph's isolated vertices: ");
            for (int i : list) sb.append(i).append(" ");
        } else sb.append("No isolated vertices.");
        sb.append("\n\r");

        if (hasEulerPath()) sb.append("Eulerian path between two vertices with odd degree: ");
        else
            if (hasEulerCycle()) sb.append("Eulerian cycle: ");
            else
                return sb.append("No Eulerian tour in the graph.").toString();

        for (int q : this.tourEuler) sb.append(q).append(" ");
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
     * Unit tests the {@code EulerTourNonRecursive} data type. <p></p>
     * Builds dfs object based on undirected graph and check whether it
     * has Eulerian tour (path or cycle), identify isolated vertices; <p>
     * Prints Euler path/cycle <p></p>
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        // read graph from input stream
        In in = new In(args[0]);
        GraphUndirected G = new GraphUndirected(in);

        // build dfs object based on undirected graph and check whether it
        // has Eulerian tour (path or cycle), identify isolated vertices
        EulerTourNonRecursive eulerTour = new EulerTourNonRecursive(G);

        Iterable<Integer> list = eulerTour.getIsolatedV();
        if (list != null) {
            StdOut.print("Graph's isolated vertices: ");
            for (int i : list) StdOut.print(i + " ");
        } else StdOut.print("No isolated vertices.");
        StdOut.println();

        // print Euler path/cycle
        if (eulerTour.hasEulerCycle()) StdOut.println("Euler cycle: ");
        else if (eulerTour.hasEulerPath()) StdOut.println("Euler path between two vertices with odd degree: ");
        if (eulerTour.hasEulerCycle() || eulerTour.hasEulerPath())
            for (int q : eulerTour.getEulerTour()) StdOut.print(q + " ");

        StdOut.println();
        StdOut.println(eulerTour.toString());
    }
}
