package strategies.os;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import strategies.os.strategies.GenerationsNumStrategy;
import utils.math.MathUtils;
import utils.Trackable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SingleExpositionStrategy extends OversamplingStrategy implements Trackable {

    int generationsNum = 1;
    protected GenerationsNumStrategy generationsNumStrategy;
    protected double ratioCoef;
    protected double errorCoef;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public SingleExpositionStrategy(GenerationsNumStrategy generationsNumStrategy) {
        this.generationsNumStrategy = generationsNumStrategy;
        if (this.generationsNumStrategy == GenerationsNumStrategy.RATIO_DRIVEN || this.generationsNumStrategy == GenerationsNumStrategy.CLASS_ERROR_DRIVEN
                || this.generationsNumStrategy == GenerationsNumStrategy.HYBRID_RATIO_ERROR) this.collectProportions = true;
    }

    @Override
    public void reset() {
        this.labeledClassProportions.clear();
        this.maxClassProportion = 0.0;
        this.trackableParameters.clear();
    }

    @Override
    public void updateLabeled(Instance instance, HashMap<String, Double> driftIndicators) {
        if (this.collectProportions) this.updateLabeledProportions(instance, (int)instance.classValue());
    }

    @Override
    public void updateUnlabeled(Instance instance, HashMap<String, Double> driftIndicators, double predictionValue) {}

    @Override
    public Instances generateInstances(Instance instance, HashMap<String, Double> driftIndicators) {
        Instances generatedInstances = new Instances(instance.dataset());
        int generationsNum = this.generationsNumStrategy((int)instance.classValue(), driftIndicators);

        for (int j = 0; j < generationsNum; j++) generatedInstances.add(instance.copy());

        this.generatedInstancesNum = generatedInstances.size();
        return generatedInstances;
    }

    int generationsNumStrategy(int classValue, HashMap<String, Double> driftIndicators) {
        switch (this.generationsNumStrategy) {
            case FIXED: return this.generationsNum;
            case ERROR_DRIVEN: return this.generationsNum - (int)(driftIndicators.get("error") * this.generationsNum);
            case ERROR_DRIVEN_INV: return (int)(Math.ceil(driftIndicators.get("error") * this.generationsNum));
            case ERROR_SIGM: return (int)(MathUtils.sigmoid(driftIndicators.get("error"), 1 - 2 * driftIndicators.get("error")) * this.generationsNum);
            case RATIO_DRIVEN: return this.ratioDriven(classValue);
            case CLASS_ERROR_DRIVEN: return this.classErrorDriven(classValue, driftIndicators);
            case HYBRID_RATIO_ERROR: return this.hybridClassErrorDriven(classValue, driftIndicators);
            case FIXED_RATIOS: return this.fixedRatio(classValue);
            default: return -1;
        }
    }

    private int ratioDriven(int classValue) {
        double maxRatio = this.labeledClassProportions.containsKey(classValue) ? this.labeledClassProportions.get(classValue).getAverage() / this.maxClassProportion : 1.0;
        return (int)Math.ceil((1 - maxRatio) * this.generationsNum);
    }

    private int classErrorDriven(int classValue, HashMap<String, Double> driftIndicators) { // todo: write tests
        double classError = driftIndicators.get(Integer.toString(classValue));
        if (Double.isNaN(classError)) classError = this.labeledClassProportions.get(classValue).getAverage();
        return (int)Math.ceil((1 - classError) * this.generationsNum);
    }

    private int hybridClassErrorDriven(int classValue, HashMap<String, Double> driftIndicators) {
        return (int)Math.ceil((this.ratioCoef * ratioDriven(classValue) + this.errorCoef * classErrorDriven(classValue, driftIndicators)));
    }

    private int fixedRatio(int classValue) {
        double maxClassRatio = MathUtils.max(this.fixedClassRatios.stream().mapToDouble(Double::doubleValue).toArray());
        double maxRatio = this.fixedClassRatios.get(classValue) / maxClassRatio;
        return (int)(this.generationsNum * (1 - maxRatio));
    }

    @Override
    public boolean getUpdateFirst() {
        return false;
    }

    public SingleExpositionStrategy setGenerationsNum(int generationsNum) {
        this.generationsNum = generationsNum;
        return this;
    }

    public SingleExpositionStrategy setHybridCoefficients(double ratioCoef, double errorCoef) {
        this.ratioCoef = ratioCoef;
        this.errorCoef = errorCoef;
        return this;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        this.trackableParameters.put("generatedInstances", (double)this.generatedInstancesNum);
        return this.trackableParameters;
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>(Arrays.asList("generatedInstances"));
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
    }

}
