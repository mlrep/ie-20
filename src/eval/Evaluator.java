package eval;

import eval.cases.os.*;
import eval.evaluators.BalancedEvaluator;
import eval.experiment.ExperimentResult;
import eval.experiment.ExperimentRow;
import eval.experiment.ExperimentStream;

import java.util.*;

public interface Evaluator {

    static void runInstanceExploitationExperiments(String inputDir, String rootOutputDir) {
        Evaluator evaluator = new BalancedEvaluator();

        // Synthetic streams
        // Run it for B = {0.5, 0.2, 0.1, 0.05, 0.01}
        (new OS_BaselineExperiment(inputDir, rootOutputDir + "/baseline/0.1")).run(evaluator);
        (new OS_WindowsExperiment(inputDir, rootOutputDir + "/windows/0.1")).run(evaluator);
        (new OS_AdaptiveWindowsExperiment(inputDir, rootOutputDir + "/windows-adapt/0.1")).run(evaluator);
        (new OS_GenerationsNumExperiment(inputDir, rootOutputDir + "/gens/0.1")).run(evaluator);
        (new OS_AdaptiveGenerationsNumExperiment(inputDir, rootOutputDir + "/gens-adapt/0.1")).run(evaluator);

        (new OS_EnsembleSignificanceExperiment(inputDir, rootOutputDir + "/ens-sign-aht")).run(evaluator); // change configuration for SGD

        // Real streams
        //(new OS_FinalExperiment(inputDir, rootOutputDir + "/final-aht")).run(evaluator); // change configuration for SGD

        // For AHT (other classifiers)
        //(new OS_FinalOtherAhtExperiment(inputDir, rootOutputDir + "/final-other-aht")).run(evaluator);

        // For SGD (other classifiers)
        //(new OS_FinalOtherSgdExperiment(inputDir, rootOutputDir + "/final-other-sgd")).run(evaluator);
    }

    ExperimentResult evaluate(ExperimentRow experimentRow, ExperimentStream experimentStream);

    static void main(String[] args) {
        System.out.println("Starting for AHT: " + new Date());

        Evaluator.runInstanceExploitationExperiments("streams", "results/aht");

        System.out.println("\nFinished: " + new Date());
        System.exit(0);

    }

}
