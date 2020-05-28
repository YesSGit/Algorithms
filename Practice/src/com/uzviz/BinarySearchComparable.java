package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public final class BinarySearchComparable {
    /**
     * This class should not be instantiated.
     */
    private BinarySearchComparable() {}

    /**
     * Returns the index of the specified generic key in the specified array of generic.
     *
     * @param  a the array of Comparable items, must be sorted in ascending order
     * @param  key the search Comparable key
     * @return index of key in array {@code a} if present; {@code -1} otherwise
     */
    public static int indexOf(Comparable[] a, Comparable key) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            if      (less(key, a[mid])) hi = mid - 1;
            else if (less(a[mid], key)) lo = mid + 1;
            else return mid;
        }
        return -1;
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    /**
     * Reads in a sequence of Point2D points from the whitelist file, specified as
     * a redirect standard input from the file; reads in integers from standard input;
     * prints to standard output those points that do <em>not</em> appear in the file.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int num = StdIn.readInt();
        Point2D[] whitelist = new Point2D[num];
        for (int i = 0; i < num; i++) {
            int x = StdIn.readInt();
            int y = StdIn.readInt();
            whitelist[i] = new Point2D(x, y);
        }

        // sort the array
        Arrays.sort(whitelist);

        // read integer key from standard input; print if not in whitelist
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            int x = in.readInt();
            int y = in.readInt();
            Point2D point = new Point2D(x, y);
            if (BinarySearchComparable.indexOf(whitelist, point) == -1)
                StdOut.println(point);
        }
    }


}
