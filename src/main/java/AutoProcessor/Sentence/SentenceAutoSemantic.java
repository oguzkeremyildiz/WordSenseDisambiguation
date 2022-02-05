package AutoProcessor.Sentence;

import AnnotatedSentence.*;
import AutoProcessor.AutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public abstract class SentenceAutoSemantic extends AutoSemantic {
    /**
     * The method should set the senses of all words, for which there is only one possible sense.
     * @param sentence The sentence for which word sense disambiguation will be determined automatically.
     */
    protected abstract boolean autoLabelSingleSemantics(AnnotatedSentence sentence);

    protected ArrayList<SynSet> getCandidateSynSets(WordNet wordNet, FsmMorphologicalAnalyzer fsm, AnnotatedSentence sentence, int index){
        AnnotatedWord twoPrevious = null, previous = null, current, twoNext = null, next = null;
        ArrayList<SynSet> synSets = new ArrayList<SynSet>();
        current = (AnnotatedWord) sentence.getWord(index);
        if (index > 1){
            twoPrevious = (AnnotatedWord) sentence.getWord(index - 2);
        }
        if (index > 0){
            previous = (AnnotatedWord) sentence.getWord(index - 1);
        }
        if (index != sentence.wordCount() - 1){
            next = (AnnotatedWord) sentence.getWord(index + 1);
        }
        if (index < sentence.wordCount() - 2){
            twoNext = (AnnotatedWord) sentence.getWord(index + 2);
        }
        synSets = wordNet.constructSynSets(current.getParse().getWord().getName(),
                current.getParse(), current.getMetamorphicParse(), fsm);
        if (twoPrevious != null && twoPrevious.getParse() != null && previous.getParse() != null){
            synSets.addAll(wordNet.constructIdiomSynSets(twoPrevious.getParse(), previous.getParse(), current.getParse(),
                    twoPrevious.getMetamorphicParse(), previous.getMetamorphicParse(), current.getMetamorphicParse(), fsm));
        }
        if (previous != null && previous.getParse() != null && next != null && next.getParse() != null){
            synSets.addAll(wordNet.constructIdiomSynSets(previous.getParse(), current.getParse(), next.getParse(),
                    previous.getMetamorphicParse(), current.getMetamorphicParse(), next.getMetamorphicParse(), fsm));
        }
        if (next != null && next.getParse() != null && twoNext != null && twoNext.getParse() != null){
            synSets.addAll(wordNet.constructIdiomSynSets(current.getParse(), next.getParse(), twoNext.getParse(),
                    current.getMetamorphicParse(), next.getMetamorphicParse(), twoNext.getMetamorphicParse(), fsm));
        }
        if (previous != null && previous.getParse() != null){
            synSets.addAll(wordNet.constructIdiomSynSets(previous.getParse(), current.getParse(),
                    previous.getMetamorphicParse(), current.getMetamorphicParse(), fsm));
        }
        if (next != null && next.getParse() != null){
            synSets.addAll(wordNet.constructIdiomSynSets(current.getParse(), next.getParse(),
                    current.getMetamorphicParse(), next.getMetamorphicParse(), fsm));
        }
        return synSets;
    }

    public void autoSemantic(AnnotatedSentence sentence){
        if (autoLabelSingleSemantics(sentence)){
            sentence.save();
        }
    }

}
