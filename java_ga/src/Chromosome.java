/** Implements a single chromosome for the GA. */
public class Chromosome {

    private double[] genes;

    public Chromosome() {

    }

    public double getGene(int index) throws IndexOutOfBoundsException {
        return this.genes[index];
    }

    public double[] getGenes() {
        return this.genes;
    }
}
