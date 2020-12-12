package model;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** model.OutputProcessor handles the output produced by the Genetic Algorithm. */
public class OutputProcessor {

    Chromosome[][] generations;

    public OutputProcessor(Chromosome[][] generations) {
        this.generations = generations;
    }

    /** Get fitness values for each generation. */
    public List<Double> fitnessValues() {

        List<Double> fitnessValues = new ArrayList<>();

        for (Chromosome[] generation : generations) {
            List<Chromosome> gen = Arrays.asList(generation.clone());
            Collections.sort(gen);
            double[] genes = gen.get(0).getGenes();
            double fitness = gen.get(0).getFitness();
            fitnessValues.add(fitness);
        }
        return fitnessValues;
    }

    public void saveFitnessToJson() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save");

        int userSelection = fileChooser.showSaveDialog(fileChooser);

        // If user has entered a name and clicked OK, write the JSON to a file
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();

            Gson gson = new Gson();
            try {
                List<Double> fitnessValues = fitnessValues();
                FileWriter writer = new FileWriter(saveFile.toString());
                gson.toJson(fitnessValues, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.out.println("Could not write to the specified file path.");
            }
        }
    }

    public void printGenerations() {

        for (Chromosome[] generation : generations) {
            List<Chromosome> gen = Arrays.asList(generation.clone());
            Collections.sort(gen);
            double[] genes = gen.get(0).getGenes();
            double fitness = gen.get(0).getFitness();

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
