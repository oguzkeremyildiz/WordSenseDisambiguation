package AutoProcessor;

import WordNet.SynSet;

import java.util.ArrayList;
import java.util.Locale;

public abstract class AutoSemantic {

    /**
     * Returns the most frequent root word in the given synsets. In the wordnet, literals are ordered and indexed
     * according to their usage. The most frequently used sense of the literal has sense number 1, then 2, etc. In order
     * to get literal from root word, the algorithm checks root for a prefix and suffix. So, if the root is a prefix or
     * suffix of a literal, it is included in the search.
     * @param synSets All possible synsets to search for most frequent literal.
     * @param root Root word to be checked.
     * @return Synset storing most frequent literal either starting or ending with the given root form.
     */
    protected SynSet mostFrequent(ArrayList<SynSet> synSets, String root){
        if (synSets.size() == 1){
            return synSets.get(0);
        }
        int minSense = 50;
        SynSet best = null;
        for (SynSet synSet : synSets){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                if (synSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr")).startsWith(root)
                        || synSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr")).endsWith(" " + root)){
                    if (synSet.getSynonym().getLiteral(i).getSense() < minSense){
                        minSense = synSet.getSynonym().getLiteral(i).getSense();
                        best = synSet;
                    }
                }
            }
        }
        return best;
    }

}
