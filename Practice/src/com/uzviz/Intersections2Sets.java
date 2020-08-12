/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: May'2020
 *  Description: Given two arrays 𝚊[n] and 𝚋[m] , each containing distinct 2D
 *               points in the plane. Intersections2Sets.java counts the number
 *               of points that are contained in both given arrays 𝚊[n] and  𝚋[m].
 *
 *  Performance: subquadratic - O(NlgN):
 *               - as the worst-case number of compares used by shellsort
 *                 with the 3x+1 increments is O(N power(3/2)) and
 *               - binary search takes time O(lgN)
 *
 *  Property:
 *            For more general applications - input arrays can be of type Comparable
 *            and of different length;
 *            to gain performance - the smallest array is sorted out before the binary
 *            search inside the another one.
 *  <p>
 *  Coursera, ALGORITHM Part I, week 2, Quiz 2 (Elementary sorts)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.*;

public final class Intersections2Sets {
    private Stack<Comparable> intersectionSet = new Stack<>();

    /**
     * Computes the intersection of the two sets (specified arrays of 2D points).
     *
     * @param  pointsA the array of 2D-points
     * @param  pointsB the array of 2D-points
     */
    private Intersections2Sets(Comparable[] pointsA, Comparable[] pointsB) {
        if (pointsA.length < pointsB.length)
            SearchIntersection(pointsA, pointsB);
        else
            SearchIntersection(pointsB, pointsA);
    }

    /**
     * To ensure immutability, add public static factory to call private constructor;
     * check arguments against any possible IllegalArgumentException.
     *
     * @param  pointsA the array of Comparable items
     * @param  pointsB the array of Comparable items
     * @throws IllegalArgumentException if {@code pointsA} is {@code null}
     * @throws IllegalArgumentException if {@code pointsB} is {@code null}
     * @throws IllegalArgumentException if any entry in {@code pointsA[]} is {@code null}
     * @throws IllegalArgumentException if any entry in {@code pointsB[]} is {@code null}
     * @throws IllegalArgumentException if {@code pointsA.length} is {@code 0}
     * @throws IllegalArgumentException if {@code pointsB.length} is {@code 0}
     */
    public static Intersections2Sets valueOf(Comparable[] pointsA, Comparable[] pointsB) {
        if (pointsA == null || pointsB == null) throw new IllegalArgumentException("Argument is nul");
        if (pointsA.length == 0 || pointsB.length == 0) throw new IllegalArgumentException("Array is of length 0");

        // defensive copy of input arrays
        int n = pointsA.length;
        Comparable[] a = new Comparable[n];
        for (int i = 0; i < n; i++) {
            if (pointsA[i] == null)
                throw new IllegalArgumentException("points[" + i + "] is null");
            a[i] = pointsA[i];
        }
        int m = pointsB.length;
        Comparable[] b = new Comparable[m];
        for (int i = 0; i < m; i++) {
            if (pointsB[i] == null)
                throw new IllegalArgumentException("points[" + i + "] is null");
            b[i] = pointsB[i];
        }

        return new Intersections2Sets(a, b);
    }

    private Stack<Comparable> SearchIntersection(Comparable[] sortArr, Comparable[] searchArr) {
        Shell.sort(sortArr); // sort the shortest of two array (sort method from com.uzviz.Shell class)
        for (Comparable p : searchArr) {
            // collect items that are contained in both arrays
            if (BinarySearchComparable.indexOf(sortArr, p) != -1) intersectionSet.push(p);
        }
        return intersectionSet;
    }

    /**
     * Returns the intersection points of two sets of distinct 2D points in the plane.
     *
     * @return the intersection points of two sets of distinct 2D points in the plane.
     */
    public Iterable<Comparable> intersectionSet() {
        Stack<Comparable> s = new Stack<>();
        for (Comparable p : intersectionSet) s.push(p);
        return s;
    }

    public static void main(String[] args) {
        // initialising two arrays of 2D points
        int num1 = StdIn.readInt();
        Point2D[] set2D_1 = new Point2D[num1];
        for (int i = 0; i < num1; i++) {
            int x = StdIn.readInt();
            int y = StdIn.readInt();
            set2D_1[i] = new Point2D(x, y);
        }

        In in = new In(args[0]);
        int num2 = in.readInt();
        Point2D[] set2D_2 = new Point2D[num2];
        for (int i = 0; i < num2; i++) {
            int x = in.readInt();
            int y = in.readInt();
            set2D_2[i] = new Point2D(x, y);
        }

        // finds and returns the interection of two given arrays
        Intersections2Sets intersects2Sets = Intersections2Sets.valueOf(set2D_1, set2D_2);
        StdOut.println("Number of intersections in two sets of 2D points: " + intersects2Sets.intersectionSet.size());
        for (Comparable p : intersects2Sets.intersectionSet())
            StdOut.println(p);
    }
}
