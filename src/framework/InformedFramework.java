package framework;

import strategies.al.ActiveLearningStrategy;
import com.yahoo.labs.samoa.instances.Instance;
import detect.DriftDetectionMethod;
import detect.StreamStateType;
import moa.classifiers.Classifier;
import moa.core.Utils;
import utils.Trackable;

import java.util.ArrayList;
import java.util.HashMap;

public class InformedFramework extends BlindFramework {

    private DriftDetectionMethod driftDetector;
    private boolean discrete = false;

    public InformedFramework(Classifier classifier, ActiveLearningStrategy activeLearningStrategy, DriftDetectionMethod driftDetector,
                             boolean discrete) {
        super(classifier, activeLearningStrategy);
        this.driftDetector = driftDetector;
        this.discrete = discrete;
    }

    @Override
    public void reset() {
        this.driftDetector.reset();
        super.reset();
    }

    @Override
    public FrameworkUpdate update(Instance instance, double[] votes, int n) {
        if (!this.omitInit && n < this.initInstances) {
            this.activeLearningStrategy.update(instance, true);
            this.driftDetector.update(Utils.maxIndex(votes), (int)instance.classValue(), instance.numClasses());
            Framework.trainClassifier(this.classifier, instance, true, this.driftDetector.getDetectorIndicators(), n);

            return new FrameworkUpdate(true);
        }

        HashMap<String, Double> driftIndicators = this.driftDetector.getDetectorIndicators();

        boolean learnActively = this.activeLearningStrategy.queryLabel(instance, votes, driftIndicators);
        this.activeLearningStrategy.update(instance, learnActively);

        if (learnActively) {
            this.driftDetector.update(Utils.maxIndex(votes), (int)instance.classValue(), instance.numClasses());

            if ((int) this.driftDetector.checkState() == StreamStateType.DRIFT.ordinal()) {
                if (this.discrete) this.classifier.resetLearning();
                this.driftDetector.reset();
            }

            Framework.trainClassifier(this.classifier, instance, true, driftIndicators, n);
        } else {
            Framework.trainClassifier(this.classifier, instance, false, driftIndicators, n);
        }

        return new FrameworkUpdate(learnActively);
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance) {
        if (!this.collectTrackableParameters) return null;
        else {
            HashMap<String, Double> parameters = new HashMap<>();
            HashMap<String, Double> driftIndicators = this.driftDetector.getDetectorIndicators();
            if (this.classifier instanceof Trackable)
                parameters.putAll(((Trackable) this.classifier).getSeriesParameters(instance, driftIndicators));
            parameters.putAll(this.activeLearningStrategy.getSeriesParameters(instance, driftIndicators));

            return parameters;
        }
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        if (!this.collectTrackableParameters) return null;
        else {
            ArrayList<String> parameterNames = new ArrayList<>(super.getSeriesParameterNames());
            parameterNames.addAll(this.driftDetector.getSeriesParameterNames());

            return parameterNames;
        }
    }

}
