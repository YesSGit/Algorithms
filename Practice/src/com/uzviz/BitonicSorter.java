/*************************************
 * size of array must be  power of 2
 *************************************/

package com.uzviz;

import java.util.Arrays;

public class BitonicSorter
{
    private int[] a;
    // sorting direction:
    private final static boolean ASCENDING=true, DESCENDING=false;
    private int levelSort, levelMerge;

    public void sort(int[] a)
    {
        this.a=a;
        levelSort=0;
        levelMerge=0;
        bitonicSort(0, a.length, ASCENDING);
    }

    private void bitonicSort(int lo, int n, boolean dir)
    {
        levelSort++;
        if (n>1)
        {
            int m=n/2;
            bitonicSort(lo, m, ASCENDING);
            bitonicSort(lo+m, m, DESCENDING);
            bitonicMerge(lo, n, dir);
        }
        levelSort--;
    }

    private void bitonicMerge(int lo, int n, boolean dir)
    {
        levelMerge++;
        if (n>1)
        {
            int m=n/2;
            for (int i=lo; i<lo+m; i++)
                compare(i, i+m, dir);
            bitonicMerge(lo, m, dir);
            bitonicMerge(lo+m, m, dir);
        }
        levelMerge--;
    }

    private void compare(int i, int j, boolean dir)
    {
        if (dir==(a[i]>a[j]))
            exchange(i, j);
    }

    private void exchange(int i, int j)
    {
        int t=a[i];
        a[i]=a[j];
        a[j]=t;
    }

    public static void main(String[] args){
        BitonicSorter bitonicSorter;
        int[] array2 = {7, -12, -5, -8, 2,  4, 0, -3}; // bitonic array
        int[] array3 = {4, -5, 2, 0}; // bitonic array

        bitonicSorter = new BitonicSorter();
        bitonicSorter.sort(array2);
        System.out.println(Arrays.toString(array2));
    }

}    // end class BitonicSorter
