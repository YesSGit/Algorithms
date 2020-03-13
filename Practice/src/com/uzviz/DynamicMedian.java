/* *****************************************************************************
 *  Name: S.Yeshchenko (based on R. Sedgewick, K. Wayne from Algorithm 4-th ed.)
 *  Date: Dec'2019
 *  Description: DynamicMedian.java finds a dynamic median in heap-ordered Binary Heap
 *  data structure; supports 'insert' in logarithmic time, 'find' the median in
 *  constant time, and 'remove' the median in logarithmic time.
 *  Dependencies: interface AverageCalculation.java
 *
 *  Coursera quiz, Algorithm I part: Priority queues
 ******************************************************************************/
package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class DynamicMedian<Key extends Number & Comparable<Key>> {
    private MinPQ<Key> minPQ;           // min-oriented priority queue for keys greater then key of root
    private MaxPQ<Key> maxPQ;           // max-oriented priority queue for keys less then key of root
    private Key effectiveMedian;        // keeps current effective median
    private Comparator<Key> comparator; // optional comparator
//    private AverageCalculation averageNumber;

    /**
     * Initializes an empty binary heap with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this binary heap
     */
    public DynamicMedian(int initCapacity) {
        maxPQ = new MaxPQ<Key>(initCapacity + 1);
        minPQ = new MinPQ<Key>(initCapacity + 1);
    }
    /**
     * Initializes an empty binary heap.
     */
    public DynamicMedian() {
        this(1);
    }
    /**
     * Initializes an empty binary heap with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this binary heap
     * @param  comparator the order in which to compare the keys
     */
    public DynamicMedian(int initCapacity, Comparator<Key> comparator) {
        this.comparator = comparator;
        maxPQ = new MaxPQ<Key>(initCapacity + 1);
        minPQ = new MinPQ<Key>(initCapacity + 1);
    }
    /**
     * Initializes an empty binary heap using the given comparator.
     *
     * @param  comparator the order in which to compare the keys
     */
    public DynamicMedian(Comparator<Key> comparator) {
        this(1, comparator);
    }

    public void insert(Key key) {
        // compares a size of both priority queues ('lower' heap (MaxPQ) vs 'higher' heap (MinPQ))
        int isHeapsBalanced = Integer.compare(maxPQ.size(), minPQ.size());

        if (isEmpty()) {
            maxPQ.insert(key);
            effectiveMedian = key;
        }
        else {
            switch (isHeapsBalanced) {
                case -1:  // 'higher' heap (MinPQ) contains more elements than 'lower' heap (MaxPQ)
                    if (less(key, effectiveMedian)) {
                        maxPQ.insert(key);
                    }
                    else {
                        maxPQ.insert(minPQ.delMin());
                        minPQ.insert(key);
                    }
                    effectiveMedian = medianCalculation(maxPQ.max(), minPQ.min());
                    break;
                case 0: // heaps are balanced- so, adding a new element to a any of heap would
                        // result in total odd number of elements, thus, an effective median is a root
                        // of that priority queue which contains more elements (+1)
                    if (less(key, effectiveMedian)) {
                        maxPQ.insert(key);
                        effectiveMedian = maxPQ.max();
                    }
                    else {
                        minPQ.insert(key);
                        effectiveMedian = minPQ.min();
                    }
                    break;
                case 1:   // 'lower' heap (MaxPQ) contains more elements than 'higher' heap (MinPQ)
                    if (less(key, effectiveMedian)) {
                        minPQ.insert(maxPQ.delMax());
                        maxPQ.insert(key);
                    }
                    else {
                        minPQ.insert(key);
                    }
                    effectiveMedian = medianCalculation(maxPQ.max(), minPQ.min());
                    break;
            }
        }
    }

    public Key findMedian() {
        return effectiveMedian;
    }

    public Key removeMedian( ) {
        if (isEmpty()) throw new NoSuchElementException("Binary heap underflow");

        // compares a size of both priority queues ('lower' heap (MaxPQ) vs 'higher' heap (MinPQ))
        int isHeapsBalanced = Integer.compare(maxPQ.size(), minPQ.size());

        switch (isHeapsBalanced) {
            case -1:  // 'higher' heap (MinPQ) contains more elements than 'lower' heap (MaxPQ)
                minPQ.delMin();
                effectiveMedian = medianCalculation(maxPQ.max(), minPQ.min());
                break;
            case 0:   // heaps are balanced- so, the 'lower' median (root of MaxPQ) to be removed (as specified), which
                      // results in total odd number of elements (unbalanced heaps) and
                      // new effective median will be a root of 'higher' heap (MinPQ) as it contains more elements (+1)
                maxPQ.delMax();
                effectiveMedian = minPQ.min();
                break;
            case 1:   // 'lower' heap (MaxPQ) contains more elements than 'higher' heap (MinPQ)
                maxPQ.delMax();
                effectiveMedian = medianCalculation(maxPQ.max(), minPQ.min());
                break;
        }
        return effectiveMedian;
    }

    public boolean isEmpty() {
        return (maxPQ.size() == 0 && minPQ.size() == 0);
    }

    public int size() {
        return maxPQ.size() + minPQ.size();
    }

    private boolean less(Key key1, Key key2) {
        if (comparator == null) {
            return key1.compareTo(key2) < 0;
        } else {
            return comparator.compare(key1, key2) < 0;
        }
    }

    /* helper methods to calculate median */
    private Key medianCalculation(Key maxPQMax, Key minPQMin) {
        List<Key> items = new ArrayList<>();
        items.add(maxPQMax);
        items.add(minPQMin);
        Key median = null;

        if (effectiveMedian.getClass() == Integer.class)
            median = (Key) averageValue(items, integerAvg);
        else if (effectiveMedian.getClass() == Double.class)
            median = (Key) averageValue(items, doubleAvg);
        else if (effectiveMedian.getClass() == Float.class)
            median = (Key) averageValue(items, floatAvg);
        else if (effectiveMedian.getClass() == Long.class)
            median = (Key) averageValue(items, longAvg);
        else median = maxPQMax;

        return median;
    }

    private static <T extends Number, R extends Number> R averageValue(Iterable<T> items, AverageCalculation<R> calc) {
        R result = calc.initialize();
        int count = 0;
        for(T val: items) {
            result = calc.add(result, val);
            count++;
        }
        return calc.divideByCount(result, count);
    }
    // Implement AverageCalculation interface for some numeric types extended from abstract class 'Number'
    private static final AverageCalculation<Double> doubleAvg = new AverageCalculation<Double>() {
        @Override
        public Double initialize() { return 0.0; }
        @Override
        public Double add(Double a, Number b) { return a + b.doubleValue(); }
        @Override
        public Double divideByCount(Double a, int b) { return a / b; }
    };
    private static final AverageCalculation<Float> floatAvg = new AverageCalculation<Float>() {
        @Override
        public Float initialize() { return 0.0f; }
        @Override
        public Float add(Float a, Number b) { return a + b.floatValue(); }
        @Override
        public Float divideByCount(Float a, int b) { return a / b; }
    };
    private static final AverageCalculation<Integer> integerAvg = new AverageCalculation<Integer>() {
        @Override
        public Integer initialize() { return 0; }
        @Override
        public Integer add(Integer a, Number b) { return a + b.intValue(); }
        @Override
        public Integer divideByCount(Integer a, int b) { return a / b; }
    };
    private static final AverageCalculation<Long> longAvg = new AverageCalculation<Long>() {
        @Override
        public Long initialize() { return 0L; }
        @Override
        public Long add(Long a, Number b) { return a + b.longValue(); }
        @Override
        public Long divideByCount(Long a, int b) { return  (a / b); }
    };

    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("No input file name.");
        In in = new In(args[0]);

        DynamicMedian<Double> intDM = new DynamicMedian<>();
        while (!in.isEmpty()) {
            intDM.insert(in.readDouble());
            StdOut.println("new effective median after insertion is: " + intDM.findMedian());
        }
        StdOut.println("new effective median after removing is: " + intDM.removeMedian());
        StdOut.println("new effective median after removing is: " + intDM.removeMedian());
        StdOut.println("new effective median after removing is: " + intDM.removeMedian());
        StdOut.println("new effective median after removing is: " + intDM.removeMedian());

    }
}
