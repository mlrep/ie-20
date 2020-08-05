package eval.cases.imb;

import cls.ens.BalancingType;
import cls.ens.MulticlassBalancingBagging;
import cls.OversamplingClassifier;
import detect.MulticlassPerformanceIndicator;
import detect.PerformanceType;
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
import strategies.os.SMOTEStrategy;
import strategies.os.SingleExpositionStrategy;
import strategies.os.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class ALI_SimpleExperiment extends Experiment {

    public ALI_SimpleExperiment(String inputDir, String outputDir) {
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

        double budget = 0.05;
        int windowSize = 50;
        int generationsNum = 100;

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RANDOM, budget)
                ),
                "ALI", "RAND"
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget).setVariableThresholdStep(0.01)
                ),
                "ALI", "RANDVAR"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.RATIO_DRIVEN)
                                .setGenerationsNum(generationsNum)
                                .setWindowSize(windowSize),
                        new WindowedErrorIndicator(windowSize)
                ),
                "ALI", "BSE_RATIO"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.CLASS_ERROR_DRIVEN)
                                .setGenerationsNum(generationsNum)
                                .setWindowSize(windowSize),
                        new MulticlassPerformanceIndicator(windowSize, PerformanceType.GMEAN)
                ),
                "ALI", "BSE_CERR"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.HYBRID_RATIO_ERROR)
                                .setGenerationsNum(generationsNum)
                                .setHybridCoefficients(0.5, 0.5)
                                .setWindowSize(windowSize),
                        new MulticlassPerformanceIndicator(windowSize, PerformanceType.GMEAN)
                ),
                "ALI", "BSE_HYBRID"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SMOTEStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD, NeighborsStrategyType.FIXED,
                                GenerationsNumStrategy.RATIO_DRIVEN, GenerationStrategyType.LINE)
                                .setLuRatioThreshold(1.0)
                                .setFixedNeighborsNum(10)
                                .setGenerationsNum(generationsNum)
                                .setWindowSize(windowSize),
                        new WindowedErrorIndicator(windowSize)
                ),
                "ALI", "BSMOTE_RATIO"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SMOTEStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD, NeighborsStrategyType.FIXED,
                                GenerationsNumStrategy.CLASS_ERROR_DRIVEN, GenerationStrategyType.LINE)
                                .setLuRatioThreshold(1.0)
                                .setFixedNeighborsNum(10)
                                .setGenerationsNum(generationsNum)
                                .setWindowSize(windowSize),
                        new MulticlassPerformanceIndicator(windowSize, PerformanceType.GMEAN)
                ),
                "ALI", "BSMOTE_CERR"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SMOTEStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD, NeighborsStrategyType.FIXED,
                                GenerationsNumStrategy.HYBRID_RATIO_ERROR, GenerationStrategyType.LINE)
                                .setLuRatioThreshold(1.0)
                                .setFixedNeighborsNum(10)
                                .setGenerationsNum(generationsNum)
                                .setHybridCoefficients(0.5, 0.5)
                                .setWindowSize(windowSize),
                        new MulticlassPerformanceIndicator(windowSize, PerformanceType.GMEAN)
                ),
                "ALI", "BSMOTE_HYBRID"
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new MulticlassBalancingBagging(10, BalancingType.OVERSAMPLING, cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ),
                "ALI", "OMBB"
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new MulticlassBalancingBagging(10, BalancingType.UNDERSAMPLING, cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ),
                "ALI", "UMBB"
        ));

        return rows;
    }
}
