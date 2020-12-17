package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Solutions.Chromosome;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// todo make this use the abstract classes instead
/** model.OutputProcessor handles the output produced by the Genetic Algorithm. */
public class OutputProcessor {

    Chromosome[][] generations;

    public OutputProcessor(Chromosome[][] generations) {
        this.generations = generations;
    }

    /** Get fitness values for each generation. */
    public List<Double> getFitnessValues() {

        List<Double> fitnessValues = new ArrayList<>();

        for (Chromosome[] generation : generations) {
            List<Chromosome> gen = Arrays.asList(generation.clone());
            Collections.sort(gen);
            double[] genes = gen.get(0).getSolutions();
            double fitness = gen.get(0).getTargetValue();
            fitnessValues.add(fitness);
        }
        return fitnessValues;
    }

    public Chromosome getSolutionChromosome() {
        Chromosome[] lastGen = this.generations[this.generations.length-1].clone();
        List<Chromosome> gen = Arrays.asList(lastGen);
        Collections.sort(gen);
        return gen.get(0);
    }

    public double[] getSolution() {
        Chromosome solutionChrom = getSolutionChromosome();
        return solutionChrom.getSolutions();
    }

    public double getTargetValue() {
        Chromosome solutionChrom = getSolutionChromosome();
        return solutionChrom.getTargetValue();
    }

    public String filePathFromSaveDialog() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save");

        int userSelection = fileChooser.showSaveDialog(fileChooser);

        // If user has entered a name and clicked OK, write the JSON to a file
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            return saveFile.toString();
        } else {
            return "";
        }
    }

    public void saveGenerationsToFile(String saveFile) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            FileWriter writer = new FileWriter(saveFile);
            gson.toJson(generations, writer);
            writer.flush();
            writer.close();
            System.out.println("Saved file: " + saveFile);
        } catch (IOException e) {
            System.out.println("Could not write to the specified file path.");
        }
    }

    public void pickSaveMethodAndSave() {
        System.out.print("Save file via dialog (1) or enter path manually (2)? ");
        Scanner s = new Scanner(System.in);

        switch (s.nextInt()) {
            case 1 -> {
                String filepath = filePathFromSaveDialog();
                saveGenerationsToFile(filepath);
            }
            case 2 -> {
                System.out.print("Enter file path and name: ");
                String filepath = s.next();
                saveGenerationsToFile(filepath);
            }
            default -> {
                System.out.println("Invalid input.");
                System.exit(1);
            }
        }
    }

    public void printGenerations() {

        for (Chromosome[] generation : generations) {
            List<Chromosome> gen = Arrays.asList(generation.clone());
            Collections.sort(gen);
            double[] genes = gen.get(0).getSolutions();
            double fitness = gen.get(0).getTargetValue();

            System.out.print("(");
            for (int i = 0; i < genes.length; i++) {
                System.out.print(genes[0]);
                if (i != genes.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print(") -> ");
            System.out.print(fitness + "\n");
        }
    }
}
