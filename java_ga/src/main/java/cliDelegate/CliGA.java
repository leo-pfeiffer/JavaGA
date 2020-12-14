package cliDelegate;

import model.Chromosome;
import model.GeneticAlgorithm;
import model.Multimodal;
import model.OutputProcessor;
import model.TargetFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CliGA {

    TargetFunction target = null;
    double cr;
    double mr;
    double mx;
    int popSize;
    int maxGen;
    double[] startingValues;
    double[] searchSpace;

    String helpText = "" +
            "[target] : a registered target function to be minimised\n" +
            "[cr] : crossover rate, must be between 0 and 1 (exclusive)\n" +
            "[mr] : mutation rate, must be between 0 and 1 (exclusive)\n" +
            "[mx] : mutation parameter, must be between 0 and 1 (exclusive)\n" +
            "[popSize] : population size of each generation, must be integer greater or equal to 1\n" +
            "[startingValues] : comma separated starting values for the algorithm, e.g. val1,val2,val3\n" +
            "[searchSpace] : comma separated search space for the algorithm, e.g. val1,val2,val3\n";

    HashMap<String, TargetFunction> registeredTargets;

    public CliGA(HashMap<String, TargetFunction> registeredTargets) {
        this.registeredTargets = registeredTargets;
    }

    public void run() {

        // Setup (user prompts)
        setup();

        // Run GA
        System.out.println("Running GA...\n");
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.setAttributes(target, cr, mr, mx, popSize, maxGen, startingValues, searchSpace);
        Chromosome[][] generations = ga.getGenerations();
        System.out.println("------------------\n");

        // Ask user for output processing instructions
        OutputProcessor op = new OutputProcessor(generations);

        switch (outputProcessingPrompt()) {
            case 1 -> op.printGenerations();
            case 2 -> op.pickSaveMethodAndSave();
            case 3 -> System.exit(0);
            default -> {
                System.out.println("Invalid input. Selected 1.");
                op.printGenerations();
            }
        }

        System.out.println("\nGood bye!");
        System.exit(0);
    }

    private void setup() {

        try {
            // Target function
            System.out.print("State your target function (default: multimodal): ");
            Scanner s = new Scanner(System.in);

            String targetName = s.nextLine();
            if (targetName.equals("")) {
                targetName = "multimodal";
                System.out.println("multimodal");
            }
            try {
                target = registeredTargets.get(targetName);
            } catch(Exception e) {
                throw new IllegalArgumentException("Target function not found.");
            }

            // Starting values
            int dimensions = target.getDimension();
            System.out.print("State your starting values (" + dimensions + " dimensions, comma separated. default: 0,0,...): ");

            String startingValuesIn = s.nextLine();

            if (startingValuesIn.equals("")) {
                startingValuesIn = "0,".repeat(dimensions);
                startingValuesIn = startingValuesIn.substring(0, startingValuesIn.length()-1);
                System.out.println(startingValuesIn);
            }
            List<String> startingValuesRaw = Arrays.asList(startingValuesIn.split(","));

            if (startingValuesRaw.toArray().length != dimensions) {
                throw new IllegalArgumentException("Incorrect number of dimensions.\n" +
                        "Expected " + dimensions + " but got " + startingValuesRaw.toArray().length);
            }

            startingValues = new double[dimensions];
            for (int j = 0; j < startingValues.length; j++) {
                startingValues[j] = Double.parseDouble(startingValuesRaw.get(j));
            }

            // Search space
            System.out.print("State your search space (" + dimensions + " dimensions, comma separated. default: 1,1,...): ");

            String searchSpaceIn = s.nextLine();

            if (searchSpaceIn.equals("")) {
                searchSpaceIn = "1,".repeat(dimensions);
                searchSpaceIn = searchSpaceIn.substring(0, searchSpaceIn.length()-1);
                System.out.println(searchSpaceIn);
            }

            List<String> searchSpaceRaw = Arrays.asList(searchSpaceIn.split(","));
            if (searchSpaceRaw.toArray().length != dimensions) {
                throw new IllegalArgumentException("Incorrect number of dimensions.\n" +
                        "Expected " + dimensions + " but got " + searchSpaceRaw.toArray().length);
            }

            searchSpace = new double[dimensions];
            for (int j = 0; j < searchSpace.length; j++) {
                searchSpace[j] = Double.parseDouble(searchSpaceRaw.get(j));
            }

            // mutation rate
            System.out.print("Mutation rate mr (default: 0.8): ");

            String mrIn = s.nextLine();
            if (mrIn.equals("")) {
                mrIn = "0.8";
                System.out.println(mrIn);
            }

            mr = Double.parseDouble(mrIn);
            if (mr <= 0 || mr >= 1) {
                throw new IllegalArgumentException("mr must be between 0 and 1.");
            }

            // crossover rate
            System.out.print("Crossover rate cr (default: 0.7): ");
            String crIn = s.nextLine();
            if (crIn.equals("")) {
                crIn = "0.7";
                System.out.println(crIn);
            }
            cr = Double.parseDouble(crIn);
            if (cr <= 0 || cr >= 1) {
                throw new IllegalArgumentException("cr must be between 0 and 1.");
            }

            // mutation parameter

            System.out.print("Mutation parameter mx (default: 0.2): ");
            String mxIn = s.nextLine();
            if (mxIn.equals("")) {
                mxIn = "0.2";
                System.out.println(mxIn);
            }
            mx = Double.parseDouble(mxIn);
            if (mx <= 0 || mx >= 1) {
                throw new IllegalArgumentException("mx must be between 0 and 1.");
            }

            // population size
            System.out.print("Population size popSize (default: 10): ");
            String popSizeIn = s.nextLine();
            if (popSizeIn.equals("")) {
                popSizeIn = "10";
                System.out.println(popSizeIn);
            }
            popSize = Integer.parseInt(popSizeIn);
            if (popSize < 1) {
                throw new IllegalArgumentException("popSize must be greater or equal than 1.");
            }

            // maximum generation
            System.out.print("Maximum generation maxGen (default: 100): ");
            String maxGenIn = s.nextLine();
            if (maxGenIn.equals("")) {
                maxGenIn = "100";
                System.out.println(maxGenIn);
            }
            maxGen = Integer.parseInt(maxGenIn);
            if (maxGen < 1) {
                throw new IllegalArgumentException("maxGen must be greater or equal than 1.");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Usage: " + helpText);
            System.exit(1);
        }
        System.out.println("------------------\n");
    }

    private int outputProcessingPrompt() {
        String optionText = "" +
                "How to process output? Enter the number of your selection.\n" +
                "1: Print each generations parameters and fitness. \n" +
                "2: Export to JSON.\n" +
                "3: Quit.";

        System.out.println(optionText);
        Scanner s = new Scanner(System.in);

        return s.nextInt();
    }

}
