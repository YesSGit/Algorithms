/* *****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: Nov'19
 *  Description: FastCollinearPoints.class recognizes line patterns in a given set of points, i.e.
 *  find every (maximal) line segment that connects a subset of 4 or more of the points.
 *  Algorithm design: Given a point p, one has to determine whether p participates in a set of 4 or more collinear points.
 *                    Think of p as the origin.
 *                    For each other point q, determine the slope it makes with p.
 *                    Sort the points according to the slopes they makes with p.
 *                    Check if any 3 (or more) adjacent points in the sorted order have equal slopes
 *                    with respect to p. If so, these points, together with p, are collinear.
 *  Corner cases: throw an IllegalArgumentException if the argument to the constructor is null,
 *                if any point in the array is null, or if the argument to the constructor contains
 *                a repeated point.
 *  Performance: the order of growth of the running time is n2 log n in the worst case and
 *               it uses space proportional to n plus the number of line segments returned.
 *  <p>
 *  Programme assignment "Collinear Points" from Coursera course "Algorithm Part I" (R. Sedgewick, K. Wayne)
 **************************************************************************** */

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;

public class FastCollinearPoints {
    private int numberOfSegments = 0;
    private LineSegment[] lineSegmentsArray = new LineSegment[1];


    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] inputPoints) {
        if (inputPoints == null) throw new IllegalArgumentException("No valid argument to the constructor.");

        Point[] points = inputPoints.clone(); // clone input array of type Point to ensure immutability
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException(
                    String.format("Item of Points array at index %d is null.", i));
        }
        Arrays.sort(points);
        Point prevPoint = new Point(-1, -1);
        for (int i = 0; i < points.length; i++) {
            if (points[i].compareTo(prevPoint) == 0) throw new IllegalArgumentException(
                    String.format("Item of Points array at position %d is a repeated Point.",
                                  i + 1));
            prevPoint = points[i];
        }

        int segmentPoints = 2; // tracks number of collinear points in a segment
        double slopePQ;
        Point endSegmentPoint = new Point(-1, -1);

        for (int i = 0; i < points.length; i++) {
            // make a clone of initial array to sort the points
            // according to the slopes they makes with p (as the origin on each iteration).
            Point[] pointsToSort = points.clone();
            Arrays.sort(pointsToSort, points[i].slopeOrder());

            boolean correctMaximalSegement = true;
            for (int j = 1; j < pointsToSort.length; j++) {
                slopePQ = points[i].slopeTo(pointsToSort[j]);

                if (Double.compare(slopePQ, points[i].slopeTo(pointsToSort[j - 1])) == 0) {
                    // collect only maximal line segment of collinear points in ascending order- to exclude duplicates
                    // corner case: p[i]==p[j-1]  - when j==i+1 and this point is part of a segment to be collected
                    if (points[i].compareTo(pointsToSort[j - 1]) <= 0 &&
                            pointsToSort[j - 1].compareTo(pointsToSort[j]) < 0 &&
                            correctMaximalSegement) {
                                segmentPoints += 1;
                                endSegmentPoint = pointsToSort[j];
                    } else correctMaximalSegement = false;
                }
                else if (segmentPoints >= 4 && correctMaximalSegement) {
                    addSegment(points[i], endSegmentPoint);
                    segmentPoints = 2;
                } else {
                    segmentPoints = 2;
                    correctMaximalSegement = true;
                }
            }
            // special case when collinear points where at the end of the array
            if (segmentPoints >= 4 && correctMaximalSegement) {
                // corner case - part of collinear points of a line segement are located at the end of array with POSITIVE_INFINITY slope and
                // rest are at the beginig of the array with NEGATIVE_INFINITY slope; such line segment has not to be collected
                if (Double.compare(points[i].slopeTo(pointsToSort[pointsToSort.length - 1]), Double.POSITIVE_INFINITY) == 0 &&
                points[i].compareTo(pointsToSort[0]) != 0) {
                    segmentPoints = 2;
                    correctMaximalSegement = false;
                }
                if (correctMaximalSegement) {
                    addSegment(points[i], endSegmentPoint);
                    segmentPoints = 2;
                }
            } else {
                segmentPoints = 2;
                correctMaximalSegement = true;
            }
        }
        // ensure no 'null' result-array elements
        if (lineSegmentsArray[lineSegmentsArray.length - 1] == null) {
            resize(numberOfSegments);
        }
    }
    // collect identified maximal line segment containing 4 (or more) points
    private void addSegment(Point startPoint, Point endPoint) {
        LineSegment lineSegments;  // An immutable data type for Line segment in the plane.
        lineSegments = new LineSegment(startPoint, endPoint);
        if (lineSegmentsArray.length == numberOfSegments)
            resize(2 * lineSegmentsArray.length);
        lineSegmentsArray[numberOfSegments++] = lineSegments;

    }
    // the number of line segments
    public int numberOfSegments() {
        return numberOfSegments;
    }
    // the line segments
    public LineSegment[] segments() {
       return lineSegmentsArray.clone();
    }
    private void resize(int max) {
        LineSegment[] tmpArray = new LineSegment[max];
        for (int i = 0; i < numberOfSegments; i++) {
            tmpArray[i] = lineSegmentsArray[i];
        }
        lineSegmentsArray = tmpArray;
    }

    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("No input file name.");

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        Point prevPoint = new Point(-1, -1);
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
            if (points[i] == null)
                throw new IllegalArgumentException("One point of the input array is null.");
            if (points[i].compareTo(prevPoint) == 0)
                throw new IllegalArgumentException("Repeated point to the constructor.");
            prevPoint = points[i];
        }

        // draw the points
        StdDraw.clear();
        StdDraw.enableDoubleBuffering(); // turn on animation mode
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
            StdDraw.show();
        }

        // print and draw the line segments
        FastCollinearPoints fastCollinear = new FastCollinearPoints(points);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius();

        for (LineSegment segment : fastCollinear.segments()) {
            if (segment == null) break;
            StdOut.println(segment);
            segment.draw();
            StdDraw.show();
        }

    }
}
