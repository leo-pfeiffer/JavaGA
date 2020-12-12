import cliDelegate.CliGA;
import guiDelegate.GuiGA;
import model.Multimodal;
import model.TargetFunction;

import java.util.HashMap;

/** Run the algorithm. */
public class AlgorithmMain {
    public static void main(String[] args) {

        HashMap<String, TargetFunction> registeredTargets = registerTargets();

        String delegate = "";

        try {
            // Set either gui or cli
            for (String arg : args) {
                if (arg.equals("--gui")) {
                    // process via gui view
                    if (delegate.equals("")) {
                        delegate = "gui";
                    } else {
                        throw new IllegalArgumentException("Can't specify both --gui and --cli. Pick one.");
                    }
                } else if (arg.equals("--cli")) {
                    // process via cli view
                    if (delegate.equals("")) {
                        delegate = "cli";
                    } else {
                        throw new IllegalArgumentException("Can't specify both --gui and --cli. Pick one.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (delegate.equals("")) {
            delegate = "gui";
        }

        if (delegate.equals("gui")) {
            GuiGA guiGA = new GuiGA(registeredTargets);
        }

        else {
            CliGA cliGA = new CliGA(registeredTargets);
            cliGA.run();
        }
    }

    public static HashMap<String, TargetFunction> registerTargets(){
        HashMap<String, TargetFunction> registeredTargets = new HashMap<>();

        Multimodal multimodal = new Multimodal();
        registeredTargets.put(multimodal.getName(), multimodal);

        return registeredTargets;
    }
}