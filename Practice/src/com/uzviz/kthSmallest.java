/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  findKth() function code fragment - https://stackoverflow.com/a/40190231
 *  by @author Kaidul https://stackoverflow.com/users/1162233/kaidul?tab=profile
 *  Date: Jul'2020
 *  Description: Given two sorted arrays of integers a[] and b[], of lengths n1
 *               and n2 and an integer 0≤k<n1+n2, kthSmallest.java finds a key
 *               of rank k.
 *
 *  Performance: The order of growth of the worst case running time is logn,
 *               where n=n1+n2.
 *
 *  Property:    Length of arrays a[] andb[] - not equal;
 *               rank of k - any integer from 0≤k<n1+n2; also, k>0;
 *               arrays are intersected;
 * Dependencies:
 *  <p>
 *  Coursera, ALGORITHM Part I, week 3, Interview question 2 (Quicksort)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class kthSmallest {
    private static int kthSmallestItem;

    private kthSmallest(int[] arrA, int[] arrB, int k) {
        kthSmallestItem = findKthSmallest(arrA, arrB, k);
    }
    public static kthSmallest valueOf(int[] arrA, int[] arrB, int kth) {
        if (arrA == null) throw new IllegalArgumentException("Argument is null");
        if (arrB == null) throw new IllegalArgumentException("Argument is null");

        // defensive copy of input arrays
        int[] frstArr = new int[arrA.length];
        System.arraycopy(arrA, 0, frstArr, 0, arrA.length);

        int[] scndArr = new int[arrB.length];
        for (int i = 0; i < arrB.length; i++) {
            scndArr[i] = arrB[i];
        }

        return new kthSmallest(frstArr, scndArr, kth);
    }

    private int findKthSmallest(int[] a, int[] b, int k) {
        if (a.length + b.length < k) return -1;
        // corner cases
        if (a.length <= 0) {
            if (k <= b.length) return b[k - 1];
            else return -1;
        }
        if (b.length <= 0) {
            if (k <= a.length) return a[k - 1];
            else return -1;
        }

        if (k == 1) return Math.min(a[0], b[0]);

        if (a[a.length - 1] <= b[0]) {
            if (k <= a.length) return a[k - 1];
            else return b[k - a.length - 1];
        }
        if (b[b.length - 1] <= a[0]) {
            if (k <= b.length) return b[k - 1];
            else return a[k - b.length - 1];
        }

        if (k <= a.length)
            if (a[k - 1] <= b[0]) return a[k - 1];
        if (k <= b.length)
            if (b[k - 1] <= a[0]) return b[k - 1];
        // end 'corner cases'

        int i = k /2;
        int j = k - i;
        int step = k / 4;
        return findKth(a,0,b, 0, k);
    }

    private static int findKth(int[] A, int i, int[] B, int j, int k) {

        // Here is the simple trick. We've just changed the parameter order if first array is not smaller.
        // so that later we won't need to write if-else conditions to check smaller/greater stuff
        if((A.length - i) > (B.length - j))
        {
            return findKth(B, j, A, i, k);
        }

        if(i >= A.length)
        {
            return B[j + k - 1];
        }
        if(k == 1)
        {
            return Math.min(A[i], B[j]);
        }

        int aMid = Math.min(k / 2, A.length - i);
        int bMid = k - aMid;

        if(A[i + aMid - 1] <= B[j + bMid - 1])
        {
            return findKth(A, i + aMid, B, j, k - aMid);
        }
        return findKth(A, i, B, j + bMid, k - bMid);
    }

    public static int getKthSmallest() {
        return kthSmallestItem;
    }

    public static void main(String[] args) {
        In arrA = new In(args[0]); // input file - array a[]
        int n1 = arrA.readInt(); // read number of array's elements
        In arrB = new In(args[1]); // input file - array b[]
        int n2 = arrB.readInt(); // read number of array's elements
        int k = Integer.parseInt(args[2]);

        int[] a = new int[n1];
        for (int i = 0; i < n1; i++)
            a[i] = arrA.readInt();

        int[] b = new int[n2];
        for (int i = 0; i < n2; i++)
            b[i] = arrB.readInt();

        kthSmallest findKthSmallest =  kthSmallest.valueOf(a, b, 20 );
        StdOut.println(kthSmallest.getKthSmallest());
        kthSmallest.valueOf(a, b, 7);
        StdOut.println(kthSmallest.getKthSmallest());
    }
}
