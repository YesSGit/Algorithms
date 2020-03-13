/* *****************************************************************************
 *  Name: S. Yeshchenko
 *  Date: Aug 2019
 *  Coursera ALGORITHMS Part I / quiz: implement a queue (FIFO) with
 *  two stacks, so that each queue operations
 *  (enqueue/dequeue) take a constant amortized number of stack operations
 **************************************************************************** */

package com.uzviz;
import java.util.Iterator;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class QueueWithTwoStacks<Item> implements Iterable<Item>
{
    private Stack<Item> enqueueStack = new Stack<Item>();
    private Stack<Item> dequeueStack = new Stack<Item>();

    private int N;      // number of items on the queue

    public boolean isEmpty() {  return N == 0; }

    public int size() {  return N;  }

    // Add item to the end of the queue
    public void enqueue(Item item)
    {
        // ensure that all items moved from dequeueStack to enqueueStack before enqueue a queue
        if (!dequeueStack.isEmpty()) PopToPushStacks(dequeueStack, enqueueStack);

        enqueueStack.push(item);
        N++;
    }

    // Remove item from the beginning of the queue.
    public Item dequeue()
    {
        Item item;
        // ensure that all items moved from enqueueStack to dequeueStack before dequeue a queue
        if (!enqueueStack.isEmpty()) PopToPushStacks(enqueueStack, dequeueStack);

        item = dequeueStack.pop();
        N--;
     return item;
    }

    private void PopToPushStacks(Stack srcStack, Stack destStack)
    {
        for (Object i : srcStack) destStack.push(srcStack.pop());
    }

    public Iterator<Item> iterator()
    {  return new QueWithTwoStacksIterator();  }

    private class QueWithTwoStacksIterator implements Iterator<Item> {
        //private Node current = first;

        public QueWithTwoStacksIterator(){
            // ensure that all items moved from enqueueStack to dequeueStack before start iterating
            if (!enqueueStack.isEmpty()) PopToPushStacks(enqueueStack, dequeueStack);
        }

        public boolean hasNext() {
            return !dequeueStack.isEmpty();
        }

        public void remove() {
        }

        public Item next() {
            Item item = dequeueStack.pop();
            enqueueStack.push(item);
            return item;
        }
    }
    public static void main(String[] args)
    {
        In in = new In(args[0]);      // input file Queue_input.txt:
                                      // to be or not to - be - - that - - - is

        // Create a queue and enqueue/dequeue strings.
        QueueWithTwoStacks<String> q = new QueueWithTwoStacks<String>();
        while (!in.isEmpty())
        {
            String item = in.readString();
            if (!item.equals("-"))
                q.enqueue(item);
            else if (!q.isEmpty()) StdOut.print(q.dequeue() + " ");
        }

        StdOut.print("(" + q.size() + " left on queue: ");
        for (String s : q ) StdOut.print(s+ " ");
        StdOut.print(")");
    }
}
