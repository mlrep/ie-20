package eval.cases.os;

import cls.OversamplingClassifier;
import detect.AdwinErrorIndicator;
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
import strategies.os.SingleExpositionStrategy;
import strategies.os.strategies.GenerationsNumStrategy;
import strategies.os.strategies.ProbabilisticStrategyType;
import strategies.os.strategies.WindowSizeStrategy;

import java.util.ArrayList;
import java.util.List;

public class OS_AdaptiveGenerationsNumExperiment extends Experiment {

    private double budget = 0.1;
    private double delta = 0.1; // 0.05, 0.1, 0.1, 0.2, 0.2
    private WindowSizeStrategy windowSizeStrategy = WindowSizeStrategy.ADWIN_WIDTH;

    public OS_AdaptiveGenerationsNumExperiment(String inputDir, String outputDir) {
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
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.ERROR_DRIVEN)
                                .setGenerationsNum(1),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE-gerr1"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.ERROR_DRIVEN)
                                .setGenerationsNum(10),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE-gerr10"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.ERROR_DRIVEN)
                                .setGenerationsNum(100),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE-gerr100"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.ERROR_DRIVEN)
                                .setGenerationsNum(1000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE-gerr1000"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(1),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-gerr1"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(10),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-gerr10"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(100),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-gerr100"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(1000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-gerr1000"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(1),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-gerr1"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(10),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-gerr10"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(100),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-gerr100"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(1000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-gerr1000"
        ));

        return rows;
    }

}
