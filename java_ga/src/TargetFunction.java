/** A collection of target functions the GA can be applied to. */
public abstract class TargetFunction {

    private int dimension;

    public TargetFunction(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return this.dimension;
    }

    public abstract double evaluate(Chromosome chromosome);


}
