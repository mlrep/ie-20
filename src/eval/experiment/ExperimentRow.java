package eval.experiment;
import framework.Framework;
import moa.classifiers.Classifier;
import moa.classifiers.functions.SGDMultiClass;
import moa.classifiers.trees.HoeffdingAdaptiveTree;


public class ExperimentRow {

    public Framework framework;
    public String label;
    public String subLabel;

    public ExperimentRow(Framework framework, String label) {
        this.framework = framework;
        this.label = label;
        this.subLabel = "";
    }

    public ExperimentRow(Framework framework, String label, String subLabel) {
        this.framework = framework;
        this.label = label;
        this.subLabel = subLabel;
    }

    public static Classifier getExperimentClassifier() {
        HoeffdingAdaptiveTree cls = new HoeffdingAdaptiveTree();
        //SGDMultiClass cls = new SGDMultiClass();

        return cls;
    }

}
