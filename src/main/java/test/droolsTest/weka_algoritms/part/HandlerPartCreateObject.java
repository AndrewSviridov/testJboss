package test.droolsTest.weka_algoritms.part;

import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import weka.classifiers.rules.PART;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandlerPartCreateObject {
    private final PART part;
    private final KnowledgeBaseWeka KB;

    public HandlerPartCreateObject() {
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
                String[] arrSign = new String[]{"!=", ">=", "<=", "=", ">", "<"};
                for (String sign : arrSign) {
                    // if (i.contains(sign)) {
                    String[] arrPartCon2 = i.split(sign);
                    if (arrPartCon2.length != 1) {
                        newCond.setField(arrPartCon2[0].trim());


                        if (dataset.attribute(arrPartCon2[0].trim()).isNominal()) {
                            newCond.setTypeClass("java.lang.String");
                        }
                        if (dataset.attribute(arrPartCon2[0].trim()).isNumeric()) {
                            newCond.setTypeClass("java.lang.Long");
                        }
                        if (dataset.attribute(arrPartCon2[0].trim()).isString()) {
                            newCond.setTypeClass("java.lang.String");
                        }
                        if (dataset.attribute(arrPartCon2[0].trim()).isDate()) {
                            newCond.setTypeClass("java.lang.String");
                        }
                        if (dataset.attribute(arrPartCon2[0].trim()).isRelationValued()) {
                            System.out.println("attribute is RelationValued");
                            throw new RuntimeException("attribute is RelationValued");
                        }

                        if (ConditionForWeka.Operator.fromValue(sign).equals(ConditionForWeka.Operator.EQUAL)) {
                            newCond.setOperator(ConditionForWeka.Operator.EQUAL_TO);
                        } else {
                            newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
                        }

                        if (arrPartCon2[1].contains(":")) {
                            String[] arr22 = arrPartCon2[1].split(":");

                            //---------------------------------------------------------

                            if (sign == ">=") {

                                Long tt = Long.parseLong(arr22[0].trim()) + 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arr22[0].trim()) + 1L));
                                // newCond.setTypeClass("java.lang.Long");
                            }

                            if (sign == "<=") {
                                Long tt = Long.parseLong(arr22[0].trim()) - 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arr22[0].trim()) - 1L));
                                //newCond.setTypeClass("java.lang.Long");
                            }

                            if (sign == ">") {
                                Long tt = Long.parseLong(arr22[0].trim()) + 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arr22[0].trim()) + 1L));
                                //newCond.setTypeClass("java.lang.Long");
                            }

                            if (sign == "<") {
                                Long tt = Long.parseLong(arr22[0].trim()) - 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arr22[0].trim()) - 1L));
                                //newCond.setTypeClass("java.lang.Long");
                            }

                            //https://regex101.com/r/kjGNuw/202
                            //https://husl.ru/questions/575862

                            //https://askdev.ru/q/regulyarnoe-vyrazhenie-dlya-chisel-s-plavayuschey-zapyatoy-40269/
                            //https://regex101.com/r/EplgDw/1
                            if (sign == "!=" || sign == "=" || sign == "==") {
                                if (arr22[0].trim().matches("[+-]?(([1-9][0-9]*)|(0))([.,][0-9]+)?")) {
                                    //  newCond.setTypeClass("java.lang.Long");
                                    newCond.setValue(arr22[0].trim());
                                } else {
                                    newCond.setValue(arr22[0].trim());
                                    //newCond.setTypeClass("java.lang.String");
                                }
                            }


                            //-------------------------------------------------------

                            //  newCond.setValue(arr22[0].trim());


                            String[] arrkk = arr22[1].trim().split(" ");
                            rule.setThenPart(arrkk[0].trim());
                            rule.setInfo(arrkk[1].trim());
                        } else {
                            if (sign == ">=") {
                                Long tt = Long.parseLong(arrPartCon2[1].trim()) + 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arrPartCon2[1].trim()) + 1L));
                                // newCond.setTypeClass("java.lang.Long");
                            }

                            if (sign == "<=") {
                                Long tt = Long.parseLong(arrPartCon2[1].trim()) - 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arrPartCon2[1].trim()) - 1L));
                                //newCond.setTypeClass("java.lang.Long");
                            }

                            if (sign == ">") {
                                Long tt = Long.parseLong(arrPartCon2[1].trim()) + 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arrPartCon2[1].trim()) + 1L));
                                //newCond.setTypeClass("java.lang.Long");
                            }

                            if (sign == "<") {
                                Long tt = Long.parseLong(arrPartCon2[1].trim()) - 1L;
                                newCond.setValue(String.valueOf(Long.parseLong(arrPartCon2[1].trim()) - 1L));
                                //newCond.setTypeClass("java.lang.Long");
                            }

                            //https://regex101.com/r/kjGNuw/202
                            //https://husl.ru/questions/575862

                            //https://askdev.ru/q/regulyarnoe-vyrazhenie-dlya-chisel-s-plavayuschey-zapyatoy-40269/
                            //https://regex101.com/r/EplgDw/1
                            if (sign == "!=" || sign == "=" || sign == "==") {
                                if (arrPartCon2[1].trim().matches("[+-]?(([1-9][0-9]*)|(0))([.,][0-9]+)?")) {
                                    //  newCond.setTypeClass("java.lang.Long");
                                    newCond.setValue(arrPartCon2[1].trim());
                                } else {
                                    newCond.setValue(arrPartCon2[1].trim());
                                    //newCond.setTypeClass("java.lang.String");
                                }
                            }


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
