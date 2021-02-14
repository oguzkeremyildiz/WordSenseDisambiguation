package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;
import java.util.Locale;

public class Lesk extends SentenceAutoSemantic{

    private WordNet turkishWordNet;
    private FsmMorphologicalAnalyzer fsm;

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
        boolean done = false;
        for (int i = 0; i < sentence.wordCount(); i++) {
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, sentence, i);
            int maxIntersection = -1;
            int maxIndex = -1;
            for (int j = 0; j < synSets.size(); j++){
                SynSet synSet = synSets.get(j);
                int intersectionCount = intersection(synSet, sentence);
                if (intersectionCount > maxIntersection){
                    maxIntersection = intersectionCount;
                    maxIndex = j;
                }
            }
            if (maxIndex != -1){
                done = true;
                ((AnnotatedWord) sentence.getWord(i)).setSemantic(synSets.get(maxIndex).getId());
            }
        }
        return done;
    }
}
