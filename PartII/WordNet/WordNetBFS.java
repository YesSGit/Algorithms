// import edu.princeton.cs.algs4.Digraph_Mod;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

/**
 *  The {@code WordNetBFS} class represents an adopted version of
 *  a BFS data type to help finding an ancestral path between two vertices v and w in a digraph -
 *  it's a directed path from v to a common ancestor x, together with a
 *  directed path from w to the same ancestor x. The shortest ancestral
 *  path is an ancestral path of minimum total length.
 *  'Common ancestor' in the shortest ancestral path is the shortest common ancestor.
 *  An ancestral path is a path, but not a directed path.
 *  <p>
 *  This implementation uses breadth-first search.
 *  The constructor takes time proportional to <em>O(V)</em>,
 *  where <em>V</em> is the number of vertices.
 *  Each call to {@link #distTo(int)} and {@link #hasPathTo(int)} takes constant time;
 *  each call to {@link #pathTo(int)} takes time proportional to the length
 *  of the path.
 *  It uses extra space (not including the digraph) proportional to <em>V</em>.
 *  <p>
 *  <p><b>Dependencies:</b></p>
 *    <li>{@code SAP} (Shortest Ancestral Path data type)</li>
 *    </ul>
 *  @author Serhii Yeshchenko
 *  @version 1.0
 *  @since September 2025
 */
public class WordNetBFS {
    private static final int INFINITY = Integer.MAX_VALUE;
    private  final  Stack<Integer> bfsTrack; // keep track of which array entries change to reuse the same array
                                             // from computation to computation (re-initializing only those entries
                                             // that changed in the previous bfs computation)
    private boolean[] marked;  // marked[v] = is there an s->v path?
    private int[] edgeTo;      // edgeTo[v] = last edge on shortest s->v path
    private int[] distTo;      // distTo[v] = length of shortest s->v path
    private Queue<Integer> queueBFS;

    // ***** for debug
    // public StringBuilder toDot;

    /**
     * Initializes a {@code WordNetBFS} object for the specified digraph.
     * This constructor allocates the {@code marked}, {@code distTo}, and {@code edgeTo}
     * arrays of size {@code V}, setting all distances to {@code INFINITY}.
     *
     * <p><b>Performance:</b> Takes time proportional to <i>V</i>, where <i>V</i>
     * is the number of vertices in the digraph.</p>
     *
     * @param G the directed graph on which BFS computations will be performed
     * @throws IllegalArgumentException if the digraph {@code G} is {@code null}
     */
    public WordNetBFS(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Digraph cannot be null");
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = INFINITY;
        }
        queueBFS = new Queue<>();
        bfsTrack = new Stack<>();

        // ***** for debug
        // toDot = new StringBuilder();
    }

    /**
     * Adopted BFS computation which helps to find an ancestral path in a digraph {@code G} between two source vertices <em>A</em>
     * and <em>B</em>  where <em>A</em> represents an 'id' of nounA synset in {@code WordNet} and <em>B</em> -
     * 'id' of nounB synset, respectively.
     * Each call gatheres adjacent vertices of the vertex, starting from source parameter, and proceeds with all
     * vertices, which are in the BFS queue and have the same distance (number of edges in the shortest path) to the
     * source vertex <em>A</em> or source vertex <em>B</em>.
     * Dependencies: SAP.java, method 'runBFS'.
     *
     * @param G the digraph (a rooted DAG)
     * @param source the source vertex
     * @return {@code Iterable<Integer>} list of all gathered adjacent vertices
     * @throws IllegalArgumentException in case an input graph argument is null
     * @throws IllegalArgumentException unless each source vertex satisfies {@code 0 <= v < G.V}
     */
    public Iterable<Integer> findSAP(Digraph G, int source) {
        if (G == null) throw new IllegalArgumentException("Input graph is null");
        validateVertex(source);
        Stack<Integer> adjcnts = new Stack<Integer>();
        if (!queueBFS.isEmpty()) {
            int currDist = distTo[source];
            int v;
            while (!queueBFS.isEmpty() && distTo[queueBFS.iterator().next()] == currDist) {
                v = queueBFS.dequeue();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                        queueBFS.enqueue(w);
                        adjcnts.push(w);
                        // collect marked vertices to re-initialize, for the next bfs computation,
                        // only those bfs arrays' entries that have been changed in the previous computation
                        bfsTrack.push(w);

                        // for debug
                        // toDot.append(v + " -> " + w + "\n");
                    }
                }
            }
        }
        return adjcnts;
    }

    /**
     * Checks whether BFS queue is empty
     * @return {@code true} if this queue is empty
     *         {@code false} otherwise
     */
    public boolean isQueueEmpty() {
        return queueBFS.isEmpty();
    }

    /**
     * Initializes the BFS queue and sets up the necessary state for starting a BFS traversal
     * from a specified source vertex. Marks the source vertex as visited, sets its distance
     * to zero, and enqueues it into the BFS processing queue while recording it in the BFS
     * tracking stack.
     *
     * @param source the source vertex to initialize the BFS queue and state from
     * @throws IllegalArgumentException unless {@code 0 <= source < V}
     */
    public void initQueue(int source) {
        validateVertex(source);
        if (isQueueEmpty()) {
            marked[source] = true;
            distTo[source] = 0;
            queueBFS.enqueue(source);
            bfsTrack.push(source);
        }
    }

    /**
     * Initializes the BFS queue and sets up the necessary state for starting a BFS traversal
     * from specified source vertices. Marks the source vertices as visited, sets their distance
     * to zero, and enqueues them into the BFS processing queue while recording it in the BFS
     * tracking stack.
     *
     * @param sources the source vertices to initialize the BFS queue and state from
     * @throws IllegalArgumentException unless {@code 0 <= source < V}
     */
    public void initQueue(Iterable<Integer> sources) {
        validateVertices(sources);
         if (isQueueEmpty()) {
             for (int s : sources) {
                 marked[s] = true;
                 distTo[s] = 0;
                 queueBFS.enqueue(s);
                 bfsTrack.push(s);
             }
         }
    }

    /**
     * Resets the internal state of the BFS tracking mechanism by clearing all recorded vertices,
     * reinitializing related arrays, and preparing for a fresh BFS computation.
     * <p>
     * Note(!): re-initializing only those entries that have been changed in the previous computation.
     * <p>
     * This method performs the following actions: <p>
     * - Empties the stack used for tracking visited vertices during BFS.<p>
     * - Resets the {@code marked} array to mark all vertices as unvisited. <p>
     * - Sets all distances in the {@code distTo} array to {@code INFINITY}. <p>
     * - Resets the {@code edgeTo} array to clear references to preceding vertices. <p>
     * - Reinitializes the BFS queue to an empty state.
     * <p>
     * The method ensures that the BFS-related data structures are fully reset to their initial states,
     * allowing a new BFS computation to begin without interference from previous computations
     * and at the same time avoiding performance bottleneck operation of re-initializing all arrays of length V.
     */
    public void resetBFStrack() {
        while (!bfsTrack.isEmpty()) {
            int v = bfsTrack.pop();
            marked[v] = false;
            distTo[v] = INFINITY;
            edgeTo[v] = 0;
        }
        queueBFS = new Queue<>();

        // ***** for debug
        // toDot = new StringBuilder();
    }

    /**
     * Is there a directed path from the source {@code s} (or sources) to vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a directed path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return marked[v];
    }

    /**
     * Returns the number of edges in the shortest path from the source {@code s}
     * (or sources) to vertex {@code v}?
     * @param v the vertex
     * @return the number of edges in the shortest path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns the shortest path from {@code s} (or sources) to {@code v}, or
     * {@code null} if no such path.
     * @param v the vertex
     * @return the sequence of vertices on the shortest path, as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> pathTo(int v) {
        validateVertex(v);

        if (!hasPathTo(v)) return null;
        Stack<Integer> path = new Stack<Integer>();
        int x;
        for (x = v; distTo[x] != 0; x = edgeTo[x])
            path.push(x);
        path.push(x);
        return path;
    }

    /**
     * Validates digraph vertex
     * @param v a digraph vertex to be validated
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
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
        int V = marked.length;
        for (int v : vertices) {
            if (v < 0 || v >= V) {
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
            }
        }
    }

    /**
     * Unit tests the {@code WordNetBFS} data type.
     *
     * @param args an array of {@code String} arguments passed from the command line
     */
    public static void main(String[] args) {
        // Create a small test digraph
        Digraph testGraph = new Digraph(6);
        testGraph.addEdge(0, 1);
        testGraph.addEdge(1, 2);
        testGraph.addEdge(2, 3);
        testGraph.addEdge(3, 4);
        testGraph.addEdge(4, 5);

        // Test constructor and BFS
        WordNetBFS bfs = new WordNetBFS(testGraph);

        // Test initQueue with single source
        bfs.initQueue(0);
        assert bfs.hasPathTo(0);
        assert bfs.distTo(0) == 0;

        // Test findSAP and path finding
        bfs.findSAP(testGraph, 1);
        assert bfs.hasPathTo(1);
        assert bfs.distTo(1) == 1;

        // Test path to vertex
        Iterable<Integer> path = bfs.pathTo(1);
        assert path != null;

        // Test reset
        bfs.resetBFStrack();
        assert !bfs.hasPathTo(0);
        assert bfs.isQueueEmpty();

        // Test multiple sources
        Stack<Integer> sources = new Stack<>();
        sources.push(0);
        sources.push(2);
        bfs.initQueue(sources);
        assert bfs.hasPathTo(0);
        assert bfs.hasPathTo(2);

        System.out.println("All tests passed!");
    }

}
