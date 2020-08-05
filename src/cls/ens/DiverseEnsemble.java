package cls.ens;

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.DoubleVector;
import moa.core.Measurement;
import moa.core.Utils;
import output.writer.OutputWriter;
import strategies.divers.DiversityMeasure;
import utils.Trackable;
import utils.windows.WindowedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DiverseEnsemble extends AbstractClassifier implements Trackable {

    private Classifier ensemble;
    private int ensembleSize;
    private DiversityMeasure diversityMeasure = new DiversityMeasure(WindowedValue.WINDOW_ESTIMATOR_WIDTH, 0.2);

    public DiverseEnsemble(Classifier ensemble) {
        this.ensemble = ensemble;
        this.ensemble.prepareForUse();
        this.ensemble.resetLearning();
        this.ensembleSize = this.ensemble.getSubClassifiers().length;

        for (int i = 0; i < this.ensembleSize; i++) {
            this.diversityMeasure.addClassifierMeasure();
        }

        this.classifierRandom = new Random();
    }


    @Override
    public double[] getVotesForInstance(Instance instance) {
        ArrayList<Integer> lastVotes = new ArrayList<>();

        for (Classifier classifier : ensemble.getSubClassifiers()) {
            DoubleVector vote = new DoubleVector(classifier.getVotesForInstance(instance));
            if (vote.sumOfValues() > 0.0) {
                vote.normalize();
                lastVotes.add(Utils.maxIndex(vote.getArrayRef()));
            }
        }

        assert this.ensembleSize == lastVotes.size();
        this.diversityMeasure.update(lastVotes, (int)instance.classValue());

        return this.ensemble.getVotesForInstance(instance);
    }

    @Override
    public void prepareForUse() {
        this.ensemble.prepareForUse();
    }

    @Override
    public void resetLearningImpl() {
        this.ensemble.resetLearning();
        this.diversityMeasure.reset();
        for (int i = 0; i < this.ensembleSize; i++) {
            this.diversityMeasure.addClassifierMeasure();
        }
    }

    @Override
    public void trainOnInstanceImpl(Instance instance) {
        this.ensemble.trainOnInstance(instance.copy());
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        return new Measurement[0];
    }

    @Override
    public void getModelDescription(StringBuilder stringBuilder, int i) {}

    @Override
    public boolean isRandomizable() {
        return false;
    }

    @Override
    public HashMap<String, Double> getSeriesParameters(Instance instance, HashMap<String, Double> driftIndicators) {
        return this.diversityMeasure.getMeasures();
    }

    @Override
    public ArrayList<String> getSeriesParameterNames() {
        return new ArrayList<>(this.diversityMeasure.getMeasures().keySet());
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
