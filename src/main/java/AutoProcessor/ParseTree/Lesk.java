package AutoProcessor.ParseTree;

import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Lesk extends TreeAutoSemantic {

    private final WordNet turkishWordNet;
    private final FsmMorphologicalAnalyzer fsm;

    /**
     * Constructor for the {@link AutoProcessor.Sentence.Lesk} class. Gets the Turkish wordnet and Turkish fst based
     * morphological analyzer from the user and sets the corresponding attributes.
     * @param turkishWordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     */
    public Lesk(WordNet turkishWordNet, FsmMorphologicalAnalyzer fsm){
        this.turkishWordNet = turkishWordNet;
        this.fsm = fsm;
    }

    /**
     * Calculates the number of words that occur (i) in the definition or example of the given synset and (ii) in the
     * given parse tree.
     * @param synSet Synset of which the definition or example will be checked
     * @param leafList Leaf nodes of the parse tree.
     * @return The number of words that occur (i) in the definition or example of the given synset and (ii) in the given
     * parse tree.
     */
    private int intersection(SynSet synSet, ArrayList<ParseNodeDrawable> leafList){
        String[] words1;
        if (synSet.getExample() != null){
            words1 = (synSet.getLongDefinition() + " " + synSet.getExample()).split(" ");
        } else {
            words1 = synSet.getLongDefinition().split(" ");
        }
        String[] words2 = new String[leafList.size()];
        for (int i = 0; i < leafList.size(); i++){
            words2[i] = leafList.get(i).getLayerData(ViewLayerType.TURKISH_WORD);
        }
        int count = 0;
        for (String word1 : words1){
            for (String word2 : words2){
                if (word1.toLowerCase(new Locale("tr")).equals(word2.toLowerCase(new Locale("tr")))){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * The method annotates the word senses of the words in the parse tree according to the simplified Lesk algorithm.
     * Lesk is an algorithm that chooses the sense whose definition or example shares the most words with the target
     * word’s neighborhood. The algorithm processes target words one by one. First, the algorithm constructs an array of
     * all possible senses for the target word to annotate. Then for each possible sense, the number of words shared
     * between the definition of sense synset and target tree is calculated. Then the sense with the maximum
     * intersection count is selected.
     * @param parseTree Parse tree to be annotated.
     * @return True, if at least one word is semantically annotated, false otherwise.
     */
    @Override
    protected boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree) {
        Random random = new Random(1);
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
        boolean done = false;
        for (int i = 0; i < leafList.size(); i++){
            ArrayList<SynSet> synSets = getCandidateSynSets(turkishWordNet, fsm, leafList, i);
            int maxIntersection = -1;
            for (SynSet synSet : synSets) {
                int intersectionCount = intersection(synSet, leafList);
                if (intersectionCount > maxIntersection) {
                    maxIntersection = intersectionCount;
                }
            }
            ArrayList<SynSet> maxSynSets = new ArrayList<>();
            for (SynSet synSet : synSets) {
                if (intersection(synSet, leafList) == maxIntersection) {
                    maxSynSets.add(synSet);
                }
            }
            if (!maxSynSets.isEmpty()){
                leafList.get(i).getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, maxSynSets.get(random.nextInt(maxSynSets.size())).getId());
                done = true;
            }
        }
        return done;
    }

}
