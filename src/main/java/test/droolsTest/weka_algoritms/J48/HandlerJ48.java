package test.droolsTest.weka_algoritms.J48;

import javafx.util.Pair;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.Rule;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Instances;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerJ48 {
    private final J48 j48;
    private final KnowledgeBaseWeka KB;

    public HandlerJ48() {
        this.j48 = new J48();
        this.KB = new KnowledgeBaseWeka();
    }

    public void setOption(String[] option) throws Exception {
        this.j48.setOptions(option);
    }


    /**
     * Method J48Parser in which weka classifier rules are parsed for the extraction of their
     * conditions and classes (label applied to patterns which have been classified by that rule)
     *
     * @param classifierRules Rules obtained by the classifier
     * @return ruleList List of rules in a format that can be compared to the existing set of rules
     */

    public List<String> J48Parser(String classifierRules) {

        List<String> ruleList = new ArrayList<String>();
        List<String> conditionList = new ArrayList<String>();
        String ruleJ48 = "([\\|\\s]*)([\\w\\_]+)\\s?([\\>\\=\\<]+)\\s?([\\w\\d\\.\\_\\/]+)\\s?\\:?\\s?(\\w*)";
        String branch = "\\|\\s*";
        String[] lines = classifierRules.split("\\r?\\n");
        int i = 0;

        Pattern J48Pattern = Pattern.compile(ruleJ48);
        Pattern branchPattern = Pattern.compile(branch);
        String rule = "";
        String condition = "";
        for (i = 1; i < lines.length; i++) {
            Matcher J48Matcher = J48Pattern.matcher(lines[i]);
            while (J48Matcher.find()) {
                int count = 0;
                Matcher branchMatcher = branchPattern.matcher(J48Matcher.group(1));
                while (branchMatcher.find()) {
                    count++;
                }
                // Attribute name
                condition += J48Matcher.group(2);
                /* Relationship, J48Matcher.group(2) can be =, <, <=, >, >= */
                condition += J48Matcher.group(3);
                // Value
                condition += J48Matcher.group(4);
                if (J48Matcher.group(5).isEmpty()) {
                    condition += " AND ";
                    conditionList.add(count, condition);
                    condition = "";
                } else {
                    condition += " THEN ";
                    // Label
                    condition += J48Matcher.group(5);
                    conditionList.add(count, condition);
                    List<String> finalItems = conditionList.subList(0, count + 1);
                    Iterator<String> it = finalItems.iterator();
                    while (it.hasNext()) {
                        String item = it.next();
                        rule += item;
                    }
                    ruleList.add(rule);
                    rule = "";
                    condition = "";
                }
            }
        }

        return ruleList;

    }


    public KnowledgeBaseWeka getRules(Pair<Instances, HashMap<String, Class>> pair) throws Exception {

        j48.setDebug(true);
        j48.buildClassifier(pair.getKey());
        String j48String = j48.toString();
        System.out.println(j48String);
        System.out.println(j48.prefix());
        List<String> dfg = J48Parser(j48.graph());

        System.out.println(j48.graph());

        String[] options = new String[4];
        options[0] = "-C";
        options[1] = "0.11";
        options[2] = "-M";
        options[3] = "3";
        J48 tree = new J48();
        tree.setOptions(options);
        tree.buildClassifier(pair.getKey());
        System.out.println(tree.getCapabilities().toString());
        System.out.println(tree.graph());


        String[] lines = j48String.split("\\r?\\n");
        List<String> itemList = new ArrayList<String>(Arrays.asList(lines));
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(0);
        itemList.remove(0);
        itemList.remove(0);

        RuleForWeka rule = new RuleForWeka();
        String[] arrSign = new String[]{"!=", ">=", "<=", "=", ">", "<"};
        for (String it : itemList) {
            ConditionForWeka newCond = new ConditionForWeka();
            if (it.contains("|")) {
                String[] arr1 = it.split("\\|");
                String last = arr1[arr1.length - 1].trim();

                if (last.contains(":")) {
                    String[] partLast = last.split(":");
                    RuleForWeka rule1 = new RuleForWeka();
                    ConditionForWeka newCond1 = new ConditionForWeka();
                    for (String sign : arrSign) {
                        if (it.contains(sign)) {
                            String[] part1 = partLast[0].split(sign);

                            if (!KB.getRuleForWekaArrayList().isEmpty() &&
                                    KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().
                                            get(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1).
                                            getField().trim().equals(part1[0].trim())) {
                                // newCond=   KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size()-1).getList()
                                //          .get((KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1));
                                //KB.getRuleForWekaArrayList().add(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size()-1));

                                rule1.getList().addAll(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList());
                                rule1.getList().remove(rule1.getList().size() - 1);
                                KB.getRuleForWekaArrayList().add(rule1);

                                newCond1.setField(part1[0].trim());
                                newCond1.setValue(part1[1].trim());
                                newCond1.setOperator(ConditionForWeka.Operator.fromValue(sign));
                                newCond1.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                                rule1.getList().add(newCond1);
                                break;
                                //KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size()-1).getList()
                                //        .get((KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1));
                            } else {
                                newCond.setField(part1[0].trim());
                                newCond.setValue(part1[1].trim());
                                newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                                newCond.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                                break;
                            }
                        } else continue;
                    }

                    if (!KB.getRuleForWekaArrayList().isEmpty() &&
                            KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().
                                    get(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1).
                                    getField().equals(newCond1.getField())) {


                        if (partLast[1].contains("(")) {
                            String[] partInfo = partLast[1].split("\\(");
                            rule1.setInfo("(" + partInfo[1].trim());
                            rule1.setThenPart(partInfo[0].trim());
                        }
                        KB.getRuleForWekaArrayList().add(rule1);

                    } else {

                        rule.getList().add(newCond);

                        if (partLast[1].contains("(")) {
                            String[] partInfo = partLast[1].split("\\(");
                            rule.setInfo("(" + partInfo[1].trim());
                            rule.setThenPart(partInfo[0].trim());
                        }
                        KB.getRuleForWekaArrayList().add(rule);
                    }
                } else {

                    for (String sign : arrSign) {
                        if (it.contains(sign)) {
                            String[] part1 = last.split(sign);


                            newCond.setField(part1[0].trim());
                            newCond.setValue(part1[1].trim());
                            newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                            newCond.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                            break;
                        } else continue;
                    }
                    rule.getList().add(newCond);
                }

            } else {
                for (String sign : arrSign) {
                    if (it.contains(sign)) {
                        String[] part1 = it.split(sign);
                        newCond.setField(part1[0].trim());
                        newCond.setValue(part1[1].trim());
                        newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                        newCond.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                        break;
                    }
                    continue;
                }
                rule.getList().add(newCond);
            }

       /*     String[] erg = it.split("=>");
            String[] valieStr = erg[1].split("=");
            String[] valueTrue = valieStr[1].split("\\(");
*/
        }

        System.out.println();

        /*
        String[] arr = par.split("\n\n");
        List<String> itemList = new ArrayList<String>(Arrays.asList(arr));
        itemList.remove(0);

        KB.setInfo(itemList.get(itemList.size() - 2) + "\n" + itemList.get(itemList.size() - 1));
        itemList.remove(itemList.size() - 2);
        itemList.remove(itemList.size() - 1);
        for (String item : itemList) {
            RuleForWeka rule = new RuleForWeka();

            String[] arrPartCon = item.split("AND\n");
            for (String i : arrPartCon) {
                ConditionForWeka newCond = new ConditionForWeka();
                System.out.println();
                String[] arrSign = new String[]{"!=", ">=", "<=", "=", ">", "<"};
                for (String sign : arrSign) {
                    // if (i.contains(sign)) {
                    String[] arrPartCon2 = i.split(sign);
                    if (arrPartCon2.length != 1) {
                        newCond.setField(arrPartCon2[0].trim());
                        if (ConditionForWeka.Operator.fromValue(sign).equals(ConditionForWeka.Operator.EQUAL)) {
                            newCond.setOperator(ConditionForWeka.Operator.EQUAL_TO);
                        } else {
                            newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                        }

                        if (arrPartCon2[1].contains(":")) {
                            String[] arr22 = arrPartCon2[1].split(":");
                            newCond.setValue(arr22[0].trim());
                            String[] arrkk = arr22[1].trim().split(" ");
                            rule.setThenPart(arrkk[0].trim());
                            rule.setInfo(arrkk[1].trim());
                        } else {
                            newCond.setValue(arrPartCon2[1].trim());
                        }
                        rule.getList().add(newCond);
                        break;
                    }
                }

            }

            KB.getRuleForWekaArrayList().add(rule);
        }

        return KB;
        */

        return null;
    }


    public KnowledgeBaseWeka getRules2(Pair<Instances, HashMap<String, Class>> pair) throws Exception {

        j48.setDebug(true);
        j48.buildClassifier(pair.getKey());
        String j48String = j48.toString();
        System.out.println(j48String);
        System.out.println(j48.prefix());
        List<String> dfg = J48Parser(j48.graph());

        System.out.println(j48.graph());

        String[] options = new String[4];
        options[0] = "-C";
        options[1] = "0.11";
        options[2] = "-M";
        options[3] = "3";
        J48 tree = new J48();
        tree.setOptions(options);
        tree.buildClassifier(pair.getKey());
        System.out.println(tree.getCapabilities().toString());
        System.out.println(tree.graph());


        String[] lines = j48String.split("\\r?\\n");
        List<String> itemList = new ArrayList<String>(Arrays.asList(lines));
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        itemList.remove(0);
        itemList.remove(0);
        itemList.remove(0);

        RuleForWeka rule = new RuleForWeka();
        String[] arrSign = new String[]{"!=", ">=", "<=", "=", ">", "<"};
        for (String it : itemList) {
            ConditionForWeka newCond = new ConditionForWeka();
            if (it.contains("|")) {
                String[] arr1 = it.split("\\|");
                String last = arr1[arr1.length - 1].trim();

                if (last.contains(":")) {
                    String[] partLast = last.split(":");
                    RuleForWeka rule1 = new RuleForWeka();
                    ConditionForWeka newCond1 = new ConditionForWeka();
                    for (String sign : arrSign) {
                        if (it.contains(sign)) {
                            String[] part1 = partLast[0].split(sign);

                            if (!KB.getRuleForWekaArrayList().isEmpty() &&
                                    KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().
                                            get(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1).
                                            getField().trim().equals(part1[0].trim())) {
                                // newCond=   KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size()-1).getList()
                                //          .get((KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1));
                                //KB.getRuleForWekaArrayList().add(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size()-1));

                                rule1.getList().addAll(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList());
                                rule1.getList().remove(rule1.getList().size() - 1);
                                KB.getRuleForWekaArrayList().add(rule1);

                                newCond1.setField(part1[0].trim());
                                newCond1.setValue(part1[1].trim());
                                newCond1.setOperator(ConditionForWeka.Operator.fromValue(sign));
                                newCond1.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                                rule1.getList().add(newCond1);
                                break;
                                //KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size()-1).getList()
                                //        .get((KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1));
                            } else {
                                newCond.setField(part1[0].trim());
                                newCond.setValue(part1[1].trim());
                                newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                                newCond.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                                break;
                            }
                        } else continue;
                    }

                    if (!KB.getRuleForWekaArrayList().isEmpty() &&
                            KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().
                                    get(KB.getRuleForWekaArrayList().get(KB.getRuleForWekaArrayList().size() - 1).getList().size() - 1).
                                    getField().equals(newCond1.getField())) {


                        if (partLast[1].contains("(")) {
                            String[] partInfo = partLast[1].split("\\(");
                            rule1.setInfo("(" + partInfo[1].trim());
                            rule1.setThenPart(partInfo[0].trim());
                        }
                        KB.getRuleForWekaArrayList().add(rule1);

                    } else {

                        rule.getList().add(newCond);

                        if (partLast[1].contains("(")) {
                            String[] partInfo = partLast[1].split("\\(");
                            rule.setInfo("(" + partInfo[1].trim());
                            rule.setThenPart(partInfo[0].trim());
                        }
                        KB.getRuleForWekaArrayList().add(rule);
                    }
                } else {

                    for (String sign : arrSign) {
                        if (it.contains(sign)) {
                            String[] part1 = last.split(sign);


                            newCond.setField(part1[0].trim());
                            newCond.setValue(part1[1].trim());
                            newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                            newCond.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                            break;
                        } else continue;
                    }
                    rule.getList().add(newCond);
                }

            } else {
                for (String sign : arrSign) {
                    if (it.contains(sign)) {
                        String[] part1 = it.split(sign);
                        newCond.setField(part1[0].trim());
                        newCond.setValue(part1[1].trim());
                        newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                        newCond.setTypeClass(pair.getValue().get(part1[0].trim()).getSimpleName());
                        break;
                    }
                    continue;
                }
                rule.getList().add(newCond);
            }

       /*     String[] erg = it.split("=>");
            String[] valieStr = erg[1].split("=");
            String[] valueTrue = valieStr[1].split("\\(");
*/
        }

        System.out.println();

        /*
        String[] arr = par.split("\n\n");
        List<String> itemList = new ArrayList<String>(Arrays.asList(arr));
        itemList.remove(0);

        KB.setInfo(itemList.get(itemList.size() - 2) + "\n" + itemList.get(itemList.size() - 1));
        itemList.remove(itemList.size() - 2);
        itemList.remove(itemList.size() - 1);
        for (String item : itemList) {
            RuleForWeka rule = new RuleForWeka();

            String[] arrPartCon = item.split("AND\n");
            for (String i : arrPartCon) {
                ConditionForWeka newCond = new ConditionForWeka();
                System.out.println();
                String[] arrSign = new String[]{"!=", ">=", "<=", "=", ">", "<"};
                for (String sign : arrSign) {
                    // if (i.contains(sign)) {
                    String[] arrPartCon2 = i.split(sign);
                    if (arrPartCon2.length != 1) {
                        newCond.setField(arrPartCon2[0].trim());
                        if (ConditionForWeka.Operator.fromValue(sign).equals(ConditionForWeka.Operator.EQUAL)) {
                            newCond.setOperator(ConditionForWeka.Operator.EQUAL_TO);
                        } else {
                            newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                        }

                        if (arrPartCon2[1].contains(":")) {
                            String[] arr22 = arrPartCon2[1].split(":");
                            newCond.setValue(arr22[0].trim());
                            String[] arrkk = arr22[1].trim().split(" ");
                            rule.setThenPart(arrkk[0].trim());
                            rule.setInfo(arrkk[1].trim());
                        } else {
                            newCond.setValue(arrPartCon2[1].trim());
                        }
                        rule.getList().add(newCond);
                        break;
                    }
                }

            }

            KB.getRuleForWekaArrayList().add(rule);
        }

        return KB;
        */

        return null;
    }


}
