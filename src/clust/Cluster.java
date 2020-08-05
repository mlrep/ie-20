package clust;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import javafx.util.Pair;

import java.util.ArrayList;


abstract public class Cluster {
    abstract public boolean update(Instance instance, boolean label, int t);
    abstract public Instance getCentroid();
    abstract public ArrayList<Double> getRadius();
    abstract public void merge(Cluster otherCluster, int t);
    abstract public Pair<Boolean, Double> withinCluster(Instance instance, int t);
    abstract public boolean significantOverlapping(Cluster otherCluster);
    abstract public boolean isActive(int t, int threshold);
    abstract public Instances getPrototypes(int t);
    abstract public Cluster copy();
}
