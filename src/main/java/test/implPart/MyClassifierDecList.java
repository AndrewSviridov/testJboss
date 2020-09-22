package test.implPart;

import weka.classifiers.trees.j48.*;
import weka.core.*;

import java.io.Serializable;

public class MyClassifierDecList implements Serializable, RevisionHandler {

    public int getM_minNumObj() {
        return m_minNumObj;
    }

    public static EntropySplitCrit getM_splitCrit() {
        return m_splitCrit;
    }

    public ModelSelection getM_toSelectModel() {
        return m_toSelectModel;
    }

    public ClassifierSplitModel getM_localModel() {
        return m_localModel;
    }

    public MyClassifierDecList[] getM_sons() {
        return m_sons;
    }

    public boolean isM_isLeaf() {
        return m_isLeaf;
    }

    public boolean isM_isEmpty() {
        return m_isEmpty;
    }

    public Instances getM_train() {
        return m_train;
    }

    public Distribution getM_test() {
        return m_test;
    }

    public int getIndeX() {
        return indeX;
    }

    /**
     * for serialization
     */
    private static final long serialVersionUID = 7284358349711992497L;

    /**
     * Minimum number of objects
     */
    protected int m_minNumObj;

    /**
     * To compute the entropy.
     */
    protected static EntropySplitCrit m_splitCrit = new EntropySplitCrit();

    /**
     * The model selection method.
     */
    protected ModelSelection m_toSelectModel;

    /**
     * Local model at node.
     */
    protected ClassifierSplitModel m_localModel;

    /**
     * References to sons.
     */
    protected MyClassifierDecList[] m_sons;

    /**
     * True if node is leaf.
     */
    protected boolean m_isLeaf;

    /**
     * True if node is empty.
     */
    protected boolean m_isEmpty;

    /**
     * The training instances.
     */
    protected Instances m_train;

    /**
     * The pruning instances.
     */
    protected Distribution m_test;

    /**
     * Which son to expand?
     */
    protected int indeX;

    /**
     * Constructor - just calls constructor of class DecList.
     */
    public MyClassifierDecList(ModelSelection toSelectLocModel, int minNum) {

        m_toSelectModel = toSelectLocModel;
        m_minNumObj = minNum;
    }

    /**
     * Method for building a pruned partial tree.
     *
     * @throws Exception if something goes wrong
     */
    public void buildRule(Instances data) throws Exception {

        buildDecList(data, false);

        cleanup(new Instances(data, 0));
    }

    /**
     * Builds the partial tree without hold out set.
     *
     * @throws Exception if something goes wrong
     */
    public void buildDecList(Instances data, boolean leaf) throws Exception {

        Instances[] localInstances;
        int ind;
        int i, j;
        double sumOfWeights;
        NoSplit noSplit;

        m_train = null;
        m_test = null;
        m_isLeaf = false;
        m_isEmpty = false;
        m_sons = null;
        indeX = 0;
        sumOfWeights = data.sumOfWeights();
        noSplit = new NoSplit(new Distribution(data));
        if (leaf) {
            m_localModel = noSplit;
        } else {
            m_localModel = m_toSelectModel.selectModel(data);
        }
        if (m_localModel.numSubsets() > 1) {
            localInstances = m_localModel.split(data);
            data = null;
            m_sons = new MyClassifierDecList[m_localModel.numSubsets()];
            i = 0;
            do {
                i++;
                ind = chooseIndex();
                if (ind == -1) {
                    for (j = 0; j < m_sons.length; j++) {
                        if (m_sons[j] == null) {
                            m_sons[j] = getNewDecList(localInstances[j], true);
                        }
                    }
                    if (i < 2) {
                        m_localModel = noSplit;
                        m_isLeaf = true;
                        m_sons = null;
                        if (Utils.eq(sumOfWeights, 0)) {
                            m_isEmpty = true;
                        }
                        return;
                    }
                    ind = 0;
                    break;
                } else {
                    m_sons[ind] = getNewDecList(localInstances[ind], false);
                }
            } while ((i < m_sons.length) && (m_sons[ind].m_isLeaf));

            // Choose rule
            indeX = chooseLastIndex();
        } else {
            m_isLeaf = true;
            if (Utils.eq(sumOfWeights, 0)) {
                m_isEmpty = true;
            }
        }
    }

    /**
     * Classifies an instance.
     *
     * @throws Exception if something goes wrong
     */
    public double classifyInstance(Instance instance) throws Exception {

        double maxProb = -1;
        double currentProb;
        int maxIndex = 0;
        int j;

        for (j = 0; j < instance.numClasses(); j++) {
            currentProb = getProbs(j, instance, 1);
            if (Utils.gr(currentProb, maxProb)) {
                maxIndex = j;
                maxProb = currentProb;
            }
        }
        if (Utils.eq(maxProb, 0)) {
            return -1.0;
        } else {
            return maxIndex;
        }
    }

    /**
     * Returns class probabilities for a weighted instance.
     *
     * @throws Exception if something goes wrong
     */
    public final double[] distributionForInstance(Instance instance)
            throws Exception {

        double[] doubles = new double[instance.numClasses()];

        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = getProbs(i, instance, 1);
        }

        return doubles;
    }

    /**
     * Returns the weight a rule assigns to an instance.
     *
     * @throws Exception if something goes wrong
     */
    public double weight(Instance instance) throws Exception {

        int subset;

        if (m_isLeaf) {
            return 1;
        }
        subset = m_localModel.whichSubset(instance);
        if (subset == -1) {
            return (m_localModel.weights(instance))[indeX]
                    * m_sons[indeX].weight(instance);
        }
        if (subset == indeX) {
            return m_sons[indeX].weight(instance);
        }
        return 0;
    }

    /**
     * Cleanup in order to save memory.
     */
    public final void cleanup(Instances justHeaderInfo) {

        m_train = justHeaderInfo;
        m_test = null;
        if (!m_isLeaf) {
            for (MyClassifierDecList m_son : m_sons) {
                if (m_son != null) {
                    m_son.cleanup(justHeaderInfo);
                }
            }
        }
    }

    /**
     * Prints rules.
     */
    @Override
    public String toString() {

        try {
            StringBuffer text;

            text = new StringBuffer();
            if (m_isLeaf) {
                text.append(": ");
                text.append(m_localModel.dumpLabel(0, m_train) + "\n");
            } else {
                dumpDecList(text);
                // dumpTree(0,text);
            }
            return text.toString();
        } catch (Exception e) {
            return "Can't print rule.";
        }
    }

    /**
     * Returns a newly created tree.
     *
     * @throws Exception if something goes wrong
     */
    protected MyClassifierDecList getNewDecList(Instances train, boolean leaf)
            throws Exception {

        MyClassifierDecList newDecList = new MyClassifierDecList(m_toSelectModel,
                m_minNumObj);
        newDecList.buildDecList(train, leaf);

        return newDecList;
    }

    /**
     * Method for choosing a subset to expand.
     */
    public final int chooseIndex() {

        int minIndex = -1;
        double estimated, min = Double.MAX_VALUE;
        int i, j;

        for (i = 0; i < m_sons.length; i++) {
            if (son(i) == null) {
                if (Utils.sm(localModel().distribution().perBag(i), m_minNumObj)) {
                    estimated = Double.MAX_VALUE;
                } else {
                    estimated = 0;
                    for (j = 0; j < localModel().distribution().numClasses(); j++) {
                        estimated -= m_splitCrit.lnFunc(localModel().distribution()
                                .perClassPerBag(i, j));
                    }
                    estimated += m_splitCrit
                            .lnFunc(localModel().distribution().perBag(i));
                    estimated /= (localModel().distribution().perBag(i) * ContingencyTables.log2);
                }
                if (Utils.smOrEq(estimated, 0)) {
                    return i;
                }
                if (Utils.sm(estimated, min)) {
                    min = estimated;
                    minIndex = i;
                }
            }
        }

        return minIndex;
    }

    /**
     * Choose last index (ie. choose rule).
     */
    public final int chooseLastIndex() {

        int minIndex = 0;
        double estimated, min = Double.MAX_VALUE;

        if (!m_isLeaf) {
            for (int i = 0; i < m_sons.length; i++) {
                if (son(i) != null) {
                    if (Utils.grOrEq(localModel().distribution().perBag(i), m_minNumObj)) {
                        estimated = son(i).getSizeOfBranch();
                        if (Utils.sm(estimated, min)) {
                            min = estimated;
                            minIndex = i;
                        }
                    }
                }
            }
        }

        return minIndex;
    }

    /**
     * Returns the number of instances covered by a branch
     */
    protected double getSizeOfBranch() {

        if (m_isLeaf) {
            return -localModel().distribution().total();
        } else {
            return son(indeX).getSizeOfBranch();
        }
    }

    /**
     * Help method for printing tree structure.
     */
    public void dumpDecList(StringBuffer text) throws Exception {

        text.append(m_localModel.leftSide(m_train));
        // System.out.println(m_localModel.sourceExpression(,m_train))
        text.append(m_localModel.rightSide(indeX, m_train));
        if (m_sons[indeX].m_isLeaf) {
            text.append(": ");
            text.append(m_localModel.dumpLabel(indeX, m_train) + "\n");
        } else {
            text.append(" AND\n");
            m_sons[indeX].dumpDecList(text);
        }
    }

    /**
     * Help method for printing tree structure.
     */
    public void MydumpDecList() throws Exception {
        StringBuffer text = new StringBuffer();

        //C45Split есть метод бsourceExpression клсс для разбиянения
        // System.out.println("c45Split m_localModel.leftSide(m_train)  "+m_train.attribute(m_localModel.attIndex).name());
        text.append(m_localModel.leftSide(m_train));
        text.append(m_localModel.rightSide(indeX, m_train));
        if (m_sons[indeX].m_isLeaf) {
            text.append(": ");
            text.append(m_localModel.dumpLabel(indeX, m_train) + "\n");
        } else {
            text.append(" AND\n");
            m_sons[indeX].dumpDecList(text);
        }
    }


    /**
     * Help method for computing class probabilities of a given instance.
     *
     * @throws Exception Exception if something goes wrong
     */
    private double getProbs(int classIndex, Instance instance, double weight)
            throws Exception {

        double[] weights;
        int treeIndex;

        if (m_isLeaf) {
            return weight * localModel().classProb(classIndex, instance, -1);
        } else {
            treeIndex = localModel().whichSubset(instance);
            if (treeIndex == -1) {
                weights = localModel().weights(instance);
                return son(indeX).getProbs(classIndex, instance,
                        weights[indeX] * weight);
            } else {
                if (treeIndex == indeX) {
                    return son(indeX).getProbs(classIndex, instance, weight);
                } else {
                    return 0;
                }
            }
        }
    }


    /**
     * Method just exists to make program easier to read.
     */
    protected ClassifierSplitModel localModel() {

        return m_localModel;
    }

    /**
     * Method just exists to make program easier to read.
     *
     * @return
     */
    protected MyClassifierDecList son(int index) {

        return m_sons[index];
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 10153 $");
    }
}

