package clust.gauss;

import clust.Cluster;
import com.yahoo.labs.samoa.instances.Instance;
import javafx.util.Pair;
import utils.math.DistanceType;
import utils.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class NaiveGaussCluster extends SphericalGaussCluster {

    private ArrayList<Double> var = new ArrayList<>(); // assumption: uncorrelated features, naive

    NaiveGaussCluster(Instance instance, boolean label, int t) {
        super(instance, label, t);
    }

    @Override
    protected double updateStats(int idx) {
        this.var.set(idx, super.updateStats(idx));
        return this.var.get(idx);
    }

    @Override
    public Pair<Boolean, Double> withinCluster(Instance instance, int t) {
        // todo: based on the var vector (uncorrelated features)
        double dist = MathUtils.euclideanDist(instance, this.centroid) / Math.pow(this.decay, t - this.timestamp);
        return new Pair<>(false, dist);
    }

    @Override
    public boolean significantOverlapping(Cluster otherCluster) {
        // todo: based on the var vector (uncorrelated features)
        return false;
    }

}
