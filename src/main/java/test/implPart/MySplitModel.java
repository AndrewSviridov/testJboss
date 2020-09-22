package test.implPart;

import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.core.Instance;
import weka.core.Instances;

public class MySplitModel extends ClassifierSplitModel {
    @Override
    public void buildClassifier(Instances instances) throws Exception {

    }

    @Override
    public String leftSide(Instances data) {
        return null;
    }

    @Override
    public String rightSide(int index, Instances data) {
        return null;
    }

    @Override
    public String sourceExpression(int index, Instances data) {
        return null;
    }

    @Override
    public double[] weights(Instance instance) {
        return new double[0];
    }

    @Override
    public int whichSubset(Instance instance) throws Exception {
        return 0;
    }

    @Override
    public String getRevision() {
        return null;
    }
}
