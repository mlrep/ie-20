package strategies.os;
import com.yahoo.labs.samoa.instances.Instances;
import moa.classifiers.lazy.neighboursearch.LinearNNSearch;
import com.yahoo.labs.samoa.instances.Instance;
import strategies.os.strategies.*;
import utils.InstanceUtils;
import utils.math.MathUtils;
import utils.Trackable;
import utils.windows.NearestNeighborWindowed;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SMOTEStrategy extends ProbabilisticWindowStrategy implements Trackable {

    Map<Integer, NearestNeighborWindowed> nnWindowed = new HashMap<>();
    private UnlabeledControlStrategyType unlabeledControlStrategy;
    private NeighborsStrategyType neighborsStrategy;
    private GenerationStrategyType generationStrategy;

    private int fixedNumNeighbors = 10;
    private double luRatioThreshold = 0.6;
    double unlabeledUncertaintyThreshold = 0.0;
    private boolean randomNeighbors = true;
    public static final double UNLABELED_LABEL = -1;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public SMOTEStrategy(WindowSizeStrategy windowSizeStrategy, UnlabeledControlStrategyType unlabeledControlStrategy, NeighborsStrategyType neighborsStrategy,
                         GenerationsNumStrategy intensityStrategy, GenerationStrategyType generationStrategy) {
        super(windowSizeStrategy, intensityStrategy, ProbabilisticStrategyType.CONSTANT);
        this.unlabeledControlStrategy = unlabeledControlStrategy;
        this.neighborsStrategy = neighborsStrategy;
        this.generationStrategy = generationStrategy;

        if (this.neighborsStrategy == NeighborsStrategyType.FIXED
                || this.neighborsStrategy == NeighborsStrategyType.ERROR_DRIVEN) this.randomNeighbors = false;
    }

    @Override
    public void reset() {
        this.nnWindowed.clear();
        this.labeledClassProportions.clear();
        this.maxClassProportion = 0.0;
        this.trackableParameters.clear();
    }

    @Override
    public void updateLabeled(Instance instance, HashMap<String, Double> driftIndicators) {
        updateClassWindow(instance, driftIndicators);
    }

    protected void updateClassWindow(Instance instance, HashMap<String, Double> driftIndicators) {
        int newWindowSize = this.windowSizeStrategy(driftIndicators);
        int classValue = (int)instance.classValue();
        if (!this.nnWindowed.containsKey(classValue)) this.init(classValue, newWindowSize, instance);

        this.nnWindowed.get(classValue).updateSize(newWindowSize);

        this.nnWindowed.get(classValue).insert(instance);
        if (this.collectProportions) this.updateLabeledProportions(instance, classValue);
    }

    protected void init(int classValue, int windowSize, Instance instanceTemplate) {
        this.nnWindowed.put(classValue, new NearestNeighborWindowed(new LinearNNSearch(), windowSize, instanceTemplate, false));
    }

    @Override
    public void updateUnlabeled(Instance instance, HashMap<String, Double> driftIndicators, double predictionValue) {
        if (this.unlabeledUncertaintyThreshold > predictionValue) return;
        for (int classValue = 0; classValue < instance.numClasses(); classValue++) {
            if (this.nnWindowed.containsKey(classValue) &&
                    this.unlabeledControlStrategy(this.nnWindowed.get(classValue).getLuRatio(), driftIndicators)) {
                Instance unlabeledInstance = InstanceUtils.prepareUnlabeled(instance);
                this.nnWindowed.get(classValue).insert(unlabeledInstance);
            }
        }
    }

    boolean unlabeledControlStrategy(double luRatio, HashMap<String, Double> driftIndicators) {
        switch (this.unlabeledControlStrategy) {
            case FIXED_RATIO_THRESHOLD: return luRatio > this.luRatioThreshold;
            case ERROR_RATIO_THRESHOLD: return luRatio >= driftIndicators.get("error");
            case SIGMOID_ERROR_DRIVEN: return Math.random() < MathUtils.sigmoid(luRatio, 2 * driftIndicators.get("error") - 1);
            default: return false;
        }
    }

    @Override
    public Instances generateInstances(Instance instance, HashMap<String, Double> driftIndicators) {
        int classValue = (int)instance.classValue();
        int numNeighborsToFind = this.neighborsNumStrategy(driftIndicators);

        Instances nearestNeighbors = getNearestNeighbors(instance, classValue, numNeighborsToFind);

        if (nearestNeighbors != null && nearestNeighbors.numInstances() > 0) {
            Instances generatedInstances = InstanceUtils.createInstances(instance, false);
            List<Integer> neighborIndices = this.generateNeighborIndices(nearestNeighbors);
            int generationsNum = this.generationsNumStrategy(classValue, driftIndicators);

            for (int i : neighborIndices) {
                Instance randNeighbor = nearestNeighbors.get(i);
                this.generationStrategy(instance, randNeighbor, generationsNum, generatedInstances);
            }

            this.generatedInstancesNum = generatedInstances.size();
            return generatedInstances;
        }

        return null;
    }

    protected Instances getNearestNeighbors(Instance instance, int classValue, int numNeighborsToFind) {
        this.generatedInstancesNum = 0;
        if (!this.nnWindowed.containsKey(classValue) || numNeighborsToFind == 0) return null;

        return this.nnWindowed.get(classValue).getNearestNeighbors(instance, numNeighborsToFind);
    }

    int neighborsNumStrategy(HashMap<String, Double> driftIndicators) {
        switch (this.neighborsStrategy) {
            case FIXED:
            case FIXED_UNIFORM:
            case FIXED_POISSON: return this.fixedNumNeighbors;
            case ERROR_DRIVEN:
            case ERROR_DRIVEN_UNIFORM:
            case ERROR_DRIVEN_POISSON: return (int)(this.fixedNumNeighbors - this.fixedNumNeighbors * (driftIndicators.get("error")));
            default: return -1;
        }
    }

    List<Integer> generateNeighborIndices(Instances nearestNeighbors) {
        List<Integer> neighborIndices = InstanceUtils.instanceIndices(nearestNeighbors);
        if (!this.randomNeighbors) return neighborIndices;

        Collections.shuffle(neighborIndices);
        int n = nearestNeighbors.numInstances();
        int r;

        switch (this.neighborsStrategy) {
            case ERROR_DRIVEN_UNIFORM:
            case FIXED_UNIFORM: r = ThreadLocalRandom.current().nextInt(0, n + 1); break;
            case ERROR_DRIVEN_POISSON:
            case FIXED_POISSON: r = (int)Math.ceil((double)n / MathUtils.randomPoisson01(1)); break;
            default: r = -1;
    }

        return neighborIndices.subList(0, r);
    }

    void generationStrategy(Instance instance, Instance neighbor, int generationsNum, Instances generatedInstances) {
        switch (this.generationStrategy) {
            case LINE: this.generateLineInstances(instance, neighbor, generationsNum, generatedInstances); break;
            case COPY: this.generateCopies(neighbor, generationsNum, generatedInstances); break;
            default:
        }
    }

    private void generateLineInstances(Instance instance, Instance neighbor, int generationsNum, Instances generatedInstances) {
        for (int i = 0; i < generationsNum; i++) {
            Instance newInstance = generateLineInstance(instance, neighbor);
            generatedInstances.add(newInstance);
        }
    }

    private void generateCopies(Instance neighbor, int generationsNum, Instances generatedInstances) {
        for (int i = 0; i < generationsNum; i++) {
            generatedInstances.add(neighbor);
        }
    }

    protected Instance generateLineInstance(Instance instance, Instance neighbor) {
        Instance newInstance = instance.copy();
        newInstance.setClassValue(instance.classValue());
        double gap = this.random.nextDouble();

        for (int j = 0; j < instance.numAttributes() - 1; j++) {
            if (instance.attribute(j).isNumeric()) {
                newInstance.setValue(j, instance.value(j) + gap * (neighbor.value(j) - instance.value(j)));
            } else {
                newInstance.setValue(j, this.random.nextBoolean() ? instance.value(j) : neighbor.value(j)); // todo: is it fine?
            }
        }

        return newInstance;
    }

    @Override
    public boolean getUpdateFirst() {
        return false;
    }

    public SMOTEStrategy setFixedNeighborsNum(int fixedNeighborsNum) {
        this.fixedNumNeighbors = fixedNeighborsNum;
        return this;
    }

    public SMOTEStrategy setLuRatioThreshold(double luRatioThreshold) {
        this.luRatioThreshold = luRatioThreshold;
        return this;
    }

    public SMOTEStrategy setNeighborsRandomness(boolean randomNeighbors)  {
        this.randomNeighbors = randomNeighbors;
        return this;
    }

    public SMOTEStrategy setUnlabeledUncertaintyThreshold(double unlabeledUncertaintyThreshold) {
        this.unlabeledUncertaintyThreshold = unlabeledUncertaintyThreshold;
        return this;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        double averageLuRatio = 0.0, n = 0.0;
        for (int classValue = 0; classValue < instance.numClasses(); classValue++) {
            if (this.nnWindowed.containsKey(classValue)) {
                averageLuRatio += this.nnWindowed.get(classValue).getLuRatio();
                n++;
            }
        }
        this.trackableParameters.put("averageLuRatio", averageLuRatio / n);
        this.trackableParameters.put("generatedInstances", (double)this.generatedInstancesNum);
        this.trackableParameters.put("windowSize", (double)this.windowSizeStrategy(driftIndicators));

        return this.trackableParameters;
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>(Arrays.asList("averageLuRatio", "generatedInstances", "windowSize"));
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
    }

}


