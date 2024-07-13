package AutoProcessor.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AutoProcessor.Sentence.RandomSentenceAutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;
import java.util.Random;

public class RandomTreeAutoSemantic extends TreeAutoSemantic{

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link RandomSentenceAutoSemantic} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public RandomTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    /**
     * The method annotates the word senses of the words in the parse tree randomly. The algorithm processes target
     * words one by one. First, the algorithm constructs an array of all possible senses for the target word to
     * annotate. Then it chooses a sense randomly.
     * @param parseTree Parse tree to be annotated.
     * @return True.
     */
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
