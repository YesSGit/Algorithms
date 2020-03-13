/* *****************************************************************************
 *  Name: R. Sedgewick, K. Wayne
 *  Date: 2011
 *  ALGORITHM 1.3 FIFO queue (linked-list implementation)
 **************************************************************************** */

package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Queue<Item> implements Iterable<Item>
{
    private Node first; // link to least recently added node
    private Node last;  // link to most recently added node
    private int N;      // number of items on the queue
    private class Node
    {  // nested class to define nodes
        Item item;
        Node next; }
    public boolean isEmpty() {  return first == null;  }  // Or: N == 0.
    public int size()        {  return N;  }
    public void enqueue(Item item)
    {  // Add item to the end of the list.
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) first = last;
        else           oldlast.next = last;
        N++;
    }
    public Item dequeue()
    {  // Remove item from the beginning of the list.
        Item item = first.item;
        first = first.next;
        if (isEmpty()) last = null;
        N--;
        return item;
    }
    // iterator() implementation.
    public Iterator<Item> iterator()
    {  return new ListIterator();  }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
        }

        public Item next() {
            Item item = current.item;
            current = current.next;
            return item;
        }
    }
    public static void main(String[] args)
    {
        In in = new In(args[0]);      // input file Queue_input.txt

        // Create a queue and enqueue/dequeue strings.
        Queue<String> q = new Queue<String>();
        while (!in.isEmpty())
        {
            String item = in.readString();
            if (!item.equals("-"))
                q.enqueue(item);
            else if (!q.isEmpty()) StdOut.print(q.dequeue() + " ");
        }
        StdOut.println("(" + q.size() + " left on queue)");
    }
}