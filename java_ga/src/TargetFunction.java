/** A collection of target functions the GA can be applied to. */
public class TargetFunction {

    public static double multimodal (Chromosome chromosome) {
        // todo find way to make sure that chromosome has correct num of arguments
        double x = chromosome.getGene(0);
        double y = chromosome.getGene(1);

        double modes = Math.pow(x, 4) - 5 * Math.pow(x, 2) + Math.pow(y, 4) - 5 * Math.pow(y, 2);
        double tilt = 0.5 * x * y + 0.3 * x + 15;
        double stretch = 0.1;

        return stretch * (modes + tilt);
    }
}
