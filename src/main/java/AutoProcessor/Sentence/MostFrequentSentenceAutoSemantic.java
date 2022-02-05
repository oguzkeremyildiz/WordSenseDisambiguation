package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;

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
