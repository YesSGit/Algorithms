/* *****************************************************************************
 *  Name: S.Yeshchenko (based on R. Sedgewick, K. Wayne from Algorithm 4-th ed.)
 *  Date: Aug'2019
 *  Coursera quiz, Algorithm I part: Pushdown stack with push/pop/return-the-maximum
 *  operatons (resizing array implementation)
 ******************************************************************************/

package com.uzviz;

import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class StackWithMax implements Iterable<Comparable>
{
    private Comparable[] a = new Comparable[1];  // stack items
    private int N = 0;                           // number of items

    public boolean isEmpty()  {  return N == 0; }
    public int size()         {  return N;      }

    private void resize(int max)
    {  // Move stack to a new array of size max.
        Comparable[] temp = new Comparable[max];
        for (int i = 0; i < N; i++)
            temp[i] = a[i];
        a = temp;
    }
    public void push(Comparable item)
    {  // Add item to top of stack.
        if (N == a.length) resize(2*a.length);
        a[N++] = item;
    }
    public Comparable pop()
    {  // Remove item from top of stack.
        Comparable item = a[--N];
        a[N] = null;
        if (N > 0 && N == a.length/4) resize(a.length/2);
        return item;
    }
    public Comparable returnMax()
    {   // Return the current maximum item of the stack
        int max = 0;                 // index of maximum entr.
        for (int j = 1; j < N; j++)
            if (less(a[max], a[j])) max = j;
        return a[max];
    }

    private static boolean less(Comparable v, Comparable w)
    {  return v.compareTo(w) < 0;  }

    private static void show(Comparable[] a)
    {  // Print the array, on a single line.
        for (int i = 0; i < a.length; i++)
            StdOut.print(a[i] + " ");
        StdOut.println();
    }

    public Iterator<Comparable> iterator()
    {  return new ReverseArrayIterator();  }

    private class ReverseArrayIterator implements Iterator<Comparable>
    {  // Support LIFO iteration.
        private int i = N;
        public boolean hasNext() {  return i > 0;   }
        public    Comparable next()    {  return a[--i];  }
        public    void remove()  {                  }
    }

    public static void main(String[] args)
    {
        In in = new In(args[0]);      // input file StackWithMax_input.txt
                                      // "3.1415 0 2.7182 4.1 - -3.1415 -5 -7 - - 10 -"
        // Create a stack and push/pop strings.
        StackWithMax s = new StackWithMax();
        while (!in.isEmpty())
        {
            String item = in.readString();
            if (!item.equals("-"))
                s.push(Double.parseDouble(item));
            else if (!s.isEmpty()) StdOut.print(s.pop() + " ");
        }
        StdOut.println("(" + s.size() + " left on stack)");
        show(s.a);
        StdOut.println("Current max item: " + s.returnMax());

        StdOut.println("Pop all items from stack");
        while (!s.isEmpty()) {
            StdOut.print(s.pop() + " ");
        }
        StdOut.println();
        if (s.isEmpty()) StdOut.println("Stack is empty");
        else StdOut.println("Stack is not empty - check!");

    }

}
