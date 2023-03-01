/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  Date: Feb'2023
 *  Description: This <SymbolTable> client creates a symbol table mapping letter
 *               grades to numerical scores, as in the table below, then reads
 *               from standard input a list of letter grades and computes and
 *               prints the GPA (the average of the numbers corresponding to the
 *               grades).
 *               A+   A    A-   B+   B    B-   C+   C    C-   D    F
 *               4.33 4.00 3.67 3.33 3.00 2.67 2.33 2.00 1.67 1.00 0.00
 *  Dependencies: ST.java StdIn.java StdOut.java
 *  Data files:  GPA_mapping.txt, GPA_grades.txt
 *
 *  Text-book "ALGORITHMS" fourth ed. R. Sedgewick & K. Wayne (Princeton University)
 *  Part 3.1 "Symbol Table", Exercises
 *****************************************************************************/
package com.uzviz;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class GradePointAverage {

    public static void main(String[] args) {
        In in = new In(args[0]);
        BinarySearchST<String, Float> gpaMappingST = new BinarySearchST<>();
        while (!in.isEmpty()) {
            String letterGrade = in.readString();
            // creates a symbol table mapping letter grades <Key> to numerical scores <Value>
             gpaMappingST.put(letterGrade, in.readFloat());
        }

        // reads from standard input a list of letter grades and computes and prints the GPA
        float GPA = 0.0f;
        int classes = 0;
        for (int i = 1; !StdIn.isEmpty(); i++) {
            String grade = StdIn.readString();
            GPA = (GPA + gpaMappingST.get(grade));
            classes = i;
        }
        GPA = GPA / classes;

        StdOut.println("Overall GPA by averaging the scores of all your classes: " + String.format("%.2f", GPA));
    }
}
