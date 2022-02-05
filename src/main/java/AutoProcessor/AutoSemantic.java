package AutoProcessor;

import WordNet.SynSet;

import java.util.ArrayList;
import java.util.Locale;

public abstract class AutoSemantic {

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
