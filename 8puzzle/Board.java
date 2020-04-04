/* *****************************************************************************
 *  Author: Serhii Yeshchenko
 *  Date: Mar'2020
 *  Description: Coursera, Algorithm, Part I (by Princeton University)
 *               Programming assignment of week 4 - 8 Puzzle.
 *               - Board.java & Solver.java implement solution to the 8-puzzle
 *               problem that illustrates a general artificial intelligence
 *               methodology known as the A* search algorithm.
 *               - Board.java is immutable data type that models an n-by-n board
 *               with sliding tiles.
 *               - constructor receives an n-by-n array containing the n2
 *               integers between 0 and n2 − 1, where 0 represents the blank
 *               square (also assuming that 2 ≤ n < 128).
 *
 *  Compilation:  javac Board.java
 *  Execution:    java Board < puzzle*.txt
 *  Dependencies: edu.princeton.cs.algs4.Stack.java, In.java, StdOut.java
 *                Solver.java
 *****************************************************************************/

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {
    private final int n;                 // size of the game board
    private char[] tiles;                // 1D array which represents initial n-by-n grid of tiles in a row-major order
    private int distanceHamming;         // number of tiles in the wrong position
    private int distanceManhattan;       // sum of the Manhattan distances (sum of the vertical and horizontal distance) from the tiles to their goal positions
    private int blankIndx;               // blank square coordinates on the board
    // private int valueHashCode;        // computed field (in overriden hashCode method) to assign unequal hash codes to unequal instances of Board (violates assignment requiements)


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException();
        n = tiles.length;
        if (n < 2 || n >= 128) throw new IllegalArgumentException("Size of the board is out of range: 2 ≤ n < 128");

        // set tiles array wich represents initial board, - ensure Board immutability
        this.tiles = new char[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] < 0 || tiles[i][j] >= n * n) throw new IllegalArgumentException("Tile's value is out of range: 0 - " + (n * n - 1));
                this.tiles[i * n + j] = (char) tiles[i][j];
                if (tiles[i][j] == 0) {
                    blankIndx = i * n + j;
                    //  blankRow = i;
                    //  blankCol = j;
                }
            }
        sumOfHamming();   // calculates Humming distance
        sumOfManhattan(); // calculates Manhattan distance


        // calculate hashCode - "equal objects must have equal hash codes"
        // Note: overidding hashCode violates assignment reqiurements
        // calcHashCode();
    }

    private Board(char[] tiles) {
        if (tiles == null) throw new IllegalArgumentException();
        // setSize(tiles.length);
        n = (int) Math.sqrt(tiles.length);
        if (n < 2 || n >= 128) throw new IllegalArgumentException("Size of the board is out of range: 2 ≤ n < 128");

        // set new tiles array
        this.tiles = new char[n * n];
        for (int i = 0; i < n * n; i++) {
            if (tiles[i] >= n * n) throw new IllegalArgumentException(
                    "Tile's value is out of range: 0 - " + (n * n - 1));
            this.tiles[i] = tiles[i];
            if (this.tiles[i] == 0) {
                blankIndx = i;
            }
        }
        sumOfHamming();   // calculates Humming distance
        sumOfManhattan(); // calculates Manhattan distance

        // calculate hashCode - "equal objects must have equal hash codes"
        // Note: overidding hashCode violates assignment reqiurements
        // calcHashCode();

    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", (int) tiles[i * n + j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        return distanceHamming;
    }
    // calculates Humming distance - number of tiles in the wrong position
    private void sumOfHamming() {
        distanceHamming = 0;

        for (int i = 0; i < n * n; i++)
            // compare current tile position with the goal one and avoid blank square
            //  '+ 1' - to compare 0-based index w tile's value
            if (tiles[i] != (i + 1) && tiles[i] != 0) {
                distanceHamming++;
            }
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return distanceManhattan;
    }
    // calculate Manhattan distance - sum of the vertical and horizontal distance of tiles to their goal position
    private void sumOfManhattan() {
        distanceManhattan = 0;

        for (int i = 0; i < n * n; i++) {
            // compare current tile position with the goal one, avoid blank square
            if (tiles[i] != (i + 1) && tiles[i] != 0) { //+ 1 to compare 0-based index w 1-based tile's value
                // convert tile`s value from 1D (1-based) representation to 2D (0-based) in row-major(lexicogrphic) order
                int row = (tiles[i] - 1) / n;    // '-1' - to convert tile value to 0-based index value
                int column = (tiles[i] - 1) % n;
                // define 2D goal indexes
                int goalI = i / n;
                int goalJ = i % n;
                // calculate Manhattan distance for the current tile
                int dist = Math.abs(goalI - row) + Math.abs(goalJ - column);

                distanceManhattan = distanceManhattan + dist;
            }
        }
    }

    // is this board the goal board?
    public boolean isGoal() {
        return (this.distanceManhattan == 0 && this.distanceHamming == 0);
    }

    // does this board equal other?
    /**
     * Compares this board to the specified board.
     *
     * @param  other the other date
     * @return {@code true} if this board equals {@code other}; {@code false} otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        Board that = (Board) other;
        return Arrays.equals(this.tiles, that.tiles) &&
                this.distanceManhattan == that.distanceManhattan &&
                this.distanceHamming == that.distanceHamming &&
                this.dimension() == that.dimension();
    }
    /**
     * Returns an integer hash code for this board.
     *
     * @return an integer hash code for this board
     */
    // Note: overidding hashCode violates assignment reqiurements

    // @Override
    // private int hashCode() {
    //     if (this.tiles == null) return 0;
    //
    //     return valueHashCode;
    // }

    // private void calcHashCode() {
    // /*calculate hashCode - "equal objects must have equal hash codes" */
    //     valueHashCode = 0; //computed field (in overriden hashCode method) to assign unequal hash codes to unequal instances of Board
    //
    //     for (int i = 0; i < n; i++)
    //         for (int j = 0; j < n; j++) {
    //             valueHashCode = valueHashCode + 31 * (i * n + j) * tiles[i][j];
    //         }
    //     valueHashCode = valueHashCode + Integer.hashCode(distanceManhattan) + Integer.hashCode(distanceHamming);
    // }

    // collect all neighboring boards
    public Iterable<Board> neighbors() {
        // convert 1D blank tile index into 2d matrix format [row][col] to fаcilitate collection of neighbors
         int blankRow = blankIndx / n;
         int blankCol = blankIndx % n;
        Stack<Board> neighborBoards; // all neighbors of the current search board
        neighborBoards = new Stack<>();
        if (blankRow - 1 >= 0) { // exchange a blank square with the tile above
            tiles[blankIndx] = tiles[(blankRow - 1) * n + blankCol];
            tiles[(blankRow - 1) * n + blankCol] =  0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[(blankRow - 1) * n + blankCol] = tiles[blankIndx];
            tiles[blankIndx] = 0;
        }
        if (blankRow + 1 < n) { // exchange a blank square with the tile below
            tiles[blankIndx] = tiles[(blankRow + 1) * n + blankCol];
            tiles[(blankRow + 1) * n + blankCol] =  0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[(blankRow + 1) * n + blankCol] = tiles[blankIndx];
            tiles[blankIndx] = 0;
        }
        if (blankCol - 1 >= 0) { // exchange a blank square with the tile on the left
            tiles[blankIndx] = tiles[blankRow * n + (blankCol - 1)];
            tiles[blankRow * n + (blankCol - 1)] =  0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[blankRow * n + (blankCol - 1)] = tiles[blankIndx];
            tiles[blankIndx] = 0;
        }
        if (blankCol + 1 < n) { // exchange a blank square with the tile on the right
            tiles[blankIndx] = tiles[blankRow * n + (blankCol + 1)];
            tiles[blankRow * n + (blankCol + 1)] =  0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[blankRow * n + (blankCol + 1)] = tiles[blankIndx];
            tiles[blankIndx] = 0;
        }
        return neighborBoards;
    }

    // a board that is obtained by exchanging of any pair of tiles
    public Board twin() {
        int tileOne, tileTwo; // 1D indexes of swapping tiles
        // obtain pair of tales to be swapped to create a twin board
        // shift from blank square index to define at least two tiles for swapping
        if (n * n - blankIndx > 2) {        // to ensure there are at least two tiles for swapping
            tileOne = blankIndx + 1;
            tileTwo = blankIndx + 2;
        } else {
            tileOne = blankIndx - 1;
            tileTwo = blankIndx - 2;
        }

        // swapping tiles (inside initial tiles[] array)
        char tmpBlock = tiles[tileOne];
        tiles[tileOne] = tiles[tileTwo];
        tiles[tileTwo] = tmpBlock;
        // create a twin board
        Board twinBoard = new Board(tiles);
        // swapping back to restore initial tiles[] array
        tiles[tileTwo] = tiles[tileOne];
        tiles[tileOne] = tmpBlock;

        return twinBoard;
    }

    // unit testing
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int size = in.readInt();
        int[][] tiles = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
