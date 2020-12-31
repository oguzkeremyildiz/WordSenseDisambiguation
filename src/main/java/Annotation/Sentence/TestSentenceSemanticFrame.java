package Annotation.Sentence;

import DataCollector.Sentence.Semantic.SentenceSemanticFrame;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

public class TestSentenceSemanticFrame {

    public static void main(String[] args){
         FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
         WordNet turkish = new WordNet();
         new SentenceSemanticFrame(fsm, turkish);
    }

}
