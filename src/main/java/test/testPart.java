package test;

import rule.Condition;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import test.implPart.MyClassifierDecList;
import test.implPart.MyPart;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.part.ClassifierDecList;
import weka.classifiers.rules.part.MakeDecList;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class testPart {

    public static void main(String[] args) throws Exception {
        //load dataset
        //C:\\Program Files\\Weka-3-8\\data\\weather.numeric.arff
        //DataSource source = new DataSource("/home/likewise-open/ACADEMIC/csstnns/Desktop/qdb.arff");
        //D:\WEKA\data\weather.numeric.arff
        //D:\WEKA\arff-datasets-master\arff-datasets-master\classification
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("D:\\WEKA\\arff-datasets-master\\arff-datasets-master\\classification\\zoo.arff");
        Instances dataset = source.getDataSet();

        //set class index to the last attribute
        dataset.setClassIndex(dataset.numAttributes() - 1);
        //use a simple filter to remove a certain attribute
        //set up options to remove 1st attribute
        // String[] opts = new String[]{ "-R", "1,2"};
        //create a Remove object (this is the filter class)
        //Remove remove = new Remove();

        //System.out.println(remove.getCapabilities());
        //set the filter options
        //remove.setOptions(opts);
        //pass the dataset to the filter
        //remove.setInputFormat(dataset);


//        System.out.println(remove.getOutputFormat());
        //apply the filter
        //      Instances newData = Filter.useFilter(dataset, remove);
        MyPart part = new MyPart();
        part.buildClassifier(dataset);

        //  System.out.println(part.getRules());
        // System.out.println(part.getRules().getToSelectModeL());
        for (MyClassifierDecList item : part.getRules().getTheRules()) {
            //text.append(m_localModel.dumpLabel(0, m_train) + "\n");
            // System.out.println(item. getM_train().toString());
            //      System.out.println(item.getM_localModel().toString());
            System.out.println(item.toString());
            // System.out.println(item.getM_localModel().dumpLabel(0,item.getM_train()));


            //   System.out.println(item.toString());
        }

        // System.out.println("/////////////\n"+part.getRules().getTheRules());
        System.out.println("++++++++++++++++++++-+++++ PART ++++++++++++++++++++++++++++++++++++++++++");
        // System.out.println(part.toString());


        PART newPart = new PART();
        newPart.setDebug(true);
        newPart.buildClassifier(dataset);
        String par = newPart.toString();
        String[] arr = par.split("\n\n");
        List<String> itemList = new ArrayList<String>(Arrays.asList(arr));
        itemList.remove(0);

        KnowledgeBaseWeka KB = new KnowledgeBaseWeka();

        KB.setInfo(itemList.get(itemList.size() - 2) + "\n" + itemList.get(itemList.size() - 1));
        itemList.remove(itemList.size() - 2);
        itemList.remove(itemList.size() - 1);
        for (String item : itemList) {


            RuleForWeka rule = new RuleForWeka();
            // ConditionForWeka.Operator dd= ConditionForWeka.Operator.fromValue("=");

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
                /*
                if (i.contains("<=")){
                    String[] arrPartCon2=i.split("<=");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue("<="));
                    if(arrPartCon2[1].contains(":")){
                        String[] arr22=arrPartCon2[1].split(":");
                    }
                }
                if(i.contains(">=")){
                    String[] arrPartCon2=i.split(">=");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue(">="));
                }
                if(i.contains(">")){
                    String[] arrPartCon2=i.split(">");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue(">"));
                }
                if(i.contains("<")){
                    String[] arrPartCon2=i.split("<");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue("<"));
                }
                if(i.contains("!=")){
                    String[] arrPartCon2=i.split("!=");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue("!="));
                }
                if(i.contains("==")){
                    String[] arrPartCon2=i.split("==");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue("=="));
                }
                if(i.contains("=")){
                    String[] arrPartCon2=i.split("=");
                    newCond.setField(arrPartCon2[0]);
                    newCond.setOperator(ConditionForWeka.Operator.fromValue("="));
                }

                 */

            }

            KB.getRuleForWekaArrayList().add(rule);
        }

        System.out.println(newPart);
        System.out.println(newPart.toString());
        System.out.println(newPart.toSummaryString());
        System.out.println();

        System.out.println("++++++++++++++++++++-+++++++ J48  ++++++++++++++++++++++++++++++++++++++++");

        J48 newJ48 = new J48();
        newJ48.buildClassifier(dataset);
        System.out.println(newJ48);
        System.out.println(newJ48.toSource("class2"));
        System.out.println(newJ48.graph().intern());
        System.out.println(newJ48.toString());
        System.out.println(newJ48.toSummaryString());
        System.out.println(newJ48.prefix());
        // MakeDecList makeDecList=new MakeDecList();


        String[] arr3 = newJ48.toString().split("\n\n");
        String[] arr5 = arr3[1].split("\n");
        List<String> itemList4 = new ArrayList<String>(Arrays.asList(arr5));

/*
        private int numVertices;
        private LinkedList<Integer> adjLists[];
        private boolean visited[];

        Graph(int v)
        {
            numVertices = v;
            visited = new boolean[numVertices];
            adjLists = new LinkedList[numVertices];
            for (int i=0; i i = adjLists[currVertex].listIterator();
            while (i.hasNext())
            {
                int adjVertex = i.next();
                if (!visited[adjVertex])
                {
                    visited[adjVertex] = true;
                    queue.add(adjVertex);
                }
            }
        }
    }
*/

        System.out.println("++++++++++++++++++++-+++++++ JRip  ++++++++++++++++++++++++++++++++++++++++");

        JRip newJRip = new JRip();
        newJRip.buildClassifier(dataset);
        System.out.println(newJRip.globalInfo());
        System.out.println(newJRip.toString());


    }

   /* public String[] gethhh(){
            return
    }
*/
}
