package guiDelegate;

import model.Algorithms.GeneticAlgorithm;
import model.Targets.TargetFunction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GeneticAlgorithmOptionsBar extends AlgorithmOptionsBar {


    private JTextField crField;
    private JTextField mrField;
    private JTextField maxGenField;
    private JTextField popSizeField;
    private JTextField mxField;

    private final static int ROWS = 11;

    public GeneticAlgorithmOptionsBar(Gui gui) {
        super(gui);
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        JButton runAlgorithmButtom = new JButton("Run Algorithm");
        runAlgorithmButtom.addActionListener(e -> submitInput());

        startingValuePanel = new JPanel();
        searchSpacePanel = new JPanel();
        parameterPanel = new JPanel();

        String [] dataTargetSelector = gui.getRegisteredTargets().keySet().toArray(new String[0]);
        String [] dataParamNumSelector = {"1", "2", "3", "4"};

        ArrayList<JComponent> optionsBarComponents = new ArrayList<>();

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

        GridLayout gl = new GridLayout(ROWS, COLUMNS); // 1 row, dimensions columns
        this.setLayout(gl);

        this.setBorder(BorderFactory.createTitledBorder("Algorithm Options"));

        targetSelector.addActionListener(event -> {
            this.dynamicFieldsOnDimension(startingValuePanel);
            this.dynamicFieldsOnDimension(searchSpacePanel);
            this.setParameterSettable();
            gui.resetFunctionInfoBoxName();
        });

        paramNumSelector.addActionListener(event -> {
            this.dynamicFieldsOnParameters();
        });


        // add buttons, label, and TextField to the optionsBar
        for (JComponent comp: optionsBarComponents) {
            this.add(comp);
        }

        this.add(runAlgorithmButtom);
    }

    /** {@inheritDoc} */
    @Override
    public void submitInput() {
        // todo split this into functions
        try {

            String targetName = (String) targetSelector.getSelectedItem();
            TargetFunction target = gui.getRegisteredTargets().get(targetName);

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
            gui.getAlgo().setTarget(target);

            // Crossover rate
            double cr = Double.parseDouble(crField.getText());
            ((GeneticAlgorithm) gui.getAlgo()).setCr(cr);

            // Mutation rate
            double mr = Double.parseDouble(mrField.getText());
            ((GeneticAlgorithm) gui.getAlgo()).setMr(mr);

            // Mutation parameter
            double mx = Double.parseDouble(mxField.getText());
            ((GeneticAlgorithm) gui.getAlgo()).setMx(mx);

            // Population Size
            int popSize = Integer.parseInt(popSizeField.getText());
            ((GeneticAlgorithm) gui.getAlgo()).setPopSize(popSize);

            // Starting values
            int dimensions = gui.getRegisteredTargets().get(targetName).getDimension();
            double[] startingValues = new double[dimensions];
            for (int i = 0; i < dimensions; i++) {
                JTextField val = (JTextField) startingValuePanel.getComponent(i);
                startingValues[i] = Double.parseDouble(val.getText());
            }
            gui.getAlgo().setStartingValues(startingValues);

            // Search space
            double[] searchSpace = new double[dimensions];
            for (int i = 0; i < dimensions; i++) {
                JTextField val = (JTextField) searchSpacePanel.getComponent(i);
                searchSpace[i] = Double.parseDouble(val.getText());
            }
            gui.getAlgo().setSearchSpace(searchSpace);

            // Maximum generation
            int maxGen = Integer.parseInt(maxGenField.getText());
            ((GeneticAlgorithm) gui.getAlgo()).setMaxGen(maxGen);

            gui.displayInfoBox();

            // Run algorithm
            gui.getAlgo().runAlgorithm();


        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this.gui.getMainFrame(), "Ooops, your arguments were faulty!");
            throw new IllegalArgumentException(exc);
        }
    }
}
