package strategies.os;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import eval.experiment.ExperimentStream;
import moa.streams.ArffFileStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import strategies.os.strategies.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SMOTEKernelStrategyTest {

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
        SMOTEKernelStrategy smote = (SMOTEKernelStrategy) new SMOTEKernelStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD,
                NeighborsStrategyType.FIXED_UNIFORM, GenerationsNumStrategy.FIXED, GenerationStrategyType.LINE).setWindowSize(10);
        for (Instance instance : test2DNumericInstances) smote.updateLabeled(instance, null);

        assertEquals(10, smote.commonWindowInstances.getWindowLength());
        assertEquals(2, smote.nnWindowed.get(0).getInstancesNum());
        assertEquals(8, smote.nnWindowed.get(1).getInstancesNum());

        smote = (SMOTEKernelStrategy) new SMOTEKernelStrategy(WindowSizeStrategy.ERROR_DRIVEN, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD,
                NeighborsStrategyType.FIXED_UNIFORM, GenerationsNumStrategy.FIXED, GenerationStrategyType.LINE).setWindowSize(3);
        Instance firstClassInstance = test2DNumericInstances.get(0);
        Instance secondClassInstance = test2DNumericInstances.get(8);

        smote.updateLabeled(firstClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        smote.updateLabeled(firstClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        smote.updateLabeled(firstClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        smote.updateLabeled(secondClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        assertEquals(3, smote.commonWindowInstances.getWindowLength());
        assertEquals(2, smote.nnWindowed.get(0).getInstancesNum());
        assertEquals(1, smote.nnWindowed.get(1).getInstancesNum());

        smote.updateLabeled(firstClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.5))));
        assertEquals(1, smote.commonWindowInstances.getWindowLength());
        assertEquals(1, smote.nnWindowed.get(0).getInstancesNum());
        assertEquals(0, smote.nnWindowed.get(1).getInstancesNum());

        smote.updateLabeled(firstClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.1))));
        smote.updateLabeled(firstClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.1))));
        assertEquals(2, smote.commonWindowInstances.getWindowLength());
        assertEquals(2, smote.nnWindowed.get(0).getInstancesNum());
        assertEquals(0, smote.nnWindowed.get(1).getInstancesNum());
    }

    @Test
    void generateInstances() {
        SMOTEKernelStrategy smote = (SMOTEKernelStrategy) new SMOTEKernelStrategy(WindowSizeStrategy.FIXED, UnlabeledControlStrategyType.FIXED_RATIO_THRESHOLD,
                NeighborsStrategyType.FIXED_UNIFORM, GenerationsNumStrategy.FIXED, GenerationStrategyType.LINE)
                .setFixedNeighborsNum(5).setGenerationsNum(10).setWindowSize(10);
        for (Instance instance : test2DNumericInstances) smote.updateLabeled(instance, new HashMap<>(Map.ofEntries(entry("error", 0.0))));

        Instance secondClassInstance = test2DNumericInstances.get(13);
        assertEquals(1.0, secondClassInstance.classValue());
        Instances generatedInstances = smote.generateInstances(secondClassInstance, new HashMap<>(Map.ofEntries(entry("error", 0.0))));
        assertEquals(50, generatedInstances.numInstances());
    }

}
