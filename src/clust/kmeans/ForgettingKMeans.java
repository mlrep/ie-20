package clust.kmeans;

import clust.Cluster;
import clust.ClusterUpdates;
import clust.Clusterer;
import clust.gauss.SphericalGaussCluster;
import com.yahoo.labs.samoa.instances.Instance;
import javafx.util.Pair;
import output.writer.ClustersWriter;
import output.writer.OutputWriter;
import utils.Trackable;
import utils.math.MathUtils;

import java.util.*;

public class ForgettingKMeans implements Clusterer, Trackable {

    private HashMap<Integer, Cluster> clusters = new HashMap<>();
    private int k;
    private int removalThreshold;
    private Cluster clusterTemplate;

    private int idx;
    private boolean init = false;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public ForgettingKMeans(int k, Cluster clusterTemplate) {
        this.k = k;
        this.removalThreshold = -1;
        this.idx = 0;
        this.clusterTemplate = clusterTemplate.copy();
    }

    public ForgettingKMeans(int k, int removalThreshold, Cluster clusterTemplate) {
        this.k = k;
        this.removalThreshold = removalThreshold;
        this.idx = 0;
        this.clusterTemplate = clusterTemplate.copy();
    }

    @Override
    public void reset() {
        this.clusters.clear();
        this.idx = 0;
        this.init = false;
    }

    @Override
    public ClusterUpdates update(Instance instance, boolean label, int t) {
        ClusterUpdates updates = new ClusterUpdates();

        if (this.clusters.size() < this.k) {
            if (this.init) {
                Pair<Integer, Boolean> idxWithin = this.findClosestCluster(instance, t);
                if (idxWithin.getValue()) {
                    int closestIdx = idxWithin.getKey();
                    this.clusters.get(closestIdx).update(instance, label, t);
                    updates.addedTo.add(closestIdx);
                    return updates;
                }
            }

            this.createNewCluster(this.idx, instance, label, t);
            updates.created.add(this.idx++);

            if (!this.init && this.clusters.size() == k) {
                this.init = true;
            }
        } else {
            Pair<Integer, Boolean> idxWithin = this.findClosestCluster(instance, t);
            int closestIdx = idxWithin.getKey();
            this.clusters.get(closestIdx).update(instance, label, t);
            updates.addedTo.add(closestIdx);
        }

        ClusterUpdates removals = this.removeOutdated(t);
        updates.removed.addAll(removals.removed);

        return updates;
    }

    private void createNewCluster(int idx, Instance instance, boolean label, int t) {
        Cluster newCluster = this.clusterTemplate.copy();
        newCluster.update(instance, label, t);
        this.clusters.put(idx, newCluster);
    }

    private Pair<Integer, Boolean> findClosestCluster(Instance instance, int t) {
        double closestDist = Double.MAX_VALUE;
        int closestIdx = -1;
        boolean within = false;

        for (Integer idx : this.clusters.keySet()) {
            Cluster cluster = this.clusters.get(idx);
            Pair<Boolean, Double> withinDist = cluster.withinCluster(instance, t);
            double d = withinDist.getValue();

            if (d < closestDist) {
                closestDist = d;
                closestIdx = idx;
                within = withinDist.getKey();
            }
        }

        return new Pair<>(closestIdx, within);
    }

    private ClusterUpdates removeOutdated(int t) {
        ClusterUpdates updates = new ClusterUpdates();
        ArrayList<Integer> toRemove = new ArrayList<>();

        for (Integer idx : this.clusters.keySet()) {
            Cluster otherCluster = this.clusters.get(idx);

            if (!otherCluster.isActive(t, this.removalThreshold)) {
                toRemove.add(idx);
            }
        }

        this.clusters.keySet().removeAll(toRemove);
        updates.removed = toRemove;

        return updates;
    }

    @Override
    public HashMap<Integer, Cluster> getClusters() {
        return this.clusters;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        HashMap<String, Double> parameters = new HashMap<>();

        if (this.clusterTemplate instanceof SphericalGaussCluster) {
            double purityMean = 0.0;
            double purityVar = 0.0;
            int n = 0;

            for (Cluster cluster : this.clusters.values()) {
                double purity = ((SphericalGaussCluster)cluster).getPurityMeasures().get("purity");
                purityVar = MathUtils.incVar(purityVar, purityMean, purity,n + 1);
                purityMean = MathUtils.incMean(purityMean, purity, n + 1);
                n++;
            }

            parameters.put("avgPurity", purityMean);
            parameters.put("stdPurity", Math.sqrt(purityVar));
        } else {
            parameters.put("avgPurity", Double.NaN);
            parameters.put("stdPurity", Double.NaN);
        }

        parameters.put("clustersNum", (double)this.clusters.size());

        this.trackableParameters = parameters;
        return this.trackableParameters;
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>(Arrays.asList("avgPurity", "stdPurity", "clustersNum"));
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
    }

    @Override
    public ArrayList<String> getOtherTrackableNames() {
        return new ArrayList<>(Collections.singletonList("clusters"));
    }

    @Override
    public HashMap<String, OutputWriter> getOtherTrackable() {
        HashMap<String, OutputWriter> other = new HashMap<>();
        List<Pair<Instance, Double>> outClusters = new LinkedList<>();

        for (Cluster c: this.clusters.values()) {
            Instance clusterInstance = c.getCentroid();
            outClusters.add(new Pair<>(clusterInstance.copy(), c.getRadius().get(0)));
        }

        other.put("clusters", new ClustersWriter(outClusters));

        return other;
    }

}
