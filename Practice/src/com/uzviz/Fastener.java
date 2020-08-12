/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: Jun'2020
 *  Description: Type Fastener.java is base class for 'fastener' types Nut.java and
 *               Bolt.java (and possibly any 'spiralock' types of fastener)
 *               Supports FitNutsAndBolts.java in resolving a
 *               "nuts and bolts" problem).
 *
 *
 *  Performance:
 *
 *  Property: overrides compareTo method to compare different types
 *            of fasteners between each other
 *
 * Dependencies:
 *  <p>
 *  Coursera, ALGORITHM Part I, week 3, Interview questions 2 (Quicksort)
 **************************************************************************** */
package com.uzviz;

public class Fastener implements Comparable<Fastener> {

    private short diameter;               // fastener nominal size (nominal major diameter)
    private float pitch;                  // fastener's thread pitch

    // package-private constructor
    Fastener(short d, float p) {
        if (Float.isInfinite(p) )
            throw new IllegalArgumentException("Pitch parameter must be finite");
        if (Float.isNaN(p))
            throw new IllegalArgumentException("Pitch parameter cannot be NaN");

        this.diameter = d;
        this.pitch = p;
    }

    /**
     * Compares this object with the specified object for order.
     * @param that the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Fastener that) {
        if (this.diameter > that.diameter) return +1;
        else if (this.diameter < that.diameter) return -1;
        else if (this.pitch > that.pitch) return +1;
        else if (this.pitch < that.pitch) return -1;
        else return 0;
    }
}
