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
 * <p>  This class implements solution based on code {@code Breadth-First Search}. Initialization of the input grid identifies
 *  rotten oranges, checks input data against provided constraints. Before to run BFS, some corner cases to be determined
 *  to avoid computation of BFS part once a solution is found or it's impossible.
 *  <p>
 *   Why BFS fits to problem?,- this algorithm correlates with "diffusion" process of getting oranges rotten:
 *  it explores the vertices in order of their distance from the source: using a (FIFO) queue,
 *  choose, of the paths yet to be explored, the one that was least recently encountered.
 *  <p> <i>Performance</i>:  BFS takes time proportional to linear-time O(E + V) in the worst case.
 *  @author Serhii Yeshchenko
 *  <p>
 *  Date: Dec'2024
 */
public class RottingOranges {
    private int[][] grid;           // given an m x n grid where each cell can have one of three values:
                                    // 0 representing an empty cell, 1 representing a fresh orange, 2 representing a rotten orange
    private java.util.Queue<Integer> rotten;  // queue of rotten oranges for Breadth-First Search computation
    private int orangesCount;       // overall number of oranges in the grid
    private int rottenCount;        // number of rotten oranges (in BFS: connected vertices to a given source-initially rotten orange)
    private int[] minutes;          // number of minutes elapsed until the orange became rotten
    private int minMinutes;         // minimum number of minutes that must elapse until no cell has a fresh orange

    /**
     * Constructor - instantiates this class with the empty m x n grid
     * @param m # of grid rows
     * @param n # of grid columns
     * @throws IllegalArgumentException if size of the grid is out of range: 1 <= m, n <= 10
     */
    public RottingOranges(int m, int n) {
        if (m < 1) throw new IllegalArgumentException("Size of the grid is less than 1");
//        if (m < 1 || n > 10) throw new IllegalArgumentException("Size of the grid is out of range: 1 <= m, n <= 10");
        grid = new int[m][n];
        minutes = new int[m * n];
        rotten = new LinkedList<>();
        minMinutes = Integer.MAX_VALUE;
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
    public RottingOranges(In in) {
        this(in.readInt(), in.readInt());  // read m and n to instantiate this class

        String aw = in.readLine();
        for (int i = 0; i < grid.length; i++) {
            String[] a = in.readLine().split(",");  // for comma-separated input values of the grid
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = Integer.parseInt(a[j]);
//                grid[i][j] = in.readInt();
                if (grid[i][j] < 0 || grid[i][j] > 2) throw new IllegalArgumentException("The grid value is out of range: 0 <= v <= 2");
                if (grid[i][j] == 2) {
                    rotten.add(i * grid[i].length + j);
                    rottenCount++;
                }
                if ( grid[i][j] == 1 || grid[i][j] == 2 ) orangesCount++;
            }
        }
        minMinutes = orangesRotting(grid);
    }

    /**
     * Constructor - instantiates this class with a given grid as input
     * Call for <code>orangesRotting(int[][] grid)</code> instance's method to build the solution.
     * @param iniGrid m x n grid where each cell can have one of three values: 0 - an empty cell, 1 - a fresh orange, 2 - a rotten orange.
      */
    public RottingOranges(int[][] iniGrid) {
        this(iniGrid.length, iniGrid[0].length);  // instantiate this class
//        grid = iniGrid;
        for (int i = 0; i < grid.length; i++)
            System.arraycopy(iniGrid[i], 0, grid[i], 0, grid[i].length);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] < 0 || grid[i][j] > 2) throw new IllegalArgumentException("The grid value is out of range: 0 <= v <= 2");
                if (grid[i][j] == 2) {
                    rotten.add(i * grid[i].length + j);
                    rottenCount++;
                }
                if ( grid[i][j] == 1 || grid[i][j] == 2 ) orangesCount++;
            }
        }
        minMinutes = orangesRotting(grid);
    }

    /**
     * Return the minimum number of minutes that must elapse until no cell has a fresh orange.
     * @param grid m x n grid where each cell can have one of three values: 0 - an empty cell, 1 - a fresh orange, 2 - a rotten orange.
     * @return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
     */
    public int orangesRotting(int[][] grid) {
        int minutesElapsed = 0;
        //  Corner cases #1-2,- to eliminate BFS part once a solution is found or it's impossible
        if (orangesCount == 0) return minutesElapsed;  //#1 -  no oranges in the grid
        if (orangesCount == rottenCount) return minutesElapsed; //#2 -  no fresh oranges

        while (!rotten.isEmpty()) {
            int rottenO = rotten.remove();
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

        if (orangesCount == rottenCount)  return minutesElapsed;
        else return -1;
    }

    public void getRotten(int row, int col, int rttnOrng) {
        this.grid[row][col] = 2;
        rottenCount++;
        minutes[row * this.grid[0].length + col] = minutes[rttnOrng] + 1;
        rotten.add(row * this.grid[0].length + col);
    }

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
     * reads an input file, resolves a problem of 'Rotting Oranges' (#994 on Leet Code) and then prints:
     * <p> - the minimum number of minutes that have been elapsed until no cell has a fresh oranges;
     * <p> - matrix grid of rotten oranges;
     * <p> - or inform that solution is impossible and prints initial matrix grid.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        RottingOranges ro = new RottingOranges(in);
        StdOut.println(ro.toString());
    }
}

