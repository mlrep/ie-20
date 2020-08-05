package strategies.os;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import eval.experiment.ExperimentStream;
import moa.streams.ArffFileStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import strategies.os.strategies.GenerationsNumStrategy;
import strategies.os.strategies.ProbabilisticStrategyType;
import strategies.os.strategies.WindowSizeStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

class ProbabilisticWindowStrategyTest {

    private static List<Instance> test2DNumericInstances = new ArrayList<>();

    @BeforeAll
    static void loadStreams() {
        ExperimentStream test2DNumericStream = new ExperimentStream(new ArffFileStream("tests/data/test2Dnumeric.arff", 3),
                "TEST2D", 16, 1);
        while (test2DNumericStream.stream.hasMoreInstances()) {
            test2DNumericInstances.add(test2DNumericStream.stream.nextInstance().getData());
        }
    }

    @Test
    void updateLabeled() {
        ProbabilisticWindowStrategy probWindow = (ProbabilisticWindowStrategy) new ProbabilisticWindowStrategy(WindowSizeStrategy.FIXED,
                GenerationsNumStrategy.FIXED, ProbabilisticStrategyType.UNIFORM).setWindowSize(10);
        assertEquals(0, probWindow.getNumOfInstances());

        probWindow.updateLabeled(test2DNumericInstances.get(0), null);
        probWindow.updateLabeled(test2DNumericInstances.get(1), null);
        assertEquals(2, probWindow.getNumOfInstances());

        for (Instance instance : test2DNumericInstances) probWindow.updateLabeled(instance, null);
        assertEquals(10, probWindow.getNumOfInstances());

        probWindow = (ProbabilisticWindowStrategy) new ProbabilisticWindowStrategy(WindowSizeStrategy.ERROR_DRIVEN,
                GenerationsNumStrategy.FIXED, ProbabilisticStrategyType.UNIFORM).setWindowSize(5);

        probWindow.updateLabeled(test2DNumericInstances.get(0), new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        probWindow.updateLabeled(test2DNumericInstances.get(1), new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        probWindow.updateLabeled(test2DNumericInstances.get(2), new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        assertEquals(3, probWindow.getNumOfInstances());

        probWindow.updateLabeled(test2DNumericInstances.get(0), new HashMap<>(Map.ofEntries(entry("error", 0.5))));
        assertEquals(2, probWindow.getNumOfInstances());

        probWindow.updateLabeled(test2DNumericInstances.get(1), new HashMap<>(Map.ofEntries(entry("error", 0.8))));
        assertEquals(1, probWindow.getNumOfInstances());

        probWindow.updateLabeled(test2DNumericInstances.get(2), new HashMap<>(Map.ofEntries(entry("error", 0.2))));
        probWindow.updateLabeled(test2DNumericInstances.get(3), new HashMap<>(Map.ofEntries(entry("error", 0.2))));
        probWindow.updateLabeled(test2DNumericInstances.get(4), new HashMap<>(Map.ofEntries(entry("error", 0.2))));
        probWindow.updateLabeled(test2DNumericInstances.get(5), new HashMap<>(Map.ofEntries(entry("error", 0.2))));
        assertEquals(4, probWindow.getNumOfInstances());
    }

    @Test
    void generateInstances() {
        ProbabilisticWindowStrategy probWindow = (ProbabilisticWindowStrategy) new ProbabilisticWindowStrategy(WindowSizeStrategy.FIXED,
                GenerationsNumStrategy.FIXED, ProbabilisticStrategyType.EXPONENTIAL).setGenerationsNum(10).setWindowSize(10);
        probWindow.updateLabeled(test2DNumericInstances.get(0), null);

        Instances generatedInstances = probWindow.generateInstances(test2DNumericInstances.get(0), null);
        assertEquals(10, generatedInstances.numInstances());
        for (int i = 0; i < 10; i++) {
            assertEquals(generatedInstances.get(i).value(0), test2DNumericInstances.get(0).value(0));
            assertEquals(generatedInstances.get(i).value(1), test2DNumericInstances.get(0).value(1));
        }

        for (Instance instance : test2DNumericInstances) probWindow.updateLabeled(instance, null);
        generatedInstances = probWindow.generateInstances(test2DNumericInstances.get(0), null);
        assertEquals(10, generatedInstances.numInstances());

        probWindow = (ProbabilisticWindowStrategy) new ProbabilisticWindowStrategy(WindowSizeStrategy.FIXED,
                GenerationsNumStrategy.ERROR_DRIVEN, ProbabilisticStrategyType.EXPONENTIAL).setGenerationsNum(10).setWindowSize(10);
        for (Instance instance : test2DNumericInstances) probWindow.updateLabeled(instance, null);

        generatedInstances = probWindow.generateInstances(test2DNumericInstances.get(0), new HashMap<>(Map.ofEntries(entry("error", 0.5))));
        assertEquals(5, generatedInstances.numInstances());
    }
}