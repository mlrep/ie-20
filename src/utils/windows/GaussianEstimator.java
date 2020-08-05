package utils.windows;

import utils.ForgettingStrategy;

public class GaussianEstimator extends ForgettingEstimator {

    public static final double NORMAL_CONSTANT = Math.sqrt(2 * Math.PI);

    public GaussianEstimator(ForgettingStrategy forgettingStrategy, double decay) {
        super(forgettingStrategy, decay);
    }

    public double getDensity(double v) {
        if (this.count == 0.0) return 0.0;

        double std = this.getStd();
        if (std > 0.0) {
            double diff = v - this.getMean();
            return (1.0 / (NORMAL_CONSTANT * std)) * Math.exp(-0.5 * Math.pow((diff / std), 2.0));
        } else {
            return this.getMean() == v ? 1.0 : 0.0000000001;
        }
    }

}
