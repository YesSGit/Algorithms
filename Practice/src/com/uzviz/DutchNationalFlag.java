/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: May'2020
 *  Description: Given an array of n buckets, each containing a red, white, or
 *               blue pebble, DutchNationalFlag.java sorts them such that all
 *               pebbles of the same color are together and their collective color
 *               groups are in the correct order.
 *
 *               The {@code DutchNationalFlag} class provides static methods for
 * 	             •	swap(i,j): swap the pebble in bucket i with the pebble in bucket j.
 *	             •	color(i): determine the color of the pebble in bucket i.
 *
 *  Performance:
 *               •	At most n calls to color()
 *               •  At most n calls to swap()
 *               •  Constant extra space
 *  Property:    Must be robust to repeated elements, may use a three-way partitioning
 *               function that groups items less than a given key (red), equal
 *               to the key (white) and greater than the key (blue).
 * Dependencies: Pebble.java
 *  <p>
 *  Coursera, ALGORITHM Part I, week 2, Interview questions (Elementary sorts)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Iterator;

public final class DutchNationalFlag implements Iterable<Comparable>
{
    private static Comparable[] a;
    /**
     * Sorts pebbles such that all of the same color are together and
     * their collective color groups are in the correct order.
     *
     * @param  pebbles the array of red, white or blue pebbles
     * @param key the key to group items in three-way partitioning function
     */

    private DutchNationalFlag(Comparable[] pebbles, Comparable key) {
        // three-way partitioning function that groups items less than a given key (red),
        // equal to the key (white) and greater than the key (blue).
        threeWayPartion(pebbles, key);
    }

    /**
     * To ensure immutability add public static factory to call private constructor;
     * check arguments against any possible IllegalArgumentException.
     *
     * @param array the array of red, white or blue pebbles
     * @param mid the key to group items in three-way partitioning function
     * @throws IllegalArgumentException if {@code pebbles} is {@code null}
     * @throws IllegalArgumentException if any entry in {@code pebbles[]} is {@code null}
     * @throws IllegalArgumentException if {@code pebbles.length} is {@code 0}
     * @throws IllegalArgumentException if {@code mid} is {@code null}
     */
    public static DutchNationalFlag valueOf(Comparable[] array, Comparable mid) {
        if (array == null) throw new IllegalArgumentException("Argument is null");
        if (array.length == 0) throw new IllegalArgumentException("Array is of length 0");
        if (mid == null) throw new IllegalArgumentException("Argument is null");

        // defensive copy of input array
        a = new Comparable[array.length];
        for (int i = 0; i < a.length; i++) {
            if (array[i] == null)
                throw new IllegalArgumentException("array[" + i + "] is null");
            a[i] = array[i];
        }
        return new DutchNationalFlag(a, mid);
    }

    private static void threeWayPartion(Comparable[] a, Comparable mid) {
        // For three-way partitioning assumes zero-based array indexing.
        // It uses three indices i, j and k, maintaining the invariant that i ≤ j ≤ k.
        //
        // - Entries from 0 up to (but not including) i are values less than mid,
        // - entries from i up to (but not including) j are values equal to mid,
        // - entries from j up to (but not including) k are values not yet sorted, and
        // - entries from k to the end of the array are values greater than mid.
        int i = 0;
        int j = 0;
        int k = a.length;

        while (j < k) {
            if (color(a[j], mid)) {
                swap(a, i, j);
                i++;
                j++;
            }
            else if ( color(mid, a[j])) {
                k--;
                swap(a, j, k);
            }
            else j++;

        }

    }

    private static boolean color(Comparable v, Comparable w) {
        return  v.compareTo(w) < 0;
    }

    private  static void swap(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    public Iterator<Comparable> iterator() {
        return new ItemsIterator();
    }

    private static class ItemsIterator implements Iterator<Comparable> {
        private int i = 0;
        public boolean hasNext() { return i < a.length; }
        public Comparable next() { return a[i++]; }
        public    void remove()  { throw new UnsupportedOperationException();  }
    }

    public static void main(String[] args) {
        char[] tokenSet = {'W', 'B', 'B', 'R', 'B', 'W', 'W', 'B', 'R', 'R', 'R', 'B', 'W', 'R', 'W'};
        Pebble[] pebbles = new Pebble[tokenSet.length];
        for (int i = 0; i < tokenSet.length; i++) {
            if (tokenSet[i] == 'R')
                pebbles[i] = new Pebble(StdDraw.RED, tokenSet[i]);
            if (tokenSet[i] == 'W')
                pebbles[i] = new Pebble(StdDraw.WHITE, tokenSet[i]);
            if (tokenSet[i] == 'B')
                pebbles[i] = new Pebble(StdDraw.BLUE, tokenSet[i]);
        }

        StdDraw.clear(StdDraw.LIGHT_GRAY);
        StdDraw.setPenRadius(0.005);
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 12));

        StdDraw.enableDoubleBuffering(); // turn on animation mode
        StdDraw.setXscale(-1000, 32768);
        StdDraw.setYscale(-1000, 32768);
        StdDraw.text(1500,32000, "Initial set: ");
        int x = 4000;
        int y = 32000;

        for (int i = 0; i < pebbles.length; i++) {
            pebbles[i].draw(x, y);
            x += 1000;
        }
        StdDraw.show();

        Pebble mid = new Pebble(StdDraw.WHITE, 'W');
        DutchNationalFlag DNF = DutchNationalFlag.valueOf(pebbles, mid);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(1500,30000, "Sorted set:  ");

        x = 4000;
        y = 30000;

        for (Iterator<Comparable> it = DNF.iterator(); it.hasNext(); ) {
            Pebble p = (Pebble) it.next();
            p.draw(x, y);
            x += 1000;
        }
        StdDraw.show();

    }
}
