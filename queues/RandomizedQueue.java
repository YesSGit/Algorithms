/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: 09.09.2019
 *  Description:
 *  The {@code RandomizedQueue} class represents a queue of generic items.
 *  It supports <em>enqueue</em> and <em>dequeue</em> operations, along with methods
 *  for sample the random item without removing it, testing if the queue is empty,
 *  and iterating through the items in random order.
 *  <p>
 *  Programme assignment from Coursera course "Algorithm Part I" (R. Sedgewick, K. Wayne)
 *  Implementation is based on code examples of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] array;                     // queue of items
    private int n;                            // number of items in queue
    private boolean needShuffle;              // trigger to shuffle array


    // construct an empty randomized queue
    public RandomizedQueue() {
        array = (Item[]) new Object[1];
        n = 0;
        needShuffle = false;
    }

    // move Queue to a new array of capacity size
    private void resize(int capacity) {
        Item[] tempArray = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++)
            tempArray[i] = array[i];
         array = tempArray;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item to the end of queue
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("null - is illegal argument of 'enqueue' operation");
        if (array.length == n) resize(2 * array.length);
        array[n++] = item;
        if (!needShuffle) needShuffle = true;
    }

    // remove and return a random item from queue
    // shuffle the array once per serie of denqueue operations
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        if (needShuffle) {
            StdRandom.shuffle(array, 0, n);
            needShuffle = false;
        }

        Item item = array[--n];
        array[n] = null;
        if (n > 0 && n == array.length / 4) resize(array.length/2);

        return item;
    }
    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return array[StdRandom.uniform(n)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomArrayIterator();
    }

    private class RandomArrayIterator implements Iterator<Item> {
        private int i;
        private final Item[] arrayCopy;
        // private final boolean iteratorDirection;

        public RandomArrayIterator() {
            i = n;
            arrayCopy =  (Item[]) new Object[n];
            for (int j = 0; j < n; j++) {
                arrayCopy[j] = array[j];
            }
            // shaffle array before traversing to ensure random sequence of items
            StdRandom.shuffle(arrayCopy);
        }

        public boolean hasNext() {
            return i > 0;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return arrayCopy[--i];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private String toStringRndmQue() {
        StringBuilder s = new StringBuilder();
        for (Item item : this) s.append(item + " ");
        return s.toString();
    }

    // unit testing (required)
    public static void main(String[] args) {
        // get k as number of strings to be manipuled
        int k = Integer.parseInt(args[0]);
        // total number of strings from standard input
        int t = 0;

        StdOut.println("Enqueue with a sequence of strings from standard input using StdIn.readString():");
        RandomizedQueue<String> rndmQueString = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            rndmQueString.enqueue(StdIn.readString());
            t++;
        }
        StdOut.print(rndmQueString.toStringRndmQue());
        StdOut.println();

        if (k > t) k %= t;
        StdOut.println("sample() a random k items of the queue (assume that 0<=k<=n), without removing them:");
        for (int i = 0; i < k; i++)
            StdOut.print(rndmQueString.sample() + " ");
        StdOut.println();

        StdOut.println("Test of two nested iterators operating independently of one another:");
        RandomizedQueue<Integer> rndmQueInt = new RandomizedQueue<>();
        for (int i = 0; i < k; i++)
            rndmQueInt.enqueue(i);
        Iterator<Integer> iteratorOuter = rndmQueInt.iterator();
        Iterator<Integer> iteratorInner = rndmQueInt.iterator();
        while (iteratorOuter.hasNext()) {
            while (iteratorInner.hasNext())
                StdOut.print(iteratorOuter.next() + "-" + iteratorInner.next() + " ");
        }
        StdOut.println();

        StdOut.println("Test of two nested 'foreach' operating independently of one another:");
        for (Integer a : rndmQueInt) {
            for (Integer b : rndmQueInt)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
       }

        StdOut.println("Sample random items from queue of string and make the queue of integers empty same time:");
        while (!rndmQueInt.isEmpty())
            StdOut.print(rndmQueString.sample() + "-" + rndmQueInt.dequeue() + " ");
        StdOut.println();
        if (rndmQueInt.isEmpty()) StdOut.println("Queue with integers is empty");
        else StdOut.println("Queue with integers is not empty - check!");

    }
}
