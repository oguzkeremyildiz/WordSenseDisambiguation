package ParseTree;

import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.TreeBankDrawable;
import AutoProcessor.ParseTree.Lesk;
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
        TreeBankDrawable treeBank = new TreeBankDrawable(new File("new-trees"));
        for (int i = 0; i < treeBank.size(); i++){
            ParseTreeDrawable parseTree = treeBank.get(i);
            lesk.autoSemantic(parseTree);
        }
    }

}
