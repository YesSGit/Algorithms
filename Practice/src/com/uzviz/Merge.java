/* *****************************************************************************
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  Date:
 *  Description: Sorts a sequence of strings from standard input using top-down mergesort.
 *  Performance: Top-down mergesort uses between 1/2 N lg N and N lg N compares and
 *  at most 6 N lg N array accesses to sort any array of length N
 *  Improvement: Test whether array is already in order
 *
 *  The {@code Merge} class provides static methods for sorting an
 *  array using mergesort.
 *  <p>
 *  ALGORITHM 2.2
 **************************************************************************** */

package com.uzviz;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Merge {
    // This class should not be instantiated.
    private Merge() { }

    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi)
    {
        // precondition: a[lo .. mid] and a[mid+1 .. hi] are sorted subarrays
        assert isSorted(a, lo, mid);       // precondition: a[lo..mid]   sorted
        assert isSorted(a, mid+1, hi); // precondition: a[mid+1..hi] sorted
//        for (int k = lo; k <= hi; k++)
//            aux[k] = a[k];
        // using System.arraycopy() is a bit faster than the above loop
        System.arraycopy(a, lo, aux, lo, hi - lo + 1);

        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++)
        {
            if      (i > mid)              a[k] = aux[j++];
            else if (j > hi)               a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else                           a[k] = aux[i++];
        }
        // postcondition: a[lo .. hi] is sorted
        assert isSorted(a, lo, hi);
    }

    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi)
    {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid+1, hi);
        // Is biggest item in first half ≤ smallest item in second half? Helps for partially-ordered arrays.
        if (!less(a[mid+1], a[mid]))
            return; // stop if already sorted.
        merge(a, aux, lo, mid, hi);
    }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void sort(Comparable[] a)
    {
        Comparable[] aux = new Comparable[a.length];
        sort(a, aux, 0, a.length - 1);
        assert isSorted(a);
    }

    /***************************************************************************
     *  Helper sorting function.
     ***************************************************************************/
    // is v < w ?
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    /***************************************************************************
     *  Check if array is sorted - useful for debugging.
     ***************************************************************************/
    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }
    // print array to standard output
    private static void show(Comparable[] a) {
        for (Comparable item : a)  /* initial code replaced by foreach */
            StdOut.println(item);
//        for (int i = 0; i < a.length; i++) {
//            StdOut.println(a[i]);
//        }
    }

    /**
     * Reads in a sequence of strings from standard input; mergesorts them;
     * and prints them to standard output in ascending order.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String[] a = StdIn.readAllStrings();
        Merge.sort(a);
        show(a);
    }

}
