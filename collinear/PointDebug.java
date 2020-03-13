/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 *  Note: PointDebug.class is the debug version to be used with BruteCollinearPointsDebug.class
 *        and FastCollinearPointsDebug.class
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Font;
import java.util.Comparator;

public class PointDebug implements Comparable<PointDebug> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    // private final Comparator<PointDebug> SLOPE_ORDER = new SlopeOrder();

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public PointDebug(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(PointDebug that) {
        // debug code fragment
        StdDraw.setPenColor(StdDraw.BLUE);

        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);

        // debug code fragment
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 8));
         StdDraw.setPenColor(StdDraw.MAGENTA);
        StdDraw.text((double) this.x, (double) this.y + 250.0, this.toString());
        StdDraw.text((double) that.x - 250.0, (double) that.y , that.toString(), 90);

    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(PointDebug that) {
        if (this.x - that.x == 0 && this.y - that.y == 0)
            return Double.NEGATIVE_INFINITY;                             // degenerate line (p0==p1)
        if (this.x - that.x == 0) return Double.POSITIVE_INFINITY;       // vertical line
        //        if (this.x == 0 && that.x == 0) return Double.POSITIVE_INFINITY; // vertical line
        //        if (this.y == 0 && that.y == 0) return +0.0;                     // horizontal line
        if (this.y - that.y == 0) return +0.0;                           // horizontal line
        return ((double) (that.y - this.y)) / (that.x - this.x);
//        return (double) (that.y - this.y) / (double) (that.x - this.x);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(PointDebug that) {
        if (this.y < that.y) return -1;
        if (this.y == that.y && this.x < that.x)  return -1;
        if (this.x == that.x && this.y == that.y) return 0;
        return 1;
    }

    private static boolean less(Comparator<PointDebug> c, PointDebug v, PointDebug w) {
        return c.compare(v, w) < 0;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<PointDebug> slopeOrder() {
        /* YOUR CODE HERE */
        //return this.SLOPE_ORDER;
        return new SlopeOrder();
    }

    private class SlopeOrder implements Comparator<PointDebug> {
        public int compare(PointDebug p1, PointDebug p2) {
            // double dp1 = slopeTo(p1);
            // double dp2 = slopeTo(p2);

            if (slopeTo(p1) < slopeTo(p2)) return -1;
            if (slopeTo(p1) > slopeTo(p2)) return 1;
            return 0;
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        /* YOUR CODE HERE */
        double slope;
        PointDebug pointP = new PointDebug(10499, 29557);
        PointDebug pointQ = new PointDebug(10499, 29481);

        System.out.println(ObjectSizeFetcher.getObjectSize(pointP));

        slope = pointP.slopeTo(pointQ);
        StdOut.println("slope between P" + pointP.toString() + " and Q" + pointQ.toString() + " : " + slope);

    }
}
