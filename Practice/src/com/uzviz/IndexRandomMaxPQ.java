/* *****************************************************************************
 *  Name: S.Yeshchenko (based on Algorithm 4-th ed., R. Sedgewick, K. Wayne )
 *  Date: Jan'2020
 *  Description: Generic maximum-oriented indexed PQ implementation
 *               using a binary heap.
 *               IndexRandomMaxPQ.java class allows clients to refer to items that are already on the priority queue
 *  Dependencies: StdIn.java StdOut.java
 *
 *  Data files:
 *
 *  Coursera, Algorithm I part: Priority queues
 ******************************************************************************/
package com.uzviz;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 *  The {@code IndexRandomMaxPQ} class represents an indexed priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-maximum</em>
 *  operations, along with <em>delete</em> and <em>change-the-key</em>
 *  methods. In order to let the client refer to items on the priority queue,
 *  an integer between {@code 0} and {@code maxN - 1}
 *  is associated with each key—the client
 *  uses this integer to specify which key to delete or change.
 *  It also supports methods for peeking at a maximum key,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  Additional two methods <em>sample</em> and <em>delRandom</em> return a key
 *  that is chosen uniformly at random among the remaining keys, with the
 *  latter method is also removing that key.
 *  <p>
 *  This implementation uses a <em>binary heap</em> along with an
 *  array to associate keys with integers in the given range.
 *  The <em>insert</em>, <em>delete-the-maximum</em>, <em>delete</em>,
 *  <em>change-key</em>, <em>decrease-key</em>, and <em>increase-key</em>,
 *  <em>delRandom</em> operations take &Theta;(log <em>n</em>) time in the worst case,
 *  where <em>n</em> is the number of elements in the priority queue,
 *  while <em>sample</em> - takes constant time.
 *  Construction takes time proportional to the specified capacity.
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  @param <Key> the generic type of key on this priority queue
 */
public final class IndexRandomMaxPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int[] maxPQ;                // priority queue based on binary heap using 1-based indexing
    private int[] maxQP;                // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private int n;                      // number of items on PQ
    private int maxN;                   // maximum number of elements on PQ
    private Key[] keys;                 // keys[i] = priority of i
    private Comparator<Key> comparator; // optional comparator

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     *
     * @param  maxN the keys on this priority queue are index from {@code 0} to {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public IndexRandomMaxPQ(int maxN) {
        if (maxN < 0) throw new IllegalArgumentException("NUmber of keys in the priority queue has to be >= 0");
        this.maxN = maxN;
        n = 0;
        keys = (Key[]) new Comparable[maxN + 1];  // make this of length maxN?? (note from R.S., K.W.)
        maxPQ = new int[maxN + 1];
        maxQP = new int[maxN + 1];                // make this of length maxN??
        for(int i = 0; i <= maxN; i++)
            maxQP[i] = -1;
    }
    /**
     * Initializes an empty priority queue.
     */
    public IndexRandomMaxPQ() {
        this(1);
    }
    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  maxN the keys on this priority queue are index from {@code 0} to {@code maxN - 1}
     * @param  comparator the order in which to compare the keys
     */
    public IndexRandomMaxPQ(int maxN, Comparator comparator) {
        if (maxN < 0) throw new IllegalArgumentException("NUmber of keys in the priority queue has to be >= 0");
        this.comparator = comparator;

        this.maxN = maxN;
        n = 0;
        keys = (Key[]) new Comparable[maxN + 1];
        maxPQ = new int[maxN + 1];
        maxQP = new int[maxN + 1];
        for(int i = 0; i <= maxN; i++)
            maxQP[i] = -1;
    }
    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order in which to compare the keys
     */
    public IndexRandomMaxPQ(Comparator comparator) {
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

    // helper function to double the size of the array of items, priority queue and inverse queue
    // in case insert operation reached the initially constructed maxN
    // !!! under development !!!
    private void resize(int maxN) {
        assert maxN > n;
        this.maxN = maxN;  // update maxN by a new maximum number of elements on PQ

        Key[] tmpKeys = (Key[]) new Comparable[maxN + 1];
        int[] tmpPQ = new int[maxN +1];
        int[] tmpQP = new int[maxN +1];

        for (int i = 0; i < n; i++) {
            tmpKeys[i] = keys[maxPQ[i + 1]];
            tmpPQ[i] = maxPQ[i];
            tmpQP[i] = maxQP[i];
        }
        keys = tmpKeys;
        maxPQ = tmpPQ;
        maxPQ = tmpQP;
        for(int i = n; i <= maxN; i++)  // ensure that all extra new elements of inverse queue are initialized by -1
            maxQP[i] = -1;
    }
    /**
     * Associate key with index i.
     *
     * @param  i an index
     * @param  key the key to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item
     *         associated with index {@code i}
     */
    public void insert(int i, Key key) {
        validateIndex(i);
        if (contains(i)) throw new IllegalArgumentException("index: "+ i + " is already in the priority queue");

        // double size of the array if necessary (has to be worked out how to implement????)
//        if (n == this.maxN) resize(2 * maxPQ.length);

        // Associate key with index i, and percolate it up ('heapify-up') to maintain heap invariant
        n++;
        maxPQ[n] = i;
        maxQP[i] = n;
        keys[i] = key;
        swim(n);
    }
    /**
     * Remove the key on the priority queue associated with index {@code i}.
     *
     * @param  i the index of the key to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void delete(int i) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = maxQP[i];
        exch(index, n--);
        swim(index);
        sink(index);
        keys[i] = null;     // to avoid loitering and help with garbage collection
        maxQP[i] = -1;      // delete
        maxPQ[n + 1] = -1;  // not needed
    }
    /**
     * Removes a maximum key and returns its associated index.
     *
     * @return an index associated with a maximum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int delMax(){
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");

        int max = maxPQ[1];
        exch(1, n--);
        sink(1);

        maxQP[max] = -1;      // delete
        keys[max] = null;     // to avoid loitering and help with garbage collection
        maxPQ[n + 1] = -1;    // not needed
        return max;
    }

    /**
     * Returns an index associated with a maximum key.
     *
     * @return an index associated with a maximum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int maxIndex() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return maxPQ[1];
    }
    /**
     * Returns a maximum key.
     *
     * @return a maximum key
     * @throws NoSuchElementException if this priority queue is empty
     */
   public Key maxKey() {
       if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
       return keys[maxPQ[1]];
   }
    /**
     * Returns the key associated with index {@code i}.
     *
     * @param  i the index of the key to return
     * @return the key associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
     public Key keyOf(int i) {
         validateIndex(i);
         if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
         else return keys[i];
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

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(int i) {
        validateIndex(i);
        return maxQP[i] != -1;
    }

    /**
     * Change the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to change
     * @param  key change the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void changeKey(int i, Key key) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        keys[i] = key;
        swim(maxQP[i]);
        sink(maxQP[i]);
    }
    /**
     * Increase the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to increase
     * @param  key increase the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code key <= keyOf(i)}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void increaseKey(int i, Key key) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (!less(i, key)) throw new IllegalArgumentException("Calling increaseKey() with a key that is not strictly less than the key in the priority queue");
        else {
            keys[i] = key;
            swim(maxQP[i]);
        }
    }
    /**
     * Decrease the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to decrease
     * @param  key decrease the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code key >= keyOf(i)}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void decreaseKey(int i, Key key) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (less(i, key)) throw new IllegalArgumentException("Calling increaseKey() with a key that is not strictly greater than the key in the priority queue");
        else {
            keys[i] = key;
            sink(maxQP[i]);
        }
    }
    /**
     * Return a key that is chosen uniformly at random among the remaining keys
     *
     * @return the key associated with index {@code i} which is chosen uniformly
     * at random among number {@code n} of items on PQ
     */
    public Key sample() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue is underflow");
        int i = maxPQ[StdRandom.uniform(n)];
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        return keys[i];
    }
    /**
     * Return and remove a key that is chosen uniformly at random among the remaining keys
     *
     * @return the key associated with index {@code i} which is chosen uniformly
     * at random among number {@code n} of items on PQ
     */
    public Key delRandom() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue is underflow");
        int i = maxPQ[StdRandom.uniform(n)];
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        Key tmpKey = keys[i];
        delete(i);
        return tmpKey;
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

    /***************************************************************************
     * Helper functions for compares and swaps.
     ***************************************************************************/
    private void exch(int i, int j) {
        int tmpIndex = maxPQ[i];
        maxPQ[i] = maxPQ[j];
        maxPQ[j] = tmpIndex;
        maxQP[maxPQ[i]] = i;
        maxQP[maxPQ[j]] = j;
    }

    // compare keys on PQ (that's why maxPQ[] is used as index) for sink and swim operations
    private boolean less(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) keys[maxPQ[i]]).compareTo(keys[maxPQ[j]]) < 0;
        }
        else {
            return comparator.compare(keys[maxPQ[i]], keys[maxPQ[j]]) < 0;
        }
    }
    // compare the key associated with index (the key is on PQ) with the new key of specified value
    // for increaseKey and decreaseKey operations
    private boolean less(int i, Key key) {
        if (comparator == null) {
            return ((Comparable<Key>) keys[i]).compareTo(key) < 0;
        }
        else {
            return comparator.compare(keys[i], key) < 0;
        }
    }

    // throw an IllegalArgumentException if i is an invalid index
    private void validateIndex(int i) {
        if (i < 0) throw new IllegalArgumentException("index is negative: " + i);
        if (i > maxN) throw new IllegalArgumentException("index >= capacity: " + i);
    }

    /**
     * Returns an iterator that iterates over the keys on the
     * priority queue in descending order.
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in descending order
     */
    public Iterator<Integer> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Integer> {
        // create a new private PQ
        private IndexRandomMaxPQ<Key> copyPQ;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            copyPQ = new IndexRandomMaxPQ<>(maxPQ.length - 1);
            for (int i = 1; i <= n; i++)
                copyPQ.insert(maxPQ[i], keys[maxPQ[i]]);
        }

        @Override
        public boolean hasNext() { return (!copyPQ.isEmpty()); }
        @Override
        public void remove() { throw new UnsupportedOperationException(); }
        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copyPQ.delMax();
        }

    }

    /**
     * Unit tests the {@code IndexMaxPQ} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String[] strings = { "a)", "it", "was", "the", "best", "of", "times", "b)", "it", "was", "the", "worst" };
        StdOut.println("Initial array of items: ");
        for (int i = 0; i < strings.length; i++) StdOut.print(strings[i] + " ");
        StdOut.println();

        // insert a bunch of strings
        IndexRandomMaxPQ<String> pq = new IndexRandomMaxPQ<String>(strings.length);
        for (int i = 0; i < strings.length; i++) pq.insert(i, strings[i]);
        // print each key using the iterator
        StdOut.println("Priority queue is built up on binary heap: ");
        for (int i : pq) StdOut.println(i + " " + strings[i]);
        StdOut.println("PQ size is: " + pq.size());

        StdOut.println();
        pq.changeKey(0, "a)NewItem");
        StdOut.println("First item changed to 'a)NewItem' using changeKey operation");
        // print PQ
        for (int i : pq) StdOut.println(i + " " + pq.keyOf(i));

        StdOut.println("Sample  item taken uniformly at random): " + pq.sample());
        StdOut.println();
        StdOut.println("Sample item taken uniformly at random and removed: " + pq.delRandom());
        StdOut.println();
        StdOut.println("Sample item taken uniformly at random and removed: " + pq.delRandom());
        StdOut.println("PQ size is (after removing): " + pq.size());
        // print PQ
        for (int i : pq) StdOut.println(i + " " + pq.keyOf(i));

        // increase or decrease the key
        for (int i : pq) {
            if (StdRandom.uniform() < 0.5)
                pq.increaseKey(i, pq.keyOf(i) + pq.keyOf(i));
            else
                pq.decreaseKey(i, pq.keyOf(i).substring(0, 1));
        }
        StdOut.println();
        StdOut.println("All items on PQ were altered randomly by increaseKey or decreaseKey operation: ");
        // print PQ
        for (int i : pq) StdOut.println(i + " " + pq.keyOf(i));

        StdOut.println("Sample item taken uniformly at random: " + pq.sample());
        StdOut.println();
        StdOut.println("Sample item taken uniformly at random and removed: " + pq.delRandom());
        StdOut.println();
        StdOut.println("Sample item taken uniformly at random and removed: " + pq.delRandom());
        StdOut.println();


        // delete and print each key
        while (!pq.isEmpty()) {
            String key = pq.maxKey();
            int i = pq.delMax();
            StdOut.println(i + " " + key);
        }
        StdOut.println("All items deleted. PQ size: " + pq.size());
        StdOut.println();

        // reinsert the same strings
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }
        StdOut.println("Priority queue is rebuilt with the same items.");
        StdOut.println();

        // delete items in PQ in random order
        int[] perm = new int[strings.length];
        for (int i = 0; i < strings.length; i++)
            perm[i] = i;
        StdRandom.shuffle(perm);
        for (int i = 0; i < perm.length; i++) {
            String key = pq.keyOf(perm[i]);
            pq.delete(perm[i]);
            StdOut.println(perm[i] + " " + key);
        }
        StdOut.println("All items deleted in random order. PQ size: " + pq.size());
    }
}