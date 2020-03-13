/**
 *  ****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: 06.05.2019
 *  Description: Percolation data type - to model a percolation system using
 *  an n-by-n grid of sites
 *******************************************************************************
*/
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int gridSize;                  // size of Lattice
    private final WeightedQuickUnionUF gridUF;   // union-find object of WeightedQuickUnionUF class
    private boolean[][] lattice;               // n-by-n percolation system
    private int openedSites;               // counter of opened sites

    /**
     * Create n-by-n grid, with all sites blocked
     *
     * @param  n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Grid size: " + n + " is less or equal 0");
        gridSize = n;
        gridUF = new WeightedQuickUnionUF(gridSize * gridSize +1); // +1 to ensure virt top gridUF[0]

        /* all sites are initially blocked, i.e. =0 (initialized by default)
         * +1 indice - to satisfy requirement of indices range: 1 - n                       */
        lattice = new boolean[gridSize +1][gridSize +1];

        /* connect all sites from top row of lattice with the virtual top site to facilitate percolation status check  */
        for (int j = 1; j <= gridSize; j++) {
            gridUF.union(0, xyTo1D(1, j));
        }
        openedSites = 0;
     }

    /**
     * open site (row, col) of lattice and connect it to
     * all of it's adjacent open sites
    **/
    public void open(int row, int col) {
        checkBounds(row, col);

        if (!isOpen(row, col)) {
            lattice[row][col] = true;
            openedSites++;

            /* unite the site with adjicent sites if their indices are valid and site is open */
            if ((row - 1 >= 1) && isOpen(row - 1, col))
                gridUF.union(xyTo1D(row, col), xyTo1D(row - 1, col));
            if ((row + 1 <= gridSize) && isOpen(row + 1, col))
                gridUF.union(xyTo1D(row, col), xyTo1D(row + 1, col));
            if ((col - 1 >= 1) && isOpen(row, col - 1))
                gridUF.union(xyTo1D(row, col), xyTo1D(row, col - 1));
            if ((col + 1 <= gridSize) && isOpen(row, col + 1))
                gridUF.union(xyTo1D(row, col), xyTo1D(row, col + 1));
        }
    }

    /* is site (row, col) open? */
    public boolean isOpen(int row, int col)  {
        checkBounds(row, col);
        return (lattice[row][col]);
    }
    
    /* is site (row, col) full?
     * check whether a site is open and connected to the virtual top site */
    public boolean isFull(int row, int col)  {
        checkBounds(row, col);
        return (isOpen(row, col) && gridUF.connected(0, xyTo1D(row, col)));
    }

    /* number of open sites */
    public int numberOfOpenSites() {
        return openedSites;
    }

    /* does the system percolate?
     * check whether any of bottom row site is connected with virtual top site */
    public boolean percolates() {
        for (int j = 1; j <= gridSize; j++)
//        return gridUF.connected(0, gridSize * gridSize +1);
            if (isOpen(gridSize, j) && gridUF.connected(0, xyTo1D(gridSize, j)))
                return true;
        return false;
    }

    /**
     * Convert 2D indices of Lattice into 1D indice
     * @param  row  -    the row indice of 2D NxN lattice
     * @param  col  -    the colom indice of 2D NxN lattice
     * @throws IllegalArgumentException unless
     *         both {@code 0 < row <= n} and {@code 0 < col <= n}
     */
    private int xyTo1D(int row, int col)  {
        checkBounds(row, col);
        return ((row - 1) * gridSize + col);
    }

    private void checkBounds(int row, int col) {
            if (row <= 0 || row > gridSize) {
                throw new IllegalArgumentException(
                        "row index " + row + " is not between 1 and " + gridSize);
            }
            if (col <= 0 || col > gridSize) {
                throw new IllegalArgumentException(
                        "column index " + col + " is not between 1 and " + gridSize);
            }
    }

    public static void main(String[] args) {
        Percolation percolation;      // object of Percolation class
        int n = 2;


        percolation = new Percolation(n);
        /*
         * simple test - call open(1, 1) and open(1, 2) to ensure
         * that the two corresponding entries are connected.
         */
        percolation.open(1, 1);
        percolation.open(1, 2);
        StdOut.println("Connection status of sites [1,1] and [1,2] is: "
                               + percolation.gridUF.connected(percolation.xyTo1D(1, 1),
                                                              percolation.xyTo1D(1, 2)));

    }

}
