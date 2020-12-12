package guiDelegate;

import model.Chromosome;
import model.GeneticAlgorithm;
import model.OutputProcessor;
import model.TargetFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GuiGA implements PropertyChangeListener {

    private HashMap<String, TargetFunction> registeredTargets;
    private GeneticAlgorithm ga;

    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_WIDTH = 1200;
    private static final int TOOLBAR_WIDTH = 200;
    private static final int TEXT_HEIGHT = 10;
    private static final int TEXT_WIDTH = 10;

    private JFrame mainFrame;

    private JToolBar toolbar;
    private JComboBox<String> targetSelector;
    private JScrollPane outputPane;
    private JTextArea outputField;

    private JTextField crField;
    private JTextField mrField;
    private JTextField mxField;
    private JTextField popSizeField;
    private JTextField startingValuesField;
    private JTextField searchSpaceField;
    private JTextField maxGenField;

    private ArrayList<JComponent> toolbarComponents;

    public GuiGA(HashMap<String, TargetFunction> registeredTargets){
        this.registeredTargets = registeredTargets;

        // setup model (ga)
        this.ga = new GeneticAlgorithm();

        mainFrame = new JFrame("GA");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setVisible(true);

        toolbar = new JToolBar(JToolBar.VERTICAL);

        String [] dataTargetSelector = registeredTargets.keySet().toArray(new String[0]);

        toolbarComponents = new ArrayList<>();
        toolbarComponents.add(targetSelector = new JComboBox<>(dataTargetSelector));
        toolbarComponents.add(crField = new JTextField("0.7", TEXT_WIDTH));
        toolbarComponents.add(mrField = new JTextField("0.8", TEXT_WIDTH));
        toolbarComponents.add(mxField = new JTextField("0.2", TEXT_WIDTH));
        toolbarComponents.add(popSizeField = new JTextField("10", TEXT_WIDTH));
        toolbarComponents.add(startingValuesField = new JTextField(TEXT_WIDTH));
        toolbarComponents.add(searchSpaceField = new JTextField(TEXT_WIDTH));
        toolbarComponents.add(maxGenField = new JTextField("100", TEXT_WIDTH));

        outputField = new JTextArea(TEXT_WIDTH, TEXT_HEIGHT);
        outputField.setEditable(false);

        outputPane = new JScrollPane(outputField);
        setupComponents();

        this.ga.addObserver(this);

    }

    private void setupComponents(){
        setupToolbar();
        mainFrame.add(outputPane, BorderLayout.CENTER);
        mainFrame.setSize (FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupToolbar(){

        JLabel tLabel = new JLabel("Target function: ");
        JLabel crLabel = new JLabel("Crossover rate: ");
        JLabel mrLabel = new JLabel("Mutation rate: ");
        JLabel mxLabel = new JLabel("Mutation parameter: ");
        JLabel popSizeLabel = new JLabel("Population size: ");
        JLabel startingValuesLabel = new JLabel("Starting values: ");
        JLabel searchSpaceLabel = new JLabel("Search space: ");
        JLabel maxGenLabel = new JLabel("Max. generation: ");

        setArrayDefaultTextFromTargetFunction();

        for (JComponent component: toolbarComponents) {

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

        targetSelector.addActionListener(event -> setArrayDefaultTextFromTargetFunction());

        JButton runAlgorithmButtom = new JButton("Run Algorithm");
        runAlgorithmButtom.addActionListener(e -> submitInput());

        // add buttons, label, and textfield to the toolbar
        toolbar.add(tLabel);
        toolbar.add(targetSelector);
        toolbar.add(crLabel);
        toolbar.add(crField);
        toolbar.add(mrLabel);
        toolbar.add(mrField);
        toolbar.add(mxLabel);
        toolbar.add(mxField);
        toolbar.add(popSizeLabel);
        toolbar.add(popSizeField);
        toolbar.add(startingValuesLabel);
        toolbar.add(startingValuesField);
        toolbar.add(searchSpaceLabel);
        toolbar.add(searchSpaceField);
        toolbar.add(maxGenLabel);
        toolbar.add(maxGenField);

        toolbar.add(runAlgorithmButtom);
        toolbar.setPreferredSize(new Dimension(TOOLBAR_WIDTH, FRAME_HEIGHT));
        // add toolbar to north of main frame
        mainFrame.add(toolbar, BorderLayout.WEST);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if(event.getSource() == ga && event.getPropertyName().equals("run_complete")) {
            // Tell the SwingUtilities thread to update the text in the GUI components.
            SwingUtilities.invokeLater(() -> {
                Chromosome[][] generations = (Chromosome[][]) event.getNewValue();
                OutputProcessor op = new OutputProcessor(generations);
                outputField.setText(Arrays.toString(op.fitnessValues().toArray()));
            });
        }
    }

    private void submitInput() {
        try {
            // Target
            String targetName = (String) targetSelector.getSelectedItem();
            ga.setTarget(registeredTargets.get(targetName));

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
            JOptionPane.showMessageDialog(mainFrame, "Ooops, your arguments were faulty!");
        }
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
        int dimensions = registeredTargets.get(targetName).getDimension();
        startingValuesField.setText(getDefaultStartingValuesString(dimensions));
        searchSpaceField.setText(getDefaultSearchSpaceString(dimensions));
    }
}
