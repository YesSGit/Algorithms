/* *****************************************************************************
 *  Name: S.Yeshchenko (based on Algorithm 4-th ed., R. Sedgewick, K. Wayne )
 *  Date: Dec'2019
 *  Description: Generic max priority queue implementation with a binary heap.
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/24pq/tinyPQ.txt
 *
 *  Coursera, Algorithm I part: Priority queues
 ******************************************************************************/

package com.uzviz;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class MaxPQ<Key extends Comparable<Key>> {
    private Key[] maxPQ;                // store items at indices 1 to n
    private int n;                      // number of items on priority queue
    private Comparator<Key> comparator; // optional comparator

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public MaxPQ(int initCapacity) {
        maxPQ = (Key[]) new Comparable[initCapacity + 1];
        n = 0;
    }
    /**
     * Initializes an empty priority queue.
     */
    public MaxPQ () {
        this(1);
    }
    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this priority queue
     * @param  comparator the order in which to compare the keys
     */
    public MaxPQ(int initCapacity, Comparator comparator) {
        this.comparator = comparator;
        maxPQ = (Key[]) new Comparable[initCapacity + 1];
        n = 0;
    }
    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order in which to compare the keys
     */
    public MaxPQ(Comparator comparator) {
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
     * Returns a largest key on this priority queue.
     *
     * @return a largest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return maxPQ[1];
    }
    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > n;
        Key[] tmpPQ = (Key[]) new Comparable[capacity];
        for (int i = 1; i <= n; i++) tmpPQ[i] = maxPQ[i];
//        System.arraycopy(maxPQ, 1, tmpPQ, 1, capacity);
        maxPQ = tmpPQ;
    }
    /**
     * Adds a new key to this priority queue.
     *
     * @param  key the new key to add to this priority queue
     */
    public void insert(Key key) {
        // double size of the array if necessary
        if (n == maxPQ.length - 1) resize(2 * maxPQ.length);
        // add x, and percolate('heapify') it up to maintain heap invariant
        maxPQ[++n] = key;
        swim(n);
    }

    /**
     * Removes and returns a largest key on this priority queue.
     *
     * @return a largest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key delMax(){
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        Key max = maxPQ[1];
        exch(1, n--);
        sink(1);
        maxPQ[n + 1] = null;  // to avoid loitering and help with garbage collection
        if ((n > 0) && (n == (maxPQ.length - 1) / 4)) resize(maxPQ.length / 2);
        return max;
    }

    /***************************************************************************
     * Helper functions to restore the heap invariant.
     ***************************************************************************/
    // Peter principle. Node promoted to level of incompetence.
    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            // children of node k are 2*k and 2*k+1
            if (j < n && less(j, j + 1)) j++;
            if (!less(k, j)) break;
            // exchange key in parent with key in larger child
            exch(k, j);
            k = j;
        }
    }
    // Power struggle. Better subordinate promoted.
    private void swim(int k) {
        while (k > 1 && less(k / 2, k)) {
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
        Key tmpKey = maxPQ[i];
        maxPQ[i] = maxPQ[j];
        maxPQ[j] = tmpKey;
    }

    private boolean less(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) maxPQ[i]).compareTo(maxPQ[j]) < 0;
        }
        else {
            return comparator.compare(maxPQ[i], maxPQ[j]) < 0;
        }
    }

    /**
     * Unit tests the {@code MaxPQ} data type.
     * test input: "P Q E - X A M - P L E -"
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        MaxPQ<String> maxPQ = new MaxPQ<>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-")) maxPQ.insert(item);
            else if (!maxPQ.isEmpty()) StdOut.print(maxPQ.delMax() + " ");
        }
        StdOut.println("(" + maxPQ.size() + " left on priority queue)");
    }
}
