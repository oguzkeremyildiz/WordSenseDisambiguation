package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;
import java.util.Random;

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

    private SynSet mostFrequent(ArrayList<SynSet> synSets){
        int minSense = 50;
        SynSet best = null;
        for (SynSet synSet : synSets){
            if (synSet.getSynonym().getLiteral(0).getSense() < minSense){
                minSense = synSet.getSynonym().getLiteral(0).getSense();
                best = synSet;
            }
        }
        return best;
    }

    @Override
    protected boolean autoLabelSingleSemantics(AnnotatedSentence sentence) {
        Random random = new Random(1);
        for (int i = 0; i < sentence.wordCount(); i++) {
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, sentence, i);
            if (synSets.size() > 0){
                ((AnnotatedWord) sentence.getWord(i)).setSemantic(mostFrequent(synSets).getId());
            }
        }
        return true;
    }

}
