package guiDelegate;

import model.Chromosome;
import model.GeneticAlgorithm;
import model.OutputProcessor;
import model.Target.TargetFunction;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JPanel outputBar;

    private JComboBox<String> targetSelector;
    private GraphPanel graphPanel;

    private JMenuBar menuBar;

    private JTextField parameterField;
    private JTextField crField;
    private JTextField mrField;
    private JTextField mxField;
    private JTextField popSizeField;
    private JTextField startingValuesField;
    private JTextField searchSpaceField;
    private JTextField maxGenField;

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
        outputBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        menuBar = new JMenuBar();

        String [] dataTargetSelector = registeredTargets.keySet().toArray(new String[0]);

        optionsBarComponents = new ArrayList<>();
        optionsBarComponents.add(targetSelector = new JComboBox<>(dataTargetSelector));
        optionsBarComponents.add(parameterField = new JTextField("", TEXT_WIDTH));
        optionsBarComponents.add(crField = new JTextField("0.7", TEXT_WIDTH));
        optionsBarComponents.add(mrField = new JTextField("0.8", TEXT_WIDTH));
        optionsBarComponents.add(mxField = new JTextField("0.2", TEXT_WIDTH));
        optionsBarComponents.add(popSizeField = new JTextField("10", TEXT_WIDTH));
        optionsBarComponents.add(startingValuesField = new JTextField(TEXT_WIDTH));
        optionsBarComponents.add(searchSpaceField = new JTextField(TEXT_WIDTH));
        optionsBarComponents.add(maxGenField = new JTextField("100", TEXT_WIDTH));

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
        setupOutputBar();
        mainFrame.add(graphPanel, BorderLayout.CENTER);
        mainFrame.pack();
    }

    private void setupOptionsBar(){

        optionsBar.setBorder(BorderFactory.createTitledBorder("Options"));

        JLabel tLabel = new JLabel("Target function: ");
        JLabel paramLabel = new JLabel("Parameters: ");
        JLabel crLabel = new JLabel("Crossover rate: ");
        JLabel mrLabel = new JLabel("Mutation rate: ");
        JLabel mxLabel = new JLabel("Mutation parameter: ");
        JLabel popSizeLabel = new JLabel("Population size: ");
        JLabel startingValuesLabel = new JLabel("Starting values: ");
        JLabel searchSpaceLabel = new JLabel("Search space: ");
        JLabel maxGenLabel = new JLabel("Max. generation: ");

        setArrayDefaultTextFromTargetFunction();

        for (JComponent component: optionsBarComponents) {

            // todo key events are not recognised yet for some reason
            component.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        submitInput();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
        }

        targetSelector.addActionListener(event -> {
            setArrayDefaultTextFromTargetFunction();
        });

        JButton runAlgorithmButtom = new JButton("Run Algorithm");
        runAlgorithmButtom.addActionListener(e -> submitInput());

        // add buttons, label, and textfield to the optionsBar
        optionsBar.add(tLabel);
        optionsBar.add(targetSelector);
        optionsBar.add(paramLabel);
        optionsBar.add(parameterField);
        optionsBar.add(crLabel);
        optionsBar.add(crField);
        optionsBar.add(mrLabel);
        optionsBar.add(mrField);
        optionsBar.add(mxLabel);
        optionsBar.add(mxField);
        optionsBar.add(popSizeLabel);
        optionsBar.add(popSizeField);
        optionsBar.add(startingValuesLabel);
        optionsBar.add(startingValuesField);
        optionsBar.add(searchSpaceLabel);
        optionsBar.add(searchSpaceField);
        optionsBar.add(maxGenLabel);
        optionsBar.add(maxGenField);

        optionsBar.add(runAlgorithmButtom);
        optionsBar.setPreferredSize(new Dimension(OPTIONS_BAR_WIDTH, FRAME_HEIGHT));
        // add optionsBar to north of main frame
        mainFrame.add(optionsBar, BorderLayout.WEST);
    }

    private void setupOutputBar() {
        outputBar.setBorder(BorderFactory.createTitledBorder("Output"));

        targetValueField = new JTextArea(1, TEXT_WIDTH);
        solutionValueField = new JTextArea(1, TEXT_WIDTH);

        targetValueField.setEditable(false);
        targetValueField.setBackground(Color.LIGHT_GRAY);

        solutionValueField.setEditable(false);
        solutionValueField.setBackground(Color.LIGHT_GRAY);

        JLabel targetValueLabel = new JLabel("Target value: ");
        JLabel solutionValueLabel = new JLabel("Solution: ");

        outputBar.add(targetValueLabel);
        outputBar.add(targetValueField);
        outputBar.add(solutionValueLabel);
        outputBar.add(solutionValueField);

        outputBar.setPreferredSize(new Dimension(OUTPUT_BAR_WIDTH, FRAME_HEIGHT));
        // add optionsBar to north of main frame
        mainFrame.add(outputBar, BorderLayout.EAST);
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
        try {

            String targetName = (String) targetSelector.getSelectedItem();
            TargetFunction target = registeredTargets.get(targetName);

            if (target.isHasParameters()) {
                String paramsIn = parameterField.getText();
                List<String> paramsInRaw = Arrays.asList(paramsIn.split(","));

                double[] params = new double[paramsInRaw.size()];
                for (int j = 0; j < params.length; j++) {
                    params[j] = Double.parseDouble(paramsInRaw.get(j));
                }
                target.setParameters(params);
            }

            // Target function
            ga.setTarget(target);

            // Cr
            double cr = Double.parseDouble(crField.getText());
            ga.setCr(cr);

            // Mr
            double mr = Double.parseDouble(mrField.getText());
            ga.setMr(mr);

            // Mx
            double mx = Double.parseDouble(mxField.getText());
            ga.setMx(mx);

            // Pop Size
            int popSize = Integer.parseInt(popSizeField.getText());
            ga.setPopSize(popSize);

            // Starting values
            int dimensions = registeredTargets.get(targetName).getDimension();
            String startingValuesIn = startingValuesField.getText();
            List<String> startingValuesRaw = Arrays.asList(startingValuesIn.split(","));

            if (startingValuesRaw.toArray().length != dimensions) {
                throw new IllegalArgumentException("Incorrect number of dimensions.\n" +
                        "Expected " + dimensions + " but got " + startingValuesRaw.toArray().length);
            }

            double[] startingValues = new double[dimensions];
            for (int j = 0; j < startingValues.length; j++) {
                startingValues[j] = Double.parseDouble(startingValuesRaw.get(j));
            }
            ga.setStartingValues(startingValues);

            // search space
            String searchSpaceIn = searchSpaceField.getText();
            List<String> searchSpaceRaw = Arrays.asList(searchSpaceIn.split(","));

            if (searchSpaceRaw.toArray().length != dimensions) {
                throw new IllegalArgumentException("Incorrect number of dimensions.\n" +
                        "Expected " + dimensions + " but got " + searchSpaceRaw.toArray().length);
            }

            double[] searchSpace = new double[dimensions];
            for (int j = 0; j < searchSpace.length; j++) {
                searchSpace[j] = Double.parseDouble(searchSpaceRaw.get(j));
            }
            ga.setSearchSpace(searchSpace);

            // max gen
            int maxGen = Integer.parseInt(maxGenField.getText());
            ga.setMaxGen(maxGen);

            // run algorithm
            ga.runAlgorithm();


        } catch (Exception exc) {
            throw new IllegalArgumentException(exc);
            // JOptionPane.showMessageDialog(mainFrame, "Ooops, your arguments were faulty!");
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

    public String getDefaultStartingValuesString(int dimensions) {
        String startingValuesIn = "0,".repeat(dimensions);
        startingValuesIn = startingValuesIn.substring(0, startingValuesIn.length()-1);
        return startingValuesIn;
    }

    public String getDefaultSearchSpaceString(int dimensions) {
        String searchSpaceIn = "1,".repeat(dimensions);
        searchSpaceIn = searchSpaceIn.substring(0, searchSpaceIn.length()-1);
        return searchSpaceIn;
    }

    public void setArrayDefaultTextFromTargetFunction() {
        String targetName = (String) targetSelector.getSelectedItem();
        TargetFunction target = registeredTargets.get(targetName);
        int dimensions = target.getDimension();
        startingValuesField.setText(getDefaultStartingValuesString(dimensions));
        searchSpaceField.setText(getDefaultSearchSpaceString(dimensions));

        if (target.isHasParameters()) {
            parameterField.setEditable(true);
            parameterField.setBackground(Color.WHITE);
            StringBuilder parameterTextBuilder = new StringBuilder();
            double[] parameters = target.getParameters();
            for (double p: parameters) {
                parameterTextBuilder.append(p);
                parameterTextBuilder.append(",");
            }
            String parameterText = parameterTextBuilder.substring(0, parameterTextBuilder.length()-1);
            parameterField.setText(parameterText);
        } else {
            parameterField.setEditable(false);
            parameterField.setBackground(Color.LIGHT_GRAY);
            parameterField.setText("");
        }
    }
}
