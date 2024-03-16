package AutoProcessor.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;
import java.util.Random;

public class RandomTreeAutoSemantic extends TreeAutoSemantic{

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    public RandomTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    @Override
    protected boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree) {
        Random random = new Random(1);
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (int i = 0; i < leafList.size(); i++){
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, leafList, i);
            if (!synSets.isEmpty()){
                leafList.get(i).getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, synSets.get(random.nextInt(synSets.size())).getId());
            }
        }
        return true;
    }
}
