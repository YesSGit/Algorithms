package com.uzviz;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
/**
 * The {@code RottingOrangesTesting} class is computing the ratio of running time of computation
 * of "Rotting Oranges" problem of 'WQU + BFS' implementation version to 'only BFS' version,
 * with size of N*N input grid increases on each iteration by  100 x 100 cells (initial grid is 2x2 cells).
 * <p>
 * Input grid values are random uniformly generated, each cell can have one of three values: <p>
 * 	• 0 - representing an empty cell, <p>
 * 	• 1 - representing a fresh orange, or <p>
 * 	• 2 - representing a rotten orange. <p>
 * 	    Note: {@code StdRandom.uniform(0, 3)} generates grids when solution is impossible (there are always connected
 * 	    components of only fresh oranges) - to show the effect from 'WQU + BFS' implementation. To generate solvable
 * 	    solution replace the left endpoint param to {@code '1'}.<p>
 * 	Dependencies: <p>
 *     - class {@link RottingOrangesWQU} ('WQU + BFS') - solution based on {@code Weighted Quick-Union}
 *     and {@code Breadth-First Search} algorithms; <p>
 *     - class {@link RottingOranges} ('only BFS') - solution based on {@code Breadth-First Search} algorithm.
 *  </p>  <i>Time complexity</i> analysis of both versions:
 *  <p> For Weighted Quick-Union with <i>m*n</i> grid size, the worst-case order of growth
 *  of the cost of {@code find()}, {@code connected()}, and {@code union()} is at most <i>log n*m</i>.
 *  </p> BFS takes time proportional to linear-time O(E + V) in the worst case.
 *  <p> Computation of Weighted Quick-Union ADT impairs overall time performance by <i>log m*n</i> factor. Thus,
 *  this solution is justified when connected components of only fresh oranges in the input grid are dominated among overall
 *  sequence of input (as BFS part is skipped after initialization stage once such components are identified) and
 *  size of the input grid is reaching 5000 x 5000 and growing. <p>
 *  Series of experiments shows reproducible results - ratio of running time of 'WQU + BFS' solution to
 *  'only BFS' version is getting closer to 0.6 when the grid has connected components of only fresh oranges and
 *  input size is crossed over 4700 x 4700.
 * </p> - Space complexity:
 * <p> WQU: Initializing the arrays <code>int[] id, int[] sz, int[]  largest, int[] smallest</code> takes space proportional
 * to size [m * n] of the grid,- space grows linear O(n);
 * </p> BSF: Initializing the queue of rotten oranges <code>Queue< Integer> rotten</code> and counting the number of minutes
 * elapsed <code>int[] minutes</code> - depends strictly on input data and can be at most linear O(n-1);
 *   </p>
 *  @author Serhii Yeshchenko
 *  <p>
 *  Date: Feb'2025
 *  <p></p>
 *
 */
public class RottingOrangesTesting {
   private static int minMinutes1;
   private static int minMinutes2;
   private static double elapsedTime1;
   private static double elapsedTime2;

   RottingOrangesTesting() {
       minMinutes1 = Integer.MAX_VALUE;
       minMinutes2 = Integer.MAX_VALUE;
       elapsedTime1 = 0.0;
       elapsedTime2 = 0.0;
   }

   public void runTest() {
       StdOut.println("              'Minutes       'Minute");
       StdOut.println("   Size" + "     to rot WQU' " + "   to rot BFS' " + "Timer WQU,c" + "     Timer BFS,c" + "     'Time Ratio' \r\n");

       for (int n = 2; true; n += 100) {
           timeTrial(n);
           StdOut.printf("%7d      %7d      %7d      %7.4f         %7.4f     ",  n, minMinutes1, minMinutes2, elapsedTime1, elapsedTime2);
           if (elapsedTime2 < 0.000001) StdOut.printf("    %5.1f\n", 0.0);
           else StdOut.printf("    %5.2f\n", elapsedTime1 / elapsedTime2);
        }
   }

    public void timeTrial(int N) {

        int[][] grid = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                grid[i][j] = StdRandom.uniform(0, 3);

        // "Rotting Oranges" problem
        Stopwatch timer1 = new Stopwatch();
        // solution 'A': based on Breadth-First Search and  Weighted quick-union which helps to identify
        // components with only fresh oranges during initialization stage.
        RottingOrangesWQU roWUF = new RottingOrangesWQU(grid);
        // return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
        minMinutes1 = roWUF.getMinMinutes();
        elapsedTime1 = timer1.elapsedTime();

        // solution 'B':  based on Breadth-First Search.
        Stopwatch timer2 = new Stopwatch();
        RottingOranges ro = new RottingOranges(grid);
        // return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
        minMinutes2 = ro.getMinMinutes();
        elapsedTime2 = timer2.elapsedTime();

    }

    public static void main(String[] args) {
        RottingOrangesTesting roTest = new RottingOrangesTesting();
        roTest.runTest();
    }

}
