package AutoProcessor.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.LayerInfo;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.WordNotExistsException;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class TurkishTreeAutoSemantic extends TreeAutoSemantic {
    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link TurkishTreeAutoSemantic} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public TurkishTreeAutoSemantic(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    /**
     * The method checks the number of possible senses of each word in the parse tree. If all words have only one
     * possible sense, it annotates the words with the corresponding sense. Otherwise, it does not annotate any words.
     * @param parseTree The parse tree for which word sense annotation will be done automatically.
     */
    protected boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree) {
        boolean modified = false;
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        for (ParseNodeDrawable parseNode : leafList){
            LayerInfo info = parseNode.getLayerInfo();
            if (info.getLayerData(ViewLayerType.INFLECTIONAL_GROUP) != null){
                try {
                    ArrayList<SynSet>[] meanings = new ArrayList[info.getNumberOfWords()];
                    for (int i = 0; i < info.getNumberOfWords(); i++){
                        meanings[i] = turkishWordNet.constructSynSets(info.getMorphologicalParseAt(i).getWord().getName(), info.getMorphologicalParseAt(i), info.getMetamorphicParseAt(i), fsm);
                    }
                    switch (info.getNumberOfWords()){
                        case 1:
                            if (meanings[0].size() == 1){
                                modified = true;
                                parseNode.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, meanings[0].get(0).getId());
                            }
                            break;
                        case 2:
                            if (meanings[0].size() == 1 && meanings[1].size() == 1){
                                modified = true;
                                parseNode.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, meanings[0].get(0).getId() + "$" + meanings[1].get(0).getId());
                            }
                            break;
                        case 3:
                            if (meanings[0].size() == 1 && meanings[1].size() == 1 && meanings[2].size() == 1){
                                modified = true;
                                parseNode.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, meanings[0].get(0).getId() + "$" + meanings[1].get(0).getId() + "$" + meanings[2].get(0).getId());
                            }
                            break;
                    }
                } catch (LayerNotExistsException | WordNotExistsException ignored) {
                }
            }
        }
        return modified;
    }
}
