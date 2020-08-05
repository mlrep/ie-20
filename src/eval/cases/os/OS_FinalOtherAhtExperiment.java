package eval.cases.os;

import detect.DDM;
import detect.EDDM;
import eval.Evaluator;
import eval.experiment.Experiment;
import eval.experiment.ExperimentRow;
import eval.experiment.ExperimentStream;
import framework.BlindFramework;
import framework.InformedFramework;
import moa.classifiers.Classifier;
import moa.classifiers.meta.*;
import moa.classifiers.trees.HoeffdingAdaptiveTree;
import moa.options.ClassOption;
import strategies.al.UncertaintyStrategy;
import strategies.al.UncertaintyStrategyType;

import java.util.ArrayList;
import java.util.List;

public class OS_FinalOtherAhtExperiment extends Experiment {
    protected double budget;

    public OS_FinalOtherAhtExperiment(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    @Override
    public void run(Evaluator evaluator) {
        double[] budgets = new double[]{1.0, 0.5, 0.2, 0.1, 0.05, 0.01};

        for (int i = budgets.length - 1; i >= 0; i--) {
            this.budget = budgets[i];

            System.out.println("\nRunning for: " + this.budget);
            this.conduct(this.createExperimentRows(), ExperimentStream.createExperimentStreams(this.inputDir), evaluator, this.outputDir + "/" + this.budget);
        }
    }

    @Override
    public List<ExperimentRow> createExperimentRows() {
        Classifier cls = new HoeffdingAdaptiveTree();
        List<ExperimentRow> rows = new ArrayList<>();

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RANDOM, this.budget)
                ).setCollectTrackableParameters(true),
                "OS", "RAND-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget).setVariableThresholdStep(0.01)
                ).setCollectTrackableParameters(true),
                "OS", "RANDVAR-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.SAMPLING, this.budget)
                ).setCollectTrackableParameters(true),
                "OS", "SAMP-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new AccuracyUpdatedEnsemble(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "AUC-" + this.budget
        ));

        LearnNSE lnse = new LearnNSE();
        lnse.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingTree");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        new LearnNSE(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "LNSE-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new OzaBoostAdwin(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "OBA-" + this.budget
        ));

        DynamicWeightedMajority dwm = new DynamicWeightedMajority();
        dwm.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingAdaptiveTree");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        dwm,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "DWM-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new ADOB(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "ADOB-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new LeveragingBag(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "LB-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new BlindFramework(
                        new OzaBagAdwin(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "BAG-" + this.budget
        ));

        rows.add(new ExperimentRow(
                new InformedFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new DDM(),
                        true
                ).setCollectTrackableParameters(false),
                "OS", "DDM" + this.budget
        ));

        rows.add(new ExperimentRow(
                new InformedFramework(
                        cls.copy(),
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget),
                        new EDDM(),
                        true
                ).setCollectTrackableParameters(false),
                "OS", "EDDM" + this.budget
        ));

        return rows;
    }

}
