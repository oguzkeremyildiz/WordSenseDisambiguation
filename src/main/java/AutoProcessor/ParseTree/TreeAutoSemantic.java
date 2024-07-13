package AutoProcessor.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedTree.LayerInfo;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.WordNotExistsException;
import AutoProcessor.AutoSemantic;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public abstract class TreeAutoSemantic extends AutoSemantic {
    protected abstract boolean autoLabelSingleSemantics(ParseTreeDrawable parseTree);

    /**
     * The method constructs all possible senses for the word at position index in the given parse tree. The method checks
     * the previous two words and the current word; the previous, current and next word, current and the next
     * two words to add three word multiword sense (that occurs in the Turkish wordnet) to the result list. The
     * method then check the previous word and current word; current word and the next word to add a two word multiword
     * sense to the result list. Lastly, the method adds all possible senses of the current word to the result list.
     * @param wordNet Turkish wordnet
     * @param fsm Turkish morphological analyzer
     * @param leafList Leaves of the parse tree to be semantically disambiguated.
     * @param index Position of the word to be disambiguated.
     * @return All possible senses for the word at position index in the given parse tree.
     */
    protected ArrayList<SynSet> getCandidateSynSets(WordNet wordNet, FsmMorphologicalAnalyzer fsm, ArrayList<ParseNodeDrawable> leafList, int index){
        LayerInfo twoPrevious = null, previous = null, current, twoNext = null, next = null;
        ArrayList<SynSet> synSets = new ArrayList<>();
        current = leafList.get(index).getLayerInfo();
        if (index > 1){
            twoPrevious = leafList.get(index - 2).getLayerInfo();
        }
        if (index > 0){
            previous = leafList.get(index - 1).getLayerInfo();
        }
        if (index != leafList.size() - 1){
            next = leafList.get(index + 1).getLayerInfo();
        }
        if (index < leafList.size() - 2){
            twoNext = leafList.get(index + 2).getLayerInfo();
        }
        try {
            synSets = wordNet.constructSynSets(current.getMorphologicalParseAt(0).getWord().getName(),
                    current.getMorphologicalParseAt(0), current.getMetamorphicParseAt(0), fsm);
            if (twoPrevious != null && twoPrevious.getMorphologicalParseAt(0) != null && previous.getMorphologicalParseAt(0) != null){
                synSets.addAll(wordNet.constructIdiomSynSets(twoPrevious.getMorphologicalParseAt(0), previous.getMorphologicalParseAt(0), current.getMorphologicalParseAt(0),
                        twoPrevious.getMetamorphicParseAt(0), previous.getMetamorphicParseAt(0), current.getMetamorphicParseAt(0), fsm));
            }
            if (previous != null && previous.getMorphologicalParseAt(0) != null && next != null && next.getMorphologicalParseAt(0) != null){
                synSets.addAll(wordNet.constructIdiomSynSets(previous.getMorphologicalParseAt(0), current.getMorphologicalParseAt(0), next.getMorphologicalParseAt(0),
                        previous.getMetamorphicParseAt(0), current.getMetamorphicParseAt(0), next.getMetamorphicParseAt(0), fsm));
            }
            if (next != null && next.getMorphologicalParseAt(0) != null && twoNext != null && twoNext.getMorphologicalParseAt(0) != null){
                synSets.addAll(wordNet.constructIdiomSynSets(current.getMorphologicalParseAt(0), next.getMorphologicalParseAt(0), twoNext.getMorphologicalParseAt(0),
                        current.getMetamorphicParseAt(0), next.getMetamorphicParseAt(0), twoNext.getMetamorphicParseAt(0), fsm));
            }
            if (previous != null && previous.getMorphologicalParseAt(0) != null){
                synSets.addAll(wordNet.constructIdiomSynSets(previous.getMorphologicalParseAt(0), current.getMorphologicalParseAt(0),
                        previous.getMetamorphicParseAt(0), current.getMetamorphicParseAt(0), fsm));
            }
            if (next != null && next.getMorphologicalParseAt(0) != null){
                synSets.addAll(wordNet.constructIdiomSynSets(current.getMorphologicalParseAt(0), next.getMorphologicalParseAt(0),
                        current.getMetamorphicParseAt(0), next.getMetamorphicParseAt(0), fsm));
            }
        } catch (LayerNotExistsException | WordNotExistsException ignored) {
        }
        return synSets;
    }

    /**
     * The method tries to semantic annotate as many words in the parse tree as possible.
     * @param parseTree Parse tree to be semantically disambiguated.
     */
    public void autoSemantic(ParseTreeDrawable parseTree){
        if (autoLabelSingleSemantics(parseTree)){
            parseTree.save();
        }
    }

}
