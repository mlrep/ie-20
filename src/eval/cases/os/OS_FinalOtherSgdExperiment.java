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
import moa.classifiers.functions.SGDMultiClass;
import moa.classifiers.meta.*;
import moa.options.ClassOption;
import strategies.al.UncertaintyStrategy;
import strategies.al.UncertaintyStrategyType;

import java.util.ArrayList;
import java.util.List;

public class OS_FinalOtherSgdExperiment extends Experiment {
    protected double budget;

    public OS_FinalOtherSgdExperiment(String inputDir, String outputDir) {
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
        Classifier cls = new SGDMultiClass();
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

        AccuracyWeightedEnsemble awe = new AccuracyWeightedEnsemble();
        awe.learnerOption = new ClassOption("learner", 'l', "Classifier to train.", Classifier.class, "functions.SGDMultiClass");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        awe,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "AWE-" + this.budget
        ));

        LearnNSE lnse = new LearnNSE();
        lnse.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "functions.SGDMultiClass");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        lnse,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "LNSE-" + this.budget
        ));

        OzaBoostAdwin oba = new OzaBoostAdwin();
        oba.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "functions.SGDMultiClass");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        oba,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "OBA-" + this.budget
        ));

        DynamicWeightedMajority dwm = new DynamicWeightedMajority();
        dwm.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "functions.SGDMultiClass");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        dwm,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "DWM-" + this.budget
        ));

        ADOB adob = new ADOB();
        adob.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.",
                Classifier.class, "drift.SingleClassifierDrift -l functions.SGDMultiClass -d ADWINChangeDetector");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        adob,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "ADOB-" + this.budget
        ));

        LeveragingBag lb = new LeveragingBag();
        lb.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "functions.SGDMultiClass");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        lb,
                        new UncertaintyStrategy(UncertaintyStrategyType.RAND_VARIABLE, this.budget)
                ).setCollectTrackableParameters(false),
                "OS", "LB-" + this.budget
        ));

        OzaBagAdwin bag = new OzaBagAdwin();
        bag.baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "functions.SGDMultiClass");
        rows.add(new ExperimentRow(
                new BlindFramework(
                        bag,
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
