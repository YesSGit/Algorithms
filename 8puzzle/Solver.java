/* *****************************************************************************
 *  Author: Serhii Yeshchenko
 *  Date: Mar'2020
 *  Description: Coursera, Algorithm, Part I (by Princeton University)
 *               Programming assignment of week 4 - 8 Puzzle.
 *               - Solver.java & Board.java implement solution to the 8-puzzle
 *               problem that illustrates a general artificial intelligence
 *               methodology known as the A* search algorithm.
 *               This program creates an initial board from filename specified
 *               on the command line and finds the minimum number of moves to
 *               reach the goal state.
 *               - Static nested class SearchNode defines a search node of the
 *               game to be a board, the number of moves made to reach the board,
 *               and the previous search node.
 *
 *  Compilation:  javac Solver.java
 *  Execution:    java Solver < puzzle*.txt
 *  Dependencies: edu.princeton.cs.algs4.MinPQ, Stack.java, In.java, StdOut.java
 *                Board.java
 **************************************************************************** */
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class Solver {
    private final SearchNode goalSearchNode;
    // private final SearchNod goalSearchTwinNode // - use this instance variable for debug

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("No valid argument to the Solver constructor.");

        // SearchNode prevSearchNode = null;
        SearchNode searchNode = new SearchNode(initial, null, 0);
        MinPQ<SearchNode> minPQ = new MinPQ<>();
        minPQ.insert(searchNode);

        Board twinBoard = initial.twin();
        // SearchNode prevTwinSearchNode = null;
        SearchNode searchTwinNode = new SearchNode(twinBoard, null, 0);
        MinPQ<SearchNode> minTwinPQ = new MinPQ<>();
        minTwinPQ.insert(searchTwinNode);

        // int step = 0; // solution trace step - uncomment for debug

        // main solver iteration cycle:
        // continues until a search node, either of original board or twin, is equal to goal board or until the queue is empty.
        while (!minPQ.isEmpty() && !minTwinPQ.isEmpty()) {

            // print PQ staff (all search nodes) before dequeue - for debug
            // printPQ(minPQ, searchNode, step, "*******************  PQ staff (Original board)");

            searchNode = minPQ.delMin();
            if (searchNode.board.isGoal()) break;
            // Original board:
            // collect all neighbors of the current board, create corresponding search node and add them to PQ
            for (Board board : searchNode.board.neighbors()) {
                SearchNode neighborNode = new SearchNode(board, searchNode, searchNode.moveNmbr + 1);
                // proceed while there is a neighbor (at least one) with equal or higher priority to the current searchnode (if less,- something is wrong!),
                // - goal node has the lowest priority than any nodes in the PQ
                if (searchNode.priorityManhattan <= neighborNode.priorityManhattan) {
                    // critical optimization:
                    // don’t enqueue a neighbor if its board is the same as the board of the previous search node.
                    if (searchNode.prevNode != null) { // case for the initial board
                        if (!searchNode.prevNode.board.equals(neighborNode.board))
                            minPQ.insert(neighborNode);
                    } else minPQ.insert(neighborNode);
                }
            }

            // print PQ staff (all search nodes) before dequeue - for debug
            // printPQ(minTwinPQ, searchTwinNode, step, "*******************  PQ staff (Twin board)");

            searchTwinNode = minTwinPQ.delMin();
            if (searchTwinNode.board.isGoal()) break;
            // Twin board:
            // collect all neighbors of the current board, create corresponding search node and add them to PQ
            for (Board board : searchTwinNode.board.neighbors()) {
                SearchNode neighborNode = new SearchNode(board, searchTwinNode, searchTwinNode.moveNmbr + 1);
                // proceed while there is a neighbor (at least one) with equal or higher priority to the current searchnode (if less,- something is wrong!),
                // - goal node has the lowest priority than any nodes in the PQ
                if (searchTwinNode.priorityManhattan <= neighborNode.priorityManhattan) {
                    // critical optimization:
                    // don’t enqueue a neighbor if its board is the same as the board of the previous search node.
                    if (searchTwinNode.prevNode != null) { // case for the initial board
                        if (!searchTwinNode.prevNode.board.equals(neighborNode.board))
                            minTwinPQ.insert(neighborNode);
                    } else minTwinPQ.insert(neighborNode);
                }
            }
            // step++; // for debug
        }

        if (searchNode.board.isGoal()) {
            goalSearchNode = searchNode; // keep the last processed search node (goal) to reconstruct the solution (in case it's resolved via original board)
        }
        else { // to meet autograder requirements: if unsolvable - # of moves made = -1;
            goalSearchNode = new SearchNode(searchNode.board, searchNode.prevNode, -1);
        }
        /* uncomment next line for debug */
        // goalSearchTwinNode = searchTwinNode; // keep the last processed search node to reconstruct the solution (in case it's resolved via twin board)
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return goalSearchNode.board.isGoal();
    }

    // min number of moves to solve initial board
    public int moves() {
        return goalSearchNode.moveNmbr;
    }

    // sequence of boards (beginning from original one) in a shortest solution
    public Iterable<Board> solution() {
        if (goalSearchNode.board.isGoal())
            return solutionTrack(goalSearchNode);
        return null; // to meet autograder requirements in case - no solution for original board
    }

    /* use this method for debug */
    // sequence of boards (beginning from twin of original board) in a shortest solution
    // private Iterable<Board> solutionTwin() {
    //     return solutionTrack(goalSearchTwinNode);
    // }

    private Iterable<Board> solutionTrack(SearchNode goalNode) {
        if (goalNode == null) throw new IllegalArgumentException("Solution reconstruction failure - goal search node is null.");
        Stack<Board> stack = new Stack<>();
        SearchNode currNode = goalNode;
        while (currNode != null) {
            stack.push(currNode.board);
            currNode = currNode.prevNode;
        }
        return stack;
    }

    // print MinPQ stuff - method for debug purpose
    /*
    private void printPQ(MinPQ<SearchNode> minPQ, SearchNode currSearchNode, int step, String title) {
        StdOut.println(title);
        StdOut.println("Current search node: ");
        StdOut.println("Step " + step + ": " + "priority  = " + currSearchNode.priorityManhattan); // manhattan distance + moves made
        StdOut.print("        moves     = " + currSearchNode.moveNmbr);                          // moves made
        StdOut.print("        manhattan = " + currSearchNode.board.manhattan());                 // manhattan distance
        StdOut.print("        humming   = " + currSearchNode.board.hamming());                   // humming distance
        StdOut.println();
        StdOut.println(currSearchNode.board);

        for (SearchNode node : minPQ) {
            StdOut.println("Step " + step + ": " + "priority  = " + node.priorityManhattan); // manhattan distance + moves made
            StdOut.print("        moves     = " + node.moveNmbr);                          // moves made
            StdOut.print("        manhattan = " + node.board.manhattan());                 // manhattan distance
            StdOut.print("        humming   = " + node.board.hamming());                   // humming distance
            StdOut.println();
            StdOut.println(node.board);
        }
    }
     */
    private static class SearchNode implements Comparable<SearchNode> {
        private final Board board;                // search node of the game
        private final SearchNode prevNode;        // previous search node
        private final int moveNmbr;               // number of moves made so far to get to the search node
        private final int priorityManhattan;
        private final int distanceManhattan;

        private SearchNode(Board currBoard, SearchNode prevSearchNode, int movesMade) {
            board = currBoard;
            prevNode = prevSearchNode; // make a linked-list of all dequeued search nodes to reconstruct the solution -
                                       // chase the pointers all the way back to the initial search node
            moveNmbr = movesMade;
            distanceManhattan = board.manhattan();            // calc & cache the value to avoid extra cost during cycling calculation
            priorityManhattan = distanceManhattan + moveNmbr;
        }

        /**
         * Compares this object with the specified object for order.
         * @param that the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object is less than,
         * equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it from being
         *                              compared to this object.
         */
        @Override
        public int compareTo(SearchNode that) {
            if (this.priorityManhattan < that.priorityManhattan) return -1;
            else if (this.priorityManhattan > that.priorityManhattan) return +1;
            else if (this.distanceManhattan < that.distanceManhattan) return -1; // tie breaks by manhattan distance
            else if (this.distanceManhattan > that.distanceManhattan) return +1;
            // else if (this.board.hamming() < that.board.hamming()) return -1;  // using humming() violates assignment requirements
            // else if (this.board.hamming() > that.board.hamming()) return +1;
            else return 0;
        }
    }

    // test client
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        String filename = args[0];
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        StdOut.println("=> " + filename);
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible. Resolved via twin board:");
            // for (Board board : solver.solutionTwin())
            //     StdOut.println(board);
        }
        else {
            StdOut.println("Solved!");
            StdOut.println(filename + ": minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

    }
}
