/* *****************************************************************************
 *  Name: S.Yeshchenko (source https://ideone.com/hx0jF1)
 *  Date: Dec'2019
 *  Description: AverageCalculation.java - abstract interface which defines
 *       methods initialize(), add(T a, Number b), divideByCount(T a, int b)
 *       to facilitate of average calculation for any type extends Number
 *       abstract class.
 *  Implementation example: DynamicMedian.java, AverageMaker.java
 ******************************************************************************/
package com.uzviz;

public interface AverageCalculation<T extends Number> {
    T initialize();
    T add(T a, Number b);
    T divideByCount(T a, int b);
}