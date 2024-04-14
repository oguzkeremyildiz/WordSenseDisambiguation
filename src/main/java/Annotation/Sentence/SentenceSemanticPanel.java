package Annotation.Sentence;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.ViewLayerType;
import AutoProcessor.Sentence.TurkishSentenceAutoSemantic;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import WordNet.SynSet;
import WordNet.WordNet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SentenceSemanticPanel extends SentenceAnnotatorPanel {

    private FsmMorphologicalAnalyzer fsm;
    private WordNet wordNet;
    private TurkishSentenceAutoSemantic turkishSentenceAutoSemantic;
    private final JTree tree;
    private final DefaultTreeModel treeModel;

    /**
     * Constructor for the sense disambiguator panel for an annotated sentence. Sets the attributes. Adds also the
     * tree selection listener which updates the semantic layer of the clickedWord.
     * @param currentPath The absolute path of the annotated file.
     * @param fileName The raw file name of the annotated file.
     * @param fsm Morphological analyzer
     * @param wordNet Turkish Wordnet
     * @param exampleSentences Enlists other annotated sentence that contains the same word in the key.
     */
    public SentenceSemanticPanel(String currentPath, String fileName, FsmMorphologicalAnalyzer fsm, WordNet wordNet, HashMap<String, HashSet<String>> exampleSentences){
        super(currentPath, fileName, ViewLayerType.SEMANTICS);
        this.fsm = fsm;
        this.wordNet = wordNet;
        turkishSentenceAutoSemantic = new TurkishSentenceAutoSemantic(wordNet, fsm);
        setLayout(new BorderLayout());
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Anlamlar");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setVisible(false);
        tree.setCellRenderer(new SemanticExampleTreeCellRenderer(exampleSentences));
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && clickedWord != null) {
                if (node.getLevel() == 0 || (node.getLevel() == 1 && node.getUserObject() instanceof SynSet)){
                    if (node.getLevel() == 0){
                        clickedWord.setSemantic(null);
                    } else {
                        if (clickedWord.getSemantic() != null){
                            sentence.updateConnectedPredicate(clickedWord.getSemantic(), ((SynSet) node.getUserObject()).getId());
                        }
                        clickedWord.setSemantic(((SynSet) node.getUserObject()).getId());
                    }
                    tree.setVisible(false);
                    sentence.writeToFile(new File(fileDescription.getFileName()));
                    pane.setVisible(false);
                    clickedWord = null;
                    repaint();
                }
            }
        });
        pane = new JScrollPane(tree);
        add(pane);
        pane.setVisible(false);
        pane.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
    }

    @Override
    protected void setWordLayer() {
    }

    /**
     * Sets the width and height of the JList that displays the word sense ids.
     */
    @Override
    protected void setBounds() {
        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getX(), ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getY() + ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getHeight(), 400, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
    }

    /**
     * Sets the space between displayed lines in the sentence.
     */
    @Override
    protected void setLineSpace() {
        lineSpace = 80;
    }

    /**
     * Draws the sense id of the word.
     * @param word Annotated word itself.
     * @param g Graphics on which sense id is drawn.
     * @param currentLeft Current position on the x-axis, where the sense id will be aligned.
     * @param lineIndex Current line of the word, if the sentence resides in multiple lines on the screen.
     * @param wordIndex Index of the word in the annotated sentence.
     * @param maxSize Maximum size in pixels of anything drawn in the screen.
     * @param wordSize Array storing the sizes of all words in pixels in the annotated sentence.
     * @param wordTotal Array storing the total size until that word of all words in the annotated sentence.
     */
    @Override
    protected void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal) {
        if (word.getSemantic() != null){
            String correct = word.getSemantic();
            g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
        }
    }

    /**
     * Compares the size of the word and the size of the semantic id in pixels and returns the maximum
     * of them.
     * @param word Word annotated.
     * @param g Graphics on which semantic id is drawn.
     * @return Maximum of the graphic sizes of word and its semantic id.
     */
    @Override
    protected int getMaxLayerLength(AnnotatedWord word, Graphics g) {
        int maxSize = g.getFontMetrics().stringWidth(word.getName());
        if (word.getSemantic() != null){
            int size = g.getFontMetrics().stringWidth(word.getSemantic());
            if (size > maxSize){
                maxSize = size;
            }
        }
        return maxSize;
    }

    /**
     * Automatically sense disambiguate words in the sentence using turkishSentenceAutoSemantic.
     */
    public void autoDetect(){
        turkishSentenceAutoSemantic.autoSemantic(sentence);
        sentence.save();
        this.repaint();
    }

    /**
     * Mutator for fsm
     * @param fsm New morphological analyzer
     */
    public void setFsm(FsmMorphologicalAnalyzer fsm){
        this.fsm = fsm;
    }

    /**
     * Mutator for wordnet
     * @param wordNet New wordnet
     */
    public void setWordnet(WordNet wordNet){
        this.wordNet = wordNet;
    }

    /**
     * Mutator for turkishSentenceAutoSemantic
     * @param turkishSentenceAutoSemantic New Turkish sense disambiguator
     */
    public void setTurkishSentenceAutoSemantic(TurkishSentenceAutoSemantic turkishSentenceAutoSemantic){
        this.turkishSentenceAutoSemantic = turkishSentenceAutoSemantic;
    }

    /**
     * Adds synSets as tree nodes to the root of the tree. Then for each synset, adds its literals to the node for
     * that synset. So, synsets will be children of the root node and literals will be grandchildren of the root node.
     * @param word Word for which tree is generated
     * @param synSets Possible candidate synsets for the word
     * @return Selected synset object for the word.
     */
    private DefaultMutableTreeNode addSynSets(AnnotatedWord word, ArrayList<SynSet> synSets){
        DefaultMutableTreeNode selectedNode = null;
        for (SynSet synSet : synSets) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(synSet);
            ((DefaultMutableTreeNode) treeModel.getRoot()).add(childNode);
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(synSet.getSynonym().getLiteral(i));
                childNode.add(grandChildNode);
            }
            if (word.getSemantic() != null && word.getSemantic().equals(synSet.getId())) {
                selectedNode = childNode;
            }
        }
        return selectedNode;
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        AnnotatedWord word;
        int lineIndex = 0, currentLeft = wordSpace, multiple = 1;
        String current;
        Font currentFont = g.getFont();
        g.setColor(Color.BLUE);
        for (int i = 0; i < sentence.wordCount(); i++){
            word = (AnnotatedWord) sentence.getWord(i);
            int maxSize = getMaxLayerLength(word, g);
            if (maxSize + currentLeft >= getWidth()){
                lineIndex++;
                currentLeft = wordSpace;
                multiple = 1;
            }
            multiple--;
            if (word.getSemantic() != null && multiple == 0) {
                SynSet synSet = wordNet.getSynSetWithId(word.getSemantic());
                if (synSet != null){
                    multiple = 1;
                    if (i + 1 < sentence.wordCount()){
                        AnnotatedWord next = (AnnotatedWord) sentence.getWord(i + 1);
                        if (next.getSemantic() != null && synSet.equals(wordNet.getSynSetWithId(next.getSemantic()))){
                            multiple = 2;
                        }
                    }
                    if (i + 2 < sentence.wordCount()){
                        AnnotatedWord twoNext = (AnnotatedWord) sentence.getWord(i + 2);
                        if (twoNext.getSemantic() != null && multiple == 2 && synSet.equals(wordNet.getSynSetWithId(twoNext.getSemantic()))){
                            multiple = 3;
                        }
                    }
                    if (i + 3 < sentence.wordCount()){
                        AnnotatedWord threeNext = (AnnotatedWord) sentence.getWord(i + 3);
                        if (threeNext.getSemantic() != null && multiple == 3 && synSet.equals(wordNet.getSynSetWithId(threeNext.getSemantic()))){
                            multiple = 4;
                        }
                    }
                    if (i + 4 < sentence.wordCount()){
                        AnnotatedWord fourNext = (AnnotatedWord) sentence.getWord(i + 4);
                        if (fourNext.getSemantic() != null && multiple == 4 && synSet.equals(wordNet.getSynSetWithId(fourNext.getSemantic()))){
                            multiple = 5;
                        }
                    }
                    if (synSet.getDefinition() != null){
                        if (synSet.getDefinition().length() < 24 + (multiple - 1) * 35){
                            current = synSet.getDefinition();
                        } else {
                            current = synSet.getDefinition().substring(0, 24 + (multiple - 1) * 35);
                        }
                        g.setFont(new Font(currentFont.getName(), Font.PLAIN, currentFont.getSize() - 2));
                        g.drawString(current, currentLeft, (lineIndex + 1) * lineSpace + 50);
                        g.setFont(currentFont);
                    }
                }
            } else {
                if (word.getSemantic() == null){
                    multiple = 1;
                }
            }
            currentLeft += maxSize + wordSpace;
        }
        setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), (lineIndex + 2) * lineSpace));
        getParent().invalidate();
    }

    /**
     * Constructs possible candidate synsets for a given word. It checks if there is a five, four, three, or two word
     * multi-word idiom expression where word appears anywhere in the idiom. It also adds single word synsets for the
     * given word.
     * @param word Word for which candidate synsets are found
     * @param wordIndex Index of the word in the sentence
     * @return Possible candidate synsets for a given word
     */
    public ArrayList<SynSet> constructCandidateSynSets(AnnotatedWord word, int wordIndex){
        ArrayList<SynSet> result = new ArrayList<>();
        for (int i = wordIndex - 4; i <= wordIndex; i++){
            if (i >= 0 && i + 4 < sentence.wordCount()){
                AnnotatedWord word1 = (AnnotatedWord) sentence.getWord(i);
                AnnotatedWord word2 = (AnnotatedWord) sentence.getWord(i + 1);
                AnnotatedWord word3 = (AnnotatedWord) sentence.getWord(i + 2);
                AnnotatedWord word4 = (AnnotatedWord) sentence.getWord(i + 3);
                AnnotatedWord word5 = (AnnotatedWord) sentence.getWord(i + 4);
                if (word1.getParse() != null && word2.getParse() != null && word3.getParse() != null && word4.getParse() != null && word5.getParse() != null){
                    result.addAll(wordNet.constructIdiomSynSets(word1.getParse(), word2.getParse(), word3.getParse(), word4.getParse(), word5.getParse(), word1.getMetamorphicParse(), word2.getMetamorphicParse(), word3.getMetamorphicParse(), word4.getMetamorphicParse(), word5.getMetamorphicParse(), fsm));
                }
            }
        }
        for (int i = wordIndex - 3; i <= wordIndex; i++){
            if (i >= 0 && i + 3 < sentence.wordCount()){
                AnnotatedWord word1 = (AnnotatedWord) sentence.getWord(i);
                AnnotatedWord word2 = (AnnotatedWord) sentence.getWord(i + 1);
                AnnotatedWord word3 = (AnnotatedWord) sentence.getWord(i + 2);
                AnnotatedWord word4 = (AnnotatedWord) sentence.getWord(i + 3);
                if (word1.getParse() != null && word2.getParse() != null && word3.getParse() != null && word4.getParse() != null){
                    result.addAll(wordNet.constructIdiomSynSets(word1.getParse(), word2.getParse(), word3.getParse(), word4.getParse(), word1.getMetamorphicParse(), word2.getMetamorphicParse(), word3.getMetamorphicParse(), word4.getMetamorphicParse(), fsm));
                }
            }
        }
        for (int i = wordIndex - 2; i <= wordIndex; i++){
            if (i >= 0 && i + 2 < sentence.wordCount()){
                AnnotatedWord word1 = (AnnotatedWord) sentence.getWord(i);
                AnnotatedWord word2 = (AnnotatedWord) sentence.getWord(i + 1);
                AnnotatedWord word3 = (AnnotatedWord) sentence.getWord(i + 2);
                if (word1.getParse() != null && word2.getParse() != null && word3.getParse() != null){
                    result.addAll(wordNet.constructIdiomSynSets(word1.getParse(), word2.getParse(), word3.getParse(), word1.getMetamorphicParse(), word2.getMetamorphicParse(), word3.getMetamorphicParse(), fsm));
                }
            }
        }
        for (int i = wordIndex - 1; i <= wordIndex; i++){
            if (i >= 0 && i + 1 < sentence.wordCount()){
                AnnotatedWord word1 = (AnnotatedWord) sentence.getWord(i);
                AnnotatedWord word2 = (AnnotatedWord) sentence.getWord(i + 1);
                if (word1.getParse() != null && word2.getParse() != null){
                    result.addAll(wordNet.constructIdiomSynSets(word1.getParse(), word2.getParse(), word1.getMetamorphicParse(), word2.getMetamorphicParse(), fsm));
                }
            }
        }
        if (word.getParse() != null){
            result.addAll(wordNet.constructSynSets(word.getParse().getWord().getName(), word.getParse(), word.getMetamorphicParse(), fsm));
        }
        return result;
    }

    /**
     * Fills the JTree that contains all possible senses for the current word.
     * @param sentence Sentence used to populate for the current word.
     * @param wordIndex Index of the selected word.
     * @return The index of the selected word sense, -1 if nothing selected.
     */
    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        DefaultMutableTreeNode selectedNode = null;
        ((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        ArrayList<SynSet> candidates = constructCandidateSynSets(word, wordIndex);
        DefaultMutableTreeNode currentSelected = addSynSets(word, candidates);
        if (currentSelected != null){
            selectedNode = currentSelected;
        }
        treeModel.reload();
        if (selectedNode != null){
            tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(selectedNode)));
        }
        tree.setVisible(true);
        pane.setVisible(true);
        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getX(), ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getY() + 20, 360, 30 + Math.max(3, Math.min(15, ((DefaultMutableTreeNode) treeModel.getRoot()).getChildCount() + 1)) * 18);
        return selectedIndex;
    }

}
