import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Run the algorithm. */
public class AlgorithmMain {
    public static void main(String[] args) {
        Multimodal target = new Multimodal();
        double cr = 0.7;
        double mr = 0.8;
        double mx = 0.2;
        int popSize = 20;
        int maxGen = 1000;
        double[] startingValues = {0d, 0d};

        // Absolute allowed deviation from starting values in first generations
        double[] searchSpace = {3d, 3d};

        GeneticAlgorithm ga = new GeneticAlgorithm(target, cr, mr, mx, popSize, maxGen, startingValues, searchSpace);

        Chromosome[][] generations = ga.getGenerations();

        for (int i = 0; i < generations.length; i++) {
            List<Chromosome> gen = Arrays.asList(generations[i].clone());
            Collections.sort(gen);
            double[] genes = gen.get(0).getGenes();
            double fitness = gen.get(0).getFitness();
            System.out.println("(" + genes[0] + ", " + genes[1] + ") -> " + fitness);
        }
    }
}