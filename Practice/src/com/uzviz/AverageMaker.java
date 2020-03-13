/* *****************************************************************************
 *  Name: S.Yeshchenko (source https://ideone.com/hx0jF1)
 *  Date: Dec'2019
 *  Description: AverageMaker.java calculates average for
 *               any numeric types which extend Java Number abstract class.
 *  Dependencies: AverageCalculation.java - abstract interface which defines
 *       methods initialize(), add(T a, Number b), divideByCount(T a, int b)
 ******************************************************************************/
package com.uzviz;

import edu.princeton.cs.algs4.StdOut;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

class AverageMaker{

    public static <T extends Number, R extends Number> R averageValue(Iterable<T> items, AverageCalculation<R> calc) {
        R result = calc.initialize();
        int count = 0;
        for(T val: items) {
            result = calc.add(result, val);
            count++;
        }
        return calc.divideByCount(result, count);
    }

    // Implement AverageCalculation interface for some numeric types extended from 'Number'
    static final AverageCalculation<Double> doubleAvg = new AverageCalculation<Double>() {
        @Override
        public Double initialize() {
            return 0.0;
        }
        @Override
        public Double add(Double a, Number b) {
            return a + b.doubleValue();
        }
        @Override
        public Double divideByCount(Double a, int b) {
            return a / b;
        }
    };
    static final AverageCalculation<Float> floatAvg = new AverageCalculation<Float>() {
        @Override
        public Float initialize() {
            return 0.0f;
        }
        @Override
        public Float add(Float a, Number b) {
            return a + b.floatValue();
        }
        @Override
        public Float divideByCount(Float a, int b) {
            return a / b;
        }
    };
    static final AverageCalculation<Integer> integerAvg = new AverageCalculation<Integer>() {
        @Override
        public Integer initialize() {
            return 0;
        }
        @Override
        public Integer add(Integer a, Number b) {
            return a + b.intValue();
        }
        @Override
        public Integer divideByCount(Integer a, int b) {
            return a / b;
        }
    };

    public static void main(String[] args) throws java.lang.Exception {
        List<Integer> items = new ArrayList<>();
        items.add(4);
        items.add(8);
        items.add(91);
        items.add(18);

        double avgDouble = averageValue(items, doubleAvg);
        int avgInt = averageValue(items, integerAvg);
        float avgFloat = averageValue(items, floatAvg);
        StdOut.println(avgDouble);
        StdOut.println(avgInt);
        StdOut.println(avgFloat);
    }
}