/* *****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: Feb'2020
 *  Description:
 *****************************************************************************/

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {
    private static int n;             // size of the game board
    // private static int tileOneRow, tileOneCol, tileTwoRow, tileTwoCol; // matrix indexes of swapping tiles
    private int[][] tiles;            // matrix represents initial n-by-n grid of tiles
    // private int[][] twinTiles;        // matrix represents twin (swapped pair of tiles) n-by-n grid of tile
    // private static int[][] tilesGoal; // goal board matrix - all tiles are sorted in a row-major order
    // private static Board goalBoard;   // goal board - instantiated on the 'goal board` matrix
    private int distanceHamming;      // number of tiles in the wrong position
    private int distanceManhattan;    // sum of the Manhattan distances (sum of the vertical and horizontal distance) from the tiles to their goal positions
    private int blankRow, blankCol;   // blank square coordinates on the board
    // private int valueHashCode;        // computed field (in overriden hashCode method) to assign unequal hash codes to unequal instances of Board


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException();

        /* to delete */
        // if (tilesGoal == null || tilesGoal.length != tiles.length)
        //     setGoalTiles(tiles.length);

        setSize(tiles.length);

        if (n < 2 || n >= 128) throw new IllegalArgumentException("Size of the board is out of range: 2 ≤ n < 128");

        // set tiles array wich represents initial board, - ensure Board immutability
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] < 0 || tiles[i][j] >= n*n) throw new IllegalArgumentException("Tile's value is out of range: 0 - " + (n*n - 1));
                this.tiles[i][j] = tiles[i][j];
                if (this.tiles[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        sumOfHamming();   // calculates Humming distance
        sumOfManhattan(); // calculates Manhattan distance


        // calculate hashCode - "equal objects must have equal hash codes"
        // Note: overidding hashCode violates course reqiurements
        // calcHashCode();
    }

    private static void setSize(int size) {
        n = size;
    }

    /* to delete */
    // private static void setGoalTiles(int size) {
    //     n = size;
    //     // set tiles array wich represents the goal board
    //     tilesGoal = new int[n][n];
    //     int tileNumber = 1;
    //     for (int i = 0; i < n; i++) {
    //         for (int j = 0; j < n; j++)
    //             tilesGoal[i][j] = tileNumber++;
    //     }
    //     tilesGoal[n - 1][n - 1] = 0; // set the blank square
    //     // goalBoard = new Board(tilesGoal);
    //     // return tilesGoal;
    // }


    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
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
        /* !!! to delete */
        // if (Arrays.deepEquals(tiles, tilesGoal))
        //     return; // = 0, current board is equal to the goal board

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                // compare current tile position with the goal one and avoid blank square
                // convert current matrix index to 1D: (i * n + j) + 1 - to compare w tile's value
                if (tiles[i][j] != (i * n + j + 1) && tiles[i][j] != 0)
                // if (tiles[i][j] != tilesGoal[i][j] && tiles[i][j] != 0)
                    distanceHamming++;
            }
        // return distanceHamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return distanceManhattan;
    }
    // calculate Manhattan distance - sum of the vertical and horizontal distance from the tiles to their goal positions
    private void sumOfManhattan() {
        distanceManhattan = 0;
        /* !!! to delete */
        // if (Arrays.deepEquals(tiles, tilesGoal))
        //     return; // = 0, current board is equal to the goal board

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                // compare current tile position with the goal one, avoid blank square
                // convert current matrix index to 1D (i * n + j) + 1 to compare w tile's value
                if (tiles[i][j] != (i * n + j + 1) && tiles[i][j] != 0) {
                    // convert tile`s value from 1D (0-based) representation to 2D (0-based) in row-major(lexicogrphic) order
                    int row = (tiles[i][j] - 1) / n;    // '-1' - to convert tile value to 0-based 1D array
                    int column = (tiles[i][j] - 1) % n;

                    distanceManhattan = distanceManhattan + Math.abs(i - row) + Math.abs(j - column);
                }
            }
        // return distanceManhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        /* !!! to delete */
        // setGoalBoard();
        // return Arrays.deepEquals(this.tiles, tilesGoal);
        // return this.equals(goalBoard);

        return (this.distanceManhattan == 0 && this.distanceHamming == 0);
    }

    /* !!! to delete */
    // private void setGoalBoard() {
    //     if (goalBoard == null || goalBoard.tiles.length != n) {
    //         goalBoard = new Board(tilesGoal);
    //     }
    // }

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
        return Arrays.deepEquals(this.tiles, that.tiles) &&
                this.distanceManhattan == that.distanceManhattan &&
                this.distanceHamming == that.distanceHamming &&
                this.dimension() == that.dimension();
    }
    /**
     * Returns an integer hash code for this board.
     *
     * @return an integer hash code for this board
     */
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

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> neighborBoards; // all neighbors of the current search board
        neighborBoards = new Stack<>();
        if (blankRow - 1 >= 0) { // exchange a blank square with the tile above
            tiles[blankRow][blankCol] = tiles[blankRow - 1][blankCol];
            tiles[blankRow - 1][blankCol] = 0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[blankRow - 1][blankCol] = tiles[blankRow][blankCol];
            tiles[blankRow][blankCol] = 0;
        }
        if (blankRow + 1 < n) { // exchange a blank square with the tile below
            tiles[blankRow][blankCol] = tiles[blankRow + 1][blankCol];
            tiles[blankRow + 1][blankCol] = 0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[blankRow + 1][blankCol] = tiles[blankRow][blankCol];
            tiles[blankRow][blankCol] = 0;
        }
        if (blankCol - 1 >= 0) { // exchange a blank square with the tile on the left
            tiles[blankRow][blankCol] = tiles[blankRow][blankCol - 1];
            tiles[blankRow][blankCol - 1] = 0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[blankRow][blankCol - 1] = tiles[blankRow][blankCol];
            tiles[blankRow][blankCol] = 0;
        }
        if (blankCol + 1 < n) { // exchange a blank square with the tile on the right
            tiles[blankRow][blankCol] = tiles[blankRow][blankCol + 1];
            tiles[blankRow][blankCol + 1] = 0;
            Board neighborBoard = new Board(this.tiles);
            neighborBoards.push(neighborBoard);
            tiles[blankRow][blankCol + 1] = tiles[blankRow][blankCol];
            tiles[blankRow][blankCol] = 0;
        }
        return neighborBoards;
    }

    // a board that is obtained by exchanging of any pair of tiles
    public Board twin() {
        int tileOneRow, tileOneCol, tileTwoRow, tileTwoCol; // matrix indexes of swapping tiles
        // !!! - to delete if (twinTiles == null) throw new IllegalArgumentException("No tiles array to create twin board.");

        // define indexes of two tiles to be swapped -
        // !!! - to delete twinTilesIndxs(blankRow * n + blankCol); // param - is 1D index of blank square

        // obtain pair of tales to be swapped for twin board
        // first: convert 2D indexes of blank square into 1D index
        int blank1Dindx = blankRow * n + blankCol;
        // then: shift from blank square index to define at least two tiles
        //       for swapping and convert their indexes back to 2D
        if (n * n - blank1Dindx > 2) {        // to ensure there are at least two tiles for swapping
            tileOneRow = (blank1Dindx + 1) / n;
            tileOneCol = (blank1Dindx + 1) % n;
            tileTwoRow = (blank1Dindx + 2) / n;
            tileTwoCol = (blank1Dindx + 2) % n;
        } else {
            tileOneRow = (blank1Dindx - 1) / n;
            tileOneCol = (blank1Dindx - 1) % n;
            tileTwoRow = (blank1Dindx - 2) / n;
            tileTwoCol = (blank1Dindx - 2) % n;
        }

        // swapping tiles
        int tmpBlock = tiles[tileOneRow][tileOneCol];
        tiles[tileOneRow][tileOneCol] = tiles[tileTwoRow][tileTwoCol];
        tiles[tileTwoRow][tileTwoCol] = tmpBlock;

        Board twinBoard = new Board(tiles);

        tiles[tileTwoRow][tileTwoCol] = tiles[tileOneRow][tileOneCol];
        tiles[tileOneRow][tileOneCol] = tmpBlock;

        return twinBoard;
    }

    // private  static void twinTilesIndxs(int blank1Dindx) {
    //     // tiles matrix of twin board
    //
    //     /* !!! twinTiles - to be eliminated from the Board */
    //     // twinTiles = new int[n][n];
    //     // for (int i = 0; i < n; i++)
    //     //     for (int j = 0; j < n; j++)
    //     //         twinTiles[i][j] = tiles[i][j];
    //
    //     // obtain pair of tales to be swapped for twin board
    //     // first: convert 2D indexes of blank square into 1D index
    //     /* int blank1Dindx = blankRow * n + blankCol; */
    //     // then: shift from blank square index to ensure at least two tiles
    //     //       for swapping and convert their indexes back to 2D
    //     if (n * n - blank1Dindx > 2) {        // to ensure there are at least two tiles for swapping
    //         tileOneRow = (blank1Dindx + 1) / n;
    //         tileOneCol = (blank1Dindx + 1) % n;
    //         tileTwoRow = (blank1Dindx + 2) / n;
    //         tileTwoCol = (blank1Dindx + 2) % n;
    //     } else {
    //         tileOneRow = (blank1Dindx - 1) / n;
    //         tileOneCol = (blank1Dindx - 1) % n;
    //         tileTwoRow = (blank1Dindx - 2) / n;
    //         tileTwoCol = (blank1Dindx - 2) % n;
    //     }
    //
    //     /* !!! to be deleted */
    //     // swapping tiles
    //     // int tmpBlock = twinTiles[tileOneRow][tileOneCol];
    //     // twinTiles[tileOneRow][tileOneCol] = twinTiles[tileTwoRow][tileTwoCol];
    //     // twinTiles[tileTwoRow][tileTwoCol] = tmpBlock;
    // }

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
