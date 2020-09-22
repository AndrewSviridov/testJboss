package test.implPart;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.rules.part.C45PruneableDecList;

import weka.classifiers.rules.part.PruneableDecList;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.CapabilitiesHandler;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;


public class MyMakeDecList implements Serializable, RevisionHandler {

    public Vector<MyClassifierDecList> getTheRules() {
        return theRules;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public double getCF() {
        return CF;
    }

    public int getMinNumObj() {
        return minNumObj;
    }

    public ModelSelection getToSelectModeL() {
        return toSelectModeL;
    }

    public int getNumSetS() {
        return numSetS;
    }

    public boolean isReducedErrorPruning() {
        return reducedErrorPruning;
    }

    public boolean isUnpruned() {
        return unpruned;
    }

    public int getM_seed() {
        return m_seed;
    }

    /**
     * for serialization
     */
    private static final long serialVersionUID = -1427481323245079123L;

    /**
     * Vector storing the rules.
     */
    private Vector<MyClassifierDecList> theRules;

    /**
     * The confidence for C45-type pruning.
     */
    private double CF = 0.25f;

    /**
     * Minimum number of objects
     */
    private final int minNumObj;

    /**
     * The model selection method.
     */
    private final ModelSelection toSelectModeL;

    /**
     * How many subsets of equal size? One used for pruning, the rest for
     * training.
     */
    private int numSetS = 3;

    /**
     * Use reduced error pruning?
     */
    private boolean reducedErrorPruning = false;

    /**
     * Generated unpruned list?
     */
    private boolean unpruned = false;

    /**
     * The seed for random number generation.
     */
    private int m_seed = 1;

    /**
     * Constructor for unpruned dec list.
     */
    public MyMakeDecList(ModelSelection toSelectLocModel, int minNum) {

        toSelectModeL = toSelectLocModel;
        reducedErrorPruning = false;
        unpruned = true;
        minNumObj = minNum;
    }

    /**
     * Constructor for dec list pruned using C4.5 pruning.
     */
    public MyMakeDecList(ModelSelection toSelectLocModel, double cf, int minNum) {

        toSelectModeL = toSelectLocModel;
        CF = cf;
        reducedErrorPruning = false;
        unpruned = false;
        minNumObj = minNum;
    }

    /**
     * Constructor for dec list pruned using hold-out pruning.
     */
    public MyMakeDecList(ModelSelection toSelectLocModel, int num, int minNum,
                         int seed) {

        toSelectModeL = toSelectLocModel;
        numSetS = num;
        reducedErrorPruning = true;
        unpruned = false;
        minNumObj = minNum;
        m_seed = seed;
    }

    /**
     * Builds dec list.
     *
     * @throws Exception if dec list can't be built successfully
     */
    public void buildClassifier(Instances data) throws Exception {

        MyClassifierDecList currentRule;
        double currentWeight;
        Instances oldGrowData, newGrowData, oldPruneData, newPruneData;
        theRules = new Vector<MyClassifierDecList>();
        if ((reducedErrorPruning) && !(unpruned)) {
            Random random = new Random(m_seed);
            data.randomize(random);
            data.stratify(numSetS);
            oldGrowData = data.trainCV(numSetS, numSetS - 1, random);
            oldPruneData = data.testCV(numSetS, numSetS - 1);
        } else {
            oldGrowData = data;
            oldPruneData = null;
        }

        while (Utils.gr(oldGrowData.numInstances(), 0)) {

            // Create rule
            if (unpruned) {
                currentRule = new MyClassifierDecList(toSelectModeL, minNumObj);
                currentRule.buildRule(oldGrowData);
                System.out.println("1111");
            } else if (reducedErrorPruning) {
                System.out.println("2222");
                currentRule = new MyPruneableDecList(toSelectModeL, minNumObj);
                ((MyPruneableDecList) currentRule).buildRule(oldGrowData, oldPruneData);
            } else {
                System.out.println("33333");
                currentRule = new MyC45PruneableDecList(toSelectModeL, CF, minNumObj);
                currentRule.buildRule(oldGrowData);
            }
            // Remove instances from growing data
            newGrowData = new Instances(oldGrowData, oldGrowData.numInstances());
            Enumeration<Instance> enu = oldGrowData.enumerateInstances();
            while (enu.hasMoreElements()) {
                Instance instance = enu.nextElement();
                currentWeight = currentRule.weight(instance);
                if (Utils.sm(currentWeight, 1)) {
                    instance.setWeight(instance.weight() * (1 - currentWeight));
                    newGrowData.add(instance);
                }
            }
            newGrowData.compactify();
            oldGrowData = newGrowData;

            // Remove instances from pruning data
            if ((reducedErrorPruning) && !(unpruned)) {
                newPruneData = new Instances(oldPruneData, oldPruneData.numInstances());
                enu = oldPruneData.enumerateInstances();
                while (enu.hasMoreElements()) {
                    Instance instance = enu.nextElement();
                    currentWeight = currentRule.weight(instance);
                    if (Utils.sm(currentWeight, 1)) {
                        instance.setWeight(instance.weight() * (1 - currentWeight));
                        newPruneData.add(instance);
                    }
                }
                newPruneData.compactify();
                oldPruneData = newPruneData;
            }
            theRules.addElement(currentRule);
        }
    }

    /**
     * Outputs the classifier into a string.
     */
    @Override
    public String toString() {

        StringBuffer text = new StringBuffer();

        for (int i = 0; i < theRules.size(); i++) {
            text.append(theRules.elementAt(i) + "\n");
        }
        text.append("Number of Rules  : \t" + theRules.size() + "\n");

        return text.toString();
    }

    /**
     * Classifies an instance.
     *
     * @throws Exception if instance can't be classified
     */
    public double classifyInstance(Instance instance) throws Exception {

        double maxProb = -1;
        double[] sumProbs;
        int maxIndex = 0;

        sumProbs = distributionForInstance(instance);
        for (int j = 0; j < sumProbs.length; j++) {
            if (Utils.gr(sumProbs[j], maxProb)) {
                maxIndex = j;
                maxProb = sumProbs[j];
            }
        }

        return maxIndex;
    }

    /**
     * Returns the class distribution for an instance.
     *
     * @throws Exception if distribution can't be computed
     */
    public double[] distributionForInstance(Instance instance) throws Exception {

        double[] currentProbs = null;
        double[] sumProbs;
        double currentWeight, weight = 1;
        int i, j;

        // Get probabilities.
        sumProbs = new double[instance.numClasses()];
        i = 0;
        while ((Utils.gr(weight, 0)) && (i < theRules.size())) {
            currentWeight = theRules.elementAt(i).weight(instance);
            if (Utils.gr(currentWeight, 0)) {
                currentProbs = theRules.elementAt(i).distributionForInstance(instance);
                for (j = 0; j < sumProbs.length; j++) {
                    sumProbs[j] += weight * currentProbs[j];
                }
                weight = weight * (1 - currentWeight);
            }
            i++;
        }

        return sumProbs;
    }

    /**
     * Outputs the number of rules in the classifier.
     */
    public int numRules() {

        return theRules.size();
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 14511 $");
    }
}


