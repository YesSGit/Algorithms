import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

/**
 *  The {@code WordNet} class represents a semantic lexicon for the English language.
 *  It groups words into sets of synonyms called <i>synsets</i> and describes semantic
 *  relationships between them. The {@code WordNet} digraph is a rooted Directed Acyclic Graph (DAG),
 *  where each vertex represents a synset and each directed edge represents a hypernym relationship.
 *
 *  <p><b>Performance requirements:</b></p>
 *  <ul>
 *    <li><b>Space:</b> The {@code WordNet} object uses space linear in the input size
 *        (the size of the synsets and hypernyms files).</li>
 *    <li><b>Constructor:</b> Takes time linearithmic in the input size.</li>
 *    <li><b>isNoun():</b> Runs in time logarithmic (or constant on average) in the number of nouns.</li>
 *    <li><b>distance() and sap():</b> Run in time linear in the size of the {@code WordNet} digraph.</li>
 *  </ul>
 *
 *  <p><b>Dependencies:</b></p>
 *  <ul>
 *    <li>{@code edu.princeton.cs.algs4.Bag}</li>
 *    <li>{@code edu.princeton.cs.algs4.Digraph}</li>
 *    <li>{@code edu.princeton.cs.algs4.In}</li>
 *    <li>{@code edu.princeton.cs.algs4.Queue}</li>
 *    <li>{@code edu.princeton.cs.algs4.SeparateChainingHashST}</li>
 *    <li>{@code edu.princeton.cs.algs4.Topological}</li>
 *    <li>{@code SAP} (Shortest Ancestral Path data type)</li>
 *  </ul>
 *
 *  @author Serhii Yeshchenko
 *  @version 1.0
 *  @since May 2025
 */
public class WordNet {
    // private static final String NEWLINE = System.getProperty("line.separator");
    private static final int INFINITY = Integer.MAX_VALUE;
    // private String graphInDOT;  //  representation of digraph in DOT format

    // symbol table which represents for each noun all synsets id where this noun appears
    private SeparateChainingHashST<String, Bag<Integer>> synsetsST;
    private int synsetsN;           // number of synsets in the input file
    private String[] synsetsByID;   // array which represents all sysnsets from input file (index is sysnset's id)

    // number of roots - wether the input to the constructor does correspond to a rooted DAG?
    private int rootsN;

    // immutable data type for finding a SAP (shortest ancestral path) object for the given directed graph.
    private SAP sap;
    private int sapDistance = INFINITY;

    private String wordA = ""; // a WordNet noun A
    private String wordB = ""; // a WordNet noun B

    /**
     * Constructor takes the name of the two input files:
     * @param synsets name of input file contains list of noun synsets, one per line.
     *               Line <i>i</i> of the file (counting from 0) contains the information for synset <i>i</i>.
     *               The first field is the synset <i>id</i>, which is always the integer <i>i</i>;
     *               the second field is the synonym set (or <i>synset</i>);
     *               and the third field is its dictionary definition (or <i>gloss</i>)
     * @param hypernyms name of input file contains the hypernym relationships.
     *                  Line <i>i</i> of the file (counting from 0) contains the hypernyms of synset <i>i</i>.
     *                  The first field is the synset <i>id</i>, which is always the integer <i>i</i>;
     *                  subsequent fields are the <i>id</i> numbers of the synset’s hypernyms.
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("The 'synsets' file name and 'hypernyms' file name cannot be null.");

        rootsN = 0;
        readSynsets(synsets);
        // if the digraph has a topological order (or equivalently, if the digraph is a DAG) and has it one root?
        if (!readHypernyms(hypernyms) || rootsN != 1)
            throw new IllegalArgumentException("The input to the constructor does not correspond to a rooted DAG.");

    }

    /**
     * Reads in and parses input file, builds symbol table of {@code String, Bag<Integer>}  key-value pairs
     * which represents list of noun synsets from input file, where key is the unique noun from all
     * of the synonym sets and value is a set of synsets <i>id</i> where the noun is appeared
     * (once for each meaning that the noun has).
     *
     * @param synsets name of input file contains list of noun synsets, one per line.
     */
    private void readSynsets(String synsets) {
        synsetsST = new SeparateChainingHashST<>();
        synsetsByID = new String[2];
        synsetsN = 0;

        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] line = in.readLine().split(",");    // for comma-separated input values of the synsets file
            int id = Integer.parseInt(line[0]);                // synset id

            if (synsetsN == synsetsByID.length) {
                resize(2 * synsetsByID.length);
            }
            // collect noun synset into array (index is sysnset's id)
            synsetsByID[id] = line[1];
            synsetsN++;                // count actual # of collected synsets from input == size of the array

            String[] synset = synsetsByID[id].split(" "); // synonym set (or synset)
            // String gloss = line[2].trim();                      // dictionary definition (or gloss)

            for (int i = 0; i < synset.length; i++) {
                if (synsetsST.contains(synset[i])) {
                    Bag<Integer> bag = synsetsST.get(synset[i]);
                    bag.add(id);
                    synsetsST.put(synset[i], bag);
                }
                else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(id);
                    synsetsST.put(synset[i], bag);
                }

            }
        }
        if (synsetsN < synsetsByID.length) {
            resize(synsetsN);
        }
    }

    /**
     * Reads in and parses input file, builds WordNet digraph: each vertex <i>v</i> is an integer that represents a synset,
     * and each directed edge <i>v→w</i> represents that <i>w</i> is a hypernym of <i>v</i>.
     * The WordNet digraph is a rooted DAG: it is acyclic and has one vertex—the <i>root</i> — that is an ancestor of every other vertex.
     * @param hypernyms name of input file contains the hypernym relationships.
     */
    private boolean readHypernyms(String hypernyms) {
        // WordNet digraph: each vertex v is an integer that represents a synset,
        // and each directed edge v → w represents that w is a hypernym(more general synset) of v.
        Digraph wordNet = new Digraph(synsetsN);

        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] line = in.readLine().split(","); // for comma-separated input values of the hypernyms file
            int id = Integer.parseInt(line[0]);              // synset id
            if (line.length > 1) {
                for (int i = 1; i < line.length; i++) {
                    wordNet.addEdge(id, Integer.parseInt(line[i]));
                }
            }
            else rootsN++; // rooted DAG has the only one root
        }

        sap = new SAP(wordNet);
        Topological topo = new Topological(wordNet);

        // String representation of this digraph in DOT format, suitable for visualization with Graphviz.
        // graphInDOT = wordNet.toDot();
        // Out out = new Out("digraph-wordnet50000.txt");
        // out.println(wordNet.toString());
        return topo.hasOrder();
    }

    /**
     *
     * @return all WordNet nouns
     */
    public Iterable<String> nouns() {
        Queue<String> queNouns = new  Queue<>();
        for (String s : synsetsST.keys()) {
            queNouns.enqueue(s);
        }
        return  queNouns;
    }

    /**
     * Is the word a WordNet noun?
     * @param word  a noun,- were said to name objects and other entities
     * @return <i>true</i> - if the word is WordNet noun, <i>false</i> - otherwise
     */
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("The input 'word' cannot be null");
        return synsetsST.contains(word);
    }

    /**
     * Computes distance between nounA and nounB.
     * @param nounA a WordNet noun
     * @param nounB a WordNet noun
     * @return distance of a shortest ancestral path between nounA and nounB or INFINITY if no such path exists
     * @throws IllegalArgumentException any of the noun arguments to the method is null
     * @throws IllegalArgumentException any of the noun arguments is not a WordNet noun
     */
    public int distance(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);

        if ((this.wordA.equals(nounA) && this.wordB.equals(nounB)) ||
                (this.wordA.equals(nounB) && this.wordB.equals(nounA)))
            return sap.length(synsetsST.get(nounA), synsetsST.get(nounB));
        else {
            sapDistance = INFINITY;
            this.wordA = nounA;
            this.wordB = nounB;

            // if (sap == null) sap = new SAP(wordNet);
            // commonAncestorSynset = synsetsByID[sap.ancestor(synsetsST.get(nounA), synsetsST.get(nounB))];
            sapDistance = sap.length(synsetsST.get(nounA), synsetsST.get(nounB));
        }
        return sapDistance;
    }

    /**
     * Finds a synset (second field of 'synsets' input file) that is the common ancestor of nounA and nounB
     * in a shortest ancestral path.
     *
     * @param nounA a WordNet noun
     * @param nounB a WordNet noun
     * @return a synset that is the common ancestor of nounA and nounB or <i>null</i> if there is no common ancestor
     * @throws IllegalArgumentException any of the noun arguments to the method is null
     * @throws IllegalArgumentException any of the noun arguments is not a WordNet noun
     */
    public String sap(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);

        Bag<Integer> subsetA, subsetB;
        // get a set of synsets' "id"s where the noun is appeared (once for each meaning that the noun has)
        subsetA = synsetsST.get(nounA);
        subsetB = synsetsST.get(nounB);

        if ((nounA.equals(wordA) && nounB.equals(wordB)) ||
                (nounA.equals(wordB) && nounB.equals(wordA)))
            return synsetsByID[sap.ancestor(subsetA, subsetB)];
        wordA = nounA;
        wordB = nounB;

        return synsetsByID[sap.ancestor(subsetA, subsetB)];
    }

    private void validateNoun(String noun) {
        if (noun == null) throw new IllegalArgumentException("The input 'noun' cannot be null");
        if (!synsetsST.contains(noun)) {
            throw new IllegalArgumentException("The input 'noun' does not correspond to a synset.");
        }
    }

    private void resize(int newCapacity) {
        String[] temp = new String[newCapacity];
        for (int i = 0; i < synsetsN; i++) {
            temp[i] = synsetsByID[i];
        }
        synsetsByID = temp;
    }

    // /**
    //  * Returns the string representation of this WordNet digraph in DOT format,
    //  * suitable for visualization with Graphviz.
    //  * @return string representation of the digraph in DOT format
    //  */
    // private String getGraphInDOT() {
    //     return graphInDOT;
    // }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        String nounA = "magnetic_flux_unit";
        String nounB = "effectuality";

        StdOut.println("Number of synsets in the input file: " + wordNet.synsetsN);
        // StdOut.println("String representation of this digraph in DOT format: " + NEWLINE + wordNet.getGraphInDOT());
        // Out out = new Out("digraph.dot");
        // out.println(wordNet.getGraphInDOT());

        // wordNet.nouns().forEach(StdOut::println);
        // StdOut.println(wordNet.nouns().toString());

        StdOut.println("Shortest common ancestor of '" + nounA + "' and '" + nounB + "' in a shortest ancestral path is the synset: '"
                               + wordNet.sap(nounA, nounB) + "'");
        StdOut.println("Distance between '" + nounA + "' and '" + nounB + "' is: " + wordNet.distance(nounA, nounB));

        // Iterable<String> nouns = wordNet.nouns();

        nounA = "conifer";
        nounB = "osmium";
        StdOut.println("Distance between '" + nounA + "' and '" + nounB + "' is: " + wordNet.distance(nounA, nounB));
        StdOut.println("Shortest common ancestor of '" + nounA + "' and '" + nounB + "' in a shortest ancestral path is the synset: '"
                               + wordNet.sap(nounA, nounB) + "'");

        // nounA = "magnetic_flux_unit";
        // nounB = "effectuality";
        // StdOut.println("Shortest common ancestor of '" + nounA + "' and '" + nounB + "' in a shortest ancestral path is the synset: '"
        //                        + wordNet.sap(nounA, nounB) + "'");
        // StdOut.println("Distance between '" + nounA + "' and '" + nounB + "' is: " + wordNet.distance(nounA, nounB));
        // // wordNet.commonAncestorSynset = "entity";
        // StdOut.println("Shortest common ancestor of '" + nounA + "' and '" + nounB + "' in a shortest ancestral path is the synset: '"
        //                        + wordNet.sap(nounA, nounB) + "'");
        // StdOut.println("Distance between '" + nounA + "' and '" + nounB + "' is: " + wordNet.distance(nounA, nounB));
    }
}