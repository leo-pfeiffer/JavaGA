package guiDelegate;

import model.Solutions.Chromosome;
import model.Algorithms.GeneticAlgorithm;
import model.OutputProcessor;
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

public class GuiGA implements PropertyChangeListener {

    private HashMap<String, TargetFunction> registeredTargets;
    private GeneticAlgorithm ga;

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
    private JPanel outputBox;
    private JPanel infoBar;
    private JPanel functionInfoBox;

    private JComboBox<String> targetSelector;
    private JComboBox<String> paramNumSelector;
    private GraphPanel graphPanel;

    private JMenuBar menuBar;

    private JTextField crField;
    private JTextField mrField;
    private JTextField mxField;
    private JTextField popSizeField;
    private JTextField maxGenField;

    private JPanel startingValuePanel;
    private JPanel searchSpacePanel;
    private JPanel parameterPanel;

    private JTextArea targetValueField;
    private JTextArea solutionValueField;

    /** Action to save the drawing. */
    Action saveAction;
    /** Action to open the help dialog. */
    Action helpAction;

    private ArrayList<JComponent> optionsBarComponents;

    public GuiGA(HashMap<String, TargetFunction> registeredTargets){
        this.registeredTargets = registeredTargets;

        // setup model (ga)
        this.ga = new GeneticAlgorithm();

        mainFrame = new JFrame("GA");
        mainFrame.setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);

        createActions();

        optionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        functionInfoBox = new JPanel(new FlowLayout(FlowLayout.LEFT));

        menuBar = new JMenuBar();

        List<Double> startGraphValues = new ArrayList<>();
        startGraphValues.add(0d);
        graphPanel = new GraphPanel(startGraphValues);
        graphPanel.setPreferredSize(new Dimension(GRAPH_WIDTH, FRAME_HEIGHT));

        setupComponents();

        this.ga.addObserver(this);
    }

    private void setupComponents(){
        setupMenus();
        setupOptionsBar();
        setupInfoBox();
        mainFrame.add(graphPanel, BorderLayout.CENTER);
        mainFrame.pack();
        dynamicFieldsOnDimension(startingValuePanel);
        dynamicFieldsOnDimension(searchSpacePanel);
        dynamicFieldsOnParameters();
    }

    private void setupOptionsBar(){

        JButton runAlgorithmButtom = new JButton("Run Algorithm");
        runAlgorithmButtom.addActionListener(e -> submitInput());

        startingValuePanel = new JPanel();
        searchSpacePanel = new JPanel();
        parameterPanel = new JPanel();

        String [] dataTargetSelector = registeredTargets.keySet().toArray(new String[0]);
        String [] dataParamNumSelector = {"1", "2", "3", "4"};

        optionsBarComponents = new ArrayList<>();

        optionsBarComponents.add(new JLabel("Target function: "));
        optionsBarComponents.add(targetSelector = new JComboBox<>(dataTargetSelector));
        optionsBarComponents.add(new JLabel("# parameters: "));
        optionsBarComponents.add(paramNumSelector = new JComboBox<>(dataParamNumSelector));
        optionsBarComponents.add(new JLabel("Parameters: "));
        optionsBarComponents.add(parameterPanel);
        optionsBarComponents.add(new JLabel("Crossover rate: "));
        optionsBarComponents.add(crField = new JTextField("0.7", TEXT_WIDTH));
        optionsBarComponents.add(new JLabel("Mutation rate: "));
        optionsBarComponents.add(mrField = new JTextField("0.8", TEXT_WIDTH));
        optionsBarComponents.add(new JLabel("Mutation parameter: "));
        optionsBarComponents.add(mxField = new JTextField("0.2", TEXT_WIDTH));
        optionsBarComponents.add(new JLabel("Population size: "));
        optionsBarComponents.add(popSizeField = new JTextField("10", TEXT_WIDTH));
        optionsBarComponents.add(new JLabel("Starting values: "));
        optionsBarComponents.add(startingValuePanel);
        optionsBarComponents.add(new JLabel("Search space: "));
        optionsBarComponents.add(searchSpacePanel);
        optionsBarComponents.add(new JLabel("Max. generation: "));
        optionsBarComponents.add(maxGenField = new JTextField("100", TEXT_WIDTH));
        optionsBarComponents.add(runAlgorithmButtom);

        GridLayout gl = new GridLayout(11, 2); // 1 row, dimensions columns
        optionsBar.setLayout(gl);

        optionsBar.setBorder(BorderFactory.createTitledBorder("Options"));

        targetSelector.addActionListener(event -> {
            dynamicFieldsOnDimension(startingValuePanel);
            dynamicFieldsOnDimension(searchSpacePanel);
            setParameterSettable();
            resetFunctionInfoBoxName();
        });

        paramNumSelector.addActionListener(event -> {
            dynamicFieldsOnParameters();
        });


        // add buttons, label, and TextField to the optionsBar
        for (JComponent comp: optionsBarComponents) {
            optionsBar.add(comp);
        }

        optionsBar.add(runAlgorithmButtom);

        optionsBar.setPreferredSize(new Dimension(OPTIONS_BAR_WIDTH, FRAME_HEIGHT));
        // add optionsBar to West of main frame
        mainFrame.add(optionsBar, BorderLayout.WEST);

    }

    private void setupInfoBox() {

        infoBar = new JPanel();
        GridLayout gl = new GridLayout(2, 1); // 1 row, dimensions columns
        infoBar.setLayout(gl);

        // Function info box
        functionInfoBox.setBorder(BorderFactory.createTitledBorder(targetSelector.getSelectedItem().toString()));
        // todo add info about the function here

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

        // Save the drawing
        saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Chromosome[][] generations = ga.getGenerations();

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
        if(event.getSource() == ga && event.getPropertyName().equals("run_complete")) {
            // Tell the SwingUtilities thread to update the text in the GUI components.
            SwingUtilities.invokeLater(() -> {
                Chromosome[][] generations = (Chromosome[][]) event.getNewValue();
                OutputProcessor op = new OutputProcessor(generations);
                displayGraph(op.getFitnessValues());
                displayOutput(op.getSolution(), op.getTargetValue());
            });
        }
    }

    private void submitInput() {
        // todo split this into functions
        try {

            String targetName = (String) targetSelector.getSelectedItem();
            TargetFunction target = registeredTargets.get(targetName);

            // If parameters are allowed, get number of parameters
            if (target.isHasParameters()) {
                int numParams = Integer.parseInt(paramNumSelector.getSelectedItem().toString());
                double[] params = new double[numParams];
                for (int i = 0; i < numParams; i++) {
                    JTextField val = (JTextField) parameterPanel.getComponent(i);
                    params[i] = Double.parseDouble(val.getText());
                }
                target.setParameters(params);
            }

            // Target function
            ga.setTarget(target);

            // Crossover rate
            double cr = Double.parseDouble(crField.getText());
            ga.setCr(cr);

            // Mutation rate
            double mr = Double.parseDouble(mrField.getText());
            ga.setMr(mr);

            // Mutation parameter
            double mx = Double.parseDouble(mxField.getText());
            ga.setMx(mx);

            // Population Size
            int popSize = Integer.parseInt(popSizeField.getText());
            ga.setPopSize(popSize);

            // Starting values
            int dimensions = registeredTargets.get(targetName).getDimension();
            double[] startingValues = new double[dimensions];
            for (int i = 0; i < dimensions; i++) {
                JTextField val = (JTextField) startingValuePanel.getComponent(i);
                startingValues[i] = Double.parseDouble(val.getText());
            }
            ga.setStartingValues(startingValues);

            // Search space
            double[] searchSpace = new double[dimensions];
            for (int i = 0; i < dimensions; i++) {
                JTextField val = (JTextField) searchSpacePanel.getComponent(i);
                searchSpace[i] = Double.parseDouble(val.getText());
            }
            ga.setSearchSpace(searchSpace);

            // Maximum generation
            int maxGen = Integer.parseInt(maxGenField.getText());
            ga.setMaxGen(maxGen);

            // Run algorithm
            ga.runAlgorithm();


        } catch (Exception exc) {
            JOptionPane.showMessageDialog(mainFrame, "Ooops, your arguments were faulty!");
            throw new IllegalArgumentException(exc);
        }
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

    public void dynamicFieldsOnDimension(JPanel panel) {

        String targetName = (String) targetSelector.getSelectedItem();
        TargetFunction target = registeredTargets.get(targetName);
        int dimensions = target.getDimension();

        panel.removeAll();
        GridLayout gl = new GridLayout(1, dimensions); // 1 row, dimensions columns
        panel.setLayout(gl);

        for (int i = 0; i < dimensions; i++){
            panel.add(new JTextField());
        }

        panel.revalidate();
        panel.repaint();
    }

    public void dynamicFieldsOnParameters() {

        int numParams = Integer.parseInt(paramNumSelector.getSelectedItem().toString());

        parameterPanel.removeAll();
        GridLayout gl = new GridLayout(1, numParams); // 1 row, dimensions columns
        parameterPanel.setLayout(gl);

        for (int i = 0; i < numParams; i++){
            parameterPanel.add(new JTextField());
        }

        parameterPanel.revalidate();
        parameterPanel.repaint();
    }

    public void setParameterSettable() {
        String targetName = (String) targetSelector.getSelectedItem();
        TargetFunction target = registeredTargets.get(targetName);

        if (target.isHasParameters()) {
            JTextField paramField = (JTextField) parameterPanel.getComponent(0);
            paramField.setEnabled(true);
            paramField.setBackground(Color.WHITE);
            paramNumSelector.setEnabled(true);
        } else {
            paramNumSelector.setSelectedItem("1");
            JTextField paramField = (JTextField) parameterPanel.getComponent(0);
            paramField.setEnabled(false);
            paramField.setBackground(Color.LIGHT_GRAY);
            paramNumSelector.setEnabled(false);
        }
    }

    public void resetFunctionInfoBoxName() {
        functionInfoBox.setBorder(BorderFactory.createTitledBorder(targetSelector.getSelectedItem().toString()));
    }
}
