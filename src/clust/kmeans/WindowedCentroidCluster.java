package clust.kmeans;

import clust.Cluster;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import javafx.util.Pair;
import utils.InstanceUtils;
import utils.windows.WindowedInstances;

import java.util.ArrayList;

public class WindowedCentroidCluster extends Cluster {

    private WindowedInstances centroidWindow;

    WindowedCentroidCluster(WindowedInstances centroidWindow) {
        this.centroidWindow = centroidWindow;
    }

    @Override
    public boolean update(Instance instance, boolean label, int t) {
        this.centroidWindow.add(instance);
        return true;
    }

    @Override
    public Instance getCentroid() {
        return this.centroidWindow.getCentroid();
    }

    @Override
    public ArrayList<Double> getRadius() {
        return null;
    }

    @Override
    public void merge(Cluster otherCluster, int t) {}

    @Override
    public Pair<Boolean, Double> withinCluster(Instance instance, int t) {
        return null;
    }

    @Override
    public boolean significantOverlapping(Cluster otherCluster) {
        return false;
    }

    @Override
    public boolean isActive(int t, int threshold) {
        return false;
    }

    @Override
    public Instances getPrototypes(int t) {
        return InstanceUtils.createInstances(centroidWindow.getCentroid(), true);
    }

    @Override
    public Cluster copy() {
        return new WindowedCentroidCluster(this.centroidWindow.copy());
    }

}
