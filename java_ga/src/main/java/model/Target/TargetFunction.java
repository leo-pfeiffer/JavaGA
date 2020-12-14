package model.Target;

import model.Chromosome;

/** A collection of target functions the GA can be applied to. */
public abstract class TargetFunction {

    private int dimension;
    private String name;
    private double[] parameters;
    private boolean hasParameters = false;

    public TargetFunction(int dimension, String name) {
        this.dimension = dimension;
        this.name = name;
    }

    public boolean isHasParameters() {
        return this.hasParameters;
    }

    public void setHasParameters(boolean hasParameters) {
        this.hasParameters = hasParameters;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setParameters(double[] parameters) throws IllegalArgumentException {
        if (!hasParameters){
            throw new IllegalArgumentException("The function " + name + " cannot have custom parameters");
        }
        this.parameters = new double[parameters.length];
        System.arraycopy(parameters, 0, this.parameters, 0, parameters.length);
    }

    public double[] getParameters() {
        if (!hasParameters){
            throw new IllegalArgumentException("The function " + name + " cannot have custom parameters");
        }
        return this.parameters;
    }

    public String getName() {return this.name;}

    public abstract double evaluate(Chromosome chromosome);


}
