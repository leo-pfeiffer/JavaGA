package cliDelegate;

import model.Chromosome;
import model.GeneticAlgorithm;
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
        System.out.println("Running GA... ----\n");
        GeneticAlgorithm ga = new GeneticAlgorithm(target, cr, mr, mx, popSize, maxGen, startingValues, searchSpace);
        Chromosome[][] generations = ga.getGenerations();
        System.out.println("------------------\n");

        // Ask user for output processing instructions
        OutputProcessor op = new OutputProcessor(generations);

        switch (outputProcessingPrompt()) {
            case 1 -> op.printGenerations();
            case 2 -> op.fitnessToJson();
            case 3 -> System.exit(0);
            default -> {
                System.err.println("Invalid input. Selected 1.");
                op.printGenerations();
            }
        }

        System.out.println("Good bye");
        System.exit(0);
    }

    private void setup() {

        try {
            // Target function
            System.out.print("State your target function: ");
            Scanner s = new Scanner(System.in);

            String targetName = s.next();

            try {
                target = registeredTargets.get(targetName);
            } catch(Exception e) {
                throw new IllegalArgumentException("Target function not found.");
            }

            // Starting values
            int dimensions = target.getDimension();
            System.out.print("State your starting values (" + dimensions + " dimensions, comma separated): ");

            List<String> startingValuesRaw = Arrays.asList(s.next().split(","));

            if (startingValuesRaw.toArray().length != dimensions) {
                throw new IllegalArgumentException("Incorrect number of dimensions.\n" +
                        "Expected " + dimensions + " but got " + startingValuesRaw.toArray().length);
            }

            startingValues = new double[dimensions];
            for (int j = 0; j < startingValues.length; j++) {
                startingValues[j] = Double.parseDouble(startingValuesRaw.get(j));
            }

            // Search space
            System.out.print("State your search space (" + dimensions + " dimensions, comma separated): ");
            List<String> searchSpaceRaw = Arrays.asList(s.next().split(","));
            if (searchSpaceRaw.toArray().length != dimensions) {
                throw new IllegalArgumentException("Incorrect number of dimensions.\n" +
                        "Expected " + dimensions + " but got " + searchSpaceRaw.toArray().length);
            }

            searchSpace = new double[dimensions];
            for (int j = 0; j < searchSpace.length; j++) {
                searchSpace[j] = Double.parseDouble(searchSpaceRaw.get(j));
            }

            // mutation rate
            System.out.print("Mutation rate mr: ");
            mr = Double.parseDouble(s.next());
            if (mr <= 0 || mr >= 1) {
                throw new IllegalArgumentException("mr must be between 0 and 1.");
            }

            // crossover rate
            System.out.print("Crossover rate cr: ");
            cr = Double.parseDouble(s.next());
            if (cr <= 0 || cr >= 1) {
                throw new IllegalArgumentException("cr must be between 0 and 1.");
            }


            // mutation parameter
            System.out.print("Mutation parameter mx: ");
            mx = Double.parseDouble(s.next());
            if (mx <= 0 || mx >= 1) {
                throw new IllegalArgumentException("mx must be between 0 and 1.");
            }

            // population size
            System.out.print("Population size popSize: ");
            popSize = Integer.parseInt(s.next());
            if (popSize < 1) {
                throw new IllegalArgumentException("popSize must be greater or equal than 1.");
            }

            // maximum generation
            System.out.print("Maximum generation maxGen: ");
            maxGen = Integer.parseInt(s.next());
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
                "1: Print each generations parameters and fitness.\n" +
                "2: Export to JSON.\n" +
                "3: Quit.\n";

        System.out.println(optionText);
        Scanner s = new Scanner(System.in);

        return s.nextInt();
    }

}
