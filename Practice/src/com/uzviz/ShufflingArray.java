/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  (based on R. Sedgewick, K. Wayne from Algorithm 4-th ed.)
 *  Date: Oct'2019
 *  Description: Given an array containing n items,
 *               rearrange the items uniformly at random.
 *  Performance: Run time proportional to N lg N in the worst case.
 *  <p>
 *  ALGORITHM 2.2
 **************************************************************************** */

package com.uzviz;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class ShufflingArray {
    // This class should not be instantiated.
    private ShufflingArray() { }

    private static void permute(Comparable[] a, int lo, int mid, int hi)
    {
        int rndmInt;
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++)
        {
//            if      (i > mid)              a[k] = aux[j++];
//            else if (j > hi)               a[k] = aux[i++];
//            else if (less(aux[j], aux[i])) a[k] = aux[j++];
//            else                           a[k] = aux[i++];

            rndmInt = StdRandom.uniform(2);
            if (rndmInt == 1 && j <= hi) {
                Comparable item = a[i];
                a[i] = a[j];
                a[j] = item;
                i++;
                j++;
            }
            else { i++;
            j++;
            }
        }
    }

    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static void shuffle(Comparable[] a,  int lo, int hi)
    {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        shuffle(a, lo, mid);
        shuffle(a, mid+1, hi);
        permute(a, lo, mid, hi);
    }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void shuffle(Comparable[] a)
    {
        shuffle(a, 0, a.length - 1);
    }

    // print array to standard output
    private static void show(Comparable[] a) {
        for (Comparable item : a)  /* initial code replaced by foreach */
            StdOut.println(item);
    }

    /**
     * Reads in a sequence of strings from standard input; mergesorts them;
     * and prints them to standard output in ascending order.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String[] a = StdIn.readAllStrings();
        ShufflingArray.shuffle(a);
        show(a);
    }

}
