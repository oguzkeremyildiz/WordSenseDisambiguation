package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Lesk extends SentenceAutoSemantic{

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link Lesk} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public Lesk(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    private int intersection(SynSet synSet, AnnotatedSentence sentence){
        String[] words1;
        if (synSet.getExample() != null){
            words1 = (synSet.getLongDefinition() + " " + synSet.getExample()).split(" ");
        } else {
            words1 = synSet.getLongDefinition().split(" ");
        }
        String[] words2 = sentence.toWords().split(" ");
        int count = 0;
        for (String word1 : words1){
            for (String word2 : words2){
                if (word1.toLowerCase(new Locale("tr")).equals(word2.toLowerCase(new Locale("tr")))){
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected boolean autoLabelSingleSemantics(AnnotatedSentence sentence) {
        Random random = new Random(1);
        boolean done = false;
        for (int i = 0; i < sentence.wordCount(); i++) {
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, sentence, i);
            int maxIntersection = -1;
            for (SynSet synSet : synSets) {
                int intersectionCount = intersection(synSet, sentence);
                if (intersectionCount > maxIntersection) {
                    maxIntersection = intersectionCount;
                }
            }
            ArrayList<SynSet> maxSynSets = new ArrayList<>();
            for (SynSet synSet : synSets) {
                if (intersection(synSet, sentence) == maxIntersection) {
                    maxSynSets.add(synSet);
                }
            }
            if (!maxSynSets.isEmpty()){
                done = true;
                ((AnnotatedWord) sentence.getWord(i)).setSemantic(maxSynSets.get(random.nextInt(maxSynSets.size())).getId());
            }
        }
        return done;
    }
}
