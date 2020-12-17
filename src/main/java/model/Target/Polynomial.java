package model.Target;

import model.Chromosome;

public class Polynomial extends TargetFunction {

    public Polynomial() {
        super(1, "polynomial");
        this.setHasParameters(true);
        this.setParameters(new double[]{0.5d, 2d, 0});
    }

    @Override
    public double evaluate (Chromosome chromosome) {
        double y = 0d;
        double x = chromosome.getSolution(0);

        double[] params = getParameters();

        // add variable terms
        for (int i = 0; i < params.length - 1; i++) {
            y += params[i] * Math.pow(x, params.length - (1 + i));
        }

        // add constant
        y += params[params.length-1];

        return y;
    }
}
