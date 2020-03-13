/* *****************************************************************************
 *  Name: Serhii Yeshchenko
 *  Date: Nov'19
 *  Description: BruteCollinearPoints.java recognizes line patterns in a given set of points, i.e
 *  examines 4 points at a time and checks whether they all lie on the same line segment,
 *  returning all such line segments.
 *  Performance: The order of growth of the running time is n4 in
 *      the worst case and it uses space proportional to n plus
 *      the number of line segments returned.
 *  <p>
 *  Programme assignment "Collinear Points" from Coursera course "Algorithm Part I" (R. Sedgewick, K. Wayne)
 *  Note!!!: issue with repeated segments is not fully resolved (check input56.txt test case)
 *  also, quadruple "if" conditions should be addressed to match an effectifle code style convention
 *  **************************************************************************** */

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;

// finds all line segments containing 4 points
public class BruteCollinearPoints {
    private int numberOfSegments = 0;
    private LineSegment[] lineSegmentsArray = new LineSegment[1];

    public BruteCollinearPoints(Point[] inputPoints) {
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

        double slopePQ, slopePR, slopePS;
        double slopePrev = -0.0, slopePQprev = -0.0, slopePRprev = -0.0;

        // keeps begining and end  points of a last found segemnt with >=4 collinear points
        Point beginSegmentPoint = new Point(-1, -1);
        Point endSegmentPoint = new Point(-1, -1);
        // end point of last found segemnt with >4 collinear points
        Point endLongSegmentPoint = new Point(-1, -1);

        int n = points.length;
        int segmentPoints = 3; // tracks number of collinear points in a segment with >=4 points
        LineSegment lineSegments;  // An immutable data type for Line segments in the plane.

        // To check whether the 4 points p, q, r, and s are collinear,-
        // check whether the three slopes between p and q, between p and r,
        // and between p and s are all equal.
        for (int p = 0; p < n; p++) {
            if (p > 0) slopePrev = points[p-1].slopeTo(points[p]);
            for (int q = p + 1; q < n; q++) {
                slopePQ = points[p].slopeTo(points[q]);
                if ((Double.compare(slopePQ, slopePrev) == 0 || Double.compare(slopePQ, slopePQprev) == 0) &&
                        points[p].compareTo(beginSegmentPoint) == 0) continue; // avoid repeated segment
                slopePQprev = slopePQ;
                for (int r = q + 1; r < n; r++) {
                    slopePR = points[p].slopeTo(points[r]);
                    // avoid repeated segment: - current slope p->r is different from slope p->r of last collected segment
                    //                         - current base point p (begining of segment) is different from p of last collected segment
                    if (Double.compare(slopePR, slopePRprev) == 0 && points[p].compareTo(beginSegmentPoint) == 0) continue;
                    slopePRprev = slopePR;
                    // no needs to consider whether 4 points are collinear in case the first 3 are not collinear;
                    // i.e. if slopePQ == slopePR - first 3 points are collinear
                   if (Double.compare(slopePQ, slopePR) == 0) {
                       int lastPoint = -1;
                       for (int s = r + 1; s < n; s++) {
                           slopePS = points[p].slopeTo(points[s]);
                           if (Double.compare(slopePQ, slopePS) == 0) {
                               lastPoint = s;
                               segmentPoints += 1;
                           }
                       }
                       if (lastPoint >= 0) {
                           if (segmentPoints > 4)
                               endLongSegmentPoint = points[lastPoint];
                           segmentPoints = 3;
                           // check whether new identified segment is not the same with one already collected:
                           // - their slopes between begin-segment-point and end-segemnt-point are not equal (not enough in case of parallel line segemnts);
                           // OR - their end-segemnt-points are not equal (case with parallel segments),
                           //      two options:1) compare with the end-segemnt-point (== 4 collinear points) of the previously collected segemnt
                           //                  2) compare with the end-LONG-segemnt-point (> 4 collinear points) of previously collected LONG segment
                           if (Double.compare(points[p].slopeTo(points[lastPoint]), beginSegmentPoint.slopeTo(endSegmentPoint)) != 0 ||
                           points[lastPoint].compareTo(endSegmentPoint) != 0 || points[lastPoint].compareTo(endLongSegmentPoint) != 0) {
                               // if 4 or more points are collinear - add new LineSegemnt to array (only endpoints)
                               lineSegments = new LineSegment(points[p], points[lastPoint]);
                               if (lineSegmentsArray.length == numberOfSegments)
                                   resize(2 * lineSegmentsArray.length);
                               lineSegmentsArray[numberOfSegments++] = lineSegments;
                               beginSegmentPoint = points[p];
                               endSegmentPoint = points[lastPoint];
                           }
                       }
                   }
                }
            }
        }
        // ensure no 'null' result-array elements
        if (lineSegmentsArray[lineSegmentsArray.length - 1] == null) {
            resize(numberOfSegments);
        }
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius();

        for (LineSegment segment : collinear.segments()) {
            if (segment == null) break;
            StdOut.println(segment);
            segment.draw();
            StdDraw.show();
        }
    }
}
