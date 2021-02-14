package Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AutoProcessor.Sentence.Lesk;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;
import org.junit.Before;

import java.io.File;

public class TestLesk {

    FsmMorphologicalAnalyzer fsm;
    WordNet wordNet;

    @Before
    public void setUp() {
        fsm = new FsmMorphologicalAnalyzer();
        wordNet = new WordNet();
    }

    @org.junit.Test
    public void testAccuracy() {
        Lesk lesk = new Lesk(wordNet, fsm);
        AnnotatedCorpus corpus = new AnnotatedCorpus(new File("new-sentences"));
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            lesk.autoSemantic(sentence);
        }
    }

}
