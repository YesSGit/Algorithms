/* *****************************************************************************
 *  Name: R. Sedgewick, K. Wayne
 *  Date: 2011
 *  ALGORITHM 1.4 Bag (This Bag implementation maintains a linked list of the
 *  items provided in calls to add())
 **************************************************************************** */
package com.uzviz;

import java.util.Iterator;
public class Bag<Item> implements Iterable<Item> {
    private Node first;  // first node in list
    private int N;      // number of items


    private class Node {
        Item item;
        Node next;
    }

    public boolean isEmpty() {  return first == null; }  // Or: N == 0.
    public int size()        {  return N; }

    public void add(Item item) {  // same as push() in LinkedStack
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        N++;
    }

    // iterator() implementation.
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

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
}