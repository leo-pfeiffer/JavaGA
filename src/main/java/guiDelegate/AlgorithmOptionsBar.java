package guiDelegate;

import model.Targets.TargetFunction;

import javax.swing.*;
import java.awt.*;

public abstract class AlgorithmOptionsBar extends JPanel {

    protected static final int COLUMNS = 2;
    protected static final int TEXT_WIDTH = 10;

    protected Gui gui;

    protected JComboBox<String> targetSelector;
    protected JComboBox<String> paramNumSelector;

    protected JPanel startingValuePanel;
    protected JPanel searchSpacePanel;
    protected JPanel parameterPanel;

    /** Setup the options bar. */
    public abstract void setup();

    /** Algorithm specific submitInput method. */
    public abstract void submitInput();

    public AlgorithmOptionsBar(Gui gui) {
        this.gui = gui;
    }

    public String getSelectedTarget() {
        return targetSelector.getSelectedItem().toString();
    };

    public void setParameterSettable() {
        String targetName = getSelectedTarget();
        TargetFunction target = gui.getRegisteredTargets().get(targetName);

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

    public void dynamicFieldsOnDimension(JPanel panel) {

        String targetName = (String) targetSelector.getSelectedItem();
        TargetFunction target = gui.getRegisteredTargets().get(targetName);
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

    public void setDynamicFields() {
        dynamicFieldsOnDimension(startingValuePanel);
        dynamicFieldsOnDimension(searchSpacePanel);
        dynamicFieldsOnParameters();
    }


}
