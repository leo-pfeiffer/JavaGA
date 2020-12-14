package model.Target;

import model.Chromosome;

public class Quadratic extends TargetFunction {

    public Quadratic() {
        super(1, "quadratic");
    }

    @Override
    public double evaluate (Chromosome chromosome) {
        double x = chromosome.getGene(0);

        return (0.5d * (Math.pow(x, 2)) + 2 * x);
    }

}
