/* *****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: 28.08.2019
 *  Description: double-ended queue that supports adding and removing items
 *               from either the front or the back of the data structure;
 *               based on code examples in R. Sedgewick "Algorithm, 4-th edition"
 *  Programme assignment from Coursera, Algorithm Part I (R. Sedgewick, K. Wayne)
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node firstNode; // link to least recently added node
    private Node lastNode;  // link to most recently added node
    private int n;          // number of items on the queue

    private class Node
    {  // nested class to define nodes
        Item item;
        Node next;
        Node prior;
    }

    // construct an empty deque
    public Deque() {
        firstNode = null;
        lastNode = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null)
            throw new IllegalArgumentException("null - is illegal argument of 'add' operation");
        Node oldFirst = firstNode;
        firstNode = new Node();
        firstNode.item = item;
        firstNode.next = oldFirst;
        if (isEmpty()) {
            lastNode = firstNode;
            firstNode.prior = null;
        }
        else {
            oldFirst.prior = firstNode;
        }
        n++;

    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null)
            throw new IllegalArgumentException("null - is illegal argument of 'add' operation");
        Node oldlast = lastNode;
        lastNode = new Node();
        lastNode.item = item;
        lastNode.next = null;
        if (isEmpty()) {
            firstNode = lastNode;
            lastNode.prior = null;
        }
        else {
            oldlast.next = lastNode;
            lastNode.prior = oldlast;
        }
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        Item item = firstNode.item;
        firstNode = firstNode.next;
        n--;
        if (isEmpty()) lastNode = null;
        else firstNode.prior = null;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        Item item = lastNode.item;
        lastNode = lastNode.prior;
        n--;
        if (isEmpty()) {
            firstNode = null; }
        else {
            lastNode.next = null; }
        return item;
    }
    /**
     * Returns a string representation of this dequeue.
     * @return the sequence of items in the dequeue, separated by spaces
     */
    private String toStringDeque() {
        StringBuilder s = new StringBuilder();
        for (Item item : this)
            s.append(item + " ");
        return s.toString();
    }
    // return an iterator over items in order from front to back
    public Iterator<Item> iterator()
    { return new ListIterator(); }

    private class ListIterator implements Iterator<Item> {
        private Node current = firstNode;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported");
        }
    }
    // unit testing (required)
    public static void main(String[] args) {
        // get k as number of strings to be manipuled
        int k = Integer.parseInt(args[0]);
        // total number of strings from standard input
        int t = 0;

        StdOut.println("Dequeue with a sequence of strings from standard input using StdIn.readString()");
        Deque<String> dequeString = new Deque<>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            dequeString.addLast(s);
            t++;
        }
        StdOut.print(dequeString.toStringDeque());
        StdOut.println();

        StdOut.println("Dequeue with "+ t + " random integers between 0 and 99");
        Deque<Integer> dequeInt = new Deque<>();
        for (int i = 0; i < t; i++) {
            dequeInt.addFirst(StdRandom.uniform(100));
        }
        StdOut.print(dequeInt.toStringDeque());
        StdOut.println();

        StdOut.println("Remove all elements from dequeue with integers via removeLast() and add them to dequeue with strings via addFirst()");
        int num = dequeInt.size();
        for (int j = 0; j < num; j++)
            dequeString.addFirst(dequeInt.removeLast().toString());
        StdOut.print(dequeString.toStringDeque());
        StdOut.println();
        if (dequeInt.isEmpty()) StdOut.println("Dequeue with integers is empty");
        else StdOut.println("Dequeue with integers is not empty - check!");

        StdOut.println("Remove all elements from dequeue with strings via removeFirst() and removeLast() sametime");
        while (!dequeString.isEmpty())
            StdOut.print(dequeString.removeFirst() + "-" + dequeString.removeLast() + " ");
        StdOut.println();
        if (dequeString.isEmpty()) StdOut.println("Dequeue with strings is empty");
        else StdOut.println("Dequeue with strings is not empty - check!");

        if (k >= t) k %= t;
        StdOut.println("Dequeue with "+ k + " random integers between 0 and 99");
        for (int i = 0; i < k; i++)
           dequeInt.addLast(StdRandom.uniform(100));
        StdOut.print(dequeInt.toStringDeque());
        StdOut.println();

        StdOut.println("Dequeue with a sequence of strings from dequeue of integers via iterator");
        Iterator<Integer> iterator = dequeInt.iterator();
        while (iterator.hasNext())
            dequeString.addFirst("'" + iterator.next() + "'");
        StdOut.print(dequeString.toStringDeque());
        StdOut.println();

    }
}
