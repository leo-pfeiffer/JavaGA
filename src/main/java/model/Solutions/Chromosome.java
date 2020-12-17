package model.Solutions;

import model.Targets.TargetFunction;

/** Implements a single chromosome for the GA. */
public class Chromosome extends Solution {

    public Chromosome(TargetFunction target, double[] solution) {
        this(target, solution, null);
    }

    public Chromosome(TargetFunction target, double[] solution, double[] searchSpace) {
        this.target = target;
        if (searchSpace == null) {
            this.solutions = solution;
        } else {
            this.solutions = this.makeSolutions(solution, searchSpace);
        }
        this.evaluated = false;
    }

    /** {@inheritDoc} */
    @Override
    public double[] makeSolutions(double[] solutions, double[] searchSpace) {
        double[] newSolutions = new double[solutions.length];
        for (int i = 0; i < solutions.length; i++) {
            double s = Math.random() > 0.5 ? 1 : -1;
            newSolutions[i] = solutions[i] + s * Math.random() * searchSpace[i];
        }
        return newSolutions;
    }

    /** {@inheritDoc} */
    @Override
    public double getSolution(int index) throws IndexOutOfBoundsException {
        return this.solutions[index];
    }

    /** {@inheritDoc} */
    @Override
    public double[] getSolutions() {
        return this.solutions;
    }

    /** {@inheritDoc} */
    @Override
    public void evaluateTargetValue() {
        this.targetValue = target.evaluate(this);
        this.evaluated = true;
    }

    /** {@inheritDoc} */
    @Override
    public double getTargetValue() {
        if (!evaluated) {
            this.evaluateTargetValue();
        }
        return this.targetValue;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Solution chrom) {
        return Double.compare(this.getTargetValue(), chrom.getTargetValue());
    }
}
