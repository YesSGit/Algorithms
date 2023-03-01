/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  (based on R. Sedgewick, K. Wayne from Coursera, ALGORITHM Part I)
 *  Date: Nov'2020
 *  Description: WordIndex.java provides a client which takes a sequence of n
 *               document words from a file, specified as a command-line argument.
 *               It takes sequence of m query words and find
 *               the shortest interval in which the m query words appear
 *               in the document in the order given.
 *               The length of an interval is the number of words in that interval.
 *  Data files:   WordIndex_input.txt, WordIndex_input_query.txt
 *  Performance:
 *  Improvement:
 *
 *  <p
 *  Coursera, Algorithm Part 1, Week 5, Interview question 2 (Balanced Search Trees)
 **************************************************************************** */
package com.uzviz;

import edu.princeton.cs.algs4.*;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class WordIndex {
    private WordIndex() {}; // do not instantiate

    public static void main(String[] args) throws IOException {
        // key = word, value = set of Integer indexes pointing out this query word position(s) inside file
        ST<String, SET<Integer>> st = new ST<String, SET<Integer>>();

        String fileWithQuery = args[0];
        File fileQuery = new File(fileWithQuery);
        In inQuery = new In(fileQuery);            // initialize a sequence of m query words

        String filename;
        for (int i = 1; i < args.length; i++) {
            filename = args[i];
            StdOut.println("  " + filename);

            File file = new File(filename);
            In in = new In(file);
            int index = 1; // index(sequence number) of a word inside the curr file
            while(!in.isEmpty()) {
                String word = in.readString();

                while(!inQuery.isEmpty()) {
                    String query = inQuery.readString();          // read the next token of the query
                        if (word.equalsIgnoreCase(query)) {
                            if(!st.contains(word))
                                st.put(word, new SET<Integer>()); // put into ST the Key (word) and Value, is initialized by SET of Integer (empty by default)
                            SET<Integer> set = st.get(word);      // initialize 'set' by Value (empty one if the Key is just put) via Key (word)
                            set.add(index);                       // replace 'empty one' by the index of the current word
                            break;
                        }
                    }
                index++;
                inQuery = new In(fileQuery);
            }
            for (String key: st.keys()) {
                SET<Integer> set = st.get(key);
                for (Integer val : set) {
                    StdOut.println("'"+ key + "'" + " at index " + val + " inside " + file.getName());
                }
            }

            inQuery = new In(fileQuery);          // iitialize a sequence of m query words
            String query = inQuery.readString();  // read the next token of the query
            SET<Integer> set = st.get(query);     // get all token(word) values(word's indexes inside the doc)
            if (set == null) throw new IllegalArgumentException("The key: '"+ query + "' is not in this symbol table");
            int wordInterval = 0;

            // keeps the shortest interval in which the m query words appear in the document in the order given.
            int shortestInterval = 0;

            // iterates through a set of values of a first query token  (all found inside the document)
            // - as the shortest interval is the shortest length between the last query token found inside the doc and the first one
            for (Integer frstTknIndx : set) {
                int wordIndx = frstTknIndx;   // index of a word inside the doc - is initialized by the first query token value

                while (!inQuery.isEmpty()) {
                    query = inQuery.readString();   // read the next token of the query
                    set = st.get(query);            // get all token(word) values(word's indexes inside the doc)
                    if (set == null) throw new IllegalArgumentException("The key: '"+ query + "' is not inside this document");

                    // iterates through a set of values (word's indexes inside the doc) of the current query token
                    for (Integer val : set) {
                        // we have to find the next query token value which is > than value of the prev query token
                        // to meet the requirement: "the m query words appear in the document in the order given"
                        if (wordIndx < val) {  // as the closest next token is found
                            wordIndx = val;    // - keep index of the word
                            break;             // - break to move on the next query token
                        }
                    }
                    // the length of an interval between the current query token value and the first query token value,
                    // i.e. current shortest interval
                    wordInterval = wordIndx - frstTknIndx;
                }
                if (shortestInterval == 0) shortestInterval = wordInterval;
                else if (wordInterval < shortestInterval) shortestInterval = wordInterval; // ensure

                // initialize again the stream of the sequence of m query words (for next iteration)
                inQuery = new In(fileQuery);
                // read the first token of the query to begin a next iteration from the second query token
                if (inQuery.readString() == null) throw new NoSuchElementException("The input stream is empty");
            }
            // "+1" - to account for substraction when calculates the word interval
            StdOut.println(shortestInterval + 1 + " : is the shortest interval in which the m query words appear in the document in the order given");
        }

    }
}
