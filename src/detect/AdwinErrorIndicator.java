package detect;

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.core.driftdetection.ADWIN;
import output.writer.OutputWriter;

import java.util.ArrayList;
import java.util.HashMap;

public class AdwinErrorIndicator implements DriftDetectionMethod {

    private ADWIN adwin = new ADWIN();
    private double delta = 0.002;

    public AdwinErrorIndicator() { }

    public AdwinErrorIndicator(double delta) {
        this.delta = delta;
        this.adwin = new ADWIN(delta);
    }

    public AdwinErrorIndicator(ADWIN adwin) {
        this.adwin = adwin;
    }

    @Override
    public void reset() {
        this.adwin = new ADWIN(this.delta);
    }

    @Override
    public AdwinErrorIndicator copy() {
        return new AdwinErrorIndicator((ADWIN)this.adwin.copy());
    }

    @Override
    public void update(int predictedClass, int trueClass, int numClasses) {
        int p = (predictedClass != trueClass ? 1 : 0);
        this.adwin.setInput(p);
    }

    @Override
    public double checkState() {
        return StreamStateType.STATIC.ordinal();
    }

    @Override
    public HashMap<String, Double> getDetectorIndicators() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("error", this.adwin.getEstimation());
        map.put("var", this.adwin.getVariance());
        map.put("width", (double)this.adwin.getWidth());
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
