package AutoProcessor.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.WordNotExistsException;
import AutoProcessor.Sentence.MostFrequentSentenceAutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;

public class MostFrequentTreeAutoSemantic extends TreeAutoSemantic{

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link MostFrequentTreeAutoSemantic} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public MostFrequentTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    /**
     * The method annotates the word senses of the words in the parse tree according to the baseline most frequent
     * algorithm. The algorithm processes target words one by one. First, the algorithm constructs an array of
     * all possible senses for the target word to annotate. Then the sense with the minimum sense index is selected. In
     * the wordnet, literals are ordered and indexed according to their usage. The most frequently used sense of the
     * literal has sense number 1, then 2, etc.
     * @param parseTree Parse tree to be annotated.
     * @return True, if at least one word is semantically annotated, false otherwise.
     */
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
