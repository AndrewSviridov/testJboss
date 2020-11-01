package test.droolsTest.weka_algoritms.part;

import javafx.util.Pair;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import test.droolsTest.weka_algoritms.IHandlerAlgorithm;
import weka.classifiers.rules.PART;
import weka.core.Instances;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HandlerPart implements IHandlerAlgorithm {
    private final PART part;
    private final KnowledgeBaseWeka KB;

    public HandlerPart() {
        this.part = new PART();
        this.KB = new KnowledgeBaseWeka();
    }

    public void setOption(String[] option) throws Exception {
        this.part.setOptions(option);
    }

    //todo разобраться c Exception-нами
    public KnowledgeBaseWeka getRules(Pair<Instances, HashMap<String, String>> pair) throws Exception {

        part.setDebug(true);
        part.buildClassifier(pair.getKey());
        String par = part.toString();
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
                            newCond.setTypeClass(pair.getValue().get(arrPartCon2[0].trim()));
                        } else {
                            newCond.setValue(arrPartCon2[1].trim());
                            newCond.setTypeClass(pair.getValue().get(arrPartCon2[0].trim()));
                        }
                        rule.getList().add(newCond);
                        break;
                    }
                }

            }

            KB.getRuleForWekaArrayList().add(rule);
        }

        return KB;
    }

    public KnowledgeBaseWeka getRulesOld(Instances data) throws Exception {

        part.setDebug(true);
        part.buildClassifier(data);
        String par = part.toString();
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
    }

}
