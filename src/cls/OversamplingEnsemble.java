package cls;

import com.yahoo.labs.samoa.instances.Instance;
import detect.DriftDetectionMethod;
import moa.classifiers.Classifier;
import moa.core.Utils;
import utils.Trackable;
import utils.math.WelchTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class OversamplingEnsemble extends OversamplingClassifier implements Trackable {

    private OversamplingClassifier oversamplingClassifier;
    private double alpha = 0.05;
    private int baseReplacementsNum = 0;
    private int osReplacementsNum = 0;
    private int baseCorrectReplacementsNum = 0;
    private int osCorrectReplacementsNum = 0;
    private int baseIncorrectReplacementsNum = 0;
    private int osIncorrectReplacementsNum = 0;
    private boolean elevating = false;
    private boolean collectReal = false;
    int i = 0;

    private DriftDetectionMethod errorIndicatorTemplate;
    private DriftDetectionMethod classifierError;
    private DriftDetectionMethod oversamplingClassifierError;

    private DriftDetectionMethod classifierRealError;
    private DriftDetectionMethod oversamplingClassifierRealError;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public OversamplingEnsemble(Classifier classifier) {
        super(classifier);
        this.oversamplingClassifier = new OversamplingClassifier(classifier.copy());
    }

    @Override
    public double[] getVotesForInstance(Instance instance) {
        i++;

        if (this.classifierError.getDetectorIndicators().get("error") < this.oversamplingClassifierError.getDetectorIndicators().get("error")) {
            return this.classifier.getVotesForInstance(instance);
        } else {
            return this.oversamplingClassifier.getVotesForInstance(instance);
        }
    }

    @Override
    public void resetLearningImpl() {
        this.classifier.resetLearning();
        this.oversamplingClassifier.resetLearning();
        this.baseReplacementsNum = 0;
        this.osReplacementsNum = 0;
        this.classifierError = this.errorIndicatorTemplate.copy();
        this.oversamplingClassifierError = this.errorIndicatorTemplate.copy();

        if (this.collectReal) {
            this.baseCorrectReplacementsNum = 0;
            this.osCorrectReplacementsNum = 0;
            this.baseIncorrectReplacementsNum = 0;
            this.osIncorrectReplacementsNum = 0;
            this.classifierRealError = this.errorIndicatorTemplate.copy();
            this.oversamplingClassifierRealError = this.errorIndicatorTemplate.copy();
        }

        this.trackableParameters = new HashMap<>();
    }

    @Override
    public void prepareForUse() {
        this.classifier.prepareForUse();
        this.oversamplingClassifier.prepareForUse();
    }

    @Override
    public void trainOnInstanceImpl(Instance instance) {
        int clsIndex = (int)instance.classValue();

        this.classifierError.update(Utils.maxIndex(classifier.getVotesForInstance(instance)), clsIndex, instance.numClasses());
        this.oversamplingClassifierError.update(Utils.maxIndex(oversamplingClassifier.getVotesForInstance(instance)), clsIndex, instance.numClasses());

        this.classifier.trainOnInstance(instance.copy()); // todo: in parallel
        this.oversamplingClassifier.trainOnInstance(instance.copy());

        if (this.elevating) {
            HashMap<String, Double> base = this.classifierError.getDetectorIndicators();
            HashMap<String, Double> os = this.oversamplingClassifierError.getDetectorIndicators();

            boolean reject = (new WelchTest()).test(base.get("error"), os.get("error"), base.get("var"), os.get("var"),
                    base.get("width"), os.get("width"), this.alpha);

            if (reject) {
                double diff = base.get("error") - os.get("error");

                if (diff < 0) {
                    this.oversamplingClassifier = new OversamplingClassifier(this.classifier.copy());
                    this.oversamplingClassifierError = this.classifierError.copy();
                    this.osReplacementsNum++;
                } else {
                    this.classifier = this.oversamplingClassifier.copy();
                    this.classifierError = this.oversamplingClassifierError.copy();
                    this.baseReplacementsNum++;
                }

                if (this.collectReal) {
                    double diffReal = this.classifierRealError.getDetectorIndicators().get("error") - this.oversamplingClassifierRealError.getDetectorIndicators().get("error");

                    if (diff < 0) {
                        this.oversamplingClassifierRealError = this.classifierRealError.copy();
                        if (diffReal < 0) this.osCorrectReplacementsNum++;
                        else this.osIncorrectReplacementsNum++;
                    }
                    else {
                        this.classifierRealError = this.oversamplingClassifierRealError.copy();
                        if (diffReal > 0) this.baseCorrectReplacementsNum++;
                        else this.baseIncorrectReplacementsNum++;
                    }
                }
            }
        }
    }

    public void trainOnSynthInstance(Instance instance) {
        this.oversamplingClassifier.trainOnSynthInstance(instance.copy());
    }

    public void updateRealAccuracy(Instance instance) {
        int clsIndex = (int)instance.classValue();
        this.classifierRealError.update(Utils.maxIndex(classifier.getVotesForInstance(instance)), clsIndex, instance.numClasses());
        this.oversamplingClassifierRealError.update(Utils.maxIndex(oversamplingClassifier.getVotesForInstance(instance)), clsIndex, instance.numClasses());
    }

    public OversamplingEnsemble setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public OversamplingEnsemble setElevating(boolean elevating) {
        this.elevating = elevating;
        return this;
    }

    public OversamplingEnsemble setCollectReal(boolean collectReal) {
        this.collectReal = collectReal;
        return this;
    }

    public OversamplingEnsemble setErrorIndicator(DriftDetectionMethod errorIndicator) {
        this.errorIndicatorTemplate = errorIndicator.copy();
        this.classifierError = errorIndicator.copy();
        this.oversamplingClassifierError = errorIndicator.copy();
        this.classifierRealError = errorIndicator.copy();
        this.oversamplingClassifierRealError = errorIndicator.copy();
        return this;
    }

    public boolean getCollectReal() {
        return this.collectReal;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        this.trackableParameters.put("baseReplacements", (double)this.baseReplacementsNum);
        this.trackableParameters.put("osReplacements", (double)this.osReplacementsNum);
        this.trackableParameters.put("baseAcc", 1.0 - this.classifierError.getDetectorIndicators().get("error"));
        this.trackableParameters.put("osAcc", 1.0 - this.oversamplingClassifierError.getDetectorIndicators().get("error"));
        if (this.collectReal) {
            this.trackableParameters.put("baseCorrectReplacements", (double)this.baseCorrectReplacementsNum);
            this.trackableParameters.put("osCorrectReplacements", (double)this.osCorrectReplacementsNum);
            this.trackableParameters.put("baseIncorrectReplacements", (double)this.baseIncorrectReplacementsNum);
            this.trackableParameters.put("osIncorrectReplacements", (double)this.osIncorrectReplacementsNum);
            this.trackableParameters.put("baseRealAcc", 1.0 - this.classifierRealError.getDetectorIndicators().get("error"));
            this.trackableParameters.put("osRealAcc", 1.0 - this.oversamplingClassifierRealError.getDetectorIndicators().get("error"));
        }

        return this.trackableParameters;
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        ArrayList<String> parameterNames = new ArrayList<>(Arrays.asList("baseReplacements", "osReplacements", "baseAcc", "osAcc"));
        if (this.collectReal) {
            parameterNames.addAll(new ArrayList<>(Arrays.asList("baseCorrectReplacements", "osCorrectReplacements",
                    "baseIncorrectReplacements", "osIncorrectReplacements", "baseRealAcc", "osRealAcc")));
        }
        return parameterNames;
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        return new HashMap<>();
    }

}
