/* *****************************************************************************
 *  Author: Serhii Yeshchenko
 *  Date: Mar'2021
 *  Description: Coursera, Algorithm, Part I (by Princeton University)
 *               Programming assignment of week 5 - Kd-Trees.
 *
 *               Mutable data type KdTree.java that represents
 *               a set of points in the unit square (all points have x- and
 *               y-coordinates between 0 and 1) and uses a 2d-tree to support
 *               efficient range search (find all of the points contained in
 *               a query rectangle) and nearest-neighbor search (find a closest
 *               point to a query point).
 *               A 2d-tree is a generalization of a BST to two-dimensional keys.
 *               The idea is to build a BST with points in the nodes, using the
 *               x- and y-coordinates of the points as keys in strictly alternating sequence.
 *               Each node corresponds to an axis-aligned rectangle in the unit square,
 *               which encloses all of the points in its subtree.
 *               The root corresponds to the unit square; the left and right children
 *               of the root corresponds to the two rectangles split by the x-coordinate
 *               of the point at the root; and so forth.
 *
 *  Corner cases: throw an IllegalArgumentException if any argument is null.
 *  Performance requirements: insert() and contains() - in time proportional to the logarithm
 *                            of the number of points in the set in the worst case;
 *                            nearest() - typical case: log N; worst case (even if tree is balanced): N.
 *                            range()- typical case: R + log N; worst case (assuming tree is balanced): R + √N.
 *                                     N = number of keys; R = number of keys that match
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

public class KdTree {
    private Node root;
    /**
     * The number of entries in the tree
     */
    private int size = 0;

    // construct an empty 2d-tree of points
    public KdTree() {
    }

    private static class Node {
        private Point2D p;      // the point
        private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    // is the set empty?
    public boolean isEmpty() {
        if (size() == 0) return true;
        return false;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the tree (if it is not already in the tree)
    // Search for key. Update value if found. Grow tree if new.
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Called insert() with a null Point!");

        root = insert(root, 0.0, 0.0, 1.0, 1.0, p, true);
    }

    private Node insert(Node node, double xMin, double yMin, double xMax, double yMax, Point2D p, boolean split) {
        if (node == null) {
            size = size + 1;
            return new Node(p, new RectHV(xMin, yMin, xMax, yMax));
        }

        // *** alternating true and false to change orientation split (true - vertical split)
        if (split) { // vertical split
            if (p.x() < node.p.x()) {
                node.lb = insert(node.lb, node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax(), p, false);
            }
            else if (p.x() >= node.p.x())  {
                if (p.x() == node.p.x() && p.y() == node.p.y())
                    node.p = p;
                else
                    node.rt = insert(node.rt, node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax(), p, false);
            }
        }
        else { // horizontal split
            if (p.y() < node.p.y()) {
                // *** alternating true and false to change orientation split (true - vertical split)
                    node.lb = insert(node.lb, node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y(), p, true);
            }
            else if (p.y() >= node.p.y())  {
                if (p.x() == node.p.x() && p.y() == node.p.y())
                    node.p = p;
                else
                    node.rt = insert(node.rt, node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax(), p, true);
            }
        }
        return node;
    }

    // does the 2dtree contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Called contains() with a null Point!");

        // return (contains(root, p, true) == null) ? false : true;
        return contains(root, p, true) != null;
    }

    private Point2D contains(Node node, Point2D p, boolean split) {
        // split = !split; // alternating true and false to change orientation split
        if (node == null) return null;

        // Point2D foundP = null;
        if (split) {
            // vertical split
            if (p.x() < node.p.x())
                // *** alternating true and false to change orientation split
                return contains(node.lb, p, false);
            else if (p.x() >= node.p.x())
                if (p.x() == node.p.x() && p.y() == node.p.y())
                    // foundP = node.p;
                    return p;
                else
                    return contains(node.rt, p, false);
        } else {
            // horizontal split
            if (p.y() < node.p.y())
                return contains(node.lb, p, true);
            else if (p.y() >= node.p.y())
                if (p.y() == node.p.y() && p.x() == node.p.x())
                    return p;
                    // foundP = node.p;
                else
                    return contains(node.rt, p, true);
        }
         return null;
    }

    // draw all points to standard draw in black
    // and the subdivisions in red (for vertical splits)
    // and blue (for horizontal splits)
    public void draw() {
        draw(root, true);
    }
    private void draw(Node node, boolean split) {
        if (node == null) return;
        draw(node.lb, !split); // Traverse left/bottom subtree

        // plot a point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        // debug code fragment
        /*
        StdDraw.setPenRadius();
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 8));
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.text(node.p.x(), node.p.y(), node.p.toString());
        */

        StdDraw.show();
        // vertical split
        if (split) {
            // draw a red vertical line through the dot within
            // the axis-aligned rectangle corresponding to this node
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        }
        else {
            // horizontal split
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        StdDraw.show();

        draw(node.rt, !split); // Traverse right/top subtree
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Query rectangle is null");
        SET<Point2D> pointsInRect = new SET<>();

        return range(root, rect, true, pointsInRect);
    }

    private Iterable<Point2D> range(Node node, RectHV qRect, boolean split, SET<Point2D> pointsInRect) {
        // no reasons to search further - query rectangle doesn't intersect w subtree rectangle
        if (node == null || !node.rect.intersects(qRect)) return pointsInRect;

         // vertical split
         if (split) {
             // the query rectangle intersects the splitting line segment - search for points in both subtrees
             if (qRect.xmin() <= node.p.x() && qRect.xmax() >= node.p.x()) {
                 range(node.lb, qRect, !split, pointsInRect);
                 if (qRect.contains(node.p)) pointsInRect.add(node.p);
                 range(node.rt, qRect, !split, pointsInRect);
             }
             // search only the left subtree
             else if (qRect.xmax() < node.p.x()) {
                 range(node.lb, qRect, !split, pointsInRect);
                 if (qRect.contains(node.p)) pointsInRect.add(node.p);
             }
             // search only the right subtree
             else if (qRect.xmin() > node.p.x()) {
                 range(node.rt, qRect, !split, pointsInRect);
                 if (qRect.contains(node.p)) pointsInRect.add(node.p);
             }
         }
         // horizontal splits
         else {
             // the query rectangle intersects the splitting line segment - search for points in both subtrees
             if (qRect.ymin() <= node.p.y() && qRect.ymax() >= node.p.y()) {
                 range(node.lb, qRect, !split, pointsInRect);
                 if (qRect.contains(node.p)) pointsInRect.add(node.p);
                 range(node.rt, qRect, !split, pointsInRect);
             }
             // search only the bottom subtree
             else if (qRect.ymax() < node.p.y()) {
                 range(node.lb, qRect, !split, pointsInRect);
                 if (qRect.contains(node.p)) pointsInRect.add(node.p);
             }
             // search only the top subtree
             else if (qRect.ymin() > node.p.y()) {
                 range(node.rt, qRect, !split, pointsInRect);
                 if (qRect.contains(node.p)) pointsInRect.add(node.p);
             }
         }
        return pointsInRect;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw  new IllegalArgumentException("Query point is null!");
        // return null if the set is empty
        if (isEmpty()) return null;
        // initial closest neighbor is root point
        else return nearest(root, p, true, root.p);
    }

    private Point2D nearest(Node node, Point2D queryP, boolean split, Point2D pClosest) {
        if (node == null) return pClosest;
        Point2D p;

        // if the closest point discovered so far is closer than the distance between the query point
        // and the rectangle corresponding to a node, there is no need to explore that node (or its subtrees).
        if (queryP.distanceSquaredTo(pClosest) < node.rect.distanceSquaredTo(queryP)) return pClosest;

        // if a point of the node is closer than the best one found so far,-
        if (queryP.distanceSquaredTo(node.p) < queryP.distanceSquaredTo(pClosest)) {
            // update the closest point
            pClosest = node.p;
        }

        // vertical split
        if (split) {
            // choose the subtree that is on the same side of the splitting line as the query point
            // as the first subtree to explore
            if (queryP.x() - node.rect.xmin() <= node.p.x() - node.rect.xmin()) {
                // recursively search the left subtree first
                p = nearest(node.lb, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
                // then, search the right subtree
                p = nearest(node.rt, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
            }
            else { // otherwise - recursively search the right subtree first
                p = nearest(node.rt, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
                // then, search the left subtree
                p = nearest(node.lb, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
            }
        }
        // horizontal splits
        else {
            // choose the subtree that is on the same side of the splitting line as the query point
            // as the first subtree to explore
            if (queryP.y() - node.rect.ymin() <= node.p.y() - node.rect.ymin()) {
                // recurcively search the bottom subtree first
                p = nearest(node.lb, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
                // then, recurcively search the top subtree first
                p = nearest(node.rt, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
            }
            else { // otherwise - recursively search the top subtree first
                p = nearest(node.rt, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
                // then, recurcively search the bottom subtree first
                p = nearest(node.lb, queryP, !split, pClosest);
                // if it might contain a point that is closer than the best one found so far
                if (queryP.distanceSquaredTo(pClosest) > queryP.distanceSquaredTo(p)) {
                    pClosest = p;
                }
            }
        }
        return pClosest;
    }

    public static void main(String[] args) {
        double timeTotal = 0.0; // total running time to build 2d-tree in sec
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            Stopwatch timer = new Stopwatch();
            kdtree.insert(p);
            timeTotal = timeTotal + timer.elapsedTime();
        }
        StdOut.print("Total actual running time to build 2d-tree (over BST): " );
        StdOut.printf("%f", timeTotal);
        StdOut.println("s");

        Point2D f = new Point2D(0.90, 0.60);
        if (kdtree.contains(f)) StdOut.println("There is a Point: " + f.toString());
        else StdOut.println("That's not a Point: " + f.toString());

        kdtree.draw();

        RectHV qRect = new RectHV(0.4, 0.1, 0.55, 0.3);
        // draw the query rectangle
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.005);
        qRect.draw();

        // draw the range search results for kd-tree in blue
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.BLUE);
        for (Point2D p : kdtree.range(qRect))
            p.draw();

        Point2D queryP = new Point2D(0.29, 0.24); // 0.29, 0.24 / 0.975528, 0.5
        StdDraw.setPenColor(StdDraw.RED);
        queryP.draw();
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.GREEN);
        kdtree.nearest(queryP).draw();

    }
}
