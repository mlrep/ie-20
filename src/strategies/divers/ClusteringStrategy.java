package strategies.divers;

import cls.ens.SimpleEnsemble;
import clust.Cluster;
import clust.ClusterUpdates;
import clust.Clusterer;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import utils.InstanceUtils;
import utils.math.MathUtils;
import utils.Trackable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClusteringStrategy extends DiversityStrategy implements Trackable {

    private Clusterer clusterer;
    protected double range = 0.2;
    private double maxRange = 1.0;
    private double beta = 0.0;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public ClusteringStrategy(Clusterer clusterer) {
        this.clusterer = clusterer;
    }

    @Override
    public void reset() {
        this.clusterer.reset();
    }

    @Override
    public void update(Instance instance, SimpleEnsemble ensemble, HashMap<String, Double> driftIndicators) {
        ClusterUpdates removedAdded = this.clusterer.update(instance, false, -1);

        for (Integer removed : removedAdded.removed) {
            ensemble.removeClassifier(removed);
        }
        for (int i = 0; i < removedAdded.addedTo.size(); i++) {
            ensemble.addClassifier();
        }

        this.range = this.maxRange * MathUtils.sigmoid(1 - driftIndicators.get("error"), this.beta);
    }

    @Override
    public void diversify(Instance instance, SimpleEnsemble ensemble) {
        Instances centroids = InstanceUtils.createInstances(instance, false);
        for (Cluster cluster : this.clusterer.getClusters().values()) {
            centroids.add(cluster.getCentroid());
        }
        int centroidsUsedNum = (int)Math.ceil(this.range * centroids.numInstances() + Double.MIN_VALUE);

        int[] classifierIndices = InstanceUtils.getClosestIndices(instance, centroids, centroidsUsedNum);

        for (int classifierIndex : classifierIndices) {
            ensemble.trainClassifierOnInstance(classifierIndex, instance);
        }
    }

    public ClusteringStrategy setMaxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }

    public ClusteringStrategy setBeta(double beta) {
        this.beta = beta;
        return this;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        trackableParameters.put("clustersNum", this.clusterer.getClusters() != null ? (double)this.clusterer.getClusters().size() : 0.0);
        trackableParameters.put("range", this.range);
        return this.trackableParameters;
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>(Arrays.asList("clustersNum", "range"));
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
    }

}
