package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 *  Coursera, ALGORITHM Part I, week 6, Interview Questions: Hash Tables
 *  <p></p>
 *  Question:
 *              Given an array {@code a[]} of {@code n} integers, the 4-SUM problem is to determine if there exist
 *              distinct indices {@code i,j,k} and {@code l} such that {@code a[i]+a[j] = a[k]+a[l]}.
 *              (assuming that overflow plays no role);
 *  <p>
 *  Performance: takes time proportional to {@code Nˆ2} (under suitable technical assumptions).
 *  <p></p>
 *  Algorithm design:
 *  <p>
 * <i>Input data</i> - unordered array of N integers;
 *  <p>
 * <i> Data structure</i> - Hash Table (Separate Chaining Symbol Tabel), where:
 * <p>
 * {@code Key} - sum of a pair of distinct integers from N, type {@code Integer}.
 * <p>
 * {@code Value} - pair of array indices of the integers which constructed the {@code Key} sum; type {@code Integer[2]}.
 * <p>
 * To find distinct indices {@code i, j, k,} and {@code l} such that {@code a[i]+a[j]=a[k]+a[l]} , the input array of N integers
 * to be processed by double nested loops which are constructed in a way to have non-overlapping ranges of indices:
 * <p>
 * {@code for(inti=0;i<N;i++)}
 * <p>
 *     {@code for(intj=i+1;j<N;j++)}
 * <p>
 *  Before to insert {@code Key-Value} pair,  of current {@code i,j} indices, into the Hash ST, check whether ST already contains this
 *  {@code Key} (sum of a pair of distinct integers from N) and either of current {@code i,j} values is not equal to one from corresponding
 *  {@code Value} of the {@code Key} inside Hash ST. If these conditions are met,- four distinct indices {@code i, j, k,} and {@code l}
 *  are determined such that {@code a[i]+a[j]=a[k]+a[l]} . Collect the {@code Key} and two pairs of indices {@code i,j} and {@code k,l}
 *  into the appropriate 'Iterable' data structure (<i>Queue</i>, enqueue {@code Key} & two pair of indices as a String entry) and proceed looping.
 *  <p>
 * Required performance of time proportional to Nˆ2 is met as order of growth of nested double loops is quadratic and <em>search/insert</em>
 * operations of Separate Chaining ST  are logarithmic in worst-case  and are constant in average-case.
 * <p></p>
 * "Suitable technical assumptions" - here is a <i> uniform hashing assumption</i> which assumes that each key is equally likely
 * to hash to an integer between 0 and M - 1 (for use as array index), where M is a number of bins to store keys.
 * As Key type is {@code Integer} this assumption is also met.
 *  <p></p>
 *  @author Serhii Yeshchenko
 *  Date: Jun'2023
 */

public class FourSumPairs {
 /**
 *  Given an array {@code a[]} of {@code n} integers, collect distinct indices {@code i,j,k} and {@code l}
 * such that {@code a[i]+a[j] = a[k]+a[l]}.
 */
    public static Iterable<String> indices(int[] a) {
        int N = a.length;
        SeparateChainingHashST<Integer, Integer[]> st = new SeparateChainingHashST<>();
        Queue<String> keys = new Queue<>();

        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++) {
                if (st.contains(a[i] + a[j])) {
                    if (i != st.get(a[i] + a[j])[0] && i != st.get(a[i] + a[j])[1] &&
                            j != st.get(a[i] + a[j])[0] && j != st.get(a[i] + a[j])[1])
                        keys.enqueue("Pair of indices: " + Arrays.toString(st.get(a[i] + a[j])) + ";[" + i + "," + j + "] Sum:  " + (a[i] + a[j]));
                }
                Integer[] vals = new Integer[2];
                vals[0] = i;
                vals[1] = j;
                st.put(a[i] + a[j], vals);
            }
        return keys;
    }

    public static void main(String[] args)
    {
        In in = new In(args[0]);
        int[] a = in.readAllInts();
        for (String s : indices(a))
            StdOut.println(s);
    }

}
