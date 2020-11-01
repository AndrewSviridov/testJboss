package test.droolsTest.droolsUtils;

import DAO.C3POData;
import javafx.util.Pair;
import test.droolsTest.ClassResultObject;
import test.droolsTest.weka_algoritms.J48.HandlerJ48;
import test.droolsTest.weka_algoritms.JRIP.HandlerJRIP;
import test.droolsTest.weka_algoritms.part.HandlerPart;
import weka.core.Attribute;
import weka.core.Instances;

import java.sql.Connection;
import java.util.*;

public class CommonTests {

    public static void main(String[] args) throws Exception {
        test1();
    }

    private static void test1() throws Exception {
        Connection conn = C3POData.getDataSource().getConnection();

        String[] arr = {"public.view03", "record_id", "namefields", "fieldvalue", "type_collum"};
        String path = "C:\\Users\\Andrew\\wekafiles\\props\\postgres\\DatabaseUtils.props";
        Pair<Instances, HashMap<String, String>> pair = DroolsHelper.getInstancesFromDB(conn, arr, path);
        //HashMap<String, Class> stringClassMapAttributes= DroolsHelper.

        //       System.out.println(instances.toSummaryString());
        //System.out.println(data);
        // todo класс для классификации нужно передевать как нибудь
        String nameClass = "_expassessment";
        String pathForLog = "src/main/resources/log/TestHandlerPartTest1Log";

        Attribute attrClass = pair.getKey().attribute(nameClass);

        //set class index to the last attribute

        System.out.println("Count attributes: " + (pair.getKey().numAttributes() - 1));
        pair.getKey().setClassIndex(attrClass.index());


        pair.getKey().randomize(new java.util.Random(0));
        int trainSize = (int) Math.round(pair.getKey().numInstances() * 0.8);
        int testSize = pair.getKey().numInstances() - trainSize;
        Instances train = new Instances(pair.getKey(), 0, trainSize);
        Instances test = new Instances(pair.getKey(), trainSize, testSize);

        //todo убрать поле с record_id из instances и там в обработках где факты создаются тоже вместо просто записи из Instances по полю record id, сделать перечисление, ну i вставлять
        // пока это на формирование правил не влияет, не попадает в правила поле record_id

        //прогонка данных по правилам от алгоритма
        HandlerPart handlerPart = new HandlerPart();

        HandlerJRIP handlerJRIP = new HandlerJRIP();

        HandlerJ48 handlerJ48 = new HandlerJ48();

        //Instances train, Instances test, HashMap<String, String> map, String nameClassForDRL, String pathForLog, IHandlerAlgorithm handlerAlgorithm
        Experiment1 partEX1 = new Experiment1(train, test, pair.getValue(), nameClass, pathForLog, handlerPart);
        partEX1.init();

        Experiment1 jripEX1 = new Experiment1(train, test, pair.getValue(), nameClass, pathForLog, handlerJRIP);
        jripEX1.init();

        //прогонка данных по правилам от человека
        // много условностей со согласованием классов для правил ручкасм и передечи названия описанного в правиле класса
        //todo спарсить название класс (nameClass) из текста правила и все, ну пока. сделать потом
        Experiment2 humanDRL_EX2 = new Experiment2(train, test, pair.getValue(), nameClass, pathForLog);
        humanDRL_EX2.init();

        System.out.println();

        List<Map<String, String>> listRecords = new ArrayList<>();
        //--------------------------------------

        //   for (int i = 0; i < test.numInstances(); i++) {

        System.out.println(partEX1.getResultObjectArrayList().size());
        System.out.println(humanDRL_EX2.getResultObjectArrayList().size());
        System.out.println("-------------------------------------------------");
        int k = 0;

        for (ClassResultObject itHuman : humanDRL_EX2.getResultObjectArrayList()) {
            for (ClassResultObject itAlgoritm : partEX1.getResultObjectArrayList()) {
                if (itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id").toString().equals(
                        itHuman.getFactType().get(itHuman.getFcObject(), "record_id").toString()
                )) {
                    TreeMap<String, String> mapFields = new TreeMap<>();
                    mapFields.put("idПрецедента", itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id").toString());
                    mapFields.put("Класс Прецедента(ЭксОцен)", itHuman.getFactType().get(itHuman.getFcObject(), "_expassessment").toString());
                    mapFields.put("Оценка ЭС (БЗ/человек)", itHuman.getAssessment().toString());
                    mapFields.put("Оценка ЭС (БЗ/алгоритм)", itAlgoritm.getAssessment().toString());
                    mapFields.put("Разница оценек", String.valueOf(Math.abs(itHuman.getAssessment() - itAlgoritm.getAssessment())));

                    listRecords.add(mapFields);
                    System.out.println("idInstances: " + itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id").toString() +
                                    " | " + itHuman.getFactType().get(itHuman.getFcObject(), "_expassessment").toString() +
                                    " | " + itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "_expassessment").toString() +
                                    " оценка по правилам Human " + itHuman.getAssessment() +
                                    " оценка по правилам Algoritm " + itAlgoritm.getAssessment() +
                                    " :=  " + Math.abs(itHuman.getAssessment() - itAlgoritm.getAssessment())
                            //String.valueOf(test.instance(i).value(test.attribute("_expassessment"))- it.getFactType().get(it.getFcObject(),"_expassessment")
                    );

                    k++;
                }
            }
            //     System.out.println(k++);
            //k++;
            //  System.out.println(itHuman.getFactType().get(itHuman.getFcObject(), "record_id").toString());
        }

        DroolsHelper.saveToExcel(listRecords);
        System.out.println("K - " + k);
        //System.out.println("-------------------------------------------------");
        //k=0;
        for (ClassResultObject itAlgoritm : partEX1.getResultObjectArrayList()) {
            //  if (it.getFactType().c)
            // System.out.println(k++);
            //      k++;
            System.out.println(itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id").toString());
            //   humanDRL_EX2.getResultObjectArrayList()

     /*       Object idAlg2 = itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id");
            for (ClassResultObject itHuman:humanDRL_EX2.getResultObjectArrayList() ) {
                Object idHuman = itHuman.getFactType().get(itHuman.getFcObject(), "record_id");
                Object idAlg = itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id");
                //DroolsHelper.g
                if (idAlg.equals(idHuman)){
                    System.out.println(String.valueOf(idHuman));
                    System.out.println(String.valueOf(idAlg));
                    System.out.println("!!");
                }

      */
/*
                        System.out.println("idInstances: "+val+" idResultObject: "+it.getFactType().get(it.getFcObject(),"record_id")+
                                " | "+test.instance(i).stringValue(test.attribute("_expassessment"))+" | "+
                                it.getFactType().get(it.getFcObject(),"_expassessment")+
                                " :=  "+test.instance(i).stringValue(test.attribute("_expassessment"))+" , "+ it.getFactType().get(it.getFcObject(),"_expassessment")
                        );
  */
            //   }

/*
                if (pair.getValue().get(test.attribute("record_id").name()).equals("String")){
                    String val = test.instance(i).stringValue(test.attribute("record_id"));
                   // String valAsse = test.instance(i).stringValue(test.attribute(""));
                    if (it.getFactType().get(it.getFcObject(),"record_id").equals(val)){
                        System.out.println("idInstances: "+val+" idResultObject: "+it.getFactType().get(it.getFcObject(),"record_id")+
                        " | "+test.instance(i).stringValue(test.attribute("_expassessment"))+" | "+
                                        it.getFactType().get(it.getFcObject(),"_expassessment")+
                                " :=  "+test.instance(i).stringValue(test.attribute("_expassessment"))+" , "+ it.getFactType().get(it.getFcObject(),"_expassessment")
                        );
                    }
                  //  ft.set(fcObject,  test.attribute(j).name(),  test.instance(i).stringValue(j));
                }else{

                    // отрабатывать должно только это
                    String val = test.instance(i).stringValue(test.attribute("record_id"));
                    Object valueAttr = DroolsHelper.getValueAttr(pair.getValue().get(test.attribute("record_id").name()),
                            test.instance(i).value(test.attribute("record_id")));
                    if (it.getFactType().get(it.getFcObject(),"record_id").equals(valueAttr)){
                        System.out.println("idInstances: "+val+" idResultObject: "+it.getFactType().get(it.getFcObject(),"record_id")+
                                " | "+test.instance(i).value(test.attribute("_expassessment"))+" | "+
                                it.getFactType().get(it.getFcObject(),"_expassessment")+
                                " :=  "+String.valueOf(test.instance(i).value(test.attribute("_expassessment"))- it.getFactType().get(it.getFcObject(),"_expassessment")
                        );
                    }
                    //ft.set(fcObject,  test.attribute(j).name(), valueAttr);

                }
*/
        }
        System.out.println("K - " + k);


/*
            for (int j = 0; j < test.numAttributes(); j++) {


                   // test.attribute("record_id");
                    if (pair.getValue().get(test.attribute("record_id").name()).equals("String")){
                        ft.set(fcObject,  test.attribute(j).name(),  test.instance(i).stringValue(j));
                    }else{

                        Object valueAttr = DroolsHelper.getValueAttr(pair.getValue().get(test.attribute(j).name()), test.instance(i).value(j));
                        ft.set(fcObject,  test.attribute(j).name(), valueAttr);

                    }


            }
*/


    }
    // }


    /*
     *
     *
     *
     *
     * */
}
