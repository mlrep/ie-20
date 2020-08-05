package eval.cases.os;

import cls.OversamplingClassifier;
import cls.OversamplingEnsemble;
import detect.AdwinErrorIndicator;
import detect.DriftDetectionMethod;
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

public class OS_EnsembleSignificanceExperiment extends Experiment {

    protected double budget;
    protected int windowSize;
    protected int generationsNum;
    protected double delta;

    private WindowSizeStrategy windowSizeStrategy;
    private GenerationsNumStrategy generationsNumStrategy;

    public OS_EnsembleSignificanceExperiment(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    @Override
    public void run(Evaluator evaluator) {
        double[] budgets = new double[]{1.0, 0.5, 0.2, 0.1, 0.05, 0.01};
        double[] deltas = new double[]{0.002, 0.05, 0.1, 0.1, 0.2, 0.2};
        this.windowSizeStrategy = WindowSizeStrategy.ADWIN_WIDTH;

        // AHT
        this.generationsNumStrategy = GenerationsNumStrategy.ERROR_DRIVEN;
        int[] generationsNums = new int[]{1, 1, 1, 10, 10, 10};

        // SGD
        //this.generationsNumStrategy = GenerationsNumStrategy.ERROR_DRIVEN;
        //int[] generationsNums = new int[]{100, 100, 100, 100, 100, 100};

        for (int i = budgets.length - 1; i >= 0; i--) {
            this.budget = budgets[i];
            this.generationsNum = generationsNums[i];
            this.delta = deltas[i];

            System.out.println("\nRunning for: " + this.budget);
            this.conduct(this.createExperimentRows(), ExperimentStream.createExperimentStreams(this.inputDir), evaluator, this.outputDir + "/" + this.budget);
        }
    }

    @Override
    public List<ExperimentRow> createExperimentRows() {
        Classifier cls = ExperimentRow.getExperimentClassifier();
        List<ExperimentRow> rows = new ArrayList<>();

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE+switch-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.01)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE+elev0.01-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.05)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE+elev0.05-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.1)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE+elev0.1-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.15)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE+elev0.15-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.2)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new SingleExpositionStrategy(this.generationsNumStrategy)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "SE+elev0.2-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW+switch-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.01)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW+elev0.01-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.05)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW+elev0.05-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.1)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW+elev0.1-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.15)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW+elev0.15-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingEnsemble(cls.copy())
                                .setAlpha(0.2)
                                .setElevating(true)
                                .setErrorIndicator(new AdwinErrorIndicator(this.delta))
                                .setCollectReal(true),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new ProbabilisticWindowStrategy(this.windowSizeStrategy, this.generationsNumStrategy, ProbabilisticStrategyType.EXPONENTIAL)
                                .setGenerationsNum(this.generationsNum),
                        new AdwinErrorIndicator(this.delta)
                ).setCollectTrackableParameters(true),
                "OS", "EW+elev0.2-" + this.budget
        ));

        return rows;
    }

}
