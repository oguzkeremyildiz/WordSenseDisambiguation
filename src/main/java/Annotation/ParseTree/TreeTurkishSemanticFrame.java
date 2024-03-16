package Annotation.ParseTree;

import AnnotatedTree.TreeBankDrawable;
import DataCollector.ParseTree.TreeEditorFrame;
import DataCollector.ParseTree.TreeEditorPanel;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class TreeTurkishSemanticFrame extends TreeEditorFrame {
    private final WordNet wordNet;
    private final FsmMorphologicalAnalyzer fsm;

    public TreeTurkishSemanticFrame(final WordNet wordNet, final FsmMorphologicalAnalyzer fsm){
        this.setTitle("Turkish Semantic Editor");
        this.wordNet = wordNet;
        this.fsm = fsm;
        TreeBankDrawable treeBank = new TreeBankDrawable(new File(TreeEditorPanel.treePath));
        JMenuItem itemViewAnnotations = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        itemViewAnnotations.addActionListener(e -> new ViewTreeSemanticAnnotationFrame(treeBank, wordNet, fsm, this));
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeTurkishSemanticPanel(currentPath, rawFileName, wordNet, fsm, true);
    }
}
