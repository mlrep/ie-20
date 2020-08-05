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
import strategies.os.SMOTEStrategy;
import strategies.os.SingleExpositionStrategy;
import strategies.os.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class OS_AdaptiveWindowsExperiment extends Experiment {

    private double budget = 0.1;
    private int generationsNum = 100;
    private double delta = 0.1; // 0.05, 0.1, 0.1, 0.2, 0.2

    public OS_AdaptiveWindowsExperiment(String inputDir, String outputDir) {
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
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(10),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-w10err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(100),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-w100err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(1000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-w1000err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(10000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-w10000err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ADWIN_WIDTH, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-adwin"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(10),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-w10err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(100),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-w100err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(1000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-w1000err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(this.generationsNum)
                                .setWindowSize(10000),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-w10000err"
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(WindowSizeStrategy.ADWIN_WIDTH, GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.UNIFORM)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "UW-adwin"
        ));

        return rows;
    }

}
