package test.droolsTest.weka_algoritms.JRIP;

import javafx.util.Pair;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.*;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.SortLabels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerJRIP {
    private final JRip jrip;
    private final KnowledgeBaseWeka KB;

    public HandlerJRIP() {
        this.jrip = new JRip();
        this.KB = new KnowledgeBaseWeka();
    }

    public void setOption(String[] option) throws Exception {
        this.jrip.setOptions(option);
    }
//https://github.com/quepas/Oy-mate/tree/7c1da59ded1d35fe3550c03d156cd7ed3da2f770

//https://github.com/MusesProject/MusesServer/tree/066bce8e86c6bca68f612558b3acea5e10237fda/src/main/java/eu/musesproject/server/dataminer

    /**
     * Method JRipParser in which weka classifier rules are parsed for the extraction of their
     * conditions and classes (label applied to patterns which have been classified by that rule)
     *
     * @param classifierRules Rules obtained by the classifier
     * @return ruleList List of rules in a format that can be compared to the existing set of rules
     */

    public List<String> JRipParser(String classifierRules) {

        //todo можно доработать экспертное выражение
        List<String> ruleList = new ArrayList<String>();
        String ruleJRip = "\\(?(\\w+)([\\s\\>\\=\\<]+)([\\w\\.]+)\\)?";
        String[] lines = classifierRules.split("\\r?\\n");
        int i = 0;

        Pattern JRipPattern = Pattern.compile(ruleJRip);
        for (i = 1; i < lines.length; i++) {
            Matcher JRipMatcher = JRipPattern.matcher(lines[i]);
            String rule = "";
            while (JRipMatcher.find()) {
                // if(JRipMatcher.group(1).contentEquals("label")) {
                // Label
                //         rule += " THEN ";
                //       rule += JRipMatcher.group(3);
                //     ruleList.add(rule);
                //} else {
                // Attribute name
                rule += JRipMatcher.group(1);
                /* Relationship, JRipMatcher.group(2) can be =, <, <=, >, >= */
                rule += JRipMatcher.group(2);
                // Value
                rule += JRipMatcher.group(3);
                rule += " AND ";
                //}
            }
        }

        return ruleList;

    }


    public KnowledgeBaseWeka getRules(Pair<Instances, HashMap<String, Class>> pair) throws Exception {

        // System.out.println("!! getRules");

        jrip.setDebug(false);
        jrip.buildClassifier(pair.getKey());
        String jripString = this.jrip.toString();

        Attribute classAttr = pair.getKey().classAttribute();
        // SortLabels sortclass = new SortLabels();
        //sortclass.se
        //-------------------------test


        //-------------------------
        ArrayList<Rule> ruleset = jrip.getRuleset();
        ruleset.remove(ruleset.size() - 1);
        for (Rule elem : ruleset) {
            RuleForWeka rule = new RuleForWeka();
            JRip.RipperRule ripperRule = (JRip.RipperRule) elem;

            for (JRip.Antd antd : ripperRule.getAntds()) {
                ConditionForWeka newCond = new ConditionForWeka();
                newCond.setField(antd.getAttr().name());
                //              System.out.println("!"+antd.getAttrValue());
                //            System.out.println("!"+antd.getAttr().numValues());
/* LE("<="),
    GE(">="),
    E("=");*/
                if (antd instanceof JRip.NumericAntd) {
                    String symbol = (antd.getAttrValue() == 0 ? "<=" : ">=");
                    double value = ((JRip.NumericAntd) antd).getSplitPoint();
                    newCond.setValue(String.valueOf(value));
                    newCond.setOperator(ConditionForWeka.Operator.fromValue(symbol));
                    newCond.setTypeClass(pair.getValue().get(antd.getAttr().name()).getSimpleName());
                    //LHS.add(new Element(attrName, symbol, value));
                } else if (antd instanceof JRip.NominalAntd) {

                    String value = antd.getAttr().value((int) antd.getAttrValue());
                    newCond.setOperator(ConditionForWeka.Operator.fromValue("="));
                    newCond.setTypeClass(pair.getValue().get(antd.getAttr().name()).getSimpleName());
                    newCond.setValue(String.valueOf(value));
                    //LHS.add(new Element(attrName, Symbol.E, value));
                }
                rule.getList().add(newCond);
            }
/*            int regeee = (int) ripperRule.getConsequent();
            String geeer = ripperRule.toString(classAttr);
            String geeer2 = ripperRule.toString();
*/

            rule.setThenPart(classAttr.value((int) ripperRule.getConsequent()));
            KB.getRuleForWekaArrayList().add(rule);
        }


        //todo криво, нужно как то отлифтровать лейблы атрибута класса чтобы класс в правой часте совпадал с теми что в строке общей
        String[] lines = jripString.split("\\r?\\n");
        List<String> itemList = new ArrayList<String>(Arrays.asList(lines));
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(0);
        itemList.remove(0);
        itemList.remove(0);

        if (itemList.size() != ruleset.size()) {
            ruleset.remove(ruleset.size() - 1);
        }

        for (Integer i = 0; i < KB.getRuleForWekaArrayList().size(); i++) {
            String[] erg = itemList.get(i).split("=>");
            String[] valieStr = erg[1].split("=");
            String[] valueTrue = valieStr[1].split("\\(");
            KB.getRuleForWekaArrayList().get(i).setThenPart(valueTrue[0].trim());
            KB.getRuleForWekaArrayList().get(i).setInfo("(" + valueTrue[1].trim());

        }


/*
        JRip.RipperRule wfe = (JRip.RipperRule) this.jrip.getRuleset().get(0);
        ArrayList<JRip.Antd> rrr = wfe.getAntds();
        RuleStats ggg = jrip.getRuleStats(0);
        String efr = ggg.getRevision();
        double ww = rrr.get(0).getAttrValue();
        double reg = wfe.getConsequent();
        */
        System.out.println();
    /*
            JRip.RipperRule ripperRule = (JRip.RipperRule) elem;
            ArrayList<Element> LHS = new ArrayList<>();
            for (JRip.Antd antd : ripperRule.getAntds()) {
                String attrName = antd.getAttr().name();
                if (antd instanceof JRip.NumericAntd) {
                    Symbol symbol = (antd.getAttrValue() == 0 ? Symbol.LE : Symbol.GE);
                    double value = ((JRip.NumericAntd)antd).getSplitPoint();
                    LHS.add(new Element(attrName, symbol, value));
                } else if (antd instanceof JRip.NominalAntd) {
                    String value = antd.getAttr().value((int) antd.getAttrValue());
                    LHS.add(new Element(attrName, Symbol.E, value));
                }
            }
            Element RHS = new Element(classAttr.value((int)ripperRule.getConsequent()));
            rules.add(new oymate.data.Rule(LHS, RHS));
        }
*/
        return KB;
    }


    public KnowledgeBaseWeka getRules2(Instances dataset) throws Exception {


        System.out.println("!! getRules");

        jrip.setDebug(false);
        jrip.buildClassifier(dataset);
        String jripString2 = this.jrip.toString();

        System.out.println(((JRip.RipperRule) this.jrip.getRuleset().get(0)).toString(dataset.classAttribute()));
        JRip.RipperRule wfe = (JRip.RipperRule) this.jrip.getRuleset().get(0);

/*
        // create classifier/algorithm

            JRip jr = new JRip();

            jr.buildClassifier(dataset);
*/
        /************************* 10-fold Cross-validation ************************/
            /*int folds = 10;
            int seed = 1;
            Random rand = new Random(seed);
            Evaluation eval = new Evaluation(dataset);
            eval.crossValidateModel(jr, dataset, folds, rand);
            */
        /******************************************************************/
/*
            FastVector fv = (FastVector) jr.getRuleset();
            JSONArray ja = new JSONArray();
			*/

/*
        FastVector fv = (FastVector) jrip.getRuleset();
        // // JSONArray ja = new JSONArray();
        //

        for (int i = 0; i < fv.size(); i++) {
            // ja.put(((RipperRule)
            // (fv.elementAt(i))).toString(dataset.classAttribute()));
            System.out.println("Rule " + i + " :" + ((JRip.RipperRule) (fv.elementAt(i))).toString(dataset.classAttribute()));
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
*/
        System.out.println(jripString2);
        List<String> result = JRipParser(jripString2);

        /*
        * JRip jrip = new JRip();
		String[] options = Utils.splitOptions("-F 3 -N 2.0 -O 2 -S 1");
		jrip.setOptions(options);
        * */

        ArrayList<Rule> rules = this.jrip.getRuleset();

        int size = rules.size();

        System.out.println("size is " + size);
        RuleStats rs = this.jrip.getRuleStats(0);

        rs.toString();

        for (int i = 0; i < size; i++) {

            System.out.println("i : " + i);
            double[] d = rs.getSimpleStats(i);
            // System.out.println(((RipperRule)rs.getRuleset().get(i)).toString(label));
            System.out.println(Arrays.toString(d));
        }

        for (int i = 0; i < size; i++) {
            System.out.println(((JRip.RipperRule) this.jrip.getRuleset().get(i))
                    .toString(dataset.classAttribute()));
            if (i == 5) {
                System.out.println(((JRip.RipperRule) this.jrip.getRuleStats(1)
                        .getRuleset().get(0)).toString(dataset.classAttribute()));
            } else {
                System.out.println(((JRip.RipperRule) this.jrip.getRuleStats(0)
                        .getRuleset().get(i)).toString(dataset.classAttribute()));
            }
        }

        // rules.get(0).grow(dataset);
        //   System.out.println(rules.get(0).getRevision());
        // System.out.println(jrip);

        for (Rule item : rules) {
            //   RuleForWeka rule = new RuleForWeka();
            //  System.out.println("!!!!!!!!!");
            // System.out.println(item.toString());
            //   for (String i : item) {
            {
                ConditionForWeka newCond = new ConditionForWeka();

            }


        }

        //  return KB;


        return null;
    }


}
