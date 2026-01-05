import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 *  The {@code Outcast} class represents an immutable data type for identifying an "outcast"
 *  from a list of WordNet nouns. An outcast is defined as the noun that is the least related
 *  to the others in the group, determined by calculating the sum of semantic distances
 *  between it and every other noun in the list.
 *
 *  <p><b>Performance requirements:</b></p>
 *  <ul>
 *    <li><b>Constructor:</b> Takes constant time O(1).</li>
 *    <li><b>outcast():</b> For an input of <i>n</i> nouns, the method performs <i>n<sup>2</sup></i>
 *        distance computations. Given that each {@code WordNet.distance()} call takes time
 *        proportional to <i>V + E</i>, the total running time is proportional to
 *        <i>n<sup>2</sup> &middot; (V + E)</i>.</li>
 *  </ul>
 *
 *  <p><b>Dependencies:</b></p>
 *  <ul>
 *    <li>{@code WordNet} - For semantic distance calculations.</li>
 *    <li>{@code edu.princeton.cs.algs4.In}</li>
 *    <li>{@code edu.princeton.cs.algs4.StdOut}</li>
 *  </ul>
 *
 *  @author Serhii Yeshchenko
 *  @version 1.0
 *  @since October 2025
 */
public class Outcast {
    private final WordNet wordNet;

    /**
     * Initializes an {@code Outcast} object for the given {@code WordNet} semantic lexicon.
     *
     * <p><b>Performance:</b> Takes constant time O(1).</p>
     *
     * @param wordnet the WordNet semantic lexicon to be used for distance calculations;
     *                must not be null
     * @throws IllegalArgumentException if the {@code wordnet} argument is {@code null}
     */
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("The input 'wordnet' cannot be null");
        this.wordNet = wordnet;
    }

    /**
     * Determines the outcast among a list of WordNet nouns. An outcast is the noun
     * that is the least related to the others in the list, based on the
     * semantic distances between the nouns in the WordNet digraph.
     *
     * @param nouns an array of nouns to analyze; each noun should be only valid
     *              WordNet nouns (and that input contains at least two such nouns).
     * @return the outcast noun, which is the noun most distantly related
     *         to the others in the provided list
     * @throws IllegalArgumentException if the {@code nouns} argument is {@code null}
     * @throws IllegalArgumentException if the {@code nouns} argument consists of less than 2 nouns
     *
     */
    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException("The input 'nouns' cannot be null");
        if (nouns.length < 1) throw new IllegalArgumentException("The input 'nouns' cannot be less than 2 nouns");
        String outcast = "";
        int distanceSumTmp;
        int distanceSumMax = 0;
        for (int i = 0; i < nouns.length; i++) {
            distanceSumTmp = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (!nouns[i].equals(nouns[j])) {
                    distanceSumTmp = distanceSumTmp + wordNet.distance(nouns[i], nouns[j]);
                }
            }
            if (distanceSumTmp > distanceSumMax) {
                distanceSumMax = distanceSumTmp;
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
