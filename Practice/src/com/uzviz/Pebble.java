package com.uzviz;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public final class Pebble implements Comparable<Pebble> {
    private final Color pebbleColor;
    private final char pebbleToken;

    public Pebble(Color color, char token) {
        pebbleColor = color;
        pebbleToken = token;
    }

    public void draw(double x, double y) {
        StdDraw.setPenColor(pebbleColor);
        StdDraw.text(x, y, this.toString());
    }

    public String toString() {
        return " " + pebbleToken + " ";
    }
    /**
     * Returns the pebble's color .
     * @return the color of the pebble
     */
    public Color color() { return pebbleColor; }

    /**
     * Returns the pebble's token.
     * @return the token which represents the pebble
     */
    public char token() { return pebbleToken; }
    /**
     * Compares this object with the specified object for order.
     *
     * @param that the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Pebble that) {
        if (this.pebbleColor == StdDraw.WHITE && that.pebbleColor == StdDraw.RED) return +1;
        if (this.pebbleColor == StdDraw.WHITE && that.pebbleColor == StdDraw.BLUE) return -1;
        if (this.pebbleColor == StdDraw.BLUE && that.pebbleColor == StdDraw.WHITE) return +1;
        if (this.pebbleColor == StdDraw.RED && that.pebbleColor == StdDraw.WHITE) return -1;

        return 0;
    }

    public static void main(String[] args) {
        Pebble bluePebble = new Pebble(StdDraw.BLUE, 'B');
        Pebble redPebble = new Pebble(StdDraw.RED, 'R');
        char[] tokenSet = {'W', 'B', 'B', 'R', 'B', 'W', 'W', 'B', 'R', 'R', 'R', 'B', 'W', 'R', 'W'};
        Pebble[] pebbles = new Pebble[tokenSet.length];
        for (int i = 0; i < tokenSet.length; i++) {
            if (tokenSet[i] == 'R')
                pebbles[i] = new Pebble(StdDraw.RED, tokenSet[i]);
            if (tokenSet[i] == 'W')
                pebbles[i] = new Pebble(StdDraw.WHITE, tokenSet[i]);
            if (tokenSet[i] == 'B')
                pebbles[i] = new Pebble(StdDraw.BLUE, tokenSet[i]);
        }

        // draw the pebbles
        StdDraw.clear(StdDraw.LIGHT_GRAY);
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 10));


//        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.005);
        StdDraw.enableDoubleBuffering(); // turn on animation mode
        StdDraw.setXscale(-1000, 32768);
        StdDraw.setYscale(-1000, 32768);

        bluePebble.draw(100.0, 100.0);
        redPebble.draw(1000.0, 100.0 );
        int x = 100;
        int y = 1000;

        for (int i = 0; i < pebbles.length; i++) {
            pebbles[i].draw(x, y);
            x += 1000;
        }
        StdDraw.show();

    }
}
