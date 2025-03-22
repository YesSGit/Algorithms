package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;
/**
 *  Leet Code problem - "Rotting Oranges" <p>
 *  You are given an m x n grid where each cell can have one of three values: <p>
 * 	• 0 - representing an empty cell, <p>
 * 	• 1 - representing a fresh orange, or <p>
 * 	• 2 - representing a rotten orange. <p>
 * Every minute, any fresh orange that is 4-directionally adjacent to a rotten orange becomes rotten. <p>
 * Return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
 * <p>
 *     Constraints:
 * <p>  m == grid.length
 * <p>  n == grid[i].length
 * <p>  1 <= m, n <= 10
 * <p>  grid[i][j] is 0, 1, or 2.
 * <p>  This class implements solution based on {@code Breadth-First Search (BFS)}. Initialization of the input grid identifies
 *  rotten oranges, checks input data against provided constraints.
 *  <p></p>Before to run BFS, some corner cases to be determined (#1 - #4) to avoid computation of BFS part once
 *  a solution is found or it's impossible:
 *  <p> #1 - no oranges in the grid;
 *  <p> #2 - no fresh oranges in the grid;
 *  <p> #3 - no solution,- there are components with only fresh oranges;
 *  <p> #4 - eliminating connected components without fresh oranges (during BFS part).
 *  <p> One particular case (#3) is when the input grid consists of 'connected component(s)' of only fresh oranges and thus gets oranges rotten
 *  is impossible. Weighted quick-union (WQU) ADT helps to identify such components during initialization stage:
 *  <p> - site-indexed array {@code int[] largest} keeps the largest element of each of the connected components at
 *  the {@code largest[index of the component's root]},- if after the initialization stage the value is == 1,- no rotten
 *  oranges in this component.
 *  <p> • {@code int[] largest} of length {@code [m*n]};
 *  <p> initially  {@code largest[k]= grid[i][j]}, i.e. each site is the largest itself;
 *  <p> • {@code largest[i]} is updated during {@code union()} operation by the largest of {@code largest[root(p)]} or {@code largest[root(q)]}.
 *  <p> - also, the same way a site-indexed array {@code int[] smallest} keeps the smallest element of each of the connected components at
 *  the {@code smallest[index of the component's root]},- once it == 2,- expedites BFS part via eliminating connected components with only rotten oranges.
 * <p>
 *   Why BFS fits to problem?,- this algorithm correlates with "diffusion" process of getting oranges rotten:
 *  it explores the vertices in order of their distance from the source (rotten orange): using a (FIFO) queue,
 *  choose, of the paths yet to be explored (fresh oranges), the one that was least recently encountered.
 *  <p> <i>Performance</i>:
 *  </p> - Time complexity:
 *  <p> For weighted quick-union with <i>m*n</i> grid size, the worst-case order of growth
 *  of the cost of {@code find()}, {@code connected()}, and {@code union()} is at most <i>log n*m</i>.
 *  </p> BFS takes time proportional to linear-time O(E + V) in the worst case.
 *  <p> Computation of Weighted Quick-Union ADT impairs overall time performance by <i>log m*n</i> factor. Thus,
 *  this solution is justified when connected components of only fresh oranges in the input grid are dominated among overall
 *  sequence of input (as BFS part is skipped after initialization stage once such components are identified) and
 *  size of the input grid is reaching 5000 x 5000 and growing. Series of automated test cases shows ratio of running time
 *  of 'WQU + BFS' solution to 'only BFS' version is getting closer to 0.6 when the grid has connected components of only
 *  fresh oranges and input size is crossed over 4700 x 4700.
 * </p> - Space complexity:
 * <p> WQU: Initializing the arrays <code>int[] id, int[] sz, int[]  largest, int[] smallest</code> takes space proportional
 * to size [m * n] of the grid,- space grows linear O(n);
 * </p> BSF: Initializing the queue of rotten oranges <code>Queue< Integer> rotten</code> and counting the number of minutes
 * elapsed <code>int[] minutes</code> - depends strictly on input data and can be at most linear O(n-1);
 *   </p>
 *  @author Serhii Yeshchenko
 *  <p>
 *  Date: Feb'2025
 */
public class RottingOrangesWQU {
    private int[][] grid;           // m x n grid where each cell can have one of three values:
                                    // 0 representing an empty cell, 1 representing a fresh orange, 2 representing a rotten orange
     private java.util.Queue<Integer> rotten;  // queue of rotten oranges for Breadth-First Search computation
    private int orangesCount;       // overall number of oranges in the grid
    private int rottenCount;        // number of rotten oranges (in BFS: connected vertices to a given source-initially rotten orange)
    private int[] minutes;          // number of minutes elapsed until the orange became rotten
    private int minMinutes;         // minimum number of minutes that must elapse until no cell has a fresh orange

    // Weighted quick-union ADT data
    private int[] id;          // parent link (site indexed)
    private int[] sz;          // size of component for roots (site indexed)
    private int countCC;       // number of connected components
    private int[] largest;     // keeps the largest element in connected components at the index of the component's root.
    private int[] smallest;    // keeps the smallest element in connected components at the index of the component's root.
    private int rottenRoots;   // number of "rotten" roots - where the largest element in connected components keeps rotten orange

    /**
     * Constructor - instantiates this class
     * @param m # of grid rows
     * @param n # of grid columns
     * @throws IllegalArgumentException if size of the grid is out of range: 1 <= m, n <= 10
     */
    public RottingOrangesWQU(int m, int n) {
        if (m < 1) throw new IllegalArgumentException("Size of the grid is less than 1");
//        if (m < 1 || n > 10) throw new IllegalArgumentException("Size of the grid is out of range: 1 <= m, n <= 10");
        grid = new int[m][n];
    }

    /**
     * Constructor - reads from input file grid dimensions: # of rows <i>m</i> and # of columns <i>n</i>, instantiates this class,
     * initializes a matrix of grid oranges.
     * Call for <code>orangesRotting(int[][] grid)</code> instance's method to build the solution.
     * @param in input file: <i>m</i> - # of rows (value per line);
     *                       <i>n</i> - # of columns (value per line);
     *                       values of matrix grid separated by comma, one row per line.
     * @throws IllegalArgumentException if the grid value is out of range: 0 <= v <= 2
     */
    public RottingOrangesWQU(In in) {
        this(in.readInt(), in.readInt());  // read m and n to instantiate this class
        if (grid.length < 1 || grid[0].length > 10) throw new IllegalArgumentException("Size of the grid is out of range: 1 <= m, n <= 10");
//        minMinutes = Integer.MAX_VALUE;

        String aw = in.readLine();
        for (int i = 0; i < grid.length; i++) {
            String[] a = in.readLine().split(",");  // for comma-separated input values of the grid
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = Integer.parseInt(a[j]);
//                grid[i][j] = in.readInt();
            }
        }
        minMinutes = orangesRotting(grid);
    }

    /**
     * Constructor - instantiates this class with a given grid as input
     * @param iniGrid m x n grid where each cell can have one of three values: 0 - an empty cell, 1 - a fresh orange, 2 - a rotten orange.
     * @throws IllegalArgumentException if grid is null or empty
     */
    public RottingOrangesWQU(int[][] iniGrid) {
        this(iniGrid.length, iniGrid[0].length);  // instantiate this class
//        if  (iniGrid == null || iniGrid.length == 0) throw new IllegalArgumentException("Grid is null or empty");
        for (int i = 0; i < grid.length; i++)
            System.arraycopy(iniGrid[i], 0, grid[i], 0, grid[i].length);

        minMinutes = orangesRotting(grid);
    }

    /**
     * Return the minimum number of minutes that must elapse until no cell has a fresh orange.
     * @param grid m x n grid where each cell can have one of three values: 0 - an empty cell, 1 - a fresh orange, 2 - a rotten orange.
     * @return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
     */
    public int orangesRotting(int[][] grid) {
        // Weighted quick-union members initialization
        countCC = grid.length * grid[0].length;
        id = new int[countCC];
        for (int i = 0; i < countCC; i++) id[i] = i;
        sz = new int[countCC];
        for (int i = 0; i < countCC; i++) sz[i] = 1;
        largest = new int[countCC];
        smallest = new int[countCC];
        rottenRoots = 0;
        // END * Weighted quick-union members initialization

        minutes = new int[grid.length * grid[0].length];
        rotten = new LinkedList<>();
        minMinutes = Integer.MAX_VALUE;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] < 0 || grid[i][j] > 2) throw new IllegalArgumentException("The grid value is out of range: 0 <= v <= 2");
                if (grid[i][j] == 0) continue;   // eliminate union of empty cell
                boolean connected = false;       // orangeP and orangeQ are not connected

                int orangeP = grid[i][j];
                int indexP = i * grid[i].length + j;
                int newRow = i + 1;
                if (newRow < grid.length && grid[newRow][j] != 0) {
                    int indexQ = newRow * grid[i].length + j;
                    if (!connected(indexP, indexQ))  // Ignore if connected.
                        union(indexP, indexQ);       // Combine components
                    connected = true;                // orangeP and orangeQ are connected
                }
                int newCol = j + 1;
                if (newCol < grid[i].length && grid[i][newCol] != 0) {
                    int indexQ = i * grid[i].length + newCol;
                    if (!connected(indexP, indexQ))  // Ignore if connected.
                        union(indexP, indexQ);       // Combine components
                    connected = true;                // orangeP and orangeQ are connected
                }
                if (!connected && largest[indexP] == 0) {
                    // for single-site component ("isolated" orange) - the largest & the smallest element is the value of the site
                    largest[indexP] = orangeP;
                    smallest[indexP] = orangeP;
                    if (orangeP == 2)
                        rottenRoots++;
                }
                 if (grid[i][j] == 2) {
                    rotten.add(i * grid[i].length + j);
                    rottenCount++;
                }
                if (grid[i][j] == 1 || grid[i][j] == 2) orangesCount++;
            }
        }
        // get the number of connected components - subtract the number of empty cells in tbe grid
        countCC = countCC - (grid.length * grid[0].length - orangesCount);

        int minutesElapsed = 0;

        //  Corner cases #1-3,- to eliminate BFS part once a solution is found or it's impossible
        if (orangesCount == 0) return minutesElapsed;  // #1 - no oranges in the grid
        if (orangesCount == rottenCount) return minutesElapsed; // #2 - no fresh oranges in the grid
        if (rottenRoots != countCC) return -1; // #3 - no solution,- there are components with only fresh oranges

        // BFS part
        while (!rotten.isEmpty())
        {
            int rottenO = rotten.remove();
            if (smallest[find(rottenO)] != 1) continue; // #4 - eliminating connected components without fresh oranges
            int row = rottenO / grid[0].length;
            int col = rottenO % grid[0].length;

            // Mark fresh oranges as rotten if they are 4-directionally adjacent to a rotten orange;
            for (int k = -1; k <= 1; k = k + 2) {
                int newRow = row + k;
                if (newRow >= 0 && newRow < grid.length && grid[newRow][col] == 1) {
                    getRotten(newRow, col, rottenO);
                }
                int newCol = col + k;
                if (newCol >= 0 && newCol < grid[row].length && grid[row][newCol] == 1) {
                    getRotten(row, newCol, rottenO);
                }
            }
            if (rotten.isEmpty()) minutesElapsed = minutes[rottenO];
        }

        if (orangesCount == rottenCount)
            return minutesElapsed;
        else return -1;
    }

    private void getRotten(int row, int col, int rttnOrng) {
        this.grid[row][col] = 2;
        rottenCount++;
        minutes[row * this.grid[0].length + col] = minutes[rttnOrng] + 1;
        rotten.add(row * this.grid[0].length + col);
    }
    // Weighted quick-union methods
    private int count()
    { return countCC; }
    private boolean connected(int p, int q)
    { return find(p) == find(q); }
    private int find(int p)
    { // Follow links to find a root.
        while (p != id[p])
            p = id[p];
        return p;
    }
    private void union(int p, int q)
    {
        int i = find(p);
        int j = find(q);
        if (i == j) return;

        if (largest[i] == 0) {
            largest[i] = grid[i / grid[0].length][i % grid[0].length];
            smallest[i] = largest[i];
            if (largest[i] == 2) rottenRoots++;
        }
        if (largest[j] == 0) {
            largest[j] = grid[j / grid[0].length][j % grid[0].length];
            smallest[j] = largest[j];
            if (largest[j] == 2) rottenRoots++;
        }

        // Make smaller root point to larger one.
        if (sz[i] < sz[j]) {
            id[i] = j;
            sz[j] += sz[i];

            // keep the largest element in connected components at the index of the component's root.
            if (largest[j] != largest[i]) { // either one of them == 2, then the other one == 1
                largest[j] = Math.max(largest[i], largest[j]);
            } else if (largest[i] == 2) {  // largest[j] == largest[i] == 2
                largest[i] = 1;
                rottenRoots--;
            }
            // keep the smallest element in connected components at the index of the component's root.
            smallest[j] = Math.min(smallest[i], smallest[j]);
            if (smallest[i] != 0) smallest[i] = 0;
        }
        else {
            id[j] = i;
            sz[i] += sz[j];

            // keep the largest element in connected components at the index of the component's root.
            if (largest[i] != largest[j]) { // either one of them == 2, then the other one == 1
                largest[i] = Math.max(largest[i], largest[j]);
            } else if (largest[j] == 2) {  // largest[j] == largest[i] == 2
                largest[j] = 1;
                rottenRoots--;
            }
             // keep the smallest element in connected components at the index of the component's root.
            smallest[i] = Math.min(smallest[i], smallest[j]);
            if (smallest[j] != 0) smallest[j] = 0;
        }
        countCC--; // decrement the number of connected components
    }
    // END * Weighted quick-union methods

    public int getMinMinutes() {
        return minMinutes;
    }

    public int getFreshCount() {
        return orangesCount - rottenCount;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        if (orangesCount == 0) {
            s.append("No oranges in the grid.").append("\n");
        } else if (orangesCount == rottenCount) {
            s.append("All oranges are rotten. Minutes elapsed: ").append(getMinMinutes()).append("\n");
        } else if (rottenCount == 0) {
            s.append("All ").append(getFreshCount()).append(" oranges are fresh.").append("\n");
        } else if (minMinutes == -1) {
            s.append("Solution is impossible: some fresh oranges are isolated from rotten one.").append("\n");
            s.append("Number of fresh oranges left: ").append(getFreshCount()).append("\n");
        }

        for (int[] ints : grid) {
            for (int anInt : ints) {
                if (anInt == 1) s.append("O ");
                else if (anInt == 2) s.append("X ");
                else if (anInt == 0) s.append("- ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Unit test the {@code RottingOranges} data type -
     * reads an input file, resolves a problem of 'Rotting Oranges' (#994 on Leet Code)  and then prints:
     * <p> - the minimum number of minutes that have been elapsed until no cell has a fresh oranges;
     * <p> - matrix grid of rotten oranges;
     * <p> - or inform that solution is impossible and prints initial matrix grid.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        RottingOrangesWQU ro = new RottingOrangesWQU(in);
        StdOut.println(ro.toString());
    }

}
