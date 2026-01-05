import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *  The {@code SAP} class represents an immutable data type for finding a Shortest Ancestral Path (SAP)
 *  and Shortest Common Ancestor (SCA) for a given directed graph.
 *
 *  An ancestral path between two vertices <i>v</i> and <i>w</i> is a directed path from <i>v</i> to
 *  a common ancestor <i>x</i>, together with a directed path from <i>w</i> to the same ancestor <i>x</i>.
 *  A shortest ancestral path is an ancestral path of minimum total length.
 *
 *  <p><b>Performance requirements:</b></p>
 *  <ul>
 *    <li><b>Time:</b> All methods (including the constructor) take time at most proportional to
 *        <i>E + V</i> in the worst case, where <i>E</i> and <i>V</i> are the number of edges
 *        and vertices in the digraph, respectively.</li>
 *    <li><b>Space:</b> The data type uses space proportional to <i>E + V</i> to store the
 *        digraph and the BFS supporting structures.</li>
 *  </ul>
 *
 *  <p><b>Dependencies:</b></p>
 *  <ul>
 *    <li>{@code edu.princeton.cs.algs4.Digraph}</li>
 *    <li>{@code edu.princeton.cs.algs4.Bag}</li>
 *    <li>{@code edu.princeton.cs.algs4.Stack}</li>
 *    <li>{@code WordNetBFS} - A specialized BFS implementation for ancestral path discovery.</li>
 *  </ul>
 *
 *  @author Serhii Yeshchenko
 *  @version 1.0
 *  @since September 2025
 */
public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final Digraph diG;
    private final WordNetBFS sapBFS_A;
    private final WordNetBFS sapBFS_B;
    private final int numberOfVertices;           // number of vertices in this digraph
    private int shortestCommonAncestor = -1;      // a vertex which is a shortest common ancestor of <i>v</i> and <i>w</i>
    // private int cachV = -1;                       // vertices of a digraph used recently in computation of length() and ancestor() (to implement a cache)
    // private int cachW = -1;
    private Iterable<Integer> V_subset, W_subset; // subsets of vertices of a digraph used recently in computation of length() and ancestor()
    private int lengthSAP = INFINITY;             // length of shortest ancestral path between <i>v</i> and <i>w</i>

    // for debug purpose
    // private String graphInDOT;  //  representation of digraph in DOT format

    /**
     * Initializes a {@code SAP} object for the specified directed graph.
     * This constructor makes a deep copy of the input graph to ensure that the data type
     * remains immutable, protecting it against subsequent changes to the original graph.
     *
     * <p><b>Performance:</b> Takes time proportional to <i>V + E</i> in the worst case,
     * where <i>V</i> is the number of vertices and <i>E</i> is the number of edges.</p>
     *
     * @param G the directed graph on which the shortest ancestral path computations will be performed.
     *          The graph must not be null and does not necessarily need to be a DAG.
     * @throws IllegalArgumentException if the argument {@code G} is {@code null}.
     */
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("The 'Digraph_Mod' parameter cannot be null.");
        diG = new Digraph(G);
        numberOfVertices = diG.V();
        sapBFS_A = new WordNetBFS(diG);
        sapBFS_B = new WordNetBFS(diG);

        // String representation of this digraph in DOT format, suitable for visualization with Graphviz.
        // for debug
        // graphInDOT = diG.toDot();
    }

    /**
     * Computes length of shortest ancestral path between <i>v</i> and <i>w</i>
     * @param v a vertice of a digraph
     * @param w a vertice of a digraph
     * @return length of shortest ancestral path between <i>v</i> and <i>w</i>
     * ; <i>-1</i> - if no such path exists
     * @throws IllegalArgumentException if any vertex argument is outside its prescribed range
     */
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        // if ((v == cachV &&  w == cachW) || (v == cachW && w == cachV)) return lengthSAP == INFINITY ? -1 : lengthSAP;
        if (v == w) return 0;
        // cachV = v;
        // cachW = w;

        Bag<Integer> subsetA, subsetB;
        subsetA = new Bag<>();
        subsetB = new Bag<>();
        subsetA.add(v);
        subsetB.add(w);

        // length(subsetA, subsetB);

        // lengthSAP = INFINITY;
        // shortestCommonAncestor = -1;
        runBFS(subsetA, subsetB);
        return lengthSAP == INFINITY ? -1 : lengthSAP;
    }

    /**
     * Computes length of a shortest ancestral path of two subsets of vertices <i>A</i> and <i>B</i> which is a shortest ancestral
     * path over all pairs of vertices <i>v</i> and <i>w</i>, with <i>v</i> in <i>A</i> and <i>w</i> in <i>B</i>.
     *
     * @param V vertices of a digraph from subset <i>A</i>
     * @param W vertices of a digraph from subset <i>B</i>
     * @return length of shortest ancestral path between <i>v</i> and <i>w</i>;
     * <i>'-1'</i> - if no such path exists
     * @throws IllegalArgumentException if any vertex argument is outside its prescribed range or Iterable argument is null
     */
    public int length(Iterable<Integer> V, Iterable<Integer> W) {
        validateVertices(V);
        validateVertices(W);

        Bag<Integer> subsetA, subsetB;
        subsetA = new Bag<>();
        subsetB = new Bag<>();
        Stack<Integer> sA = new Stack<>();
        Stack<Integer> sB = new Stack<>();

        for (Integer i : V) sA.push(i);
        for (Integer  i : sA) subsetA.add(i);

        for (Integer j : W) sB.push(j);
        for (Integer j : sB) subsetB.add(j);

        // if ((compareIterables(v, V_subset) && compareIterables(w, W_subset)) ||
        //     (compareIterables(v, W_subset) && compareIterables(w, V_subset)))  return lengthSAP == INFINITY ? -1 : lengthSAP;
        // lengthSAP = INFINITY;
        // shortestCommonAncestor = -1;
        // ancestor(v, w);

        runBFS(subsetA, subsetB);
        return lengthSAP == INFINITY ? -1 : lengthSAP;
    }

    /**
     * Computes a shortest common ancestor of <i>v</i> and <i>w</i> that participates in a shortest ancestral path;
     *
     * @param v a vertice of a digraph
     * @param w a vertice of a digraph
     * @return a vertex which is a shortest common ancestor of <i>v</i> and <i>w</i> ;
     * <i>'-1'</i> - if no common ancestor exists
     * @throws IllegalArgumentException if any vertex argument is outside its prescribed range
     */
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        // if ((v == cachV && w == cachW) || (v == cachW && w == cachV)) return shortestCommonAncestor;
        if (v == w) return v;
        // cachV = v;
        // cachW = w;

        Bag<Integer> subsetA, subsetB;
        subsetA = new Bag<>();
        subsetB = new Bag<>();
        subsetA.add(v);
        subsetB.add(w);

        // lengthSAP = INFINITY;
        // shortestCommonAncestor = -1;
        // ancestor(subsetA, subsetB);

        return  runBFS(subsetA, subsetB);
    }

    /**
     * Computes a shortest common ancestor of two subsets of vertices <i>A</i> and <i>B</i>
     * that participates in a shortest ancestral path;
     *
     * @param V vertices of a digraph from subset <i>A</i>
     * @param W vertices of a digraph from subset <i>B</i>
     * @return a vertex which is a shortest common ancestor of <i>v</i> and <i>w</i> ;
     *  <i>'-1'</i> - if no common ancestor exists
     *  @throws IllegalArgumentException if any vertex argument is outside its prescribed range or Iterable argument is null
     */
    public int ancestor(Iterable<Integer> V, Iterable<Integer> W) {
        validateVertices(V);
        validateVertices(W);

        Bag<Integer> subsetA, subsetB;
        subsetA = new Bag<>();
        subsetB = new Bag<>();
        Stack<Integer> sA = new Stack<>();
        Stack<Integer> sB = new Stack<>();

        for (Integer i : V) sA.push(i);
        for (Integer  i : sA) subsetA.add(i);

        for (Integer j : W) sB.push(j);
        for (Integer j : sB) subsetB.add(j);

        return runBFS(subsetA, subsetB);
    }

    private int runBFS(Iterable<Integer> v_subset, Iterable<Integer> w_subset) {
         if ((compareIterables(v_subset, V_subset) && compareIterables(w_subset, W_subset)) ||
                (compareIterables(v_subset, W_subset) && compareIterables(w_subset, V_subset)))
            return shortestCommonAncestor;
        V_subset = v_subset;
        W_subset = w_subset;

        boolean isSAP = false;
        lengthSAP = INFINITY;
        shortestCommonAncestor = -1;

        // In case of any common element between two given subsets of vertices A and B ,-
        // return it as a shortest common ancestor with length of shortest ancestral path equal to 0
        int commonElement = findAnyCommonElement(v_subset, w_subset);
        if (commonElement != -1) {
            lengthSAP = 0;
            shortestCommonAncestor = commonElement;
            return shortestCommonAncestor;
        }

        // run the two breadth-first searches from v and w in lockstep
        // (alternating back and forth between exploring vertices in each of the two searches),
        // then one can terminate the BFS from v (or w) as soon as the distance exceeds
        // the length of the best ancestral path found so far.
        if (!v_subset.iterator().hasNext() || !w_subset.iterator().hasNext()) return -1;  // empty subset

        int v = V_subset.iterator().next();
        int w = W_subset.iterator().next();
        if (sapBFS_A.isQueueEmpty()) sapBFS_A.initQueue(V_subset);
        if (sapBFS_B.isQueueEmpty()) sapBFS_B.initQueue(W_subset);
        while (!sapBFS_A.isQueueEmpty() || !sapBFS_B.isQueueEmpty()) {
            // bfs from vertex v
            for (int hypernymA : sapBFS_A.findSAP(diG, v)) {
                if (sapBFS_B.hasPathTo(hypernymA)) {
                    int sapDistanceTmp = sapBFS_A.distTo(hypernymA)
                            + sapBFS_B.distTo(hypernymA);
                    if (sapDistanceTmp < lengthSAP || (sapDistanceTmp == lengthSAP && hypernymA < shortestCommonAncestor)) {
                        lengthSAP = sapDistanceTmp;
                        shortestCommonAncestor = hypernymA;
                    } else {
                        if (sapBFS_A.distTo(hypernymA) >= lengthSAP) {
                            isSAP = true;
                            break;
                        }
                    }
                }
                v = hypernymA;
            }

            // bfs from vertex w
            for (int hypernymB : sapBFS_B.findSAP(diG, w)) {
                if (sapBFS_A.hasPathTo(hypernymB)) {
                    int sapDistanceTmp = sapBFS_A.distTo(hypernymB)
                            + sapBFS_B.distTo(hypernymB);
                    if (sapDistanceTmp < lengthSAP || (sapDistanceTmp == lengthSAP && hypernymB < shortestCommonAncestor)) {
                        lengthSAP = sapDistanceTmp;
                        shortestCommonAncestor = hypernymB;
                    } else {
                        if (sapBFS_B.distTo(hypernymB) >= lengthSAP) {
                            isSAP = true;
                            break;
                        }
                    }
                }
                w = hypernymB;
            }
            if (isSAP) break;
        }

        // *** uncomment for debug - crearte a file with representation of this digraph (only marked vertices!!!) in DOT format,
        //  suitable for visualization with Graphviz.
        // Out out = new Out("digraph-wordnet_sub.dot");
        // String s = "digraph {" + "\n"
        //         + "node[shape=circle, style=filled, fixedsize=true, width=0.3, fontsize=\"8pt\"]"
        //         + "\n"
        //         + "edge[arrowhead=normal]" + "\n";
        // out.println(s);
        // out.println(sapBFS_A.toDot);
        // out.println(sapBFS_B.toDot);
        // out.println("}");
        // *****

        // re-initializing only those bfs arrays' entries that have been changed in the previous bfs computation
        sapBFS_A.resetBFStrack();
        sapBFS_B.resetBFStrack();

        return shortestCommonAncestor;
    }

    /**
     * Validates digraph vertex
     * @param v a digraph vertex to be validated
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    private void validateVertex(int v) {
        if (v < 0 || v >= numberOfVertices)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (numberOfVertices-1));
    }

    /**
     * Validates digraph vertices
     * @param vertices digraph vertices to be validated
     * @throws IllegalArgumentException if vertices is null
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        // ***  see comments below
        // int vertexCount = 0;
        for (Integer v : vertices) {
            // vertexCount++;
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
        // **** Validation of "zero vertices" in a subset of vertices
        // (!!! doesn't meet correctness test of Princenton Autograder)
        // if (vertexCount == 0) {
        //     throw new IllegalArgumentException("zero vertices");
        // }
    }

    /**
     * Compares two Iterable objects for equality
     * @param bag1 First iterable collection
     * @param bag2 Second iterable collection
     * @return true if collections contain the same elements (regardless of order), false otherwise
     */
    private static <T> boolean compareIterables(Iterable<T> bag1, Iterable<T> bag2) {
        if (Objects.equals(bag1, bag2)) return true;
        if (bag1 == null || bag2 == null) return false;

        // Convert to lists for easier comparison
        List<T> list1 = new ArrayList<>();
        List<T> list2 = new ArrayList<>();

        // Fill lists from iterables
        for (T item : bag1) list1.add(item);
        for (T item : bag2) list2.add(item);

        // Check sizes first
        if (list1.size() != list2.size()) return false;

        // Compare elements
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) return false;
        }

        return true;
    }

    /**
     * Finds any common element between two given collections of integers.
     *
     * @param collectionA an iterable collection of integers, must not be null
     * @param collectionB another iterable collection of integers, must not be null
     * @return an integer that is common between the two collections; returns -1 if no common element exists
     * @throws IllegalArgumentException if either of the input collections is null
     */
    private static int findAnyCommonElement(Iterable<Integer> collectionA, Iterable<Integer> collectionB) {
        if (collectionA == null || collectionB == null) {
            throw new IllegalArgumentException("Input collections cannot be null");
        }

        // Put all elements of 'a' into a set
        Set<Integer> seen = new HashSet<>();
        for (int x : collectionA) {
            seen.add(x);
        }

        // Scan 'b' and return first element that appears in the set
        for (int y : collectionB) {
            if (seen.contains(y)) {
                return y;
            }
        }
        return -1; // no common element
    }

    /**
     * Returns the string representation of this WordNet digraph in DOT format,
     * suitable for visualization with Graphviz.
     * @return string representation of the digraph in DOT format
     */
    // private String getGraphInDOT() {
    //     return graphInDOT;
    // }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        // version of `Digraph` type with method of "DOT format" represantation is not suitable for Corsera grading
        // for debug purpose
        // StdOut.println("String representation of this digraph in DOT format: " + "\n" + sap.getGraphInDOT());
        // Out out = new Out("digraph3.dot");
        // out.println(sap.getGraphInDOT());

        Bag<Integer> bagA = new Bag<>();
        Bag<Integer> bagB = new Bag<>();
        int length;
        int ancestor;

        int vertexV = 15;
        int vertexW = 10;
        length   = sap.length(vertexV, vertexW);
        ancestor = sap.ancestor(vertexW, vertexV);
        StdOut.printf("v(%d) - w(%d) : length = %d, ancestor = %d\n", vertexV, vertexW, length, ancestor);

        length   = sap.length(vertexV, vertexW);
        ancestor = sap.ancestor(vertexW, vertexV);
        StdOut.printf("Repeated same vertices: length = %d, ancestor = %d\n", length, ancestor);
        vertexV = 15;
        vertexW = 15;
        length   = sap.length(vertexV, vertexW);
        ancestor = sap.ancestor(vertexW, vertexV);
        StdOut.printf("v(%d)==w(%d) : length = %d, ancestor = %d\n", vertexV, vertexW, length, ancestor);

        bagA.add(21);
        bagA.add(20);
        bagA.add(23);
        // bagA.add(7);
        // bagA.add(15);

        // bagB.add(0);
        bagB.add(2);
        bagB.add(12);
        // bagB.add(9);
        length   = sap.length(bagA, bagB);
        ancestor = sap.ancestor(bagB, bagA);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

        vertexV = 15;
        vertexW = 10;
        length   = sap.length(vertexV, vertexW);
        ancestor = sap.ancestor(vertexW, vertexV);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

        bagA = new Bag<>();
        bagB = new Bag<>();

        while (!StdIn.isEmpty()) {
            int bagNunmber = StdIn.readInt();
            int v = StdIn.readInt();
            if (bagNunmber == 1) {
                bagA.add(v);
            } else if (bagNunmber == 2) {
                bagB.add(v);
            } else {
                StdOut.println("Error: invalid bag number");
                break;
            }
        }

        length   = sap.length(bagA, bagB);
        ancestor = sap.ancestor(bagB, bagA);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
}
