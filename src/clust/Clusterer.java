package clust;

import com.yahoo.labs.samoa.instances.Instance;

import java.util.HashMap;

public interface Clusterer {
    ClusterUpdates update(Instance instance, boolean label, int t);
    HashMap<Integer, Cluster> getClusters();
    void reset();
}


