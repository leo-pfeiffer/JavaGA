package model;

import com.google.gson.Gson;

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

    /** Get fitness values for each generation in json format as a String. */
    public String fitnessToJson() {

        List<Double> fitnessValues = new ArrayList<>();

        for (Chromosome[] generation : generations) {
            List<Chromosome> gen = Arrays.asList(generation.clone());
            Collections.sort(gen);
            double[] genes = gen.get(0).getGenes();
            double fitness = gen.get(0).getFitness();
            fitnessValues.add(fitness);
            // System.out.println("(" + genes[0] + ", " + genes[1] + ") -> " + fitness);
        }

        Gson gson = new Gson();
        return gson.toJson(fitnessValues);
    }

    public void printGenerations() {

    }
}
