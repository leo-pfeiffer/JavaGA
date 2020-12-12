import cliDelegate.CliGA;
import guiDelegate.GuiGA;
import model.Chromosome;
import model.GeneticAlgorithm;
import model.Multimodal;
import model.OutputProcessor;
import model.TargetFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

//        String helpText = "Usage: java AlgorithmMain  --target[target] --cr [cr] --mr [mr] --mx [mx] " +
//                "--popSize [popSize] --maxGen [maxGen] --startingValues [startingValues] --searchSpace[searchSpace]\n\n" +
//                "[target] : a registered target function to be minimised\n" +
//                "[cr] : crossover rate, must be between 0 and 1 (exclusive)\n" +
//                "[mr] : mutation rate, must be between 0 and 1 (exclusive)\n" +
//                "[mx] : mutation parameter, must be between 0 and 1 (exclusive)\n" +
//                "[popSize] : population size of each generation, must be integer greater or equal to 1\n" +
//                "[startingValues] : comma separated starting values for the algorithm, e.g. val1,val2,val3\n" +
//                "[searchSpace] : comma separated search space for the algorithm, e.g. val1,val2,val3\n";


//
//        double cr = 0.7;
//        double mr = 0.8;
//        double mx = 0.2;
//        int popSize = 20;
//        int maxGen = 1000;
//        double[] startingValues = {0d, 0d};
//
//        // Absolute allowed deviation from starting values in first generations
//        double[] searchSpace = {3d, 3d};
//
//        GeneticAlgorithm ga = new GeneticAlgorithm(target, cr, mr, mx, popSize, maxGen, startingValues, searchSpace);
//
//        Chromosome[][] generations = ga.getGenerations();
//
//        // Process the output...
//        OutputProcessor op = new OutputProcessor(generations);
    }

    public static HashMap<String, TargetFunction> registerTargets(){
        HashMap<String, TargetFunction> registeredTargets = new HashMap<String, TargetFunction>();

        Multimodal multimodal = new Multimodal();
        registeredTargets.put(multimodal.getName(), multimodal);

        return registeredTargets;
    }
}