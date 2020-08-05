package framework;

import cls.OversamplingClassifier;
import cls.OversamplingEnsemble;
import moa.core.Utils;
import strategies.al.ActiveLearningStrategy;
import com.yahoo.labs.samoa.instances.Instances;
import detect.DriftDetectionMethod;
import detect.StreamStateType;
import strategies.os.OversamplingStrategy;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import utils.ClassifierUtils;
import utils.Trackable;
import utils.TrackableFramework;

import java.util.ArrayList;
import java.util.HashMap;

public class OversamplingFramework extends Framework implements TrackableFramework {

    private OversamplingStrategy oversamplingStrategy;
    private DriftDetectionMethod driftDetector;
    private boolean updateFirst;

    public OversamplingFramework(OversamplingClassifier classifier, ActiveLearningStrategy activeLearningStrategy,
                                 OversamplingStrategy oversamplingStrategy, DriftDetectionMethod driftDetector) {
        this.classifier = classifier;
        this.classifier.prepareForUse();
        this.activeLearningStrategy = activeLearningStrategy;
        this.oversamplingStrategy = oversamplingStrategy;
        this.driftDetector = driftDetector;
        this.updateFirst = this.oversamplingStrategy.getUpdateFirst();
    }

    @Override
    public void reset() {
        this.classifier.resetLearning();
        this.activeLearningStrategy.reset();
        this.oversamplingStrategy.reset();
        this.driftDetector.reset();
        this.ready = true;
    }

    @Override
    public FrameworkUpdate update(Instance instance, double[] votes, int n) {
        HashMap<String, Double> driftIndicators = this.driftDetector.getDetectorIndicators();

        if (this.classifier instanceof OversamplingEnsemble) {
            OversamplingEnsemble ens = (OversamplingEnsemble) this.classifier;
            if (ens.getCollectReal()) ens.updateRealAccuracy(instance);
        }

        if (!this.omitInit && n <= this.initInstances) {
            this.classifier.trainOnInstance(instance);
            this.activeLearningStrategy.update(instance, true);
            this.oversamplingStrategy.updateLabeled(instance, driftIndicators);
            this.driftDetector.update(Utils.maxIndex(votes), (int)instance.classValue(), instance.numClasses());

            return new FrameworkUpdate(true);
        }

        boolean learnActively = false;

        if (this.activeLearningStrategy != null) {
            learnActively = this.activeLearningStrategy.queryLabel(instance, votes, driftIndicators);
            this.activeLearningStrategy.update(instance, learnActively);
        }

        if (learnActively) {
            this.driftDetector.update(Utils.maxIndex(votes), (int)instance.classValue(), instance.numClasses());
            if ((int)this.driftDetector.checkState() == StreamStateType.DRIFT.ordinal()) this.driftDetector.reset();
            this.classifier.trainOnInstance(instance);

            if (this.updateFirst) this.oversamplingStrategy.updateLabeled(instance, driftIndicators);
            Instances generatedInstances = this.oversamplingStrategy.generateInstances(instance, driftIndicators);

            if (generatedInstances != null) {
                for (int i = 0; i < generatedInstances.numInstances(); i++) {
                    ((OversamplingClassifier)this.classifier).trainOnSynthInstance(generatedInstances.get(i));
                }
            }

            if (!updateFirst) this.oversamplingStrategy.updateLabeled(instance, driftIndicators);
        } else {
            this.oversamplingStrategy.updateUnlabeled(instance, driftIndicators, ClassifierUtils.combinePredictionsMax(votes));
        }

        return new FrameworkUpdate(learnActively);
    }

    public void setFixedClassRatios(ArrayList<Double> fixedClassRatios) {
        this.oversamplingStrategy.setFixedClassRatios(fixedClassRatios);
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance) {
        if (!this.collectTrackableParameters) return null;
        else {
            HashMap<String, Double> parameters = new HashMap<>();
            HashMap<String, Double> driftIndicators = this.driftDetector.getDetectorIndicators();
            if (this.classifier instanceof Trackable) parameters.putAll(((Trackable) this.classifier).getSeriesParameters(instance, driftIndicators));
            parameters.putAll(this.activeLearningStrategy.getSeriesParameters(instance, driftIndicators));
            parameters.putAll(this.oversamplingStrategy.getSeriesParameters(instance, driftIndicators));

            return parameters;
        }
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        if (!this.collectTrackableParameters) return null;
        else {
            ArrayList<String> parameterNames = new ArrayList<>(this.activeLearningStrategy.getSeriesParameterNames());
            if (this.classifier instanceof Trackable) parameterNames.addAll(((Trackable) this.classifier).getSeriesParameterNames());
            parameterNames.addAll(this.oversamplingStrategy.getSeriesParameterNames());

            return parameterNames;
        }
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        HashMap<String, Double> parameters = new HashMap<>();
        if (!this.collectTrackableParameters) return parameters;

        if (this.classifier instanceof Trackable) parameters.putAll(((Trackable) this.classifier).getAggregateParameters());
        parameters.putAll(this.activeLearningStrategy.getAggregateParameters());
        parameters.putAll(this.oversamplingStrategy.getAggregateParameters());

        return parameters;
    }

}
