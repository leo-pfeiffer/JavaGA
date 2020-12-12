package guiDelegate;

import model.TargetFunction;

import java.util.HashMap;

public class GuiGA {

    HashMap<String, TargetFunction> registeredTargets;

    public GuiGA(HashMap<String, TargetFunction> registeredTargets) {
        this.registeredTargets = registeredTargets;
    }

    public void run() {
        System.out.println("Running gui...");
    }
}
