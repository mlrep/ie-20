package detect;
import com.yahoo.labs.samoa.instances.Instance;
import output.writer.OutputWriter;
import utils.windows.WindowedValue;

import java.util.ArrayList;
import java.util.HashMap;

public class WindowedErrorIndicator implements DriftDetectionMethod {

    private WindowedValue estimator = new WindowedValue(WindowedValue.WINDOW_ESTIMATOR_WIDTH);

    public WindowedErrorIndicator() {}

    public WindowedErrorIndicator(int windowSize) {
        this.estimator = new WindowedValue(windowSize);
    }

    public WindowedErrorIndicator(WindowedValue estimator) { this.estimator = estimator; }

    @Override
    public WindowedErrorIndicator copy() {
        return new WindowedErrorIndicator(this.estimator.copy());
    }

    @Override
    public void reset() {
        this.estimator = new WindowedValue(this.estimator.getWindowSize());
    }

    @Override
    public void update(int predictedClass, int trueClass, int numClasses) {
        int p = (predictedClass != trueClass ? 1 : 0);
        this.estimator.add(p);
    }

    @Override
    public double checkState() {
        return StreamStateType.STATIC.ordinal();
    }

    @Override
    public HashMap<String, Double> getDetectorIndicators() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("error", this.estimator.getAverage());
        map.put("var", Math.pow(this.estimator.getStd(), 2.0));
        map.put("width", (double)this.estimator.getWindowLength());
        return map;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        return new HashMap<>();
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>();
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
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
