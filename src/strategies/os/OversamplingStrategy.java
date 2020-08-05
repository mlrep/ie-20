package strategies.os;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import output.writer.OutputWriter;
import utils.Trackable;
import utils.windows.WindowedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class OversamplingStrategy implements Trackable {

    abstract public void updateLabeled(Instance instance, HashMap<String, Double> driftIndicators);
    abstract public void updateUnlabeled(Instance instance, HashMap<String, Double> driftIndicators, double predictionValue);
    abstract public Instances generateInstances(Instance instance, HashMap<String, Double> driftIndicators);
    abstract public void reset();
    abstract public boolean getUpdateFirst();

    protected int fixedWindowSize;
    protected double maxClassProportion = 0.0;
    protected int generatedInstancesNum = 0;
    protected Random random = new Random();
    protected HashMap<Integer, WindowedValue> labeledClassProportions = new HashMap<>();
    protected ArrayList<Double> fixedClassRatios;
    protected boolean collectProportions = false;

    void updateLabeledProportions(Instance instance, int classLabel) {
        double max = 0.0;

        for (int i = 0; i < instance.numClasses(); i++) {
            if (!labeledClassProportions.containsKey(i)) {
                labeledClassProportions.put(i, new WindowedValue(this.fixedWindowSize));
            }

            labeledClassProportions.get(i).add((i == classLabel) ? 1.0 : 0.0);

            double classProportion = this.labeledClassProportions.get(i).getAverage();
            if (classProportion > max) {
                max = classProportion;
            }
        }

        this.maxClassProportion = max;
    }

    public OversamplingStrategy setWindowSize(int fixedWindowSize) {
        this.fixedWindowSize = fixedWindowSize;
        return this;
    }

    public OversamplingStrategy setFixedClassRatios(ArrayList<Double> fixedClassRatios) {
        this.fixedClassRatios = fixedClassRatios;
        return this;
    }

    @Override
    public ArrayList<String> getOtherTrackableNames() {
        return new ArrayList<>();
    }

    @Override
    public HashMap<String, OutputWriter> getOtherTrackable() {
        return new HashMap<>();
    }

}
