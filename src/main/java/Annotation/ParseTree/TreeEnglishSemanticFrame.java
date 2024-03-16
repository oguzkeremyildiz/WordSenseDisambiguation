package Annotation.ParseTree;

import DataCollector.ParseTree.TreeEditorFrame;
import DataCollector.ParseTree.TreeEditorPanel;
import WordNet.*;

public class TreeEnglishSemanticFrame extends TreeEditorFrame {
    private final WordNet englishWordNet;
    private final WordNet turkishWordNet;

    public TreeEnglishSemanticFrame(final WordNet englishWordNet, final WordNet turkishWordNet){
        this.setTitle("English Semantic Editor");
        this.englishWordNet = englishWordNet;
        this.turkishWordNet = turkishWordNet;
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeEnglishSemanticPanel(currentPath, rawFileName, englishWordNet, turkishWordNet);
    }

}
