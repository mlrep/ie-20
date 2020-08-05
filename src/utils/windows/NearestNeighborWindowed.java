package utils.windows;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;
import strategies.os.SMOTEStrategy;
import utils.windows.WindowedInstances;

public class NearestNeighborWindowed {

    private NearestNeighbourSearch nnSearch;
    private int windowSize;
    private WindowedInstances windowInstances;
    private int labeledInstancesNum = 0;

    public NearestNeighborWindowed(NearestNeighbourSearch nnSearch, int windowSize, Instance instanceTemplate, boolean means) {
        this.nnSearch = nnSearch;
        this.windowSize = windowSize;
        this.windowInstances = new WindowedInstances(windowSize, instanceTemplate, false).setKeepStatistics(means);
    }

    public void insert(Instance instance) {
        if (instance == null) return;

        if (this.windowInstances.getWindowLength() == windowSize) {
            if (this.windowInstances.getInstance(0).classValue() != SMOTEStrategy.UNLABELED_LABEL) this.labeledInstancesNum -= 1;
        }
        if (instance.classValue() != SMOTEStrategy.UNLABELED_LABEL) this.labeledInstancesNum += 1;

        this.windowInstances.add(instance);
    }

    public void removeOldest() {
        this.windowInstances.remove(0);
    }

    public void updateSize(int newSize) {
        this.windowInstances.updateSize(newSize);
    }

    public Instances getNearestNeighbors(Instance instance, int k) {
        try {
            this.nnSearch.setInstances(this.windowInstances.getInstances());
            return clean(this.nnSearch.kNearestNeighbours(instance, k), k);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Instances clean(Instances nearestNeighbors, double k) {
        if (nearestNeighbors == null) return null;

        int nnSize = nearestNeighbors.size();
        if (nnSize > k) {
            for (int i = 0; i < nnSize - k; i++) nearestNeighbors.delete(nnSize-i-1);
        }

        return nearestNeighbors;
    }

    public int getInstancesNum() {
        return this.windowInstances.getWindowLength();
    }

    public double getLuRatio() {
        int winLen = this.windowInstances.getWindowLength();
        return winLen == 0 ? 0 : (double) this.labeledInstancesNum / winLen;
    }

    public Instance getCentroid() { return this.windowInstances.getCentroid(); }

    // todo: add variance

    public Instances getInstances() { return this.windowInstances.getInstances(); }
}
