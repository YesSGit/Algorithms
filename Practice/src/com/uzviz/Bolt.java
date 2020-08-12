/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: Jun'2020
 *  Description: Type Bolt.java implements specific Fastener.java type to support
 *               FitNutsAndBolts.java in resolving "nuts and bolts" problem:
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

public final class Bolt extends Fastener {
    /**
     * Type's fields represents labeling of 'Metric bolt/screw callout'
     * like M12 x 1.75 x 85, i.e. diam x pitch x length
     */
    private static final String type= "Bolt"; // name of item type
    private static final char form = 'M';     // thread series designation (for example M or MJ in Metric system)
    private final short diameter;             // fastener nominal size (nominal major diameter) , mm.
    private final float pitch;                // bolt's thread pitch, mm
    private final short length;               // bolt's length, mm (measured from the end of the shaft to the bottom of the head)

    /**
     * Initializes a new bolt (x, y).
     * @param d the bolt's diameter
     * @param p the bolt's pitch
     * @param l the bolt's length
     * @throws IllegalArgumentException if {@code p}
     *    is {@code Float.NaN}, {@code Float.POSITIVE_INFINITY} or
     *    {@code Float.NEGATIVE_INFINITY}
     */
    public Bolt(short d, float p, short l) {
        super(d, p);

        diameter = d;
        pitch = p;
        length = l;
    }

    @Override
    public String toString(){
        return type + " " + form + diameter + " x " + pitch +  " x " + length;
    }

    public static void  main(String[] args) {
        int num = StdIn.readInt();
        Bolt[] boltHeap = new Bolt[num];
        for (int i = 0; i < num; i++) {
            boltHeap[i] = new Bolt(StdIn.readShort(), StdIn.readFloat(), StdIn.readShort());
        }

        for (int i = 0; i < num; i++)
            StdOut.println(boltHeap[i]);
    }

}
