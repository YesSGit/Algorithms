/* *****************************************************************************
 *  Name: R. Sedgewick, K. Wayne
 *  Date: 2017
 *  ALGORITHM 1.2 Pushdown stack (linked-list implementation)
 **************************************************************************** */

package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedStack<Item> implements Iterable<Item>
{
    private Node first; // top of stack (most recently added node)
    private int N;      // number of items

    private class Node
    {  // nested class to define nodes
        Item item;
        Node next; }

    /**
     * Initializes an empty stack.
     */
    public LinkedStack() {
        first = null;
        N = 0;
        assert check();
    }
    /**
     * Is this stack empty?
     * @return true if this stack is empty; false otherwise
     */
    public boolean isEmpty() {  return first == null; }  // Or: N == 0.
    /**
     * Returns the number of items in the stack.
     * @return the number of items in the stack
     */
    public int size()        {  return N; }

    /**
     * Adds the item to this stack.
     * @param item the item to add
     */
    public void push(Item item)
    {  // Add item to top of stack.
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        N++;
        assert check();
    }
    /**
     * Removes and returns the item most recently added to this stack.
     * @return the item most recently added
     * @throws java.util.NoSuchElementException if this stack is empty
     */
    public Item pop()
    {  // Remove item from top of stack.
        Item item = first.item;
        first = first.next;
        N--;
        assert check();
        return item;
    }
    /**
     * Returns (but does not remove) the item most recently added to this stack.
     * @return the item most recently added to this stack
     * @throws java.util.NoSuchElementException if this stack is empty
     */
    public Item peek() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        return first.item;
    }
    // check internal invariants
    private boolean check() {

        // check a few properties of instance variable 'first'
        if (N < 0) {
            return false;
        }
        if (N == 0) {
            if (first != null) return false;
        }
        else if (N == 1) {
            if (first == null)      return false;
            if (first.next != null) return false;
        }
        else {
            if (first == null)      return false;
            if (first.next == null) return false;
        }

        // check internal consistency of instance variable N
        int numberOfNodes = 0;
        for (Node x = first; x != null && numberOfNodes <= N; x = x.next) {
            numberOfNodes++;
        }
        if (numberOfNodes != N) return false;

        return true;
    }

    /**
     * Returns a string representation of this stack.
     * @return the sequence of items in the stack in LIFO order, separated by spaces
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : this)
            s.append(item + " ");
        return s.toString();
    }
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
        In in = new In(args[0]);      // input file Stack_input.txt
        // Create a stack and push/pop strings as directed on StdIn.
        LinkedStack<String> s = new LinkedStack<String>();
        while (!in.isEmpty())
        {
//            String item = StdIn.readString();
            String item = in.readString();
            if (!item.equals("-"))
                s.push(item);
            else if (!s.isEmpty()) StdOut.print(s.pop() + " ");
        }
        StdOut.println("(" + s.size() + " left on stack)");
    }
}
