package model.Target;

import model.Chromosome;

public class Multimodal extends TargetFunction {

    public Multimodal() {
        super(2, "multimodal");
    }

    @Override
    public double evaluate (Chromosome chromosome) {
        double x = chromosome.getGene(0);
        double y = chromosome.getGene(1);

        double modes = Math.pow(x, 4) - 5 * Math.pow(x, 2) + Math.pow(y, 4) - 5 * Math.pow(y, 2);
        double tilt = 0.5 * x * y + 0.3 * x + 15;
        double stretch = 0.1;

        return stretch * (modes + tilt);
    }

}
