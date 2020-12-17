package model;

import com.google.gson.annotations.Expose;
import model.Target.TargetFunction;

public abstract class Solution implements Comparable<Solution>{
    /** Genes of the chromosome */
    @Expose
    double[] solutions;

    /** Target value. */
    @Expose
    double targetValue;

    /** Whether the solution has already been evaluated. */
    boolean evaluated;

    /** Target function to be optimised */
    TargetFunction target;

    /** Make the initial solutions for iterative algorithms.
     * @param solutions An array with the initial solution.
     * @param searchSpace An array with the search space for the initial solution. */
    public abstract double[] makeSolutions(double[] solutions, double[] searchSpace);

    /** Get the i-th solution.
     * @return double with the i-th solution. */
    public abstract double getSolution(int index);

    /** Get all solutions.
     * @return Array with all solutions. */
    public abstract double[] getSolutions();

    /** Evaluate the target value of the solution by plugging it into the target function.*/
    public abstract void evaluateTargetValue();

    /** Get the target value of the function.
     * @return Target value */
    public abstract double getTargetValue();


    /** Implemented to allow comparisons.
     * {@inheritDoc} */
    @Override
    public abstract int compareTo(Solution chrom);

}
