package model.Targets;

import model.Solutions.Chromosome;

public class Multimodal extends TargetFunction {

    public Multimodal() {
        super(2, "multimodal");
    }

    @Override
    public double evaluate (Chromosome chromosome) {
        double x = chromosome.getSolution(0);
        double y = chromosome.getSolution(1);

        double modes = Math.pow(x, 4) - 5 * Math.pow(x, 2) + Math.pow(y, 4) - 5 * Math.pow(y, 2);
        double tilt = 0.5 * x * y + 0.3 * x + 15;
        double stretch = 0.1;

        return stretch * (modes + tilt);
    }

    @Override
    public String toString() {
        return "<html>f(x) = 0.1 * <br>" +
                "((x<sup>4</sup> - 5 * x<sup>2</sup> + y<sup>4</sup> - 5 * y<sup>2</sup>) +<br>" +
                "(0.5 * x * y + 0.3 * x + 15))</html>";
    }

}
