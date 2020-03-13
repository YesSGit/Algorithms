/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: 11.09.2019
 *  Description:
 *  The {@code Permutation} class takes an integer k as a command-line argument;1
 *  reads a sequence of strings from standard input using StdIn.readString();
 *  and prints exactly k of them, uniformly at random
 *  <p>
 *  Programme assignment from Coursera course "Algorithm Part I" (R. Sedgewick, K. Wayne)
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

public class Permutation {
    public static void main(String[] args) {
        // get k as number of strings taken to be processed
        int k = Integer.parseInt(args[0]);
        int n = 0;

        RandomizedQueue<String> rndmQueue = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            // n++;
            rndmQueue.enqueue(StdIn.readString());
        }

        Iterator<String> iteratorString = rndmQueue.iterator();
        while (iteratorString.hasNext() && n < k) {
            StdOut.println(iteratorString.next());
            n++;
        }

        /* chellenge: use only one Deque or RandomizedQueue object of maximum size at most k */
        // while (!StdIn.isEmpty()) {
        //     n++;
        //     if (n > k) {
        //         rndmQueue.enqueue(StdIn.readString());
        //         rndmQueue.dequeue();
        //     }
        //     else {
        //         rndmQueue.enqueue(StdIn.readString());
        //     }
        // }
        // for (String s : rndmQueue)
        //     StdOut.println(s);

    }
}