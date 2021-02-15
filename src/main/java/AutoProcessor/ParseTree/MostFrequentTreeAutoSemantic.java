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
import java.util.Locale;

public class MostFrequentTreeAutoSemantic extends TreeAutoSemantic{

    private WordNet turkishWordNet;
    private FsmMorphologicalAnalyzer fsm;

    public MostFrequentTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    private SynSet mostFrequent(ArrayList<SynSet> synSets, String root){
        if (synSets.size() == 1){
            return synSets.get(0);
        }
        int minSense = 50;
        SynSet best = null;
        for (SynSet synSet : synSets){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                if (synSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr")).startsWith(root)
                        || synSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr")).endsWith(" " + root)){
                    if (synSet.getSynonym().getLiteral(i).getSense() < minSense){
                        minSense = synSet.getSynonym().getLiteral(i).getSense();
                        best = synSet;
                    }
                }
            }
        }
        return best;
    }

    @Override
    protected boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree) {
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (int i = 0; i < leafList.size(); i++){
            ArrayList<SynSet> synSets;
            try {
                synSets = getCandidateSynSets(turkishWordNet, fsm, leafList, i);
                if (synSets.size() > 0){
                    SynSet best = mostFrequent(synSets, leafList.get(i).getLayerInfo().getMorphologicalParseAt(0).getWord().getName());
                    if (best != null){
                        leafList.get(i).getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, best.getId());
                    }
                }
            } catch (LayerNotExistsException | WordNotExistsException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
