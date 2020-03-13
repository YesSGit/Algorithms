/* *****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: 26.05.2019
 *  Dependencies: Percolation.java
 *
 *  Description:
 *  This program takes as command-line arguments:
 *   - n: size of n-by-n lattice for percolation system
 *   - T: number of independent percolation experiments
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] xT;   // array of xt - the fraction of open sites in series of computational experiments t
    // private final double confidenceInterval;
    private final double exprmntMean, exprmntStdDev, exprmntConfdncLo, exprmntConfdncHi;

    // perform trials independent experiments on an n-by-n griddu
    public PercolationStats(int n, int trials) {
        if (n <= 0)
            throw new IllegalArgumentException("Grid size: " + n + " is less or equal 0");
        if (trials <= 0)
            throw new IllegalArgumentException("Number of experiment trials: " + trials + " is less or equal 0");

        xT = new double[trials];
        double confidenceInterval = 1.96;

        runPercolation(n, trials);
        // exprmntMean = this.mean();
        // exprmntStdDev = this.stddev();
        // exprmntConfdncLo = this.confidenceLo();
        // exprmntConfdncHi = this.confidenceHi();

        exprmntMean = StdStats.mean(xT);
        exprmntStdDev = StdStats.stddev(xT);
        exprmntConfdncLo = exprmntMean-(confidenceInterval*exprmntStdDev)/Math.sqrt(xT.length);
        exprmntConfdncHi = exprmntMean+(confidenceInterval*exprmntStdDev)/Math.sqrt(xT.length);
    }

    // sample mean of percolation threshold
    public double mean() {
        // exprmntMean = StdStats.mean(xT);
        return exprmntMean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        // exprmntStdDev = StdStats.stddev(xT);
        return exprmntStdDev;
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        // exprmntConfdncLo = exprmntMean-(confidenceInterval*exprmntStdDev)/Math.sqrt(xT.length);
        return exprmntConfdncLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        // exprmntConfdncHi = exprmntMean+(confidenceInterval*exprmntStdDev)/Math.sqrt(xT.length);
        return exprmntConfdncHi;
    }

    private void runPercolation(int n, int trials) {
        int rndmRow, rndmCol;         // random indeces of row and column of a lattice
        Percolation perc;

        // repeat the computational experiment by number of trials times
        for (int i = 0; i < trials; i++) {
            perc = new Percolation(n); // object of n-by-n percolation system
            // repeatedly, open sites randomly till the system is getting percolated
            while (!perc.percolates()) {
                rndmRow = StdRandom.uniform(1, n+1);
                rndmCol = StdRandom.uniform(1, n+1);
                perc.open(rndmRow, rndmCol);

            }
            if (perc.percolates()) {
                xT[i] = (double) perc.numberOfOpenSites()/(double) (n*n);
            }

        }

    }

    public static void main(String[] args) {
       int n = Integer.parseInt(args[0]);      // size of n-by-n lattice for percolation system
       int trials = Integer.parseInt(args[1]); // T independent computational experiments

        PercolationStats percStat = new PercolationStats(n, trials);

        StdOut.println("mean                    = " + percStat.exprmntMean);
        StdOut.println("stddev                  = " + percStat.exprmntStdDev);
        StdOut.println("95% confidence interval = [" + percStat.exprmntConfdncLo + ", " + percStat.exprmntConfdncHi + "]");
    }

}
