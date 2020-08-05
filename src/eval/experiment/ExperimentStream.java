package eval.experiment;
import moa.streams.ArffFileStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExperimentStream {

    public ArffFileStream stream;
    public String streamName;
    public double streamSize;
    public int logGranularity;
    public ArrayList<Double> classRatios;

    public ExperimentStream(ArffFileStream stream, String streamName, double streamSize, int logGranularity) {
        this.stream = stream;
        this.streamName = streamName;
        this.streamSize = streamSize;
        this.logGranularity = logGranularity;
    }

    public ExperimentStream(ArffFileStream stream, String streamName, double streamSize, int logGranularity, ArrayList<Double> classRatios) {
        this.stream = stream;
        this.streamName = streamName;
        this.streamSize = streamSize;
        this.logGranularity = logGranularity;
        this.classRatios = classRatios;
    }

    public static List<ExperimentStream> createExperimentStreams(String rootDataDir) {
        List<ExperimentStream> streams = new ArrayList<>();

        streams.addAll(createSyntheticStreams(rootDataDir + "/synthetic"));
        //streams.addAll(createRealStreams(rootDataDir + "/real"));

        return streams;
    }

    private static List<ExperimentStream> createSyntheticStreams(String rootDataDir) {
        List<ExperimentStream> streams = new ArrayList<>();
        int smallerLog = 100;
        int biggerLog = 100;

        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/SEA_1.arff", 4), "SEA_1", 600000, smallerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/SEA_2.arff", 4), "SEA_2", 600000, smallerLog));

        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/STAGGER_1.arff", 4), "STAGGER_1", 600000, smallerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/STAGGER_2.arff", 4), "STAGGER_2", 600000, smallerLog));

        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/HYPERPLANE_1.arff", 16), "HYPERPLANE_1", 500000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/HYPERPLANE_2.arff", 16), "HYPERPLANE_2", 500000, biggerLog));

        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/TREE_1.arff", 16), "TREE_1", 1000000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/TREE_2.arff", 16), "TREE_2", 1000000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/TREE_3.arff", 16), "TREE_3", 1200000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/TREE_4.arff", 16), "TREE_4", 1200000, biggerLog));

        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/RBF_1.arff", 16), "RBF_1", 1000000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/RBF_2.arff", 16), "RBF_2", 1000000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/RBF_3.arff", 16), "RBF_3", 1200000, biggerLog));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/RBF_4.arff", 16), "RBF_4", 1200000, biggerLog));

        return streams;
    }

    private static List<ExperimentStream> createRealStreams(String rootDataDir) {
        List<ExperimentStream> streams = new ArrayList<>();

        // HIGH RATE
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/ACTIVITY.arff", 44), "ACTIVITY", 10853, 100));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/COVERTYPE.arff", 55), "COVERTYPE", 581012, 2500));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/EEG.arff", 15), "EEG", 14980, 100));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/POKER.arff", 11), "POKER", 829201, 3000));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/ELEC.arff", 9), "ELEC", 45312, 100));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/GAS.arff", 129), "GAS", 13910, 100));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/SPAM.arff", 500), "SPAM", 9324, 100));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/CONNECT4.arff", 43), "CONNECT4", 67557, 500));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/WEATHER.arff", 9), "WEATHER", 18158, 200));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/ACTIVITY_RAW.arff", 4), "ACTIVITY_RAW", 1048570, 2500));

        // LOW RATE
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/AIRLINES.arff", 8), "AIRLINES", 539384, 2500));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/CRIMES.arff", 4), "CRIMES", 878049, 3000));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/DJ30.arff", 8), "DJ30", 138166, 500));
        streams.add(new ExperimentStream(new ArffFileStream(rootDataDir + "/SENSOR.arff", 6), "SENSOR", 2219802, 10000));

        return streams;
    }

}
