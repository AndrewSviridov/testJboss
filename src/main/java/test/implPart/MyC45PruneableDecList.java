package test.implPart;


import weka.classifiers.trees.j48.Distribution;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.NoSplit;
import weka.classifiers.trees.j48.Stats;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.Utils;

public class MyC45PruneableDecList extends MyClassifierDecList {

    /**
     * for serialization
     */
    private static final long serialVersionUID = -2757684345218324559L;

    /**
     * CF
     */
    private double CF = 0.25;

    /**
     * Constructor for pruneable tree structure. Stores reference to associated
     * training data at each node.
     *
     * @param toSelectLocModel selection method for local splitting model
     * @param cf               the confidence factor for pruning
     * @param minNum           the minimum number of objects in a leaf
     * @throws Exception if something goes wrong
     */
    public MyC45PruneableDecList(ModelSelection toSelectLocModel, double cf,
                                 int minNum) throws Exception {

        super(toSelectLocModel, minNum);

        CF = cf;
    }

    /**
     * Builds the partial tree without hold out set.
     *
     * @throws Exception if something goes wrong
     */
    @Override
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

            // Check if all successors are leaves
            for (j = 0; j < m_sons.length; j++) {
                if ((m_sons[j] == null) || (!m_sons[j].m_isLeaf)) {
                    break;
                }
            }
            if (j == m_sons.length) {
                pruneEnd();
                if (!m_isLeaf) {
                    indeX = chooseLastIndex();
                }
            } else {
                indeX = chooseLastIndex();
            }
        } else {
            m_isLeaf = true;
            if (Utils.eq(sumOfWeights, 0)) {
                m_isEmpty = true;
            }
        }
    }

    /**
     * Returns a newly created tree.
     *
     * @return
     * @throws Exception if something goes wrong
     */
    @Override
    protected MyClassifierDecList getNewDecList(Instances data, boolean leaf)
            throws Exception {

        MyC45PruneableDecList newDecList = new MyC45PruneableDecList(m_toSelectModel,
                CF, m_minNumObj);

        newDecList.buildDecList(data, leaf);

        return newDecList;
    }

    /**
     * Prunes the end of the rule.
     */
    protected void pruneEnd() {

        double errorsLeaf, errorsTree;

        errorsTree = getEstimatedErrorsForTree();
        errorsLeaf = getEstimatedErrorsForLeaf();
        if (Utils.smOrEq(errorsLeaf, errorsTree + 0.1)) { // +0.1 as in C4.5
            m_isLeaf = true;
            m_sons = null;
            m_localModel = new NoSplit(localModel().distribution());
        }
    }

    /**
     * Computes estimated errors for tree.
     */
    private double getEstimatedErrorsForTree() {

        if (m_isLeaf) {
            return getEstimatedErrorsForLeaf();
        } else {
            double error = 0;
            for (int i = 0; i < m_sons.length; i++) {
                if (!Utils.eq(son(i).localModel().distribution().total(), 0)) {
                    error += ((MyC45PruneableDecList) son(i)).getEstimatedErrorsForTree();
                }
            }
            return error;
        }
    }

    /**
     * Computes estimated errors for leaf.
     */
    public double getEstimatedErrorsForLeaf() {

        double errors = localModel().distribution().numIncorrect();

        return errors
                + Stats.addErrs(localModel().distribution().total(), errors, (float) CF);
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
