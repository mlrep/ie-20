package eval.cases.divers;

import cls.ens.DecisionScheme;
import strategies.al.UncertaintyStrategy;
import strategies.al.UncertaintyStrategyType;
import cls.ens.SimpleEnsemble;
import clust.kmeans.KMeans;
import detect.WindowedErrorIndicator;
import strategies.divers.ClusteringStrategy;
import eval.Evaluator;
import eval.experiment.Experiment;
import eval.experiment.ExperimentRow;
import eval.experiment.ExperimentStream;
import framework.DiversityFramework;
import moa.classifiers.Classifier;
import strategies.os.SingleExpositionStrategy;
import strategies.os.strategies.GenerationsNumStrategy;

import java.util.ArrayList;
import java.util.List;

public class DIV_EnhancingDiversityExperiment extends Experiment {

    public DIV_EnhancingDiversityExperiment(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    @Override
    public void run(Evaluator evaluator) {
        this.conduct(this.createExperimentRows(), ExperimentStream.createExperimentStreams(this.inputDir), evaluator, this.outputDir);
    }

    @Override
    public List<ExperimentRow> createExperimentRows() {
        Classifier cls = ExperimentRow.getExperimentClassifier();
        List<ExperimentRow> rows = new ArrayList<>();

        rows.add(new ExperimentRow(
                new DiversityFramework(
                        new SimpleEnsemble(cls.copy()).setDecisionScheme(DecisionScheme.WEIGHTED),
                        new UncertaintyStrategy(UncertaintyStrategyType.RANDOM, 0.1),
                        new ClusteringStrategy(new KMeans(10)).setBeta(0.0),
                        new WindowedErrorIndicator(100)
                ),
                "ENS", "CLUST"
        ));

        rows.add(new ExperimentRow(
                new DiversityFramework(
                        new SimpleEnsemble(cls.copy()).setDecisionScheme(DecisionScheme.WEIGHTED),
                        new UncertaintyStrategy(UncertaintyStrategyType.RANDOM, 0.1),
                        new ClusteringStrategy(new KMeans(10)).setBeta(0.0),
                        new WindowedErrorIndicator(100),
                        new SingleExpositionStrategy(GenerationsNumStrategy.FIXED)
                                .setGenerationsNum(20)
                                .setWindowSize(100)
                ),
                "ENS", "CLUST+SE"
        ));

        rows.add(new ExperimentRow(
                new DiversityFramework(
                        new SimpleEnsemble(cls.copy()).setDecisionScheme(DecisionScheme.WEIGHTED),
                        new UncertaintyStrategy(UncertaintyStrategyType.RANDOM, 0.1),
                        new ClusteringStrategy(new KMeans(10)).setBeta(0.0),
                        new WindowedErrorIndicator(100),
                        new SingleExpositionStrategy(GenerationsNumStrategy.ERROR_DRIVEN_INV)
                                .setGenerationsNum(20)
                                .setWindowSize(100)
                ),
                "ENS", "CLUST+SERR"
        ));

        return rows;
    }
}
