package utils.windows;

import org.junit.jupiter.api.Test;
import utils.ForgettingStrategy;

import static org.junit.jupiter.api.Assertions.*;

public class ForgettingEstimatorTest {

    @Test
    void update() {
        ForgettingEstimator fe = new ForgettingEstimator(ForgettingStrategy.FIXED, 0.9);
        assertEquals(0.0, fe.getCount());
        assertEquals(0.0, fe.getSum());
        assertEquals(Double.NaN, fe.getMean());
        assertEquals(Double.NaN, fe.getVar());

        fe.update(1.0, null, 0);
        assertEquals(1.0, fe.getCount());
        assertEquals(1.0, fe.getSum());
        assertEquals(1.0, fe.getMean());
        assertEquals(0.0, fe.getVar());

        fe.update(1.0, null, 1);
        assertEquals(1.9, fe.getCount());
        assertEquals(1.9, fe.getSum());
        assertEquals(1.0, fe.getMean());
        assertEquals(0.0, fe.getVar());

        fe.update(2.0, null, 2);
        assertEquals(2.71, fe.getCount());
        assertEquals(3.71, fe.getSum());
        assertEquals(1.36, fe.getMean(), 0.01);
        assertEquals(0.23, fe.getVar(), 0.01);
    }

    @Test
    void getDensity() {
        GaussianEstimator ge = new GaussianEstimator(ForgettingStrategy.FIXED,1.0);
        assertEquals(0.0, ge.getDensity(1.0));

        ge.update(5.0, null, 0);
        ge.update(5.0, null, 0);
        assertEquals(1.0, ge.getDensity(5.0));

        ge.update(9.0, null, 0);
        ge.update(9.0, null, 0);
        assertEquals(0.12, ge.getDensity(5.0), 0.01);
        assertEquals(0.19, ge.getDensity(7.0), 0.01);
        assertEquals(0.12, ge.getDensity(9.0), 0.01);
    }

}
