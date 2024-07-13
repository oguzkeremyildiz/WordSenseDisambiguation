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
     * Constructor for the {@link RandomSentenceAutoSemantic} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public RandomSentenceAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    /**
     * The method annotates the word senses of the words in the sentence randomly. The algorithm processes target
     * words one by one. First, the algorithm constructs an array of all possible senses for the target word to
     * annotate. Then it chooses a sense randomly.
     * @param sentence Sentence to be annotated.
     * @return True.
     */
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
