/*************************************************************************
 *  Compilation:  javac LineSegment.java
 *  Execution:    none
 *  Dependencies: Point.java
 *
 *  An immutable data type for Line segments in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 *  DO NOT MODIFY THIS CODE.
 *
 *  Note: LineSegmentDebug.class is the debug version to be used with BruteCollinearPointsDebug.class,
 *        FastCollinearPointsDebug.class and PointDebug.class
 *************************************************************************/

public class LineSegmentDebug {
    private final PointDebug p;   // one endpoint of this line segment
    private final PointDebug q;   // the other endpoint of this line segment

    /**
     * Initializes a new line segment.
     *
     * @param  p one endpoint
     * @param  q the other endpoint
     * @throws NullPointerException if either <tt>p</tt> or <tt>q</tt>
     *         is <tt>null</tt>
     */
    public LineSegmentDebug(PointDebug p, PointDebug q) {
        if (p == null || q == null) {
            throw new NullPointerException("argument is null");
        }
        this.p = p;
        this.q = q;
    }

    
    /**
     * Draws this line segment to standard draw.
     */
    public void draw() {
        p.drawTo(q);
    }

    /**
     * Returns a string representation of this line segment
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this line segment
     */
    public String toString() {
        return p + " -> " + q;
    }

    /**
     * Throws an exception if called. The hashCode() method is not supported because
     * hashing has not yet been introduced in this course. Moreover, hashing does not
     * typically lead to good *worst-case* performance guarantees, as required on this
     * assignment.
     *
     * @throws UnsupportedOperationException if called
     */
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

}

