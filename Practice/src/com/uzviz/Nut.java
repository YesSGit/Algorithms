/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: Jun'2020
 *  Description: Type Nut.java implements specific Fastener.java type to support
 *               FitNutsAndBolts.java in resolving "nuts and bolts" problem.
 *               "There is a mixed pile of nuts and bolts. FitNutsAndBolts.java finds
 *               the corresponding pairs of nuts and bolts. Each nut fits exactly
 *               one bolt and each bolt fits exactly one nut."
 *
 *  Performance:
 *
 *  Property:
 *
 * Dependencies: Fastener.java
 *  <p>
 *  Coursera, ALGORITHM Part I, week 3, Interview questions 2 (Quicksort)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public final class Nut extends Fastener {
    /**
     * Type's fields represents labeling of 'Metric nut callout'
     * like M12 x 1.75 x 10, i.e. diam x pitch x thickness
     */
    private static final String type= "Nut"; // name of item type
    private static final char form = 'M';    // thread series designation, mm
    private final short diameter;            // the size associated with nut threads is the nominal diameter, mm.
    private final float pitch;               // nut's pitch, mm
    private final float thickness;           // nut's thickness, mm

    /**
     * Initializes a new nut (x, y).
     * @param d the nut's diameter
     * @param p the nut's pitch
     * @param t the nut's thickness
     * @throws IllegalArgumentException if {@code p}
     *    is {@code Float.NaN}, {@code Float.POSITIVE_INFINITY} or {@code Float.NEGATIVE_INFINITY}
     * @throws IllegalArgumentException if {@code t}
     *    is {@code Float.NaN}, {@code Float.POSITIVE_INFINITY} or {@code Float.NEGATIVE_INFINITY}
     */
    public Nut(short d, float p, float t) {
        super(d, p);
        if (Float.isInfinite(t) )
            throw new IllegalArgumentException("Thickness parameter must be finite");
        if (Float.isNaN(t))
            throw new IllegalArgumentException("Thickness parameter cannot be NaN");

        diameter = d;
        pitch = p;
        thickness = t;
    }

    public String toString(){
        return type + " " + form + diameter + " x " + pitch +  " x " + thickness;
    }

    public static void  main(String[] args) {
        int num = StdIn.readInt();
        Nut[] nutHeap = new Nut[num];
            for (int i = 0; i < num; i++) {
            nutHeap[i] = new Nut(StdIn.readShort(), StdIn.readFloat(), StdIn.readFloat());
        }

            for (int i = 0; i < num; i++)
                StdOut.println(nutHeap[i]);
    }

}
