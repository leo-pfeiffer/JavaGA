package model.Targets;

import model.Solutions.Chromosome;

public class Quadratic extends TargetFunction {

    public Quadratic() {
        super(1, "quadratic");
    }

    @Override
    public double evaluate (Chromosome chromosome) {
        double x = chromosome.getSolution(0);

        return (0.5d * (Math.pow(x, 2)) + 2 * x);
    }

    @Override
    public String toString() {
        return "f(x) = quadratic";
    }
}
