package test.implPart;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.part.MakeDecList;
import weka.classifiers.trees.j48.BinC45ModelSelection;
import weka.classifiers.trees.j48.C45ModelSelection;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.*;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class MyPart extends AbstractClassifier implements OptionHandler,
        WeightedInstancesHandler, Summarizable, AdditionalMeasureProducer,
        TechnicalInformationHandler {
    public MyMakeDecList getRules() {
        return m_root;
    }


    /**
     * for serialization
     */
    static final long serialVersionUID = 8121455039782598361L;

    /**
     * The decision list
     */
    private MyMakeDecList m_root;

    /**
     * Confidence level
     */
    private float m_CF = 0.25f;

    /**
     * Minimum number of objects
     */
    private int m_minNumObj = 2;

    /**
     * Use MDL correction?
     */
    private boolean m_useMDLcorrection = true;

    /**
     * Use reduced error pruning?
     */
    private boolean m_reducedErrorPruning = false;

    /**
     * Number of folds for reduced error pruning.
     */
    private int m_numFolds = 3;

    /**
     * Binary splits on nominal attributes?
     */
    private boolean m_binarySplits = false;

    /**
     * Generate unpruned list?
     */
    private boolean m_unpruned = false;

    /**
     * The seed for random number generation.
     */
    private int m_Seed = 1;

    /**
     * Do not relocate split point to actual data value
     */
    private boolean m_doNotMakeSplitPointActualValue;

    /**
     * Returns a string describing classifier
     *
     * @return a description suitable for displaying in the explorer/experimenter
     * gui
     */
    public String globalInfo() {

        return "Class for generating a PART decision list. Uses "
                + "separate-and-conquer. Builds a partial C4.5 decision tree "
                + "in each iteration and makes the \"best\" leaf into a rule.\n\n"
                + "For more information, see:\n\n" + getTechnicalInformation().toString();
    }

    /**
     * Returns an instance of a TechnicalInformation object, containing detailed
     * information about the technical background of this class, e.g., paper
     * reference or book this class is based on.
     *
     * @return the technical information about this class
     */
    @Override
    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result;

        result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
        result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Ian H. Witten");
        result.setValue(TechnicalInformation.Field.TITLE,
                "Generating Accurate Rule Sets Without Global Optimization");
        result.setValue(TechnicalInformation.Field.BOOKTITLE,
                "Fifteenth International Conference on Machine Learning");
        result.setValue(TechnicalInformation.Field.EDITOR, "J. Shavlik");
        result.setValue(TechnicalInformation.Field.YEAR, "1998");
        result.setValue(TechnicalInformation.Field.PAGES, "144-151");
        result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
        result.setValue(TechnicalInformation.Field.PS,
                "http://www.cs.waikato.ac.nz/~eibe/pubs/ML98-57.ps.gz");

        return result;
    }

    /**
     * Returns default capabilities of the classifier.
     *
     * @return the capabilities of this classifier
     */
    @Override
    public Capabilities getCapabilities() {
        Capabilities result;

        result = new Capabilities(this);
        result.disableAll();
        // attributes
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
        result.enable(Capabilities.Capability.MISSING_VALUES);

        // class
        result.enable(Capabilities.Capability.NOMINAL_CLASS);
        result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);

        // instances
        result.setMinimumNumberInstances(0);

        return result;
    }

    /**
     * Generates the classifier.
     *
     * @param instances the data to train with
     * @throws Exception if classifier can't be built successfully
     */
    @Override
    public void buildClassifier(Instances instances) throws Exception {

        if ((m_unpruned) && (m_reducedErrorPruning)) {
            throw new Exception("Unpruned rule set and reduced error pruning cannot be selected simultaneously!");
        }
        if ((m_unpruned) && (m_CF != 0.25f)) {
            throw new Exception("It does not make sense to change the confidence for an unpruned tree!");
        }
        if ((m_reducedErrorPruning) && (m_CF != 0.25f)) {
            throw new Exception("Changing the confidence does not make sense for reduced error pruning.");
        }
        if ((!m_reducedErrorPruning) && (m_numFolds != 3)) {
            throw new Exception("Changing the number of folds does not make sense if"
                    + " reduced error pruning is not selected.");
        }
        if ((m_CF <= 0) || (m_CF >= 1)) {
            throw new Exception("Confidence has to be greater than zero and smaller than one!");
        }
        // can classifier handle the data?
        getCapabilities().testWithFail(instances);

        // remove instances with missing class
        instances = new Instances(instances);
        instances.deleteWithMissingClass();

        ModelSelection modSelection;

        if (m_binarySplits) {
            modSelection = new BinC45ModelSelection(m_minNumObj, instances,
                    m_useMDLcorrection, m_doNotMakeSplitPointActualValue);
        } else {
            modSelection = new C45ModelSelection(m_minNumObj, instances,
                    m_useMDLcorrection, m_doNotMakeSplitPointActualValue);
        }
        if (m_unpruned) {
            m_root = new MyMakeDecList(modSelection, m_minNumObj);
        } else if (m_reducedErrorPruning) {
            m_root = new MyMakeDecList(modSelection, m_numFolds, m_minNumObj, m_Seed);
        } else {
            m_root = new MyMakeDecList(modSelection, m_CF, m_minNumObj);
        }
        m_root.buildClassifier(instances);
        if (m_binarySplits) {
            ((BinC45ModelSelection) modSelection).cleanup();
        } else {
            ((C45ModelSelection) modSelection).cleanup();
        }
    }

    /**
     * Classifies an instance.
     *
     * @param instance the instance to classify
     * @return the classification
     * @throws Exception if instance can't be classified successfully
     */
    @Override
    public double classifyInstance(Instance instance) throws Exception {

        return m_root.classifyInstance(instance);
    }

    /**
     * Returns class probabilities for an instance.
     *
     * @param instance the instance to get the distribution for
     * @return the class probabilities
     * @throws Exception if the distribution can't be computed successfully
     */
    @Override
    public final double[] distributionForInstance(Instance instance)
            throws Exception {

        return m_root.distributionForInstance(instance);
    }

    /**
     * Returns an enumeration describing the available options.
     * <p>
     * Valid options are:
     * <p>
     * <p>
     * -C confidence <br>
     * Set confidence threshold for pruning. (Default: 0.25)
     * <p>
     * <p>
     * -M number <br>
     * Set minimum number of instances per leaf. (Default: 2)
     * <p>
     * <p>
     * -R <br>
     * Use reduced error pruning.
     * <p>
     * <p>
     * -N number <br>
     * Set number of folds for reduced error pruning. One fold is used as the
     * pruning set. (Default: 3)
     * <p>
     * <p>
     * -B <br>
     * Use binary splits for nominal attributes.
     * <p>
     * <p>
     * -U <br>
     * Generate unpruned decision list.
     * <p>
     * <p>
     * -Q <br>
     * The seed for reduced-error pruning.
     * <p>
     *
     * @return an enumeration of all the available options.
     */
    @Override
    public Enumeration<Option> listOptions() {

        Vector<Option> newVector = new Vector<Option>(8);

        newVector.addElement(new Option("\tSet confidence threshold for pruning.\n"
                + "\t(default 0.25)", "C", 1, "-C <pruning confidence>"));
        newVector.addElement(new Option(
                "\tSet minimum number of objects per leaf.\n" + "\t(default 2)", "M", 1,
                "-M <minimum number of objects>"));
        newVector.addElement(new Option("\tUse reduced error pruning.", "R", 0,
                "-R"));
        newVector.addElement(new Option("\tSet number of folds for reduced error\n"
                + "\tpruning. One fold is used as pruning set.\n" + "\t(default 3)", "N",
                1, "-N <number of folds>"));
        newVector.addElement(new Option("\tUse binary splits only.", "B", 0, "-B"));
        newVector.addElement(new Option("\tGenerate unpruned decision list.", "U",
                0, "-U"));
        newVector.addElement(new Option(
                "\tDo not use MDL correction for info gain on numeric attributes.", "J",
                0, "-J"));
        newVector.addElement(new Option(
                "\tSeed for random data shuffling (default 1).", "Q", 1, "-Q <seed>"));
        newVector.addElement(new Option("\tDo not make split point actual value.",
                "-doNotMakeSplitPointActualValue", 0, "-doNotMakeSplitPointActualValue"));

        newVector.addAll(Collections.list(super.listOptions()));

        return newVector.elements();
    }

    /**
     * Parses a given list of options.
     * <p/>
     * <p>
     * <!-- options-start --> Valid options are:
     * <p/>
     *
     * <pre>
     * -C &lt;pruning confidence&gt;
     *  Set confidence threshold for pruning.
     *  (default 0.25)
     * </pre>
     *
     * <pre>
     * -M &lt;minimum number of objects&gt;
     *  Set minimum number of objects per leaf.
     *  (default 2)
     * </pre>
     *
     * <pre>
     * -R
     *  Use reduced error pruning.
     * </pre>
     *
     * <pre>
     * -N &lt;number of folds&gt;
     *  Set number of folds for reduced error
     *  pruning. One fold is used as pruning set.
     *  (default 3)
     * </pre>
     *
     * <pre>
     * -B
     *  Use binary splits only.
     * </pre>
     *
     * <pre>
     * -U
     *  Generate unpruned decision list.
     * </pre>
     *
     * <pre>
     * -J
     *  Do not use MDL correction for info gain on numeric attributes.
     * </pre>
     *
     * <pre>
     * -Q &lt;seed&gt;
     *  Seed for random data shuffling (default 1).
     * </pre>
     *
     * <pre>
     * -doNotMakeSplitPointActualValue
     *  Do not make split point actual value.
     * </pre>
     * <p>
     * <!-- options-end -->
     *
     * @param options the list of options as an array of strings
     * @throws Exception if an option is not supported
     */
    @Override
    public void setOptions(String[] options) throws Exception {

        // Pruning options
        m_unpruned = Utils.getFlag('U', options);
        m_reducedErrorPruning = Utils.getFlag('R', options);
        m_binarySplits = Utils.getFlag('B', options);
        m_useMDLcorrection = !Utils.getFlag('J', options);
        m_doNotMakeSplitPointActualValue = Utils.getFlag("doNotMakeSplitPointActualValue", options);
        String confidenceString = Utils.getOption('C', options);
        if (confidenceString.length() != 0) {
            setConfidenceFactor((new Float(confidenceString)).floatValue());
        } else {
            m_CF = 0.25f;
        }
        String numFoldsString = Utils.getOption('N', options);
        if (numFoldsString.length() != 0) {
            m_numFolds = Integer.parseInt(numFoldsString);
        } else {
            m_numFolds = 3;
        }

        // Other options
        String minNumString = Utils.getOption('M', options);
        if (minNumString.length() != 0) {
            m_minNumObj = Integer.parseInt(minNumString);
        } else {
            m_minNumObj = 2;
        }
        String seedString = Utils.getOption('Q', options);
        if (seedString.length() != 0) {
            m_Seed = Integer.parseInt(seedString);
        } else {
            m_Seed = 1;
        }

        super.setOptions(options);
    }

    /**
     * Gets the current settings of the Classifier.
     *
     * @return an array of strings suitable for passing to setOptions
     */
    @Override
    public String[] getOptions() {

        Vector<String> options = new Vector<String>(13);

        // Issue some warnings for the current configuration if necessary
        if (m_unpruned) {
            if (m_reducedErrorPruning) {
                System.err.println("WARNING: Unpruned tree and reduced error pruning cannot be selected simultaneously!");
            }
        }
        if (m_unpruned || m_reducedErrorPruning) {
            if (m_CF != 0.25f) {
                System.err.println("WARNING: Changing the confidence will only affect error-based pruning!");
            }
        }
        if (m_unpruned || !m_reducedErrorPruning) {
            if (m_Seed != 1) {
                System.err.println("WARNING: Changing the seed only makes sense when using reduced error pruning");
            }
            if (m_numFolds != 3) {
                System.err.println("WARNING: Changing the number of folds does not make sense if " +
                        "reduced error pruning is not selected.");
            }
        }

        if (m_unpruned) {
            options.add("-U");
        }
        if (m_reducedErrorPruning) {
            options.add("-R");
        }
        if (m_binarySplits) {
            options.add("-B");
        }
        if (!m_useMDLcorrection) {
            options.add("-J");
        }
        if (m_doNotMakeSplitPointActualValue) {
            options.add("-doNotMakeSplitPointActualValue");
        }
        if (m_reducedErrorPruning) {
            options.add("-N");
            options.add("" + m_numFolds);
            options.add("-Q");
            options.add("" + m_Seed);
        } else if (!m_unpruned) {
            options.add("-C");
            options.add("" + m_CF);
        }
        options.add("-M");
        options.add("" + m_minNumObj);
        Collections.addAll(options, super.getOptions());

        return options.toArray(new String[0]);
    }

    /**
     * Returns a description of the classifier
     *
     * @return a string representation of the classifier
     */
    @Override
    public String toString() {

        if (m_root == null) {
            return "No classifier built";
        }
        return "PART decision list\n------------------\n\n" + m_root.toString();
    }

    /**
     * Returns a superconcise version of the model
     *
     * @return a concise version of the model
     */
    @Override
    public String toSummaryString() {

        return "Number of rules: " + m_root.numRules() + "\n";
    }

    /**
     * Return the number of rules.
     *
     * @return the number of rules
     */
    public double measureNumRules() {
        return m_root.numRules();
    }

    /**
     * Returns an enumeration of the additional measure names
     *
     * @return an enumeration of the measure names
     */
    @Override
    public Enumeration<String> enumerateMeasures() {
        Vector<String> newVector = new Vector<String>(1);
        newVector.addElement("measureNumRules");
        return newVector.elements();
    }

    /**
     * Returns the value of the named measure
     *
     * @param additionalMeasureName the name of the measure to query for its value
     * @return the value of the named measure
     * @throws IllegalArgumentException if the named measure is not supported
     */
    @Override
    public double getMeasure(String additionalMeasureName) {
        if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
            return measureNumRules();
        } else {
            throw new IllegalArgumentException(additionalMeasureName
                    + " not supported (PART)");
        }
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String confidenceFactorTipText() {
        return "The confidence factor used for pruning (smaller values incur "
                + "more pruning).";
    }

    /**
     * Get the value of CF.
     *
     * @return Value of CF.
     */
    public float getConfidenceFactor() {

        return m_CF;
    }

    /**
     * Set the value of CF.
     *
     * @param v Value to assign to CF.
     */
    public void setConfidenceFactor(float v) {

        m_CF = v;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String minNumObjTipText() {
        return "The minimum number of instances per rule.";
    }

    /**
     * Get the value of minNumObj.
     *
     * @return Value of minNumObj.
     */
    public int getMinNumObj() {

        return m_minNumObj;
    }

    /**
     * Set the value of minNumObj.
     *
     * @param v Value to assign to minNumObj.
     */
    public void setMinNumObj(int v) {

        m_minNumObj = v;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String reducedErrorPruningTipText() {
        return "Whether reduced-error pruning is used instead of C.4.5 pruning.";
    }

    /**
     * Get the value of reducedErrorPruning.
     *
     * @return Value of reducedErrorPruning.
     */
    public boolean getReducedErrorPruning() {

        return m_reducedErrorPruning;
    }

    /**
     * Set the value of reducedErrorPruning.
     *
     * @param v Value to assign to reducedErrorPruning.
     */
    public void setReducedErrorPruning(boolean v) {

        m_reducedErrorPruning = v;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String unprunedTipText() {
        return "Whether pruning is performed.";
    }

    /**
     * Get the value of unpruned.
     *
     * @return Value of unpruned.
     */
    public boolean getUnpruned() {

        return m_unpruned;
    }

    /**
     * Set the value of unpruned.
     *
     * @param newunpruned Value to assign to unpruned.
     */
    public void setUnpruned(boolean newunpruned) {

        m_unpruned = newunpruned;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String useMDLcorrectionTipText() {
        return "Whether MDL correction is used when finding splits on numeric attributes.";
    }

    /**
     * Get the value of useMDLcorrection.
     *
     * @return Value of useMDLcorrection.
     */
    public boolean getUseMDLcorrection() {

        return m_useMDLcorrection;
    }

    /**
     * Set the value of useMDLcorrection.
     *
     * @param newuseMDLcorrection Value to assign to useMDLcorrection.
     */
    public void setUseMDLcorrection(boolean newuseMDLcorrection) {

        m_useMDLcorrection = newuseMDLcorrection;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String numFoldsTipText() {
        return "Determines the amount of data used for reduced-error pruning. "
                + " One fold is used for pruning, the rest for growing the rules.";
    }

    /**
     * Get the value of numFolds.
     *
     * @return Value of numFolds.
     */
    public int getNumFolds() {

        return m_numFolds;
    }

    /**
     * Set the value of numFolds.
     *
     * @param v Value to assign to numFolds.
     */
    public void setNumFolds(int v) {

        m_numFolds = v;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String seedTipText() {
        return "The seed used for randomizing the data "
                + "when reduced-error pruning is used.";
    }

    /**
     * Get the value of Seed.
     *
     * @return Value of Seed.
     */
    public int getSeed() {

        return m_Seed;
    }

    /**
     * Set the value of Seed.
     *
     * @param newSeed Value to assign to Seed.
     */
    public void setSeed(int newSeed) {

        m_Seed = newSeed;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String binarySplitsTipText() {
        return "Whether to use binary splits on nominal attributes when "
                + "building the partial trees.";
    }

    /**
     * Get the value of binarySplits.
     *
     * @return Value of binarySplits.
     */
    public boolean getBinarySplits() {

        return m_binarySplits;
    }

    /**
     * Set the value of binarySplits.
     *
     * @param v Value to assign to binarySplits.
     */
    public void setBinarySplits(boolean v) {

        m_binarySplits = v;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String doNotMakeSplitPointActualValueTipText() {
        return "If true, the split point is not relocated to an actual data value."
                + " This can yield substantial speed-ups for large datasets with numeric attributes.";
    }

    /**
     * Gets the value of doNotMakeSplitPointActualValue.
     *
     * @return the value
     */
    public boolean getDoNotMakeSplitPointActualValue() {
        return m_doNotMakeSplitPointActualValue;
    }

    /**
     * Sets the value of doNotMakeSplitPointActualValue.
     *
     * @param m_doNotMakeSplitPointActualValue the value to set
     */
    public void setDoNotMakeSplitPointActualValue(
            boolean m_doNotMakeSplitPointActualValue) {
        this.m_doNotMakeSplitPointActualValue = m_doNotMakeSplitPointActualValue;
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 15232 $");
    }

    /**
     * Main method for testing this class.
     *
     * @param argv command line options
     */
    public static void main(String[] argv) {
        runClassifier(new PART(), argv);
    }


}
