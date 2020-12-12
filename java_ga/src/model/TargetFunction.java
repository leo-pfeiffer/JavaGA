package model;

/** A collection of target functions the GA can be applied to. */
public abstract class TargetFunction {

    private int dimension;
    private String name;

    public TargetFunction(int dimension, String name) {
        this.dimension = dimension;
        this.name = name;
    }

    public int getDimension() {
        return this.dimension;
    }

    public String getName() {return this.name;}

    public abstract double evaluate(Chromosome chromosome);


}
