/* *****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: Feb'2020
 *  Description:
 **************************************************************************** */
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class Solver {
    private SearchNode prevSearchNode, prevTwinSearchNode;
    private SearchNode searchNode, searchTwinNode;
    private final Iterable<Board> solutionOrig, solutionTwin;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("No valid argument to the Solver constructor.");

        int movesOrig = 0;
        int movesTwin = 0;
        prevSearchNode = null;
        searchNode = new SearchNode(initial, prevSearchNode, movesOrig);
        MinPQ<SearchNode> minPQ = new MinPQ<>();
        minPQ.insert(searchNode);

        Board twinBoard = initial.twin();
        prevTwinSearchNode = null;
        searchTwinNode = new SearchNode(twinBoard, prevTwinSearchNode, movesTwin);
        MinPQ<SearchNode> minTwinPQ = new MinPQ<>();
        minTwinPQ.insert(searchTwinNode);

        int step = 0; // solution trace step

        // main solver iteration cycle:
        // continues until a search node, either of original board or twin, is equal to goal board or until the queue is empty.
        boolean proceedOriginal  = true;
        boolean proceedTwin = true;

        while (!minPQ.isEmpty() && !minTwinPQ.isEmpty()) {

                prevSearchNode = searchNode; // keep previous `search node` for critical optimazation
                // print PQ staff (all search nodes) before dequeue - for debug
                printPQ(minPQ, searchNode, step, "*******************  PQ staff (Original board)");

                searchNode = minPQ.delMin();
                if (searchNode.board.isGoal()) break;

                movesOrig = searchNode.moveNmbr;
                movesOrig++;
                // Original board:
                // collect all neighbors of the current board, create corresponding search node and add them to PQ
                for (Board board : searchNode.board.neighbors()) {
                    SearchNode neighborNode = new SearchNode(board, searchNode, movesOrig);
                    // proceed' while there is a neighbor (at least one) with equal priority to the current searchnode (if less,- something is wrong!),
                    // - goal node has the lowest priority than any nodes in the PQ
                    if (searchNode.priorityManhattan <= neighborNode.priorityManhattan) {
                        // critical optimization:
                        // don’t enqueue a neighbor if its board is the same as the board of the previous search node.
                        if (!prevSearchNode.board.equals(neighborNode.board))
                            minPQ.insert(neighborNode);
                    }
                }

                prevTwinSearchNode = searchTwinNode; // keep previous `search node` for critical optimazation
                // print PQ staff (all search nodes) before dequeue - for debug
                printPQ(minTwinPQ, searchTwinNode, step, "*******************  PQ staff (Twin board)");

                searchTwinNode = minTwinPQ.delMin();
                if (searchTwinNode.board.isGoal()) break;

                movesTwin = searchTwinNode.moveNmbr;
                movesTwin++;
                // Twin board:
                // collect all neighbors of the current board, create corrsponding search node and add them to PQ
                for (Board board : searchTwinNode.board.neighbors()) {
                    SearchNode neighborNode = new SearchNode(board, searchTwinNode, movesTwin);
                    // `proceed' while there is a neighbor (at least one) with equal priority to the current searchnode (if less,- something is wrong!),
                    // - goal node has the lowest priority than any nodes in the PQ
                    if (searchTwinNode.priorityManhattan <= neighborNode.priorityManhattan) {
                        // critical optimization:
                        // don’t enqueue a neighbor if its board is the same as the board of the previous search node.
                        if (!prevTwinSearchNode.board.equals(neighborNode.board))
                            minTwinPQ.insert(neighborNode);
                    }
                }
          step++;
        }
        solutionOrig = solutionTrack(searchNode);
        solutionTwin = solutionTrack(searchTwinNode);
    }

    // is the initial board solvable?
    public boolean isSolvable() {

        return searchNode.board.isGoal();
    }

    // min number of moves to solve initial board
    public int moves() {
        return searchNode.moveNmbr;
    }

    // sequence of boards (beginning from original one) in a shortest solution
    public Iterable<Board> solution() {
        return solutionOrig;
    }
    // sequence of boards (beginning from twin board) in a shortest solution
    private Iterable<Board> solutionTwin() {
        return solutionTwin;
    }

    private Iterable<Board> solutionTrack(SearchNode goalSearchNode) {
        if (goalSearchNode == null) throw new IllegalArgumentException("Solution reconstruction failure - goal search node is null.");
        Stack<Board> stack = new Stack<>();
        SearchNode currNode = goalSearchNode;
        while (currNode != null) {
            stack.push(currNode.board);
            currNode = currNode.prevNode;
        }
        return stack;
    }

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;                // search node of the game
        private SearchNode prevNode;              // previous search node
        private final int moveNmbr;               // number of moves made so far to get to the search node
        private final int priorityManhattan;

        private SearchNode(Board currBoard, SearchNode prevSearchNode, int movesMade) {
            board = currBoard;
            prevNode = prevSearchNode; // make a linked-list of all dequeued search nodes to reconstruct the solution -
                                       // chase the pointers all the way back to the initial search node
            moveNmbr = movesMade;
            priorityManhattan = board.manhattan() + moveNmbr;
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
            else if (this.board.manhattan() < that.board.manhattan()) return -1; // tie breaks by manhattan distance
            else if (this.board.manhattan() > that.board.manhattan()) return +1;
            else if (this.board.hamming() < that.board.hamming()) return -1;
            else if (this.board.hamming() > that.board.hamming()) return +1;
            else return 0;
        }
    }

    // helper functions
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
            StdOut.println("No solution possible");
            for (Board board : solver.solutionTwin())
                StdOut.println(board);
        }
        else {
            StdOut.println("Solved!");
            StdOut.println(filename + ": minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

    }
}
