package cls;

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import output.writer.OutputWriter;
import utils.Trackable;

import java.util.ArrayList;
import java.util.HashMap;

public class OversamplingClassifier extends AbstractClassifier implements Trackable {

    protected Classifier classifier;

    private HashMap<String, Double> trackableParameters = new HashMap<>();

    public OversamplingClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public double[] getVotesForInstance(Instance instance) {
        return classifier.getVotesForInstance(instance);
    }

    @Override
    public void resetLearningImpl() {
        this.classifier.resetLearning();
    }

    @Override
    public void prepareForUse() {
        this.classifier.prepareForUse();
    }

    @Override
    public void trainOnInstanceImpl(Instance instance) {
        this.classifier.trainOnInstance(instance.copy());
    }

    public void trainOnSynthInstance(Instance instance) {
        this.classifier.trainOnInstance(instance.copy());
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() { return new Measurement[0];}

    @Override
    public void getModelDescription(StringBuilder stringBuilder, int i) {}

    @Override
    public boolean isRandomizable() { return false; }

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
