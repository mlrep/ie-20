package strategies.os;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.lazy.neighboursearch.LinearNNSearch;
import strategies.os.strategies.*;
import utils.InstanceUtils;
import utils.math.MathUtils;
import utils.Trackable;
import utils.windows.NearestNeighborWindowed;

import java.util.HashMap;

public class SMOTEMeansStrategy extends SMOTEStrategy implements Trackable {

    public SMOTEMeansStrategy(WindowSizeStrategy windowSizeStrategy, UnlabeledControlStrategyType unlabeledControlStrategy, NeighborsStrategyType neighborsStrategy,
                              GenerationsNumStrategy generationNumStrategy, GenerationStrategyType generationStrategy) {
        super(windowSizeStrategy, unlabeledControlStrategy, neighborsStrategy, generationNumStrategy, generationStrategy);
    }

    @Override
    public void updateUnlabeled(Instance instance, HashMap<String, Double> driftIndicators, double predictionValue) {
        if (this.unlabeledUncertaintyThreshold > predictionValue) return;
        int minDistClassValue = this.findClosestCentroidClass(instance);

        if (minDistClassValue != -1 && this.unlabeledControlStrategy(this.nnWindowed.get(minDistClassValue).getLuRatio(), driftIndicators)) {
            Instance unlabeledInstance = InstanceUtils.prepareUnlabeled(instance);
            this.nnWindowed.get(minDistClassValue).insert(unlabeledInstance);
        }
    }

    @Override
    protected void init(int classValue, int windowSize, Instance instanceTemplate) {
        this.nnWindowed.put(classValue, new NearestNeighborWindowed(new LinearNNSearch(), windowSize, instanceTemplate, true));
    }

    int findClosestCentroidClass(Instance instance) {
        int classValues = instance.numClasses();
        int minDistClassValue = -1;
        double minDist = Double.MAX_VALUE;

        for (int classValue = 0; classValue < classValues; classValue++) {
            if (!this.nnWindowed.containsKey(classValue)) continue;

            double dist = MathUtils.euclideanDist(instance, this.nnWindowed.get(classValue).getCentroid());
            if (minDist > dist) {
                minDist = dist;
                minDistClassValue = classValue;
            }
        }

        return minDistClassValue;
    }
}
