package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;
import java.util.Random;

public class RandomSentenceAutoSemantic extends SentenceAutoSemantic{

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link Lesk} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public RandomSentenceAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    @Override
    protected boolean autoLabelSingleSemantics(AnnotatedSentence sentence) {
        Random random = new Random(1);
        for (int i = 0; i < sentence.wordCount(); i++) {
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, sentence, i);
            if (!synSets.isEmpty()){
                ((AnnotatedWord) sentence.getWord(i)).setSemantic(synSets.get(random.nextInt(synSets.size())).getId());
            }
        }
        return true;
    }
}
