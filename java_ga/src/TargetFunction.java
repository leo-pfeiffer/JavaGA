/** A collection of target functions the GA can be applied to. */
public abstract class TargetFunction {

    private int dimension;
    private double value = 0.0d;

    public TargetFunction(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return this.dimension;
    }

    public double evaluate(Chromosome chromosome) {
        return this.value;
    }


}
