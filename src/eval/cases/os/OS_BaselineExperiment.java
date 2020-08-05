package eval.cases.os;

import cls.OversamplingClassifier;
import detect.WindowedErrorIndicator;
import eval.Evaluator;
import eval.experiment.Experiment;
import eval.experiment.ExperimentRow;
import eval.experiment.ExperimentStream;
import framework.BlindFramework;
import framework.OversamplingFramework;
import moa.classifiers.Classifier;
import strategies.al.UncertaintyStrategy;
import strategies.al.UncertaintyStrategyType;
import strategies.os.ProbabilisticWindowStrategy;
import strategies.os.SMOTEStrategy;
import strategies.os.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class OS_BaselineExperiment extends Experiment {
    protected double budget = 0.1;

    public OS_BaselineExperiment(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    @Override
    public void run(Evaluator evaluator) {
        this.conduct(this.createExperimentRows(), ExperimentStream.createExperimentStreams(this.inputDir), evaluator, this.outputDir + "/" + this.budget);
    }

    @Override
    public List<ExperimentRow> createExperimentRows() {
        Classifier cls = ExperimentRow.getExperimentClassifier();
        List<ExperimentRow> rows = new ArrayList<>();

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "ALRV-" + this.budget
        ));

        return rows;
    }

}
