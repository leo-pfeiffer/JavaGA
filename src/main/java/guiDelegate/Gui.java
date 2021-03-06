package guiDelegate;

import model.Algorithms.Algorithm;
import model.Solutions.Chromosome;
import model.Algorithms.GeneticAlgorithm;
import model.OutputProcessor;
import model.Solutions.Solution;
import model.Targets.TargetFunction;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gui implements PropertyChangeListener {

    private final HashMap<String, TargetFunction> registeredTargets;
    private final HashMap<String, Algorithm> registeredAlgos;
    private Algorithm algo;

    private static final int MIN_FRAME_HEIGHT = 500;
    private static final int MIN_FRAME_WIDTH = 1000;

    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_WIDTH = 1200;
    private static final int OPTIONS_BAR_WIDTH = 300;
    private static final int OUTPUT_BAR_WIDTH = 200;
    private static final int GRAPH_WIDTH = FRAME_WIDTH - OPTIONS_BAR_WIDTH - OPTIONS_BAR_WIDTH;
    private static final int TEXT_WIDTH = 10;

    private JFrame mainFrame;

    private JPanel optionsBar;
    private AlgorithmOptionsBar algorithmOptionsBar;
    private JPanel outputBox;
    private JPanel infoBar;
    private JPanel functionInfoBox;

    private JComboBox<String> algoSelector;
    private GraphPanel graphPanel;

    private JMenuBar menuBar;

    private JTextPane expressionField;
    private JTextArea targetValueField;
    private JTextArea solutionValueField;

    /** Action to save the drawing. */
    Action saveAction;
    /** Action to open the help dialog. */
    Action helpAction;

    public Gui(HashMap<String, TargetFunction> registeredTargets, HashMap<String, Algorithm> registeredAlgos){
        this.registeredTargets = registeredTargets;
        this.registeredAlgos = registeredAlgos;

        // setup model (ga)
        // this.algo = new GeneticAlgorithm();

        // setup the main frame
        mainFrame = new JFrame("OptiVis");
        mainFrame.setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);

        // setup menu actions
        createActions();

        // initialise options bar
        optionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputBox = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // initialise info box
        functionInfoBox = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // initialise menu bar
        menuBar = new JMenuBar();

        // initialise the graph canvas
        List<Double> startGraphValues = new ArrayList<>();
        startGraphValues.add(0d);
        graphPanel = new GraphPanel(startGraphValues);
        graphPanel.setPreferredSize(new Dimension(GRAPH_WIDTH, FRAME_HEIGHT));

        // setup all components
        setupComponents();

        // add the observer
        this.algo.addObserver(this);
    }

    private void setupComponents(){
        setupMenus();
        setupOptionsBar();
        setupInfoBox();
        mainFrame.add(graphPanel, BorderLayout.CENTER);
        mainFrame.pack();
        algorithmOptionsBar.setDynamicFields();
    }

    private void setupOptionsBar(){

        // Set up algoSelector
        String [] dataAlgoSelector = registeredAlgos.keySet().toArray(new String[0]);
        algoSelector = new JComboBox<>(dataAlgoSelector);

        JPanel algoSelectorPanel = new JPanel();
        GridLayout gl = new GridLayout(1,2); // 1 row, 2 columns
        algoSelectorPanel.setLayout(gl);
        algoSelectorPanel.add(new JLabel("Algorithm:"));
        algoSelectorPanel.add(algoSelector);
        algoSelectorPanel.setBorder(BorderFactory.createEmptyBorder());

        optionsBar.add(algoSelectorPanel);

        algoSelector.addActionListener(e -> {
            this.algo = registeredAlgos.get(getAlgoSelectorSelected());
        });

        // Select selected algo as this.algo
        this.algo = registeredAlgos.get(getAlgoSelectorSelected());

        // Determine which AlgorithmOptionsBar to use
        if (this.algo instanceof GeneticAlgorithm) {
            algorithmOptionsBar = new GeneticAlgorithmOptionsBar(this);
        } else {
            JOptionPane.showMessageDialog(this.mainFrame, "Ooops, this algorithm is not registered!");
            throw new RuntimeException("This algorithm is not registered: " + getAlgoSelectorSelected());
        }

        // Setup und add AlgorithmOptionsBar
        algorithmOptionsBar.setup();
        optionsBar.add(algorithmOptionsBar);

        // Set size of optionsBar and add to mainFrame
        optionsBar.setPreferredSize(new Dimension(OPTIONS_BAR_WIDTH, FRAME_HEIGHT));
        mainFrame.add(optionsBar, BorderLayout.WEST);
    }

    private void setupInfoBox() {

        infoBar = new JPanel();
        GridLayout gl = new GridLayout(2, 1);
        infoBar.setLayout(gl);

        // Function info box
        functionInfoBox.setBorder(BorderFactory.createTitledBorder(algorithmOptionsBar.getSelectedTarget()));
        expressionField = new JTextPane();
        expressionField.setVisible(true);
        expressionField.setContentType("text/html");
        expressionField.setText("<html>f(x) = ...</html>");
        expressionField.setEditable(false);
        expressionField.setBackground(null);
        expressionField.revalidate();

        functionInfoBox.add(expressionField);

        infoBar.add(functionInfoBox);

        // Output Box
        outputBox.setBorder(BorderFactory.createTitledBorder("Output"));

        targetValueField = new JTextArea(1, TEXT_WIDTH);
        solutionValueField = new JTextArea(1, TEXT_WIDTH);

        targetValueField.setEditable(false);
        targetValueField.setBackground(Color.LIGHT_GRAY);

        solutionValueField.setEditable(false);
        solutionValueField.setBackground(Color.LIGHT_GRAY);

        JLabel targetValueLabel = new JLabel("Target value: ");
        JLabel solutionValueLabel = new JLabel("Solution: ");

        outputBox.add(targetValueLabel);
        outputBox.add(targetValueField);
        outputBox.add(solutionValueLabel);
        outputBox.add(solutionValueField);

        infoBar.add(outputBox);

        // add optionsBar to north of main frame
        infoBar.setPreferredSize(new Dimension(OUTPUT_BAR_WIDTH, FRAME_HEIGHT));
        mainFrame.add(infoBar, BorderLayout.EAST);
    }

    private void setupMenus() {
        // Set up file and edit menus
        JMenu file = new JMenu ("File");
        JMenu edit = new JMenu ("Edit");

        // Create the items
        JMenuItem save = new JMenuItem ("Export to JSON");
        JMenuItem help = new JMenuItem ("Help");

        // Add items to the menu
        file.add (save);
        file.add (help);

        // Add menus to the overall menu
        menuBar.add(file);
        menuBar.add(edit);

        // Add action listeners and actions to the items
        addActionListenerToMenuItem(save, saveAction);
        addActionListenerToMenuItem(help, helpAction);

        // Attach the menu bar to the main frame
        mainFrame.setJMenuBar(menuBar);
    }

    /** Create all actions. */
    public void createActions() {

        // Save as a JSON file
        saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Solution[][] generations = algo.getGenerations();

                if (generations == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Nothing to save yet.\n" +
                                    "Run an algorithm first.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Open a file chooser dialog
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save");

                    int userSelection = fileChooser.showSaveDialog(fileChooser);

                    // If user has entered a name and clicked OK, write the shapes to a file
                    if (userSelection == JFileChooser.APPROVE_OPTION) {

                        File saveFile = fileChooser.getSelectedFile();
                        if (!FilenameUtils.getExtension(saveFile.getName()).equalsIgnoreCase("json")) {
                            // remove the extension (if any) and replace it with ".json"
                            saveFile = new File(saveFile.getParentFile(),
                                    FilenameUtils.getBaseName(saveFile.getName())+".json");
                        }

                        OutputProcessor op = new OutputProcessor(generations);
                        op.saveGenerationsToFile(saveFile.toString());
                    }
                }
            }
        };

        // Open the help dialog
        helpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, HelpText.getText(), "Help",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
    }

    /** Add an action listener and an action to a JMenuItem.
     * @param item the JMenuItem.
     * @param action the action to be attached. */
    public void addActionListenerToMenuItem(JMenuItem item, Action action) {
        item.addActionListener(action);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if(event.getSource() == algo && event.getPropertyName().equals("run_complete")) {
            // Tell the SwingUtilities thread to update the text in the GUI components.
            SwingUtilities.invokeLater(() -> {
                Chromosome[][] generations = (Chromosome[][]) event.getNewValue();
                OutputProcessor op = new OutputProcessor(generations);
                displayGraph(op.getFitnessValues());
                displayOutput(op.getSolution().getSolutions(), op.getTargetValue());
            });
        }
    }

    public void displayInfoBox() {
        expressionField.setText(this.algo.getTarget().toString());
        expressionField.revalidate();
    }

    public void displayGraph(List<Double> fitnessValues) {
        graphPanel.setScores(fitnessValues);
    }

    public void displayOutput(double[] solution, double targetValue) {

        // solution
        StringBuilder solutionTextBuilder = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        for (double p: solution) {
            solutionTextBuilder.append(df.format(p));
            solutionTextBuilder.append("\n");
        }

        String solutionText = solutionTextBuilder.substring(0, solutionTextBuilder.length()-2);
        solutionValueField.setRows(solution.length);
        solutionValueField.setText(solutionText);

        // target value
        targetValueField.setText(df.format(targetValue));
    }


    public void resetFunctionInfoBoxName() {
        functionInfoBox.setBorder(BorderFactory.createTitledBorder(algorithmOptionsBar.getSelectedTarget()));
    }

    public HashMap<String, TargetFunction> getRegisteredTargets() {
        return registeredTargets;
    }

    public Algorithm getAlgo() {
        return this.algo;
    }

    public JFrame getMainFrame() {
        return this.mainFrame;
    }

    public String getAlgoSelectorSelected() {
        return this.algoSelector.getSelectedItem().toString();
    }
}
