package strategies.os;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import strategies.os.strategies.GenerationsNumStrategy;
import strategies.os.strategies.ProbabilisticStrategyType;
import strategies.os.strategies.WindowSizeStrategy;
import utils.math.MathUtils;
import utils.Trackable;
import utils.windows.WindowedInstances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProbabilisticWindowStrategy extends SingleExpositionStrategy implements Trackable {

    private WindowedInstances windowInstances;
    private WindowSizeStrategy windowSizeStrategy;
    private ProbabilisticStrategyType probabilisticStrategyType;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public ProbabilisticWindowStrategy(WindowSizeStrategy windowSizeStrategy, GenerationsNumStrategy generationsNumStrategy,
                                       ProbabilisticStrategyType probabilisticStrategyType) {
        super(generationsNumStrategy);
        this.windowSizeStrategy = windowSizeStrategy;
        this.probabilisticStrategyType = probabilisticStrategyType;
    }

    @Override
    public void reset() {
        this.windowInstances = null;
    }

    @Override
    public void updateLabeled(Instance instance, HashMap<String, Double> driftIndicators) {
        int newWindowSize = this.windowSizeStrategy(driftIndicators);
        if (this.windowInstances == null) this.init(instance, newWindowSize);

        this.windowInstances.updateSize(newWindowSize);

        windowInstances.add(instance);
        if (this.collectProportions) this.updateLabeledProportions(instance, (int)instance.classValue());
    }

    private void init(Instance instanceTemplate, int windowSize) {
        this.windowInstances = new WindowedInstances(windowSize, instanceTemplate.copy(), false);
    }

    int windowSizeStrategy(HashMap<String, Double> driftIndicators) {
        switch(this.windowSizeStrategy) {
            case FIXED: return this.fixedWindowSize;
            case ERROR_DRIVEN: return (int)(this.fixedWindowSize - this.fixedWindowSize * (driftIndicators.get("error")));
            case ADWIN_WIDTH: return (int)driftIndicators.get("width").doubleValue();
            default: return -1;
        }
    }

    @Override
    public void updateUnlabeled(Instance instance, HashMap<String, Double> driftIndicators, double predictionValue) {}

    @Override
    public Instances generateInstances(Instance instanceTemplate, HashMap<String, Double> driftIndicators) {
        Instances generatedInstances = new Instances(instanceTemplate.dataset());
        if (this.windowInstances.getWindowLength() < 1) return generatedInstances;

        double r;
        int numGenerations = this.generationsNumStrategy((int) instanceTemplate.classValue(), driftIndicators);

        for (int i = 0; i < numGenerations; i++) {
            switch (this.probabilisticStrategyType) {
                case UNIFORM: r = Math.random(); break;
                case EXPONENTIAL: r = 1.0 - MathUtils.randomExpNormal(4.0); break;
                default: r = 1.0;
            }

            int x = (int)(r * this.windowInstances.getWindowLength());
            generatedInstances.add(this.windowInstances.getInstance(x).copy());
        }

        this.generatedInstancesNum = generatedInstances.size();
        return generatedInstances;
    }

    @Override
    public boolean getUpdateFirst() {
        return true;
    }

    public int getNumOfInstances() {
        return this.windowInstances == null ? 0 : this.windowInstances.getWindowLength();
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        this.trackableParameters.put("generatedInstances", (double)this.generatedInstancesNum);
        this.trackableParameters.put("windowSize", (double)this.windowSizeStrategy(driftIndicators));
        return this.trackableParameters;
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>(Arrays.asList("generatedInstances", "windowSize"));
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
    }

}
