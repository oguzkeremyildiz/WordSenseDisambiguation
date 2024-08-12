import AnnotatedSentence.*;
import AutoProcessor.Sentence.TurkishSentenceAutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        WordNet wordNet = new WordNet();
        TurkishSentenceAutoSemantic autoSemantic = new TurkishSentenceAutoSemantic(wordNet, new FsmMorphologicalAnalyzer());
        int n = 10;
        ArrayList<String> dataSetNames = new ArrayList<>();
        dataSetNames.add("Boun");
        dataSetNames.add("Gb");
        dataSetNames.add("Imst");
        dataSetNames.add("Pud");
        for (String dataSetName : dataSetNames) {
            AnnotatedCorpus sentences = new AnnotatedCorpus(new File(dataSetName));
            for (int i = 0; i < sentences.sentenceCount(); i++) {
                AnnotatedSentence sentence = (AnnotatedSentence) sentences.getSentence(i);
                LinkedList<ArrayList<String>> listOfIds = autoSemantic.getLabelNSemantics(sentence, n);
                for (int j = 0; j < listOfIds.size(); j++) {
                    ArrayList<String> ids = listOfIds.get(j);
                    if (!ids.isEmpty()) {
                        StringBuilder str = new StringBuilder();
                        str.append(sentence.getFileName()).append("\t").append(dataSetName).append("\t").append(sentence.getWord(j).getName()).append("\t").append(j).append("\t");
                        for (int k = 0; k < ids.size(); k++) {
                            str.append(wordNet.getSynSetWithId(ids.get(k)).getDefinition()).append("\t").append(ids.get(k)).append("\t");
                        }
                        str.append(sentence.toWords());
                        System.out.println(str);
                    }
                }
            }
        }
    }
}
