package strategies.os;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import strategies.os.strategies.*;
import utils.InstanceUtils;
import utils.math.MathUtils;
import utils.Trackable;
import utils.windows.WindowedInstances;

import java.util.HashMap;

public class SMOTEKernelStrategy extends SMOTEStrategy implements Trackable {

    public SMOTEKernelStrategy(WindowSizeStrategy windowSizeStrategy, UnlabeledControlStrategyType unlabeledControlStrategy, NeighborsStrategyType neighborsStrategy,
                               GenerationsNumStrategy generationNumStrategy, GenerationStrategyType generationStrategy) {
        super(windowSizeStrategy, unlabeledControlStrategy, neighborsStrategy, generationNumStrategy, generationStrategy);
    }

    WindowedInstances commonWindowInstances = null;

    @Override
    public void reset() {
        super.reset();
        this.commonWindowInstances = null;
    }

    @Override
    public void updateLabeled(Instance instance, HashMap<String, Double> driftIndicators) {
        if (this.commonWindowInstances == null) {
            this.commonWindowInstances = new WindowedInstances(this.fixedWindowSize, instance, true);
        }
        else {
            int newSize = this.windowSizeStrategy(driftIndicators);

            for (int i = 0; i < this.commonWindowInstances.getWindowLength() - newSize + 1; i++) {
                int cr = (int)this.commonWindowInstances.getInstance(i).classValue();
                this.nnWindowed.get(cr).removeOldest();
            }

            this.commonWindowInstances.updateSize(newSize);
            this.commonWindowInstances.add(instance);
        }

        this.updateClassWindow(instance, driftIndicators);
    }


    @Override
    public Instances generateInstances(Instance instance, HashMap<String, Double> driftIndicators) {
        int classValue = (int)instance.classValue();
        int numNeighborsToFind = this.neighborsNumStrategy(driftIndicators);

        Instances nearestNeighbors = getNearestNeighbors(instance, classValue, numNeighborsToFind);

        if (nearestNeighbors != null && nearestNeighbors.numInstances() > 0) {
            Instances generatedInstances = InstanceUtils.createInstances(instance, false);
            int generationsNum = this.generationsNumStrategy(classValue, driftIndicators);

            double maxDist = InstanceUtils
                    .findInstance(instance, this.nnWindowed.get(classValue).getInstances(), false)
                    .getValue();
            double newInstanceRp = this.radialPotential(instance, maxDist);

            for (int i = 0; i < nearestNeighbors.size(); i++) {
                Instance newInstance = generateLineInstance(instance, nearestNeighbors.get(i));
                double generatedInstanceRp = this.radialPotential(newInstance, maxDist); // todo: slow; use rp only for proportion of closest NN?

                if (generatedInstanceRp > newInstanceRp) {
                    for (int j = 0; j < generationsNum; j++) {
                        generatedInstances.add(newInstance);
                    }
                }
            }

            if (generatedInstances.size() > 0) this.fill(generatedInstances, nearestNeighbors.size(), generationsNum);

            this.generatedInstancesNum = generatedInstances.size();
            return generatedInstances;
        }

        return null;
    }

    private void fill(Instances generatedInstances, int nnNum, int numGenerations) {
        int target = nnNum * numGenerations;
        int n = generatedInstances.size();

        while (generatedInstances.size() < target) {
            int i = generatedInstances.size() % n;

            for (int j = 0; j < numGenerations; j++) {
                generatedInstances.add(generatedInstances.get(i));
            }
        }
    }

    private double radialPotential(Instance instance, double maxDist) {
        double rp = 0.0;
        int classValue = (int)instance.classValue();
        Instances cwInstances = this.commonWindowInstances.getInstances();

        for (int i = 0; i < cwInstances.size(); i++) {
            Instance otherInstance = cwInstances.get(i);

            double newRp = Math.exp(-Math.pow(MathUtils.euclideanDist(instance, otherInstance) / maxDist, 2.0));

            if (classValue == (int)otherInstance.classValue()) rp += newRp;
            else rp -= newRp;
        }

        return rp;
    }

}
