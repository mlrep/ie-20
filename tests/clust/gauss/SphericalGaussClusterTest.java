package clust.gauss;

import com.yahoo.labs.samoa.instances.Instance;
import eval.experiment.ExperimentStream;
import moa.streams.ArffFileStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class SphericalGaussClusterTest {

    private static List<Instance> test2DNumericInstances = new ArrayList<>();

    @BeforeAll
    static void loadStreams() {
        ExperimentStream test2DNumericStream = new ExperimentStream(new ArffFileStream("tests/data/test2Dnumeric.arff", 3), "TEST2D", 16, 1);
        while (test2DNumericStream.stream.hasMoreInstances()) {
            test2DNumericInstances.add(test2DNumericStream.stream.nextInstance().getData());
        }
    }

    @Test
    void init() {
        SphericalGaussCluster sgc = new SphericalGaussCluster(test2DNumericInstances.get(0), true, 0);
        assertEquals(0.0, sgc.getCentroid().value(0));
        assertEquals(1.0, sgc.getCentroid().value(1));
        assertEquals(0.0, sgc.getRadius().get(0));
        assertEquals(0, sgc.getClassIndex());

        assertEquals(0.0, sgc.getPrototypes(0).get(0).value(0));
        assertEquals(1.0, sgc.getPrototypes(0).get(0).value(1));
        assertEquals(0.0, sgc.getPrototypes(0).get(0).classValue());

        assertEquals(1.0, sgc.getPurityMeasures().get("purity"));
        assertTrue(sgc.isActive(0, -1));
    }

    @Test
    void update() {
        SphericalGaussCluster sgc = new SphericalGaussCluster(0.95, 10, test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 8; i++) sgc.update(test2DNumericInstances.get(i), true, i);
        assertEquals(1.0, sgc.getPurityMeasures().get("purity"));
        assertEquals(1.19, sgc.getCentroid().value(0), 0.01);
        assertEquals(1.10, sgc.getCentroid().value(1),0.01);
        assertEquals(1.59, sgc.getRadius().get(0), 0.01);
        assertTrue(sgc.isActive(8, -1));
        assertEquals(0, sgc.getClassIndex());

        for (int i = 8; i < 16; i++) sgc.update(test2DNumericInstances.get(i), true, i);
        assertEquals(0.60, sgc.getPurityMeasures().get("purity"), 0.01);
        assertEquals(-0.24, sgc.getCentroid().value(0), 0.01);
        assertEquals(-0.22, sgc.getCentroid().value(1), 0.01);
        assertTrue(sgc.isActive(16, -1));
        assertEquals(1, sgc.getClassIndex());

        sgc = new SphericalGaussCluster(0.95, 10, test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 101; i++) sgc.update(test2DNumericInstances.get(0), true, i);
        assertEquals(0.0, sgc.getCentroid().value(0));
        assertEquals(1.0, sgc.getCentroid().value(1));
        assertTrue(sgc.isActive(101, -1));
        assertEquals(0, sgc.getClassIndex());
    }

    @Test
    void merge() {
        SphericalGaussCluster sgc1 = new SphericalGaussCluster(1.0, 10, test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 8; i++) sgc1.update(test2DNumericInstances.get(i), true, i);
        SphericalGaussCluster sgc2 = new SphericalGaussCluster(1.0, 10, test2DNumericInstances.get(8), true, 8);
        for (int i = 9; i < 16; i++) sgc2.update(test2DNumericInstances.get(i), true, i);

        sgc1.merge(sgc2, 17);
        assertEquals(16, sgc1.count);
        assertEquals(15, sgc1.timestamp);
        assertEquals(0.0, sgc1.getCentroid().value(0));
        assertEquals(0.0, sgc1.getCentroid().value(1));
        assertEquals(0.5, sgc1.getPurityMeasures().get("purity"));
        assertEquals(10, sgc1.getPrototypes(17).numInstances());
        assertEquals(0, sgc1.getClassIndex());
        assertEquals(10, sgc1.getPrototypes(17).numInstances());
    }

    @Test
    void withinCluster() {
        SphericalGaussCluster sgc = new SphericalGaussCluster(1.0, 10, test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 8; i++) sgc.update(test2DNumericInstances.get(i), true, i);

        assertTrue(sgc.withinCluster(test2DNumericInstances.get(0), 8).getKey());
        assertTrue(sgc.withinCluster(test2DNumericInstances.get(1), 8).getKey());
        assertTrue(sgc.withinCluster(test2DNumericInstances.get(5), 8).getKey());
        assertTrue(sgc.withinCluster(test2DNumericInstances.get(6), 8).getKey());
        assertFalse(sgc.withinCluster(test2DNumericInstances.get(9), 8).getKey());
        assertFalse(sgc.withinCluster(test2DNumericInstances.get(13), 8).getKey());

        sgc = new SphericalGaussCluster(0.95, 10, test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 8; i++) sgc.update(test2DNumericInstances.get(i), true, i);

//        assertTrue(sgc.withinCluster(test2DNumericInstances.get(1), 10).getKey());
//        assertTrue(sgc.withinCluster(test2DNumericInstances.get(2), 10).getKey());
//        assertTrue(sgc.withinCluster(test2DNumericInstances.get(1), 30).getKey());
//        assertFalse(sgc.withinCluster(test2DNumericInstances.get(2), 30).getKey());
    }

    @Test
    void significantOverlapping() {
        SphericalGaussCluster sgc1 = new SphericalGaussCluster(1.0, 10,test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 8; i++) sgc1.update(test2DNumericInstances.get(i), true, i);
        SphericalGaussCluster sgc2 = new SphericalGaussCluster(1.0, 10, test2DNumericInstances.get(8), true, 8);
        for (int i = 9; i < 16; i++) sgc2.update(test2DNumericInstances.get(i), true, i);

        assertFalse(sgc1.significantOverlapping(sgc2));
        assertTrue(sgc1.significantOverlapping(sgc1));

        for (int i = 0; i < 8; i++) sgc2.update(test2DNumericInstances.get(i), true, i + 16);
        assertTrue(sgc1.significantOverlapping(sgc2));
    }

    @Test
    void getPrototypes() {
        SphericalGaussCluster sgc = new SphericalGaussCluster(0.95, 10,test2DNumericInstances.get(0), true, 0);
        for (int i = 1; i < 8; i++) sgc.update(test2DNumericInstances.get(i), true, i);

        assertEquals(7, sgc.getPrototypes(7).numInstances());
        assertEquals(6, sgc.getPrototypes(10).numInstances());
        assertEquals(1, sgc.getPrototypes(100).numInstances());
    }

}
