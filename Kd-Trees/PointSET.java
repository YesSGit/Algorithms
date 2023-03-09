/* *****************************************************************************
 *  Author: Serhii Yeshchenko
 *  Date: Mar'2021
 *  Description: Coursera, Algorithm, Part I (by Princeton University)
 *               Programming assignment of week 5 - Kd-Trees.
 *               Mutable data type PointSET.java represents a set of points
 *               in the unit square (all points have x- and y-coordinates between 0 and 1).
 *
 *  Corner cases: throw an IllegalArgumentException if any argument is null.
 *  Performance requirements: insert() and contains() - in time proportional to the logarithm
 *                            of the number of points in the set in the worst case;
 *                            nearest() and range() in time proportional to the number of points in the set.
 *
 *  Dependencies: edu.princeton.cs.algs4.Point2D.java - represents points in the plane,
 *                RectHV.java - represents axis-aligned rectangles, In.java, StdOut.java
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class PointSET {
    private final SET<Point2D> set2D; // represents an ordered set of comparable keys - 2d-points in the unit square

    // construct an empty set of points
    public PointSET() {
        set2D = new SET<Point2D>();
    }
    // is the set empty?
    public boolean isEmpty() {
        return set2D.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set2D.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Called insert() with a null Point!");
        set2D.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Called contains() with a null Point!");
        return set2D.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set2D) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            p.draw();

            // debug code fragment
            /*
            StdDraw.setPenRadius();
            StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 8));
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.text(p.x(), p.y(), p.toString());
             */
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Query rectangle is null");
        SET<Point2D> pointsInRect = new SET<>();

        for (Point2D p : set2D) {
            if (rect.contains(p)) {
                pointsInRect.add(p);
            }
        }
        return pointsInRect;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point is null!");
        Point2D nearstP = null;
        double prevDist = 1.1; // more than 'max' possible distance
        for (Point2D currP : set2D) {
            if (p.distanceSquaredTo(currP) < prevDist) {
                nearstP = currP;
                prevDist = p.distanceSquaredTo(currP);
            }
        }
        return nearstP;
    }

    public static void main(String[] args) {
        double timeTotal = 0.0; // total running time to build 2d-tree in sec

        String filename = args[0];
        In in = new In(filename);

        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p2D = new Point2D(x, y);
            Stopwatch timer = new Stopwatch();
            brute.insert(p2D);
            timeTotal = timeTotal + timer.elapsedTime();
        }
        StdOut.print("Total actual running time to build 2d-tree (Brute-force): " );
        StdOut.printf("%f", timeTotal);
        StdOut.println("s");

        // Point2D pTop = new Point2D(0.216107, 0.904508);
        Point2D pTop = new Point2D(0.975528, 0.5);

        // StdDraw.enableDoubleBuffering();  // turn on animation mode
        StdDraw.clear();
        brute.draw();
        // StdDraw.show();

        StdDraw.setPenRadius(0.02);
        // draw in red the nearest neighbor (using brute-force algorithm)
        StdDraw.setPenColor(StdDraw.RED);
        pTop.draw();
        // draw in green the nearest neighbor (using brute-force algorithm)
        StdDraw.setPenColor(StdDraw.GREEN);
        brute.nearest(pTop).draw();

    }
}
