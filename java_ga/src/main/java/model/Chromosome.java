package model;

import com.google.gson.annotations.Expose;

/** Implements a single chromosome for the GA. */
public class Chromosome implements Comparable<Chromosome> {

    /** Genes of the chromosome */
    @Expose
    private double[] genes;

    /** Fitness value. */
    @Expose
    private double fitness;

    private boolean evaluated;

    /** Target function to be optimised */
    TargetFunction target;

    public Chromosome(TargetFunction target, double[] genes) {
        this(target, genes, null);
    }

    public Chromosome(TargetFunction target, double[] genes, double[] searchSpace) {
        this.target = target;
        if (searchSpace == null) {
            this.genes = genes;
        } else {
            this.genes = this.makeGenes(genes, searchSpace);
        }
        this.evaluated = false;
    }

    public double[] makeGenes(double[] genes, double[] searchSpace) {
        double[] newGenes = new double[genes.length];
        for (int i = 0; i < genes.length; i++) {
            double s = Math.random() > 0.5 ? 1 : -1;
            newGenes[i] = genes[i] + s * Math.random() * searchSpace[i];
        }
        return newGenes;
    }

    public double getGene(int index) throws IndexOutOfBoundsException {
        return this.genes[index];
    }

    public double[] getGenes() {
        return this.genes;
    }

    public void evaluateFitness() {
        this.fitness = target.evaluate(this);
        this.evaluated = true;
    }

    public double getFitness() {
        if (!evaluated) {
            this.evaluateFitness();
        }
        return this.fitness;
    }

    @Override
    public int compareTo(Chromosome chrom) {
        return Double.compare(this.getFitness(), chrom.getFitness());
    }
}
