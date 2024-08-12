package AutoProcessor.Sentence;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;
import java.util.LinkedList;

public class TurkishSentenceAutoSemantic extends SentenceAutoSemantic {

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link TurkishSentenceAutoSemantic} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public TurkishSentenceAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    /**
     * The method checks
     * 1. the previous two words and the current word; the previous, current and next word, current and the next
     * two words for a three word multiword expression that occurs in the Turkish wordnet.
     * 2. the previous word and current word; current word and the next word for a two word multiword expression that
     * occurs in the Turkish wordnet.
     * 3. the current word
     * if it has only one sense. If there is only one sense for that multiword expression or word; it sets that sense.
     * @param sentence The sentence for which word sense disambiguation will be determined automatically.
     */
    public boolean autoLabelSingleSemantics(AnnotatedSentence sentence) {
        boolean done = false;
        AnnotatedWord twoPrevious = null, previous = null, current, twoNext = null, next = null;
        for (int i = 0; i < sentence.wordCount(); i++){
            current = (AnnotatedWord) sentence.getWord(i);
            if (i > 1){
                twoPrevious = (AnnotatedWord) sentence.getWord(i - 2);
            }
            if (i > 0){
                previous = (AnnotatedWord) sentence.getWord(i - 1);
            }
            if (i != sentence.wordCount() - 1){
                next = (AnnotatedWord) sentence.getWord(i + 1);
            }
            if (i < sentence.wordCount() - 2){
                twoNext = (AnnotatedWord) sentence.getWord(i + 2);
            }
            if (current.getSemantic() == null && current.getParse() != null){
                if (twoPrevious != null && twoPrevious.getParse() != null && previous.getParse() != null){
                    ArrayList<SynSet> idioms = turkishWordNet.constructIdiomSynSets(twoPrevious.getParse(), previous.getParse(), current.getParse(), twoPrevious.getMetamorphicParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), fsm);
                    if (idioms.size() == 1){
                        current.setSemantic(idioms.get(0).getId());
                        done = true;
                        continue;
                    }
                }
                if (previous != null && previous.getParse() != null && next != null && next.getParse() != null){
                    ArrayList<SynSet> idioms = turkishWordNet.constructIdiomSynSets(previous.getParse(), current.getParse(), next.getParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), fsm);
                    if (idioms.size() == 1){
                        current.setSemantic(idioms.get(0).getId());
                        done = true;
                        continue;
                    }
                }
                if (next != null && next.getParse() != null && twoNext != null && twoNext.getParse() != null){
                    ArrayList<SynSet> idioms = turkishWordNet.constructIdiomSynSets(current.getParse(), next.getParse(), twoNext.getParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), twoNext.getMetamorphicParse(), fsm);
                    if (idioms.size() == 1){
                        current.setSemantic(idioms.get(0).getId());
                        done = true;
                        continue;
                    }
                }
                if (previous != null && previous.getParse() != null){
                    ArrayList<SynSet> idioms = turkishWordNet.constructIdiomSynSets(previous.getParse(), current.getParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), fsm);
                    if (idioms.size() == 1){
                        current.setSemantic(idioms.get(0).getId());
                        done = true;
                        continue;
                    }
                }
                if (current.getSemantic() == null && next != null && next.getParse() != null){
                    ArrayList<SynSet> idioms = turkishWordNet.constructIdiomSynSets(current.getParse(), next.getParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), fsm);
                    if (idioms.size() == 1){
                        current.setSemantic(idioms.get(0).getId());
                        done = true;
                        continue;
                    }
                }
                ArrayList<SynSet> meanings = turkishWordNet.constructSynSets(current.getParse().getWord().getName(), current.getParse(), current.getMetamorphicParse(), fsm);
                if (current.getSemantic() == null && meanings.size() == 1){
                    done = true;
                    current.setSemantic(meanings.get(0).getId());
                }
            }
        }
        return done;
    }

    public LinkedList<ArrayList<String>> getLabelNSemantics(AnnotatedSentence sentence, int n) {
        LinkedList<ArrayList<String>> labels = new LinkedList<>();
        AnnotatedWord twoPrevious = null, previous = null, current, twoNext = null, next = null;
        for (int i = 0; i < sentence.wordCount(); i++) {
            labels.addLast(new ArrayList<>());
            current = (AnnotatedWord) sentence.getWord(i);
            if (i > 1) {
                twoPrevious = (AnnotatedWord) sentence.getWord(i - 2);
            }
            if (i > 0){
                previous = (AnnotatedWord) sentence.getWord(i - 1);
            }
            if (i != sentence.wordCount() - 1) {
                next = (AnnotatedWord) sentence.getWord(i + 1);
            }
            if (i < sentence.wordCount() - 2) {
                twoNext = (AnnotatedWord) sentence.getWord(i + 2);
            }
            ArrayList<SynSet> idiomsPlusMeanings = new ArrayList<>();
            if (current.getSemantic() == null && current.getParse() != null) {
                if (twoPrevious != null && twoPrevious.getParse() != null && previous.getParse() != null) {
                    idiomsPlusMeanings.addAll(turkishWordNet.constructIdiomSynSets(twoPrevious.getParse(), previous.getParse(), current.getParse(), twoPrevious.getMetamorphicParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), fsm));
                }
                if (previous != null && previous.getParse() != null && next != null && next.getParse() != null) {
                    idiomsPlusMeanings.addAll(turkishWordNet.constructIdiomSynSets(previous.getParse(), current.getParse(), next.getParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), fsm));
                }
                if (next != null && next.getParse() != null && twoNext != null && twoNext.getParse() != null) {
                    idiomsPlusMeanings.addAll(turkishWordNet.constructIdiomSynSets(current.getParse(), next.getParse(), twoNext.getParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), twoNext.getMetamorphicParse(), fsm));
                }
                if (previous != null && previous.getParse() != null) {
                    idiomsPlusMeanings.addAll(turkishWordNet.constructIdiomSynSets(previous.getParse(), current.getParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), fsm));
                }
                if (current.getSemantic() == null && next != null && next.getParse() != null) {
                    idiomsPlusMeanings.addAll(turkishWordNet.constructIdiomSynSets(current.getParse(), next.getParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), fsm));
                }
                idiomsPlusMeanings.addAll(turkishWordNet.constructSynSets(current.getParse().getWord().getName(), current.getParse(), current.getMetamorphicParse(), fsm));
                if (idiomsPlusMeanings.size() == n) {
                    for (SynSet synSet : idiomsPlusMeanings) {
                        labels.getLast().add(synSet.getId());
                    }
                }
            }
        }
        return labels;
    }
}
