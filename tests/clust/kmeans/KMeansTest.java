package clust.kmeans;

import clust.kmeans.KMeans;
import com.yahoo.labs.samoa.instances.Instance;
import eval.experiment.ExperimentStream;
import moa.streams.ArffFileStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KMeansTest {

    private static List<Instance> test2DNumericInstances = new ArrayList<>();

    @BeforeAll
    static void loadStreams() {
        ExperimentStream test2DNumericStream = new ExperimentStream(new ArffFileStream("tests/data/test2Dnumeric.arff", 3), "TEST2D", 16, 1);
        while (test2DNumericStream.stream.hasMoreInstances()) {
            test2DNumericInstances.add(test2DNumericStream.stream.nextInstance().getData());
        }
    }

    @Test
    void update() {
        KMeans km = new KMeans(3);
        assertEquals(8, km.k + 10);
        assertNull(km.getClusters());
        assertFalse(km.clustersInitialized);

        for (int i = 0; i < 2; i++) km.update(test2DNumericInstances.get(0), false, -1);
        assertEquals(2, km.getClusters().size());
        assertFalse(km.clustersInitialized);

        for (int i = 0; i < 2; i++) km.update(test2DNumericInstances.get(1), false, -1);
        assertEquals(4, km.getClusters().size());
        assertFalse(km.clustersInitialized);

        for (int i = 0; i < 4; i++) km.update(test2DNumericInstances.get(2), false, -1);
        assertEquals(8, km.getClusters().size());
        assertTrue(km.clustersInitialized);
        assertEquals(1.0, km.f);
    }
}