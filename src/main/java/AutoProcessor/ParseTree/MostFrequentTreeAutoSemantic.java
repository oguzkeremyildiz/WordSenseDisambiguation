package AutoProcessor.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.WordNotExistsException;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;

public class MostFrequentTreeAutoSemantic extends TreeAutoSemantic{

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    public MostFrequentTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    @Override
    protected boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree) {
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (int i = 0; i < leafList.size(); i++){
            ArrayList<SynSet> synSets;
            try {
                synSets = getCandidateSynSets(turkishWordNet, fsm, leafList, i);
                if (!synSets.isEmpty()){
                    SynSet best = mostFrequent(synSets, leafList.get(i).getLayerInfo().getMorphologicalParseAt(0).getWord().getName());
                    if (best != null){
                        leafList.get(i).getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, best.getId());
                    }
                }
            } catch (LayerNotExistsException | WordNotExistsException ignored) {
            }
        }
        return true;
    }
}
