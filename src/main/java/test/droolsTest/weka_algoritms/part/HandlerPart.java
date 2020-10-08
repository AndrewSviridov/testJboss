package test.droolsTest.weka_algoritms.part;

import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import weka.classifiers.rules.PART;
import weka.core.Instances;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandlerPart {
    private final PART part;
    private final KnowledgeBaseWeka KB;

    public HandlerPart() {
        this.part = new PART();
        this.KB = new KnowledgeBaseWeka();
    }

    public KnowledgeBaseWeka getRules(Instances dataset) throws Exception {

        part.setDebug(true);
        part.buildClassifier(dataset);
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
                String[] arrSign = new String[]{"=", ">", "<", "!=", ">=", "<="};
                for (String sign : arrSign) {
                    if (i.contains(sign)) {
                        String[] arrPartCon2 = i.split(sign);
                        newCond.setField(arrPartCon2[0].trim());
                        newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                        rule.getList().add(newCond);
                        if (arrPartCon2[1].contains(":")) {
                            String[] arr22 = arrPartCon2[1].split(":");
                            newCond.setValue(arr22[0].trim());
                            String[] arrkk = arr22[1].trim().split(" ");
                            rule.setThenPart(arrkk[0].trim());
                            rule.setInfo(arrkk[1].trim());
                        } else {
                            newCond.setValue(arrPartCon2[1].trim());
                        }
                    }
                }

            }

            KB.getRuleForWekaArrayList().add(rule);
        }

        return KB;
    }


}
