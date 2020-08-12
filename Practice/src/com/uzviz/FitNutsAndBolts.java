/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: Jun'2020
 *  Description: There is a mixed pile of nuts and bolts. FitNutsAndBolts.java finds
 *               the corresponding pairs of nuts and bolts. Each nut fits exactly
 *               one bolt and each bolt fits exactly one nut.
 *
 *  Performance: At most proportional to nlogn compares (probabilistically).
 *
 *  Property:    compares only a nut and a bolt together (it's not allowed to
 *               compare two nuts or two bolts directly).
 *
 * Dependencies: Nut.java, Bolt.java
 *  <p>
 *  Coursera, ALGORITHM Part I, week 3, Interview question 1 (Quicksort)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class FitNutsAndBolts {
    private NutsAndBoltsTuple[] nutsAndBoltsPair;

    /**
     * Find the Nut that matches a particular Bolt.
     *
     * @param  nuts the array of type Nut
     * @param  bolts the array of type Bolt
     */
    private FitNutsAndBolts(Comparable[] nuts, Comparable[] bolts) {
        fit(nuts, bolts); // finding the matching nut for every bolt

        // collect all mating pairs of nut and bolt as array of tuples
        nutsAndBoltsPair = new NutsAndBoltsTuple[nuts.length];
        for (int i = 0; i < nuts.length; i++) {
            nutsAndBoltsPair[i] = new NutsAndBoltsTuple(nuts[i], bolts[i]);
        }
    }

    /**
     * To ensure immutability add public static factory to call private constructor;
     * check arguments against any possible IllegalArgumentException.
     *
     * @param  aN the array of type Nut
     * @param  aB the array of type Bolt
     * @throws IllegalArgumentException if {@code aN} is {@code null}
     * @throws IllegalArgumentException if {@code aB} is {@code null}
     * @throws IllegalArgumentException if any entry in {@code aN[]} is {@code null}
     * @throws IllegalArgumentException if any entry in {@code aB[]} is {@code null}
     * @throws IllegalArgumentException if {@code aN.length} is {@code 0}
     * @throws IllegalArgumentException if {@code aB.length} is {@code 0}
     */
    public static FitNutsAndBolts valueOf(Comparable[] aN, Comparable[] aB) {
        if (aN == null) throw new IllegalArgumentException("Argument is null");
        if (aB == null) throw new IllegalArgumentException("Argument is null");
        if (aN.length == 0) throw new IllegalArgumentException("Array is of length 0");
        if (aB.length == 0) throw new IllegalArgumentException("Array is of length 0");

        // defensive copy of input arrays
        Comparable[] arrN = new Comparable[aN.length];
        for (int i = 0; i < aN.length; i++) {
            if (aN[i] == null) throw new IllegalArgumentException("Nuts array[" + i + "] is null");
            arrN[i] = aN[i];
        }
        Comparable[] arrB = new Comparable[aB.length];
        for (int i = 0; i < aB.length; i++) {
            if (aB[i] == null) throw new IllegalArgumentException("Bolts array[" + i + "] is null");
            arrB[i] = aB[i];
        }
        return new FitNutsAndBolts(arrN, arrB);
    }

    private void fit(Comparable[] nuts, Comparable[] bolts) {
        StdRandom.shuffle(bolts);
        StdRandom.shuffle(nuts);
        //once array shuffled, any item can be chosen as the pivot element
        sort(nuts, bolts,0, nuts.length - 1, bolts[0]);
    }

    private static void sort(Comparable[] nuts, Comparable[] bolts, int lo, int hi, Comparable pivotBolt) {
        if (hi <= lo) return;
        int partitionNut = partition(nuts, lo, hi, pivotBolt);
        int partitionBolt = partition(bolts, lo, hi, nuts[partitionNut]);

        sort(nuts, bolts, lo, partitionBolt-1, bolts[lo]);
        sort(nuts, bolts, partitionBolt + 1, hi, bolts[hi]);
    }

    private static int partition(Comparable[] a, int lo, int hi, Comparable pivot) {
        int i = lo - 1 , j = hi + 1;
        int pivotIndex = 0;
        while (true)
        {
            // find item on left to swap
            while (less(a[++i], pivot)) {
                if (i == hi) break;
            }
            // find item on right to swap
            while (less(pivot, a[--j])) {
                if (j == lo) break;
            }

            // keep index of found pivot item inside the array  (either i-th or j-th)
            if (equal(a[i], pivot)) pivotIndex = i;
            if (equal(a[j], pivot)) pivotIndex = j;

            if (i >= j) break; // check if pointers cross swap

            exch(a, i, j);     // swap
            // update index of pivot item in case it's been exchanged
            if (equal(a[i], pivot)) pivotIndex = i;
            if (equal(a[j], pivot)) pivotIndex = j;

        }
        if (pivotIndex > j) {
            exch(a, pivotIndex, j + 1);  //  swap with partitioning item
            return j + 1;                   // return index of item now known to be in place
        }
        else {
            exch(a, pivotIndex, j);        //  swap with partitioning item
            return j;                      // return index of item now known to be in place
        }
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }
    private static boolean equal(Comparable v, Comparable w) {
        return v.compareTo(w) == 0;
    }

    private static void exch(Comparable[] a, int i, int j) {
       Comparable swap = a[i];
       a[i] = a[j];
       a[j] = swap;
    }
    /**
     * Tuple which represents mating pair of nut and bolt
     * fitting each other by a similar size - thread diameter and pitch;
     * +, in a real word, other parameters like a thread
     *    thread angle, root, crest , etc.
     */
    private static final class NutsAndBoltsTuple  {
        Comparable nut;
        Comparable bolt;

        private NutsAndBoltsTuple(Comparable n, Comparable b) {
            nut = n;
            bolt = b;
        }

        @Override
        public String toString() {
            return nut.toString() + " - " + bolt.toString();
        }
    }

    // print collection of fitted nuts and bolts into standard output
    public void listNutsAndBolts() {
        for (NutsAndBoltsTuple nbt : nutsAndBoltsPair)
            StdOut.println(nbt.toString());
    }

    public NutsAndBoltsTuple[] collectionNutsAndBolts() {
        return nutsAndBoltsPair.clone();
    }

    public static void main(String[] args) {
        In inB = new In(args[0]); // input file with bolts
        int numB = inB.readInt(); // read number of bolts provided
        In inN = new In(args[1]); // input file with nuts
        int numN = inN.readInt(); // read number of nuts provided

        Bolt[] boltHeap = new Bolt[numB];
        for (int i = 0; i < numB; i++) {
            boltHeap[i] = new Bolt(inB.readShort(), inB.readFloat(), inB.readShort());
        }

        Nut[] nutHeap = new Nut[numN];
        for (int i = 0; i < numN; i++) {
            nutHeap[i] = new Nut(inN.readShort(), inN.readFloat(), inN.readFloat());
        }

        // finds the corresponding pairs of nuts and bolts.
        FitNutsAndBolts fitNutsAndBolts = FitNutsAndBolts.valueOf(nutHeap, boltHeap);
        fitNutsAndBolts.listNutsAndBolts();
    }
}
