package eval.evaluators;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import eval.Evaluator;
import eval.experiment.ExperimentResult;
import eval.experiment.ExperimentRow;
import eval.experiment.ExperimentStream;
import framework.Framework;
import framework.FrameworkUpdate;
import moa.core.InstanceExample;
import moa.evaluation.WindowClassificationPerformanceEvaluator;
import moa.streams.ArffFileStream;
import utils.eval.AdwinPerformanceEvaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class WindowsEvaluator implements Evaluator {

    public ExperimentResult evaluate(ExperimentRow experimentRow, ExperimentStream experimentStream) {
        System.out.print("\n" + new Date() + ": [" + experimentStream.streamName + " & " + experimentRow.label +
                (experimentRow.subLabel.isEmpty() ? "" : "#" + experimentRow.subLabel) + "] ");

        ExperimentResult result = new ExperimentResult(experimentRow.label, experimentRow.subLabel);

        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES, new ArrayList<>());

        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_10, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_100, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_1000, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_10000, new ArrayList<>());

        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_ADWIN, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_ADWIN_001, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_ADWIN_005, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_ADWIN_01, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.ACCURACY_SERIES_ADWIN_02, new ArrayList<>());

        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_10, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_100, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_1000, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_10000, new ArrayList<>());

        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_ADWIN, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_ADWIN_001, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_ADWIN_005, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_ADWIN_01, new ArrayList<>());
        result.seriesMeasurements.put(ExperimentResult.KAPPA_SERIES_ADWIN_02, new ArrayList<>());

        ArffFileStream stream = experimentStream.stream;
        Framework framework = experimentRow.framework.setInitInstances(
                experimentRow.framework.omitInit ? 0 : (int)(Framework.INIT_INSTANCES * experimentStream.streamSize)
        );
        framework.classifier.setModelContext(stream.getHeader());

        if (!framework.ready) {
            System.out.println("Framework is not prepared, you need to reset it before using");
            return result;
        }

        AdwinPerformanceEvaluator windowMeasurements = new AdwinPerformanceEvaluator(0.0002);

        AdwinPerformanceEvaluator windowMeasurementsAdwin = new AdwinPerformanceEvaluator(0.002);
        AdwinPerformanceEvaluator windowMeasurementsAdwin001 = new AdwinPerformanceEvaluator(0.01);
        AdwinPerformanceEvaluator windowMeasurementsAdwin005 = new AdwinPerformanceEvaluator(0.05);
        AdwinPerformanceEvaluator windowMeasurementsAdwin01 = new AdwinPerformanceEvaluator(0.1);
        AdwinPerformanceEvaluator windowMeasurementsAdwin02 = new AdwinPerformanceEvaluator(0.2);

        WindowClassificationPerformanceEvaluator windowMeasurements10 = new WindowClassificationPerformanceEvaluator();
        windowMeasurements10.widthOption = new IntOption("width", 'w', "Size of Window", 10);
        WindowClassificationPerformanceEvaluator windowMeasurements100 = new WindowClassificationPerformanceEvaluator();
        windowMeasurements100.widthOption = new IntOption("width", 'w', "Size of Window", 100);
        WindowClassificationPerformanceEvaluator windowMeasurements1000 = new WindowClassificationPerformanceEvaluator();
        windowMeasurements1000.widthOption = new IntOption("width", 'w', "Size of Window", 1000);
        WindowClassificationPerformanceEvaluator windowMeasurements10000 = new WindowClassificationPerformanceEvaluator();
        windowMeasurements10000.widthOption = new IntOption("width", 'w', "Size of Window", 10000);

        if (framework.collectTrackableParameters) {
            for (String key : framework.getSeriesParameterNames()) {
                result.seriesMeasurements.put(key, new ArrayList<>());
            }
        }

        int log = (int)(experimentStream.streamSize / 10.0);
        int i = 0;

        while (stream.hasMoreInstances()) {
            Instance instance = stream.nextInstance().getData();
            if ((i % log) == 0) System.out.print("#");

            double[] votes = framework.classifier.getVotesForInstance(instance);
            FrameworkUpdate update = framework.update(instance.copy(), votes, i);

            windowMeasurements.addResult(new InstanceExample(instance), votes);

            if (update.labeled) {
                windowMeasurements10.addResult(new InstanceExample(instance), votes);
                windowMeasurements100.addResult(new InstanceExample(instance), votes);
                windowMeasurements1000.addResult(new InstanceExample(instance), votes);
                windowMeasurements10000.addResult(new InstanceExample(instance), votes);

                windowMeasurementsAdwin.addResult(new InstanceExample(instance), votes);
                windowMeasurementsAdwin001.addResult(new InstanceExample(instance), votes);
                windowMeasurementsAdwin005.addResult(new InstanceExample(instance), votes);
                windowMeasurementsAdwin01.addResult(new InstanceExample(instance), votes);
                windowMeasurementsAdwin02.addResult(new InstanceExample(instance), votes);
            }

            if (framework.activeLearningStrategy == null || i >= framework.initInstances) {

                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES).add(windowMeasurements.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_10).add(windowMeasurements10.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_100).add(windowMeasurements100.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_1000).add(windowMeasurements1000.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_10000).add(windowMeasurements10000.getFractionCorrectlyClassified());

                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_ADWIN).add(windowMeasurementsAdwin.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_ADWIN_001).add(windowMeasurementsAdwin001.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_ADWIN_005).add(windowMeasurementsAdwin005.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_ADWIN_01).add(windowMeasurementsAdwin01.getFractionCorrectlyClassified());
                result.seriesMeasurements.get(ExperimentResult.ACCURACY_SERIES_ADWIN_02).add(windowMeasurementsAdwin02.getFractionCorrectlyClassified());

                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES).add(windowMeasurements.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_10).add(windowMeasurements10.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_100).add(windowMeasurements100.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_1000).add(windowMeasurements1000.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_10000).add(windowMeasurements10000.getKappaStatistic());

                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_ADWIN).add(windowMeasurementsAdwin.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_ADWIN_001).add(windowMeasurementsAdwin001.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_ADWIN_005).add(windowMeasurementsAdwin005.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_ADWIN_01).add(windowMeasurementsAdwin01.getKappaStatistic());
                result.seriesMeasurements.get(ExperimentResult.KAPPA_SERIES_ADWIN_02).add(windowMeasurementsAdwin02.getKappaStatistic());

                if (framework.collectTrackableParameters) {
                    for (Map.Entry<String, Double> entry : framework.getSeriesParameters(instance).entrySet()) {
                        result.seriesMeasurements.get(entry.getKey()).add(entry.getValue());
                    }
                }
            }

            i++;
        }

        result.queriesFactor = (double) framework.activeLearningStrategy.labeledInstances / i;
        System.out.print(" Queried: " + result.queriesFactor);
        framework.ready = false;

        return result;
    }
}
