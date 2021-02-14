package AutoProcessor.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;

public class MostFrequentTreeAutoSemantic extends TreeAutoSemantic{

    private WordNet turkishWordNet;
    private FsmMorphologicalAnalyzer fsm;

    public MostFrequentTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    private SynSet mostFrequent(ArrayList<SynSet> synSets){
        int minSense = 50;
        SynSet best = null;
        for (SynSet synSet : synSets){
            if (synSet.getSynonym().getLiteral(0).getSense() < minSense){
                minSense = synSet.getSynonym().getLiteral(0).getSense();
                best = synSet;
            }
        }
        return best;
    }

    @Override
    protected boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree) {
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (int i = 0; i < leafList.size(); i++){
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, leafList, i);
            if (synSets.size() > 0){
                leafList.get(i).getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, mostFrequent(synSets).getId());
            }
        }
        return true;
    }
}
