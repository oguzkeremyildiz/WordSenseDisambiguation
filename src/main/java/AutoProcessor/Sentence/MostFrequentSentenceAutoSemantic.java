package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;
import java.util.Locale;

public class MostFrequentSentenceAutoSemantic extends SentenceAutoSemantic{

    private WordNet turkishWordNet;
    private FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link Lesk} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public MostFrequentSentenceAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    private SynSet mostFrequent(ArrayList<SynSet> synSets, String root){
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

    @Override
    protected boolean autoLabelSingleSemantics(AnnotatedSentence sentence) {
        for (int i = 0; i < sentence.wordCount(); i++) {
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, sentence, i);
            if (synSets.size() > 0){
                SynSet best = mostFrequent(synSets, ((AnnotatedWord) sentence.getWord(i)).getParse().getWord().getName());
                if (best != null){
                    ((AnnotatedWord) sentence.getWord(i)).setSemantic(best.getId());
                }
            }
        }
        return true;
    }

}
