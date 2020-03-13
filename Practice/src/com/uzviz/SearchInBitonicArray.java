package com.uzviz;

public class SearchInBitonicArray {

    private final int arrSize;
    private int bitonicPoint;


    public SearchInBitonicArray(int[] array){
        int l, r;
        if (array.length < 3)
            throw new IllegalArgumentException("Input array size: " + array.length + " is less than 3");

        arrSize = array.length;
        l = 0;
        r = arrSize - 1;

        bitonicPoint = findBitonicPoint(array, l, r);
        System.out.println("Bitonic point <" + array[bitonicPoint] + "> at index: " + bitonicPoint);
    }

    // Function for binary search in ascending part of array
    private int ascendingBinarySearch(int[] arr, int low, int high, int key) {
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

    // Function for binary search in descending part of array
    private int descendingBinarySearch(int[] arr, int low, int high, int key) {
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

    // finding bitonic point
    private int findBitonicPoint(int[] arr, int l, int r) {
        int mid;

        mid = l + (r - l) / 2;
        if (arr[mid] > arr[mid - 1] && arr[mid] > arr[mid + 1]) {
            return mid;
        } else {
            if (arr[mid] > arr[mid - 1] && arr[mid] < arr[mid + 1]) {
                mid = findBitonicPoint(arr, mid, r);
            } else {
                if (arr[mid] < arr[mid - 1] && arr[mid] > arr[mid + 1]) {
                    mid = findBitonicPoint(arr, l, mid);
                }
            }
        }
        return mid;
    }

    // Find a given key inside bitonic array
    public int FindInBitonic(int[] arr, int key) {
        if (key > arr[bitonicPoint]) {
            return -1;
        } else if (key == arr[bitonicPoint]) {
            return bitonicPoint;
        } else {
            int temp = ascendingBinarySearch(arr, 0, bitonicPoint - 1, key);
            if (temp != -1) {
                return temp;
            }

            return descendingBinarySearch(arr, bitonicPoint + 1, arrSize - 1, key);
        }
    }

    public static void main(String[] args) {
        SearchInBitonicArray searchBitonic;
        int array[] = {-12, -8, -5, 0,  2, 4, 7, 10, 4, 2, -3}; // bitonic array
        int array2[] = {-12, -8, -5, -1,  -3, -4, -7, -10}; // bitonic array


        searchBitonic = new SearchInBitonicArray(array);

        int key = 4; // a key to be found inside bitonaic array
        int x = searchBitonic.FindInBitonic(array, key);

        if (x == -1)
            System.out.println("Element <"+ key + "> has not been found inside bitonic array.");
        else
            System.out.println("Element <"+ key + "> found inside bitonic array at index: " + x);

    }
}
