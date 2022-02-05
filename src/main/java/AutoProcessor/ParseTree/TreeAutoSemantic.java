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

    protected ArrayList<SynSet> getCandidateSynSets(WordNet wordNet, FsmMorphologicalAnalyzer fsm, ArrayList<ParseNodeDrawable> leafList, int index){
        LayerInfo twoPrevious = null, previous = null, current, twoNext = null, next = null;
        ArrayList<SynSet> synSets = new ArrayList<SynSet>();
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
        } catch (LayerNotExistsException | WordNotExistsException e) {
            e.printStackTrace();
        }
        return synSets;
    }

    public void autoSemantic(ParseTreeDrawable parseTree){
        if (autoLabelSingleSemantics(parseTree)){
            parseTree.save();
        }
    }

}
