package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.Target.TargetFunction;
import org.apache.commons.lang3.ArrayUtils;


/** Implements the genetic algorithm with all associated functions. */
public class GeneticAlgorithm {

    /** Target function to be optimised */
    TargetFunction target;

    /** Crossover rate. */
    double cr;

    /** Mutation rate. */
    double mr;

    /** Mutation parameter. */
    double mx;

    /** Population size. */
    int popSize;

    /** Maximum generation number. */
    int maxGen;

    /** Starting values. */
    double[] startingValues;

    /** Search space. */
    double[] searchSpace;

    /** Nested ArrayList of the populations of each generation. */
    Chromosome[][] generations;

    /** Intermediate population. */
    Chromosome[] intermediatePop;

    /* The property change support object to use when notifying listeners of the model. */
    private PropertyChangeSupport notifier;

    public GeneticAlgorithm() {
        notifier = new PropertyChangeSupport(this);
    }


    public void setAttributes(TargetFunction target, double cr, double mr, double mx, int popSize,
                              int maxGen, double [] startingValues, double[] searchSpace) {
        this.target = target;
        this.cr = cr;
        this.mr = mr;
        this.mx = mx;
        this.popSize = popSize;
        this.startingValues = startingValues;
        this.maxGen = maxGen;
        this.searchSpace = searchSpace;
    }

    public void setTarget(TargetFunction target) {
        this.target = target;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }

    public void setMr(double mr) {
        this.mr = mr;
    }

    public void setMx(double mx) {
        this.mx = mx;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public void setStartingValues(double[] startingValues){
        this.startingValues = startingValues;
    }

    public void setSearchSpace (double[] searchSpace){
        this.searchSpace = searchSpace;
    }

    public void setMaxGen(int maxGen) {
        this.maxGen = maxGen;
    }

    public Chromosome[][] getGenerations() {
        return this.generations;
    }

    public Chromosome[] getLastGeneration() {
        return this.generations[this.generations.length - 1];
    }

    public double getFitness() {
        return getFittestChromosome().getFitness();
    }

    public Chromosome getFittestChromosome() {
        List<Chromosome> lastGen = Arrays.asList(this.getLastGeneration().clone());
        Collections.sort(lastGen);
        return lastGen.get(0);
    }

    public void runAlgorithm() {

        // initialise the generations and intermediate population
        generations = new Chromosome[maxGen][popSize];
        this.intermediatePop = new Chromosome[this.popSize];

        // set the parent population
        for (int i = 0; i < popSize; i++) {
            Chromosome chromosome = new Chromosome(this.target, this.startingValues, this.searchSpace);
            chromosome.evaluateFitness();
            generations[0][i] = chromosome;
        }

        for (int gen = 0; gen < maxGen; gen++) {
            System.out.print("" + gen + " / " + maxGen + "\r");
            this.crossover(gen);
            this.mutation();
            this.survival(gen);
        }
        System.out.print("" + maxGen + " / " + maxGen + "\n");
        notifier.firePropertyChange("run_complete", null, this.generations);
    }

    private void crossover(int gen) {

        Chromosome[] pool = generations[gen];

        // 1d functions cannot crossover
        if (this.target.getDimension() == 1) {
            System.arraycopy(pool, 0, this.intermediatePop, 0, pool.length);
        } else {
            // Shuffle chromosomes in pool
            Random rand = new Random();
            for (int i = 0; i < pool.length; i++) {
                int randomIndexToSwap = rand.nextInt(pool.length);
                Chromosome chrom = pool[randomIndexToSwap];
                pool[randomIndexToSwap] = pool[i];
                pool[i] = chrom;
            }

            for (int i = 0; i < Math.ceil((double) popSize / 2); i++) {
                int cut = rand.nextInt(target.getDimension()-1) + 1;

                // create first crossover part
                double[] cross1Start = new double[cut];
                System.arraycopy(pool[i*2].getGenes(), 0, cross1Start, 0, cut);
                double[] cross1End = new double[target.getDimension()-cut];
                System.arraycopy(pool[i*2+1].getGenes(), cut, cross1End, 0, target.getDimension()-cut);
                double[] cross1 = ArrayUtils.addAll(cross1Start, cross1End);

                // create second crossover part
                double[] cross2Start = new double[cut];
                System.arraycopy(pool[i*2+1].getGenes(), 0, cross2Start, 0, cut);
                double[] cross2End = new double[target.getDimension()-cut];
                System.arraycopy(pool[i*2].getGenes(), cut, cross2End, 0, target.getDimension()-cut);
                double[] cross2 = ArrayUtils.addAll(cross2Start, cross2End);

                // Create new Chromosomes
                this.intermediatePop[i*2] = new Chromosome(this.target, cross1);
                this.intermediatePop[i*2+1] = new Chromosome(this.target, cross2);
            }
        }
    }

    private void mutation() {
        double[] genes;
        for (int i = 0; i < this.popSize; i++) {
            double[] newGenes = new double[target.getDimension()];
            Chromosome chromosome = this.intermediatePop[i];
            genes = chromosome.getGenes();

            for (int g = 0; g < newGenes.length; g++) {
                double u = Math.random();
                if (u <= this.mr) {
                    newGenes[g] = (genes[g] * (1 - this.mx) + genes[g] * 2 * this.mx  * u);
                } else {
                    newGenes[g] = genes[g];
                }
            }

            Chromosome newChromosome = new Chromosome(target, newGenes);
            newChromosome.evaluateFitness();
            this.intermediatePop[i] = newChromosome;
        }
    }

    /** Elitist selection.
     * @param gen Number of the current parent generation. */
    private void survival(int gen) {
        if (gen >= maxGen-1) {
            return;
        }
        List<Chromosome> pool = Arrays.asList(ArrayUtils.addAll(this.generations[gen], this.intermediatePop));
        Collections.sort(pool);

        for (int i = 0; i < this.popSize; i++) {
            this.generations[gen+1][i] = pool.get(i);
        }
    }

    /**
     * Utility method to add an observer using this object's private (encapsulated) property change support object.
     * @param listener the listener to add
     */
    public void addObserver(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }
}
