/* *****************************************************************************
 *  Name: S.Yeshchenko (based on Algorithm 4-th ed., R. Sedgewick, K. Wayne )
 *  Date: Dec'2019
 *  Description: Generic min priority queue implementation with a binary heap.
 *  Dependencies: StdIn.java StdOut.java
 *
 *  Coursera, Algorithm I part: Priority queues
 ******************************************************************************/
package com.uzviz;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class MinPQ<Key extends Comparable<Key>> {
    private Key[] minPQ;                // store items at indices 1 to n
    private int n;                      // number of items on priority queue
    private Comparator<Key> comparator; // optional comparator

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public MinPQ(int initCapacity) {
        minPQ = (Key[]) new Comparable[initCapacity + 1];
        n = 0;
    }
    /**
     * Initializes an empty priority queue.
     */
    public MinPQ () {
        this(1);
    }
    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this priority queue
     * @param  comparator the order in which to compare the keys
     */
    public MinPQ(int initCapacity, Comparator comparator) {
        this.comparator = comparator;
        minPQ = (Key[]) new Comparable[initCapacity + 1];
        n = 0;
    }
    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order in which to compare the keys
     */
    public MinPQ(Comparator comparator) {
        this(1, comparator);
    }
    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    }
    /**
     * Returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return minPQ[1];
    }
    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > n;
        Key[] tmpPQ = (Key[]) new Comparable[capacity];
        for (int i = 1; i <= n; i++) tmpPQ[i] = minPQ[i];
//        System.arraycopy(minPQ, 1, tmpPQ, 1, capacity);
        minPQ = tmpPQ;
    }
    /**
     * Adds a new key to this priority queue.
     *
     * @param  key the new key to add to this priority queue
     */
    public void insert(Key key) {
        // double size of the array if necessary
        if (n == minPQ.length - 1) resize(2 * minPQ.length);
        // add x, and percolate('heapify') it up to maintain heap invariant
        minPQ[++n] = key;
        swim(n);
    }

    /**
     * Removes and returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key delMin(){
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        Key min = minPQ[1];
        exch(1, n--);
        sink(1);
        minPQ[n + 1] = null;  // to avoid loitering and help with garbage collection
        if ((n > 0) && (n == (minPQ.length - 1) / 4)) resize(minPQ.length / 2);
        return min;
    }

    /***************************************************************************
     * Helper functions to restore the heap invariant.
     ***************************************************************************/
    // Peter principle. Node promoted to level of incompetence.
    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            // children of node k are 2*k and 2*k+1
            if (j < n && greater(j, j + 1)) j++;
            if (!greater(k, j)) break;
            // exchange key in parent with key in larger child
            exch(k, j);
            k = j;
        }
    }
    // Power struggle. Better subordinate promoted.
    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            // Exchange key in child with key in parent.
            exch(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty(){
        return n == 0;
    }

    /***************************************************************************
     * Helper functions for compares and swaps.
     ***************************************************************************/
    private void exch(int i, int j) {
        Key tmpKey = minPQ[i];
        minPQ[i] = minPQ[j];
        minPQ[j] = tmpKey;
    }

    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) minPQ[i]).compareTo(minPQ[j]) > 0;
        }
        else {
            return comparator.compare(minPQ[i], minPQ[j]) > 0;
        }
    }

    /**
     * Unit tests the {@code MaxPQ} data type.
     * test input: "P Q E - X A M - P L E -"
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        MinPQ<String> minPQ = new MinPQ<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-")) minPQ.insert(item);
            else if (!minPQ.isEmpty()) StdOut.print(minPQ.delMin() + " ");
        }
        StdOut.println("(" + minPQ.size() + " left on priority queue)");
    }

}
