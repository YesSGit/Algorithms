/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  (based on R. Sedgewick, K. Wayne from Algorithm 4-th ed.)
 *  Date: Apr'2020
 *  Description: Shell sort uses Insertion sort approach but
 *               moves entries more than one position at a time by h-sorting
 *               the array.
 *               Increment sequence 3x + 1: 1, 4, 13, 40, 121, 364, ...
 *  Performance: The worst-case number of compares used by
 *               shellsort with the 3x+1 increments is O(N power(3/2)).
 *  Property:    Number of compares used by shellsort with the 3x+1 increments
 *               is at most by a small multiple of N times the # of increments
 *               used.
 *  <p>
 *  Coursera, ALGORITHM Part I, week 2, Quiz 2 (Elementary sorts)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.io.File;

public final class Shell {
    public static void sort(Comparable[] a) {
        int aN = a.length;
        int h = 1;
        while (h < aN / 3) h = 3 * h + 1; // 3*x + 1 increment sequence

        while (h >= 1) {
            // h-sort array based on insertion sort
            for (int i = h; i < aN; i++) {
                for (int j = i; j >= h && less(a[j], a[j - h]); j -= h)
                    exch(a, j, j - h);
            }
            h = h / 3; // move to next increment
        }
    }
    /***************************************************************************
     * General helper functions.
     ***************************************************************************/
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    private static void exch(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    private static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++)
            if (less(a[i], a[i - 1])) return false;
        return true;
    }

    public static void main(String[] args) {

        String[] c = StdIn.readAllStrings();
        StdOut.print("Initial array: ");
        for (int i = 0; i < c.length; i++) StdOut.print(c[i] + " ");
        StdOut.println();
        StdOut.print("Sorted: ");
        Shell.sort(c);
        for (int i = 0; i < c.length; i++) StdOut.print(c[i] + " ");


        String[] b = { "a)", "it", "was", "the", "best", "of", "times", "b)", "it", "was", "the", "worst" };
        StdOut.println();
        StdOut.print("Initial array: ");
        for (int i = 0; i < b.length; i++) StdOut.print(b[i] + " ");
        StdOut.println();
        StdOut.print("Sorted: ");
        Shell.sort(b);
        for (int i = 0; i < b.length; i++) StdOut.print(b[i] + " ");

        StdOut.println();
        StdOut.println();
        StdOut.println("Sorted list of files from the directory '" + args[0] + "'");
        File directory = new File(args[0]);
        File[] files = directory.listFiles();
        Shell.sort(files);
        for (int i = 0; i < files.length; i++)
            StdOut.println(files[i].getName());

    }
}
