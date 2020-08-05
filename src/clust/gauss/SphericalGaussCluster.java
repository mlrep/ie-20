package clust.gauss;

import clust.Cluster;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import javafx.util.Pair;
import utils.InstanceUtils;
import utils.math.MathUtils;
import utils.windows.WindowedInstances;

import java.util.ArrayList;
import java.util.HashMap;

public class SphericalGaussCluster extends Cluster {

    protected double count;
    protected Instance centroid;
    private double varMax;
    private ArrayList<Double> ls = new ArrayList<>();
    private ArrayList<Double> ss = new ArrayList<>();

    private HashMap<String, Double> purityMeasures = new HashMap<>();
    private ArrayList<Double> clsCounts = new ArrayList<>();
    private double allClsCount;
    private double maxClsCount;
    private int maxClsIdx;
    private WindowedInstances buffer;

    protected int timestamp;
    protected double decay = 0.95; // todo: adjust based on rate
    private int bufferSize = 100;
    private double r = 2.0;

    private boolean init = false;
    private int n = 0;

    public SphericalGaussCluster(double decay) {
        this.decay = decay;
    }

    public SphericalGaussCluster(double decay, double r) {
        this.decay = decay;
        this.r = r;
    }

    public SphericalGaussCluster(Instance instance, boolean label, int t) {
        this.init(instance, label, t);
    }

    public SphericalGaussCluster(double decay, int bufferSize, Instance instance, boolean label, int t) {
        this.init(instance, label, t);
        this.decay = decay;
        this.bufferSize = bufferSize;
    }

    private void init(Instance instance, boolean label, int t) {
        this.centroid = instance.copy();
        this.count = 1.0;
        this.varMax = 0.0;

        for (int i = 0; i < instance.numAttributes() - 1; i++) {
            this.ls.add(instance.value(i));
            this.ss.add(Math.pow(instance.value(i), 2.0));
        }

        for (int i = 0; i < instance.numClasses(); i++) {
            this.clsCounts.add(0.0);
        }

        this.buffer = new WindowedInstances(this.bufferSize, instance, label);

        if (label) {
            int clsIdx = (int)instance.classValue();
            this.clsCounts.set(clsIdx, this.clsCounts.get(clsIdx) + 1);
            this.allClsCount = 1.0;
            this.maxClsCount = 1.0;
            this.maxClsIdx = clsIdx;
            this.purityMeasures.put("purity", 1.0);
        } else {
            this.allClsCount = 0.0;
            this.maxClsCount = 0.0;
            this.maxClsIdx = -1;
            this.purityMeasures.put("purity", 0.0);
        }

        this.timestamp = t;
        this.init = true;
        this.n = 1;
    }

    @Override
    public boolean update(Instance instance, boolean label, int t) {
        if (!this.init) {
            this.init(instance, label, t);
            return true;
        }

        double d = Math.pow(this.decay, t - this.timestamp);
        this.count = d * this.count + 1.0;
        this.varMax = 0.0;

        for (int i = 0; i < instance.numAttributes() - 1; i++) {
            this.ls.set(i, d * this.ls.get(i) + instance.value(i));
            this.ss.set(i, d * this.ss.get(i) + Math.pow(instance.value(i), 2.0));
            this.updateStats(i);
        }

        if (label) {
            this.buffer.add(instance);
            int clsIdx = (int)instance.classValue();
            this.allClsCount = 0;
            this.maxClsCount = 0.0;

            for (int i = 0; i < instance.numClasses(); i++) {
                double newClsCount = d * this.clsCounts.get(i) + (i == clsIdx ? 1.0 : 0.0);
                this.clsCounts.set(i, newClsCount);
                this.allClsCount += this.clsCounts.get(i);

                if (newClsCount > this.maxClsCount) {
                    this.maxClsCount = newClsCount;
                    this.maxClsIdx = i;
                }
            }

            this.purityMeasures.put("purity", (this.allClsCount > 0.0 ? this.maxClsCount / this.allClsCount : 0.0));
        }

        this.timestamp = t;
        this.n++;

        return true;
    }

    protected double updateStats(int idx) {
        double newMean = this.ls.get(idx) / this.count;
        double newVar = this.pVariance(idx);
        this.centroid.setValue(idx, newMean);

        if (newVar > this.varMax) {
            this.varMax = newVar;
        }

        return newVar;
    }

    private double pVariance(int idx) {
        return (this.ss.get(idx) / this.count - Math.pow(this.ls.get(idx) / this.count, 2.0));
    }

    @Override
    public Instance getCentroid() {
        return this.centroid;
    }

    @Override
    public ArrayList<Double> getRadius() {
        ArrayList<Double> radius = new ArrayList<>();
        for  (int i = 0; i < this.centroid.numAttributes() - 1; i++) {
            radius.add(this.r * Math.sqrt(this.varMax));
        }

        return radius;
    }

    @Override
    public void merge(Cluster otherCluster, int t) {
        SphericalGaussCluster otherGaussCluster = (SphericalGaussCluster)otherCluster;
        this.count = this.count + otherGaussCluster.count;

        int ot = ((SphericalGaussCluster)otherCluster).timestamp;
        if (this.timestamp < ot) {
            this.timestamp = ot;
        }

        double d = Math.pow(this.decay, t - this.timestamp);
        this.varMax = 0.0;
        this.maxClsCount = 0.0;

        for (int i = 0; i < this.centroid.numAttributes() - 1; i++) {
            this.ls.set(i, d * (this.ls.get(i) + otherGaussCluster.ls.get(i)));
            this.ss.set(i, d * (this.ss.get(i) + otherGaussCluster.ss.get(i)));
            this.updateStats(i);
        }

        for (int i = 0; i < this.clsCounts.size(); i++) {
            double newClsCounts = d * (this.clsCounts.get(i) + otherGaussCluster.clsCounts.get(i));
            this.clsCounts.set(i, newClsCounts);

            if (newClsCounts > this.maxClsCount) {
                this.maxClsCount = newClsCounts;
                this.maxClsIdx = i;
            }
        }

        this.allClsCount = d * (this.allClsCount + otherGaussCluster.allClsCount);
        this.purityMeasures.put("purity", (this.allClsCount > 0.0 ? this.maxClsCount / this.allClsCount : 0.0));

        this.mergeBuffers(otherGaussCluster, t);
        this.n += otherGaussCluster.n;
    }

    private void mergeBuffers(SphericalGaussCluster otherGaussCluster, int t) {
        Instances smallerWindow;
        Instances biggerWindow;

        if (this.getPrototypes(t).size() > otherGaussCluster.getPrototypes(t).size()) {
            biggerWindow = this.getPrototypes(t);
            smallerWindow = otherGaussCluster.getPrototypes(t);
        } else {
            biggerWindow = otherGaussCluster.getPrototypes(t);
            smallerWindow = this.getPrototypes(t);
        }

        WindowedInstances newBuffer = new WindowedInstances(this.bufferSize, this.centroid, false);
        int smallSize = smallerWindow.size();
        int bigSize = biggerWindow.size();
        int maxSize = Math.min(bigSize, this.bufferSize);

        int remainSize = Math.max(maxSize - 2 * smallSize, 0);
        for (int i = remainSize - 1; i >= 0; i--) {
            newBuffer.add(biggerWindow.get(bigSize - smallSize - i - 1));
        }

        for (int i = 0; i < smallerWindow.size(); i++) {
            newBuffer.add(biggerWindow.get(i));
            newBuffer.add(smallerWindow.get(i));
        }

        this.buffer = newBuffer;
    }

    @Override
    public Pair<Boolean, Double> withinCluster(Instance instance, int t) {
        double weightedDist = MathUtils.euclideanDist(instance, this.centroid); // todo: / d
        return new Pair<>(this.n == 1 || weightedDist <= this.r * Math.sqrt(this.varMax), weightedDist);
    }

    @Override
    public boolean significantOverlapping(Cluster otherCluster) {
        SphericalGaussCluster otherSphericalCluster = (SphericalGaussCluster) otherCluster;
        double dist = MathUtils.euclideanDist(this.centroid, otherSphericalCluster.getCentroid());
        return dist <= this.r * Math.sqrt(this.varMax) || dist <= otherSphericalCluster.r * Math.sqrt(otherSphericalCluster.varMax);
    }

    @Override
    public boolean isActive(int t, int threshold) {
        return t - this.timestamp >= threshold;
    }

    @Override
    public Instances getPrototypes(int t) {
        double d = Math.pow(this.decay, t - this.timestamp);
        int l = this.buffer.getWindowLength();
        int currentClsCount = (int)(d * this.allClsCount);

        if (currentClsCount < l) {
            Instances prototypes = InstanceUtils.createInstances(this.centroid, false);
            int n = (int)Math.ceil((d * this.allClsCount));

            for (int i = 0; i < n; i++) {
                prototypes.add(this.buffer.getInstance(l - i - 1));
            }

            return prototypes;
        }

        return this.buffer.getInstances();
    }

    @Override
    public Cluster copy() {
        SphericalGaussCluster sgc = new SphericalGaussCluster(this.decay);
        sgc.count = this.count;

        if (this.centroid != null) {
            sgc.centroid = this.centroid.copy();
        }

        sgc.varMax = this.varMax;
        sgc.ls =  new ArrayList<>(this.ls);
        sgc.ss = new ArrayList<>(this.ss);

        sgc.purityMeasures = new HashMap<>(this.purityMeasures);
        sgc.clsCounts = new ArrayList<>(this.clsCounts);
        sgc.allClsCount = this.allClsCount;
        sgc.maxClsCount = this.maxClsCount;
        sgc.maxClsIdx = this.maxClsIdx;

        if (this.buffer != null) {
            sgc.buffer = this.buffer.copy();
        }

        sgc.timestamp =  this.timestamp;
        sgc.decay = this.decay;
        sgc.bufferSize = this.bufferSize;

        sgc.init = this.init;
        sgc.n = this.n;

        return sgc;
    }

    public HashMap<String, Double> getPurityMeasures() {
        return this.purityMeasures;
    }

    public int getClassIndex() {
        return this.maxClsIdx;
    }

}
