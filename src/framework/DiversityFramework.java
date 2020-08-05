package framework;

import com.yahoo.labs.samoa.instances.Instances;
import moa.core.Utils;
import strategies.al.ActiveLearningStrategy;
import cls.ens.SimpleEnsemble;
import com.yahoo.labs.samoa.instances.Instance;
import detect.DriftDetectionMethod;
import detect.StreamStateType;
import strategies.divers.DiversityStrategy;
import moa.classifiers.Classifier;
import strategies.os.OversamplingStrategy;
import utils.Trackable;
import utils.TrackableFramework;

import java.util.ArrayList;
import java.util.HashMap;

public class DiversityFramework extends Framework implements TrackableFramework {

    private DiversityStrategy diversityStrategy;
    private DriftDetectionMethod driftDetector;
    private OversamplingStrategy oversamplingStrategy;
    private boolean oversamplingUpdateFirst = false;

    public DiversityFramework(Classifier classifier, ActiveLearningStrategy activeLearningStrategy, DiversityStrategy diversityStrategy,
                       DriftDetectionMethod driftDetector) {
        this.classifier = classifier;
        this.classifier.prepareForUse();
        this.activeLearningStrategy = activeLearningStrategy;
        this.diversityStrategy = diversityStrategy;
        this.driftDetector = driftDetector;
    }

    public DiversityFramework(Classifier classifier, ActiveLearningStrategy activeLearningStrategy, DiversityStrategy diversityStrategy,
                              DriftDetectionMethod driftDetector, OversamplingStrategy oversamplingStrategy) {
        this.classifier = classifier;
        this.classifier.prepareForUse();
        this.activeLearningStrategy = activeLearningStrategy;
        this.diversityStrategy = diversityStrategy;
        this.driftDetector = driftDetector;
        this.oversamplingStrategy = oversamplingStrategy;
        this.oversamplingUpdateFirst = oversamplingStrategy.getUpdateFirst();
    }

    @Override
    public void reset() {
        this.classifier.resetLearning();
        this.diversityStrategy.reset();
        this.driftDetector.reset();
        this.ready = true;
    }

    @Override
    public FrameworkUpdate update(Instance instance, double[] votes, int n) {
        HashMap<String, Double> driftIndicators = this.driftDetector.getDetectorIndicators();

        if (!this.omitInit && n <= this.initInstances) {
            this.diversityStrategy.update(instance, (SimpleEnsemble)this.classifier, driftIndicators); // todo: max val?
            this.diversityStrategy.diversify(instance, (SimpleEnsemble)this.classifier);
            this.activeLearningStrategy.update(instance, true);
            this.driftDetector.update(Utils.maxIndex(votes), (int)instance.classValue(), instance.numClasses());
            if (this.oversamplingStrategy != null) this.oversamplingStrategy.updateLabeled(instance, driftIndicators);

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

            this.diversityStrategy.update(instance, (SimpleEnsemble)this.classifier, driftIndicators);

            if (this.oversamplingStrategy != null) {
                if (this.oversamplingUpdateFirst) this.oversamplingStrategy.updateLabeled(instance, driftIndicators);

                Instances generatedInstances = this.oversamplingStrategy.generateInstances(instance, driftIndicators);
                if (generatedInstances != null) {
                    for (int i = 0; i < generatedInstances.numInstances(); i++) {
                        this.diversityStrategy.diversify(generatedInstances.get(i), (SimpleEnsemble)this.classifier);
                    }
                }

                if (!this.oversamplingUpdateFirst) this.oversamplingStrategy.updateLabeled(instance, driftIndicators);

            } else {
                this.diversityStrategy.diversify(instance, (SimpleEnsemble)this.classifier);
            }
        }

        return new FrameworkUpdate(learnActively);
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance) {
        if (!this.collectTrackableParameters) return null;
        else {
            HashMap<String, Double> parameters = new HashMap<>();
            HashMap<String, Double> driftIndicators = this.driftDetector.getDetectorIndicators();
            parameters.putAll(this.diversityStrategy.getSeriesParameters(instance, driftIndicators));
            parameters.putAll(((Trackable) this.classifier).getSeriesParameters(instance, driftIndicators));
            parameters.putAll(this.activeLearningStrategy.getSeriesParameters(instance, driftIndicators));

            return parameters;
        }
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        if (!this.collectTrackableParameters) return null;
        else {
            ArrayList<String> parameterNames = new ArrayList<>(this.diversityStrategy.getSeriesParameterNames());
            if (this.classifier instanceof Trackable)
                parameterNames.addAll(((Trackable) this.classifier).getSeriesParameterNames());
            parameterNames.addAll(this.activeLearningStrategy.getSeriesParameterNames());

            return parameterNames;
        }
    }

    @Override
    public HashMap<String, Double> getAggregateParameters() {
        HashMap<String, Double> parameters = new HashMap<>();
        if (!this.collectTrackableParameters) return parameters;

        parameters.putAll(this.diversityStrategy.getAggregateParameters());
        parameters.putAll(((Trackable) this.classifier).getAggregateParameters());
        parameters.putAll(this.activeLearningStrategy.getAggregateParameters());

        return parameters;
    }

}
