package utils.math;

import org.apache.commons.math3.stat.inference.TTest;

public class WelchTest extends TTest {

    public boolean test(double m1, double m2, double v1, double v2, double n1, double n2, double alpha) {
//        System.out.println(m1 + " " + m2 + " " + v1 + " " + v2 + " " + n1 + " " + n2);
//        System.out.println("Test: " + (this.tTest(m1, m2, v1, v2, n1, n2)));

        return this.tTest(m1, m2, v1, v2, n1, n2) < alpha; // if rejected with confidence 1 - alpha
    }

}
