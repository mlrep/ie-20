package utils;
import moa.core.DoubleVector;
import moa.core.Utils;

import java.util.Arrays;

public class ClassifierUtils {

    static public double combinePredictionsMax(double[] predictionValues) {
        double outPosterior;

        if (predictionValues.length > 1) {
            DoubleVector vote = new DoubleVector(predictionValues);

            if (vote.sumOfValues() > 0.0) {
                vote.normalize();
            }

            predictionValues = vote.getArrayRef();
            outPosterior = predictionValues[Utils.maxIndex(predictionValues)];

        } else {
            outPosterior = 0.0;
        }

        return Double.isInfinite(outPosterior) || Double.isNaN(outPosterior) ? 0 : outPosterior;
    }

    static public double[] prediction(double[] representation, int numClasses) {
        double[] prediction = new double[numClasses];

        for (int i = 0; i < representation.length; i += numClasses) {
            int n = 1 + (i / numClasses);
            for (int j = 0; j < numClasses; j++) {
                prediction[j] = prediction[j] + ((representation[i + j] - prediction[j]) / n);
            }
        }

        return prediction;
    }

}
