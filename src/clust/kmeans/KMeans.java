package clust.kmeans;

import clust.Cluster;
import clust.ClusterUpdates;
import clust.Clusterer;
import com.yahoo.labs.samoa.instances.Instance;
import javafx.util.Pair;
import utils.math.MathUtils;
import utils.windows.WindowedInstances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class KMeans implements Clusterer {

    private ArrayList<Cluster> clusters = new ArrayList<>();
    protected Random random = new Random();

    protected int k;
    private int n = 0;
    private ArrayList<Double> distances = new ArrayList<>();
    boolean clustersInitialized = false;

    protected double f = 0.0;
    private int q = 0;

    public KMeans(int k) {
        this.k = (int)Math.ceil((k - 15) / 5.0);
    }

    @Override
    public ClusterUpdates update(Instance instance, boolean label, int t) {
        if (!this.clustersInitialized) {
            this.initializingClusters(instance);
            return new ClusterUpdates(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(Collections.singletonList(1)), new ArrayList<>());
        }

        return this.updateCentroids(instance, t);
    }

    private void initializingClusters(Instance instance) {
        for (Cluster cluster : this.clusters) {
            this.distances.add(MathUtils.euclideanDist(instance, cluster.getCentroid()));
        }

        this.clusters.add(new WindowedCentroidCluster(new WindowedInstances(WindowedInstances.WINDOW_ESTIMATOR_WIDTH, instance, true)
                .setKeepStatistics(true))); // todo: false? compare diversity

        if (++this.n >= this.k + 10) {
            this.clustersInitialized = true;
            this.initializeCost();
        }
    }

    private void initializeCost() {
        Collections.sort(this.distances);
        for (int i = 0; i < 10; i++) {
            double dist = this.distances.get(i);
            this.f += dist*dist;
        }

        this.f /= 2.0;
    }

    private ClusterUpdates updateCentroids(Instance instance, int t) {
        ArrayList<Integer> removed = new ArrayList<>();
        int added = this.clusters.size();

        Pair<Integer, Double> minIndexDist = null; // todo: InstanceUtils.findInstance(instance, this.getCentroids(), true);
        int minIndex = minIndexDist.getKey();
        double minDist = minIndexDist.getValue();
        double p = Math.min(minDist*minDist / this.f, 1.0);

        if (p >= this.random.nextDouble()) {
            this.addNewCluster(instance);
            added++;
        } else {
            this.clusters.get(minIndex).update(instance, false, t);
        }

        // todo: removing instances for the rest, so we can also remove old clusters?
        return new ClusterUpdates(removed, new ArrayList<>(), new ArrayList<>(Collections.singletonList(added)), new ArrayList<>());
    }

    private void addNewCluster(Instance instance) {
        this.clusters.add(new WindowedCentroidCluster(new WindowedInstances(WindowedInstances.WINDOW_ESTIMATOR_WIDTH, instance, true)
                .setKeepStatistics(true)));
        this.q++;

        if (q >= this.k) {
            this.q = 0;
            this.f *= 10; // todo: after removing (window) cluster: f *= 0.1, so adding new is more likely
        }
    }

    @Override
    public HashMap<Integer, Cluster> getClusters() {
        if (this.clusters.size() == 0) return null;
        HashMap<Integer, Cluster> clusters = new HashMap<>();

        for (int i = 0; i < this.clusters.size(); i++) {
            clusters.put(i, this.clusters.get(i));
        }

        return clusters;
    }

    @Override
    public void reset() {
        this.clusters.clear();

        this.n = 0;
        this.distances.clear();
        this.clustersInitialized = false;

        this.f = 0;
        this.q = 0;
    }

}
