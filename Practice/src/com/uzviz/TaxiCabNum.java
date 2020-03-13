/* *****************************************************************************
 *  Name: S.Yeshchenko
 *  Date: Jan'2020
 *  Description: TaxiCabNum.java finds all 'taxi-cab numbers' on the interval
 *  of 0 and given number.
 *  A taxicab number is an integer that can be expressed as the sum
 *  of two cubes of positive integers in two different ways: a3+b3=c3+d3.
 *  For example, 1729 is the smallest taxicab number: 93+103=13+123.
 * 	Assuming a[i] < a[j] < a[k] < a[m] < n.
 *
 *  Dependencies: MinPQ.java (Generic min priority queue implementation with a binary heap)
 *
 *  Coursera, Algorithm I part: Priority queues
 ******************************************************************************/
package com.uzviz;

import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

public final class TaxiCabNum {
    private int k;                         // number of found taxi-cab numbers
    private TaxiCabTuple[] taxicabNumbers; //

    private TaxiCabNum(int n){
        MinPQ<TaxiCabTuple> minPQ = new MinPQ<>(n);
        taxicabNumbers = new TaxiCabTuple[10];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                minPQ.insert(new TaxiCabTuple(i, j));
            }
        }
        k = 0;
        if (minPQ.isEmpty()) throw new NoSuchElementException("Priority queue is empty - no cubes added out of the interval 0 - " + n);
        TaxiCabTuple firstCubesPair = minPQ.delMin();
        while (!minPQ.isEmpty()) {
            TaxiCabTuple secondCubesPair = minPQ.delMin();
            if (firstCubesPair.cubesSum == secondCubesPair.cubesSum)
                taxicabNumbers[k++] = firstCubesPair;
            firstCubesPair = secondCubesPair;
            if (taxicabNumbers.length - 1 == k) resize(taxicabNumbers.length * 2);
        }
        if (taxicabNumbers.length > k) resize(k);
    }

    public static TaxiCabNum valueOf(int n){
        return new TaxiCabNum(n);
    }
    public void listTaxiCabNum(){
        for (TaxiCabTuple tcn : taxicabNumbers) StdOut.println(tcn + " = " + tcn.cubesSum);
    }
    /**
     * Returns collection (int[]) of found taxi-cab numbers.
     *
     * @return the array of found taxi-cab numbers
     */
    public TaxiCabTuple[] TaxiCabNumbers() {
        return taxicabNumbers.clone();
    }
    /**
     * Returns the number of found taxi-cab numbers.
     *
     * @return the number of found taxi-cab numbers
     */
    public int size() { return k;}
    /**
     * Returns true if no taxi-cab numbers have been found.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
     public boolean isEmpty() {
         return k == 0;
     }

    private void resize(int size){
        TaxiCabTuple[] tmpTCnumbers = new TaxiCabTuple[size];
        if (k + 1 >= 0) System.arraycopy(taxicabNumbers, 0, tmpTCnumbers, 0, k );
        taxicabNumbers = tmpTCnumbers;
    }

    private final class TaxiCabTuple implements Comparable<TaxiCabTuple> {
         private final int i;
         private final int j;
         private final long cubesSum;

        // create a new tuple (i, j, i^3 + j^3) to be inserted into priority queue
        private TaxiCabTuple(int i, int j) {
            this.cubesSum = (long) i * i * i + (long) j * j * j;
            this.i = i;
            this.j = j;
        }

        @Override
        public int compareTo(TaxiCabTuple that) {
            if     (this.cubesSum < that.cubesSum) return -1;
            else if(this.cubesSum > that.cubesSum) return +1;
            else if(this.i < that.i)               return -1;
            else if(this.i > that.i)               return +1;
            else                                   return  0;
        }

        public String toString() {
            return i + "ˆ3 + "+ j + "ˆ3";
        }
    }

    public static void main(String[] args){
        int n = Integer.parseInt(args[0]);
        TaxiCabNum taxicabNums = TaxiCabNum.valueOf(n);
        StdOut.println("Numbers of taxi-cab found: " + taxicabNums.size());
        taxicabNums.listTaxiCabNum();
    }
}
