package clust.kmeans;

import clust.ClusterUpdates;
import clust.gauss.SphericalGaussCluster;
import com.yahoo.labs.samoa.instances.Instance;
import eval.experiment.ExperimentStream;
import moa.streams.ArffFileStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForgettingKMeansTest {
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
        ForgettingKMeans fkm = new ForgettingKMeans(2, new SphericalGaussCluster(0.95));
        ClusterUpdates update = fkm.update(test2DNumericInstances.get(0), true, 0);
        assertEquals(1, fkm.getClusters().size());
        assertEquals(0, update.created.get(0));

        update = fkm.update(test2DNumericInstances.get(1), true, 1);
        assertEquals(2, fkm.getClusters().size());
        assertEquals(1, update.created.get(0));

        update = fkm.update(test2DNumericInstances.get(2), true, 2);
        assertEquals(2, fkm.getClusters().size());
        assertEquals(0, update.created.size());
        assertEquals(1, update.addedTo.get(0));

        for (int i = 3; i < 16; i++) fkm.update(test2DNumericInstances.get(i), true, i);
        assertEquals(2, fkm.getClusters().size());
    }

}
