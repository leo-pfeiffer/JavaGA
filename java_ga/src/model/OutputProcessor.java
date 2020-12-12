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
import java.util.Scanner;

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

    public void saveFitnessToFile(String saveFile) {
        Gson gson = new Gson();
        try {
            List<Double> fitnessValues = fitnessValues();
            FileWriter writer = new FileWriter(saveFile);
            gson.toJson(fitnessValues, writer);
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
                saveFitnessToFile(filepath);
            }
            case 2 -> {
                System.out.print("Enter file path and name: ");
                String filepath = s.next();
                saveFitnessToFile(filepath);
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
