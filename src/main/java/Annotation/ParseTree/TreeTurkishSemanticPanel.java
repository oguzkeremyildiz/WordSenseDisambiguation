package Annotation.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.*;
import DataCollector.ParseTree.TreeAction.LayerAction;
import DataCollector.ParseTree.TreeAction.LayerClearAction;
import DataCollector.ParseTree.TreeLeafEditorPanel;
import DataCollector.WordNet.ExampleTreeCellRenderer;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;

public class TreeTurkishSemanticPanel extends TreeLeafEditorPanel {

    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final WordNet wordNet;
    private final FsmMorphologicalAnalyzer fsm;
    private ArrayList<SynSet>[] meanings;
    private ArrayList<SynSet> idioms, idioms1, idioms2;

    /**
     * Constructor for the sense disambiguation panel for a parse tree in Turkish. It also adds the
     * tree selection listener which will update the parse tree according to the selection.
     * @param path The absolute path of the annotated parse tree.
     * @param fileName The raw file name of the annotated parse tree.
     * @param wordNet Turkish wordnet
     * @param fsm Morphological analyzer
     * @param defaultFillEnabled If true, automatic annotation will be done.
     */
    public TreeTurkishSemanticPanel(String path, String fileName, WordNet wordNet, FsmMorphologicalAnalyzer fsm, boolean defaultFillEnabled) {
        super(path, fileName, ViewLayerType.SEMANTICS, defaultFillEnabled);
        heightDecrease = 280;
        this.wordNet = wordNet;
        this.fsm = fsm;
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Anlamlar");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setVisible(false);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                if (node.getLevel() == 0){
                    LayerClearAction action = new LayerClearAction(((TreeTurkishSemanticPanel) tree.getParent().getParent().getParent()), previousNode.getLayerInfo(), ViewLayerType.SEMANTICS);
                    setAction(action);
                    tree.setVisible(false);
                } else {
                    ArrayList<String> selectedMeanings = getSelectedMeanings(node);
                    if (!selectedMeanings.isEmpty()) {
                        StringBuilder semantics = new StringBuilder(selectedMeanings.get(0));
                        for (int i = 1; i < selectedMeanings.size(); i++) {
                            semantics.append("$").append(selectedMeanings.get(i));
                        }
                        if (selectedMeanings.size() == 1 && previousNode.getLayerData(ViewLayerType.SEMANTICS) != null){
                            if (currentTree.updateConnectedPredicate(previousNode.getLayerData(ViewLayerType.SEMANTICS), semantics.toString())){
                                currentTree.save();
                            }
                        }
                        LayerAction action = new LayerAction(((TreeTurkishSemanticPanel) tree.getParent().getParent().getParent()), previousNode.getLayerInfo(), semantics.toString(), ViewLayerType.SEMANTICS);
                        setAction(action);
                        tree.setVisible(false);
                    }
                }
            }
        });
        pane = new JScrollPane(tree);
        add(pane);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Returns selected word senses for all words in the leaf node. There can be at most three words in a
     * leaf node, therefore the method returns an array of sense id's selected.
     * @param node Selected tree node in JTree
     * @return An array of selected sense id's of the word(s) in the leaf node of the parse tree.
     */
    private ArrayList<String> getSelectedMeanings(DefaultMutableTreeNode node){
        ArrayList<String> selectedMeanings = new ArrayList<>();
        switch (meanings.length){
            case 1:
                if (node.getLevel() == 1){
                    if (!idioms1.isEmpty() && node.getParent().getIndex(node) < idioms1.size()){
                        selectedMeanings.add(idioms1.get(node.getParent().getIndex(node)).getId());
                    } else {
                        if (!idioms2.isEmpty() && node.getParent().getIndex(node) < idioms1.size() + idioms2.size()){
                            selectedMeanings.add(idioms2.get(node.getParent().getIndex(node) - idioms1.size()).getId());
                        } else {
                            if (!meanings[0].isEmpty()){
                                selectedMeanings.add(meanings[0].get(node.getParent().getIndex(node) - idioms1.size() - idioms2.size()).getId());
                            }
                        }
                    }
                }
                break;
            case 2:
                if (node.getLevel() == 1 && node.getParent().getIndex(node) < idioms.size()){
                    selectedMeanings.add(idioms.get(node.getParent().getIndex(node)).getId());
                } else {
                    if (!meanings[0].isEmpty() && !meanings[1].isEmpty() && node.getLevel() == 2){
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                        selectedMeanings.add(meanings[0].get((parentNode.getParent().getIndex(parentNode) - idioms.size())).getId());
                        selectedMeanings.add(meanings[1].get(node.getParent().getIndex(node)).getId());
                    }
                }
                break;
            case 3:
                if (node.getLevel() == 1 && node.getParent().getIndex(node) < idioms.size()) {
                    selectedMeanings.add(idioms.get(node.getParent().getIndex(node)).getId());
                } else {
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                    if (node.getLevel() == 2){
                        if (parentNode.getParent().getIndex(parentNode) < idioms.size() + idioms1.size()){
                            selectedMeanings.add(idioms1.get((parentNode.getParent().getIndex(parentNode) - idioms.size())).getId());
                            selectedMeanings.add(idioms1.get((parentNode.getParent().getIndex(parentNode) - idioms.size())).getId());
                            selectedMeanings.add(meanings[2].get(node.getParent().getIndex(node)).getId());
                        } else {
                            if (node.getParent().getIndex(node) < idioms2.size()){
                                selectedMeanings.add(meanings[0].get((parentNode.getParent().getIndex(parentNode) - idioms.size() - idioms1.size())).getId());
                                selectedMeanings.add(idioms2.get(node.getParent().getIndex(node)).getId());
                                selectedMeanings.add(idioms2.get(node.getParent().getIndex(node)).getId());
                            }
                        }
                    } else {
                        if (!meanings[0].isEmpty() && !meanings[1].isEmpty() && !meanings[2].isEmpty() && node.getLevel() == 3) {
                            DefaultMutableTreeNode grandParentNode = (DefaultMutableTreeNode) parentNode.getParent();
                            selectedMeanings.add(meanings[0].get(grandParentNode.getParent().getIndex(grandParentNode) - idioms.size() - idioms1.size()).getId());
                            selectedMeanings.add(meanings[1].get(parentNode.getParent().getIndex(parentNode) - idioms2.size()).getId());
                            selectedMeanings.add(meanings[2].get(node.getParent().getIndex(node)).getId());
                        }
                    }
                }
                break;
        }
        return selectedMeanings;
    }

    /**
     * Fills the JTree that contains all possible word senses of the word(s) in the leaf node. For every
     * node in the leaf node, first all single word possible word senses are identified and placed in meanings.
     * meanings[0] for the first word, meanings[1] for the second word, and meanings[2] for the third word. If the
     * number of words in the leaf one is(are)
     * <ul>
     *     <li>one: Possible two word idioms are constructed with the previous sibling node and next sibling node
     *     separately and placed in idioms1 and idioms2 respectively</li>
     *     <li>two: Possible two word idioms are constructed with the two words in the leaf node and placed in
     *     idioms. </li>
     *     <li>three: Possible three word idioms are constructed with the three words in the leaf node and
     *     placed in idioms. Possible two word idioms are constructed with the first two and last two words separately
     *     and placed in idioms1 and idioms2 respectively</li>
     * </ul>
     * According to these arrays, the first level of the tree shows all possible senses associated with the first word,
     * second level of the tree will show all possible senses associated with the second word, and third level of the
     * tree shows all possible senses associated with the third word in the leaf node.
     * @param node Selected node for which options will be displayed.
     */
    public void populateLeaf(ParseNodeDrawable node){
        DefaultMutableTreeNode selectedNode = null;
        if (previousNode != null){
            previousNode.setSelected(false);
        }
        previousNode = node;
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        LayerInfo info = node.getLayerInfo();
        if (info.getLayerData(ViewLayerType.INFLECTIONAL_GROUP) != null){
            try{
                meanings = new ArrayList[info.getNumberOfWords()];
                for (int i = 0; i < info.getNumberOfWords(); i++){
                    meanings[i] = wordNet.constructSynSets(info.getMorphologicalParseAt(i).getWord().getName(), info.getMorphologicalParseAt(i), info.getMetamorphicParseAt(i), fsm);
                }
                switch (info.getNumberOfWords()){
                    case 1:
                        ParseNodeDrawable previous = currentTree.previousLeafNode(node);
                        if (previous != null && previous.getLayerInfo().getNumberOfWords() == 1){
                            idioms1 = wordNet.constructIdiomSynSets(previous.getLayerInfo().getMorphologicalParseAt(0), info.getMorphologicalParseAt(0), previous.getLayerInfo().getMetamorphicParseAt(0), info.getMetamorphicParseAt(0), fsm);
                            for (SynSet idiom: idioms1){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                    selectedNode = childNode;
                                }
                            }
                        } else {
                            idioms1 = new ArrayList<>();
                        }
                        ParseNodeDrawable next = currentTree.nextLeafNode(node);
                        if (next != null && next.getLayerInfo().getNumberOfWords() == 1){
                            idioms2 = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), next.getLayerInfo().getMorphologicalParseAt(0), info.getMetamorphicParseAt(0), next.getLayerInfo().getMetamorphicParseAt(0), fsm);
                            for (SynSet idiom: idioms2){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                    selectedNode = childNode;
                                }
                            }
                        } else {
                            idioms2 = new ArrayList<>();
                        }
                        if (!idioms1.isEmpty() || !idioms2.isEmpty() || !meanings[0].isEmpty()){
                            for (SynSet meaning: meanings[0]){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(meaning);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning.getId())){
                                    selectedNode = childNode;
                                }
                            }
                        }
                        break;
                    case 2:
                        idioms = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), info.getMorphologicalParseAt(1), info.getMetamorphicParseAt(0), info.getMetamorphicParseAt(1), fsm);
                        for (SynSet idiom: idioms){
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                            if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                selectedNode = childNode;
                            }
                        }
                        if (!meanings[0].isEmpty() && !meanings[1].isEmpty()){
                            for (SynSet meaning0: meanings[0]){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(meaning0);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                for (SynSet meaning1: meanings[1]){
                                    DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(meaning1);
                                    childNode.add(grandChildNode);
                                    if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning0.getId() + "$" + meaning1.getId())){
                                        selectedNode = grandChildNode;
                                    }
                                }
                            }
                        }
                        break;
                    case 3:
                        idioms = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), info.getMorphologicalParseAt(1), info.getMorphologicalParseAt(2), info.getMetamorphicParseAt(0), info.getMetamorphicParseAt(1), info.getMetamorphicParseAt(2), fsm);
                        for (SynSet idiom: idioms){
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                            if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId())){
                                selectedNode = childNode;
                            }
                        }
                        idioms1 = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(0), info.getMorphologicalParseAt(1), info.getMetamorphicParseAt(0), info.getMetamorphicParseAt(1), fsm);
                        for (SynSet idiom: idioms1){
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(idiom);
                            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                            for (SynSet meaning2: meanings[2]){
                                DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(meaning2);
                                childNode.add(grandChildNode);
                                if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(idiom.getId() + "$" + idiom.getId() + "$" + meaning2.getId())){
                                    selectedNode = grandChildNode;
                                }
                            }
                        }
                        idioms2 = wordNet.constructIdiomSynSets(info.getMorphologicalParseAt(1), info.getMorphologicalParseAt(2), info.getMetamorphicParseAt(1), info.getMetamorphicParseAt(2), fsm);
                        if (!meanings[0].isEmpty() && !meanings[1].isEmpty() && !meanings[2].isEmpty()){
                            for (SynSet meaning0: meanings[0]){
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(meaning0);
                                ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
                                for (SynSet idiom: idioms2){
                                    DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(idiom);
                                    childNode.add(grandChildNode);
                                    if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning0.getId() + "$" + idiom.getId() + "$" + idiom.getId())){
                                        selectedNode = grandChildNode;
                                    }
                                }
                                for (SynSet meaning1: meanings[1]){
                                    DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(meaning1);
                                    childNode.add(grandChildNode);
                                    for (SynSet meaning2: meanings[2]){
                                        DefaultMutableTreeNode grandGrandChildNode = new DefaultMutableTreeNode(meaning2);
                                        grandChildNode.add(grandGrandChildNode);
                                        if (node.getLayerData(ViewLayerType.SEMANTICS) != null && node.getLayerData(ViewLayerType.SEMANTICS).equals(meaning0.getId() + "$" + meaning1.getId() + "$" + meaning2.getId())){
                                            selectedNode = grandGrandChildNode;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                }
            } catch (WordNotExistsException | LayerNotExistsException ignored) {
            }
        }
        treeModel.reload();
        if (selectedNode != null){
            tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(selectedNode)));
        }
        tree.setVisible(true);
        pane.setVisible(true);
        pane.getVerticalScrollBar().setValue(0);
        pane.setBounds(node.getArea().getX() - 5, node.getArea().getY() + 30, 250, 30 + Math.max(3, Math.min(15, ((DefaultMutableTreeNode) treeModel.getRoot()).getChildCount() + 1)) * 18);
        this.repaint();
        isEditing = true;
        tree.setCellRenderer(new ExampleTreeCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(tree);
    }

    /**
     * Some of the words in the leaf node are sense disambiguated automatically. The words automatically
     * disambiguated are the words that only have one sense.
     * @param node Leaf node for which sense disambiguation will be done.
     * @return True, if automatic sense disambiguation is done, false otherwise.
     */
    protected boolean defaultFill(ParseNodeDrawable node){
        if (wordNet == null || fsm == null){
            return false;
        }
        if (node.getLayerData(ViewLayerType.SEMANTICS) != null){
            return false;
        }
        if (node.getLayerData(ViewLayerType.TURKISH_WORD) != null && node.getLayerData(ViewLayerType.META_MORPHEME) != null && node.getLayerData(ViewLayerType.INFLECTIONAL_GROUP) != null){
            LayerInfo info = node.getLayerInfo();
            try {
                if (info.getNumberOfWords() == 1){
                    ArrayList<SynSet> synSetList = wordNet.constructSynSets(info.getMorphologicalParseAt(0).getWord().getName(), info.getMorphologicalParseAt(0), info.getMetamorphicParseAt(0), fsm);
                    if (synSetList.size() == 1){
                        node.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, synSetList.get(0).getId());
                        return true;
                    }
                } else {
                    if (info.getNumberOfWords() == 2){
                        ArrayList<SynSet> synSetList1 = wordNet.constructSynSets(info.getMorphologicalParseAt(0).getWord().getName(), info.getMorphologicalParseAt(0), info.getMetamorphicParseAt(0), fsm);
                        ArrayList<SynSet> synSetList2 = wordNet.constructSynSets(info.getMorphologicalParseAt(1).getWord().getName(), info.getMorphologicalParseAt(1), info.getMetamorphicParseAt(1), fsm);
                        if (synSetList1.size() == 1 && synSetList2.size() == 1){
                            node.getLayerInfo().setLayerData(ViewLayerType.SEMANTICS, synSetList1.get(0).getId() + "$" + synSetList2.get(0).getId());
                            return true;
                        }
                    }
                }
            } catch (LayerNotExistsException | WordNotExistsException ignored) {
            }
        }
        return false;
    }

    /**
     * The size of the string displayed. If it is a leaf node, it returns the maximum size of the sense id's
     * of word(s) in the leaf node. Otherwise, it returns the size of the symbol in the node.
     * @param parseNode Parse node
     * @param g Graphics on which tree will be drawn.
     * @return Size of the string displayed.
     */
    protected int getStringSize(ParseNodeDrawable parseNode, Graphics g) {
        int i, stringSize = 0;
        if (parseNode.numberOfChildren() == 0) {
            try {
                stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerData(ViewLayerType.TURKISH_WORD));
                for (i = 0; i < parseNode.getLayerInfo().getNumberOfMeanings(); i++)
                    if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getSemanticAt(i).substring(6)) > stringSize){
                        stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getSemanticAt(i).substring(6));
                    }
            } catch (LayerNotExistsException | WordNotExistsException ignored) {
            }
            return stringSize;
        } else {
            return g.getFontMetrics().stringWidth(parseNode.getData().getName());
        }
    }

    /**
     * If the node is a leaf node, it draws the word and its sense id. Otherwise, it draws the node symbol.
     * @param parseNode Parse Node
     * @param g Graphics on which symbol is drawn.
     * @param x x coordinate
     * @param y y coordinate
     */
    protected void drawString(ParseNodeDrawable parseNode, Graphics g, int x, int y){
        int i;
        if (parseNode.numberOfChildren() == 0){
            g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
            g.setColor(Color.RED);
            for (i = 0; i < parseNode.getLayerInfo().getNumberOfMeanings(); i++){
                try {
                    y += 20;
                    g.drawString(parseNode.getLayerInfo().getSemanticAt(i).substring(6), x, y);
                } catch (LayerNotExistsException | WordNotExistsException ignored) {

                }
            }
        } else {
            g.drawString(parseNode.getData().getName(), x, y);
        }
    }

    /**
     * Sets the size of the enclosing area of the parse node (for selecting, editing etc.).
     * @param parseNode Parse Node
     * @param x x coordinate of the center of the node.
     * @param y y coordinate of the center of the node.
     * @param stringSize Size of the string in terms of pixels.
     */
    protected void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize){
        if (parseNode.numberOfChildren() == 0){
            try {
                parseNode.setArea(x - 5, y - 15, stringSize + 10, 20 * (parseNode.getLayerInfo().getNumberOfWords() + 1));
            } catch (LayerNotExistsException ignored) {
            }
        } else {
            parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
        }
    }
}
