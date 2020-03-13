package com.uzviz;

import java.util.Arrays;

public class SearchInBitonicForArbitraryN
{
    private int[] a;
    private final boolean ASCENDING=true;    // sorting direction

    public void sort(int[] a)
    {
        this.a=a;
        bitonicSort(0, a.length, ASCENDING);
    }

    private void bitonicSort(int lo, int n, boolean dir)
    {
        if (n>1)
        {
            int mid=n/2;
            bitonicSort( lo, mid, !dir);
            bitonicSort(lo+mid, n-mid, dir);
            bitonicMerge( lo, n, dir);
        }
    }

    private void bitonicMerge(int lo, int n, boolean dir)
    {
        if (n>1)
        {
            int m=greatestPowerOfTwoLessThan(n);
            for (int i=lo; i<lo+n-m; i++)
                compare(i, i+m, dir);
            bitonicMerge(lo, m, dir);
            bitonicMerge(lo+m, n-m, dir);
        }
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

    // n>=2  and  n<=Integer.MAX_VALUE
    private int greatestPowerOfTwoLessThan(int n)
    {
        int k=1;
        while (k>0 && k<n)
            k=k<<1;
        return k>>>1;
    }

    // binary search in ascending array
    private int ascendingBinarySearch(int[] arr, int key) {
        int low, high;

        low = 0;
        high = arr.length-1;


        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == key) {
                return mid;
            }
            if (arr[mid] > key) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    // binary search in descending array
    private int descendingBinarySearch(int[] arr, int key) {
        int low, high;

        low = 0;
        high = arr.length-1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == key) {
                return mid;
            }
            if (arr[mid] < key) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }


    public static void main(String[] args){
        SearchInBitonicForArbitraryN bitonicSorter;

        // bitonic arrays
        int[] array = {-12, -8, -5, 0,  2, 4, 7, 10, 4, 2, -3};
        int[] array2 = {-12, -5, -8, 2,  4, 0, 7, -3, 4, 2, 10};
        int[] array3 = {4, -5, -12, 2, 0};

        // sort bitonic array
        bitonicSorter = new SearchInBitonicForArbitraryN();
        bitonicSorter.sort(array3);
        System.out.println(Arrays.toString(array3));

        // a key to be found inside bitonic array
        int key = 4;
        // index of the key inside bitonic array (if not found =-1)
        int x;
        // find a key inside bitonic array, depends on sorting direction
        if (bitonicSorter.ASCENDING) x = bitonicSorter.ascendingBinarySearch(array3, key);
        else x = bitonicSorter.descendingBinarySearch(array3, key);

        if (x == -1)
            System.out.println("Element <"+ key + "> has not been found inside bitonic array.");
        else
            System.out.println("Element <"+ key + "> found inside bitonic array at index: " + x);

    }
}