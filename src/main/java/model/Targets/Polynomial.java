package model.Targets;

import model.Solutions.Chromosome;

import java.util.Arrays;

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

    @Override
    public String toString() {

        StringBuilder expr = new StringBuilder("<html>f(x) = <br>");
        double[] params = getParameters();

        for (int i = 0; i < params.length; i++) {

            if (params[i] < 0) {
                expr.append('(');
            }
            expr.append(params[i]);

            if (params[i] < 0) {
                expr.append(')');
            }

            expr.append(" * x<sup>");
            expr.append(params.length - i > 1 ? params.length - i : "");
            expr.append("</sup>");

            if (i < params.length - 1) {
                expr.append(" + ");
            }

            expr.append("<br>");
        }

        expr.append("</html>");

        return String.valueOf(expr);
    }


}
