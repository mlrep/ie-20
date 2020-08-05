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
import moa.classifiers.meta.*;
import moa.classifiers.trees.HoeffdingAdaptiveTree;
import moa.options.ClassOption;
import strategies.al.UncertaintyStrategy;
import strategies.al.UncertaintyStrategyType;
import strategies.os.SMOTEStrategy;
import strategies.os.SingleExpositionStrategy;
import strategies.os.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class ALI_BudgetsExperiment extends Experiment {

    protected double budget;
    protected int windowSize;
    protected int generationsNum;

    public ALI_BudgetsExperiment(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    @Override
    public void run(Evaluator evaluator) {
        double[] budgets = new double[]{1.0, 0.5, 0.2, 0.1, 0.05, 0.01, 0.005, 0.001};
        int[] windowSizes = new int[]{1000, 500, 200, 100, 50, 10, 10, 10};
        int[] generationsNums = new int[]{100, 100, 100, 100, 100, 100, 100, 100};

        for (int i = 0; i < budgets.length; i++) {
            this.budget = budgets[i];
            this.windowSize = windowSizes[i];
            this.generationsNum = generationsNums[i];

            System.out.println("Running for: " + this.budget);
            this.conduct(this.createExperimentRows(), ExperimentStream.createExperimentStreams(this.inputDir), evaluator, this.outputDir + "/" + this.budget);
        }
    }

    @Override
    public List<ExperimentRow> createExperimentRows() {
        Classifier cls = ExperimentRow.getExperimentClassifier();
        List<ExperimentRow> rows = new ArrayList<>();

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RANDOM, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "RAND-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget).setVariableThresholdStep(0.01)
                ).setCollectTrackableParameters(false),
                "ALI", "RANDVAR-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.SAMPLING, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "SAMP-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SingleExpositionStrategy(GenerationsNumStrategy.RATIO_DRIVEN)
                                .setGenerationsNum(generationsNum)
                                .setWindowSize(windowSize),
                        new WindowedErrorIndicator(windowSize)
                ).setCollectTrackableParameters(false),
                "ALI", "BSE_RATIO-" + this.budget
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
                ).setCollectTrackableParameters(false),
                "ALI", "BSE_HYBRID-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SMOTEStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD, NeighborsStrategyType.FIXED,
                                GenerationsNumStrategy.RATIO_DRIVEN, GenerationStrategyType.LINE)
                                .setLuRatioThreshold(1.0)
                                .setGenerationsNum(generationsNum),
                        new WindowedErrorIndicator(windowSize)
                ).setCollectTrackableParameters(false),
                "ALI", "BSMOTE_RATIO-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new OversamplingFramework(
                        new OversamplingClassifier(cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget),
                        new SMOTEStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD, NeighborsStrategyType.FIXED,
                                GenerationsNumStrategy.HYBRID_RATIO_ERROR, GenerationStrategyType.LINE)
                                .setLuRatioThreshold(1.0)
                                .setGenerationsNum(generationsNum)
                                .setHybridCoefficients(0.5, 0.5)
                                .setWindowSize(this.windowSize),
                        new MulticlassPerformanceIndicator(windowSize, PerformanceType.GMEAN)
                ).setCollectTrackableParameters(false),
                "ALI", "BSMOTE_HYBRID-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new MulticlassBalancingBagging(10, BalancingType.OVERSAMPLING, cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "OMBB-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new MulticlassBalancingBagging(10, BalancingType.UNDERSAMPLING, cls.copy()),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "UMBB-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new OzaBagASHT(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "OZABAG-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new LeveragingBag(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "LB-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new AdaptiveRandomForest(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "ARF-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new HoeffdingAdaptiveTree(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "AHT-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new OzaBoostAdwin(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "OB_ADWIN-" + this.budget
        ));

        DynamicWeightedMajority dwm = new DynamicWeightedMajority();
        dwm.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingTree");

        rows.add(new ExperimentRow(
                new BlindFramework(
                        dwm,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, budget)
                ).setCollectTrackableParameters(false),
                "ALI", "DWM-" + this.budget
        ));

        return rows;
    }
}
