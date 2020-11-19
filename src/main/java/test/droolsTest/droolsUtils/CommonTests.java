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

//C:\Program Files\Java\jdk1.8.0_191\bin>java -jar "C:\Program Files\Weka-3-9-4\weka.jar" auto-weka
public class CommonTests {

    public static void main(String[] args) throws Exception {
        test1();
    }

    private static void test2() throws Exception {

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


        pair.getKey().randomize(new java.util.Random(5));
        int trainSize = (int) Math.round(pair.getKey().numInstances() * 0.8);
        int testSize = pair.getKey().numInstances() - trainSize;
        Instances train = new Instances(pair.getKey(), 0, trainSize);
        Instances test = new Instances(pair.getKey(), trainSize, testSize);

        //todo убрать поле с record_id из instances и там в обработках где факты создаются тоже вместо просто записи из Instances по полю record id, сделать перечисление, ну i вставлять
        // пока это на формирование правил не влияет, не попадает в правила поле record_id

        //прогонка данных по правилам от алгоритма
        HandlerPart handlerPart = new HandlerPart();


        //Instances train, Instances test, HashMap<String, String> map, String nameClassForDRL, String pathForLog, IHandlerAlgorithm handlerAlgorithm
        Experiment1 partEX1 = new Experiment1(train, test, pair.getValue(), nameClass, pathForLog, handlerPart);
        partEX1.init();


        String partStatistic = DroolsHelper.getStatisticSimple(train, test, handlerPart.getPart());

        //прогонка данных по правилам от человека
        // много условностей со согласованием классов для правил ручкасм и передечи названия описанного в правиле класса
        //todo спарсить название класс (nameClass) из текста правила и все, ну пока. сделать потом
        Experiment3 humanDRL_EX3 = new Experiment3(train, test, pair.getValue(), nameClass, pathForLog);
        humanDRL_EX3.init();

        System.out.println();

        //---------------------------------------------------------------
        double parog = 0.8;


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


        pair.getKey().randomize(new java.util.Random(5));
        int trainSize = (int) Math.round(pair.getKey().numInstances() * 0.8);
        int testSize = pair.getKey().numInstances() - trainSize;
        Instances train = new Instances(pair.getKey(), 0, trainSize);
        Instances test = new Instances(pair.getKey(), trainSize, testSize);

        DroolsHelper.saveToARFF(pair.getKey(), "D:\\JAVA\\testJboss\\allData.arff");
        // DroolsHelper.saveToARFF(train,"D:\\JAVA\\testJboss\\trainData.arff");
        //DroolsHelper.saveToARFF(test,"D:\\JAVA\\testJboss\\testData.arff");

        //todo убрать поле с record_id из instances и там в обработках где факты создаются тоже вместо просто записи из Instances по полю record id, сделать перечисление, ну i вставлять
        // пока это на формирование правил не влияет, не попадает в правила поле record_id

        //прогонка данных по правилам от алгоритма
        HandlerPart handlerPart = new HandlerPart();

        HandlerJRIP handlerJRIP = new HandlerJRIP();

        HandlerJ48 handlerJ48 = new HandlerJ48();

        //Instances train, Instances test, HashMap<String, String> map, String nameClassForDRL, String pathForLog, IHandlerAlgorithm handlerAlgorithm
        Experiment1 partEX1 = new Experiment1(train, test, pair.getValue(), nameClass, pathForLog, handlerPart);
        partEX1.init();


        String partStatistic = DroolsHelper.getStatisticSimple(train, test, handlerPart.getPart());

        //    System.out.println(partStatistic);

        Experiment1 jripEX1 = new Experiment1(train, test, pair.getValue(), nameClass, pathForLog, handlerJRIP);
        jripEX1.init();

        String jripStatistic = DroolsHelper.getStatisticSimple(train, test, handlerJRIP.getJrip());
        //System.out.println(jripStatistic);
        //прогонка данных по правилам от человека
        // много условностей со согласованием классов для правил ручкасм и передечи названия описанного в правиле класса
        //todo спарсить название класс (nameClass) из текста правила и все, ну пока. сделать потом
        Experiment2 humanDRL_EX2 = new Experiment2(train, test, pair.getValue(), nameClass, pathForLog);
        humanDRL_EX2.init();

        System.out.println();

        List<Map<String, String>> listRecords = new ArrayList<>();
        //--------------------------------------

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

        }

        DroolsHelper.saveToExcel(listRecords, "D:\\JAVA\\testJboss\\CommonTest1Part.xlsx");
        System.out.println("K - " + k);
        System.out.println("-------------------------------------------------");


        listRecords.clear();
        System.out.println(jripEX1.getResultObjectArrayList().size());
        System.out.println(humanDRL_EX2.getResultObjectArrayList().size());
        System.out.println("-------------------------------------------------");
        k = 0;

        for (ClassResultObject itHuman : humanDRL_EX2.getResultObjectArrayList()) {
            for (ClassResultObject itAlgoritm : jripEX1.getResultObjectArrayList()) {
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

        }

        DroolsHelper.saveToExcel(listRecords, "D:\\JAVA\\testJboss\\CommonTest1JRIP.xlsx");
        System.out.println("K - " + k);
        System.out.println("-------------------------------------------------");
        System.out.println("PART-----");
        System.out.println(partStatistic);
        System.out.println("JRIP-----");
        System.out.println(jripStatistic);
        System.out.println("------------");
        //   DroolsHelper.getROC(train,test,handlerPart.getPart(),1);
    }


    /*
    *
    *https://www.researchgate.net/post/can_i_do_kappa_statistics_metric_for_find_the_best_model_among_3_different_models
    * https://en.wikipedia.org/wiki/Cohen%27s_kappa
    *
        Evaluation eval = new Evaluation(dataset);
        Random rand = new Random(1);
        int folds = 10;

        //Notice we build the classifier with the training dataset
        //we initialize evaluation with the training dataset and then
        //evaluate using the test dataset

        //test dataset for evaluation
        DataSource source1 = new DataSource("C:\\Program Files\\Weka-3-8\\data\\iris-test.arff");
        Instances testDataset = source1.getDataSet();
        //set class index to the last attribute
        testDataset.setClassIndex(testDataset.numAttributes() - 1);
        //now evaluate model
        //eval.evaluateModel(tree, testDataset);
        eval.crossValidateModel(tree, testDataset, folds, rand);
        System.out.println(eval.toSummaryString("Evaluation results:\n", false));

* Получает процент правильно классифицированных экземпляров (то есть для которых было сделано правильное предсказание).
        System.out.println("Correct % = " + eval.pctCorrect());
        *Получает процент экземпляров, неправильно классифицированных (то есть для которых было сделано неверное предсказание).
        System.out.println("Incorrect % = " + eval.pctIncorrect());
        *Возвращает область под ROC для тех прогнозов, которые были собраны в методе evaluateClassifier(классификатор, экземпляры).
        *  Возвращает Utils.missingValue (), если область недоступна.
        System.out.println("AUC = " + eval.areaUnderROC(1));
        * Возвращает значение статистики каппа, если класс является номинальным.
        System.out.println("kappa = " + eval.kappa());
        * Returns the mean absolute error.
        * https://en.wikipedia.org/wiki/Mean_absolute_error
        System.out.println("MAE = " + eval.meanAbsoluteError());
        * Returns the root mean squared error.
        * https://www.researchgate.net/post/How_to_calculate_Root_Relative_Squared_Error_and_Relative_Absolute_Error_in_Weka
        * https://stackoverflow.com/questions/10776673/formula-for-relative-absolute-error-and-root-relative-squared-error-used-in
        * https://katie.mtech.edu/classes/csci347/Resources/Weka_error_measurements.pdf
        System.out.println("RMSE = " + eval.rootMeanSquaredError());
        * Returns the relative absolute error.
        System.out.println("RAE = " + eval.relativeAbsoluteError());
        * Returns the root relative squared error if the class is numeric.
        System.out.println("RRSE = " + eval.rootRelativeSquaredError());
        * Calculate the precision with respect to a particular class. This is defined as
 correctly classified positives
 ------------------------------
  total predicted as positive

Unclassified instances are not included in the calculation.
        System.out.println("Precision = " + eval.precision(1));
        * Calculate the F-Measure with respect to a particular class. This is defined as
 2 * recall * precision
 ----------------------
   recall + precision

Returns zero when both precision and recall are zero. Unclassified instances are not included in the calculation.
        System.out.println("Recall = " + eval.recall(1));
        * Calculate the F-Measure with respect to a particular class. This is defined as
 2 * recall * precision
 ----------------------
   recall + precision

Returns zero when both precision and recall are zero. Unclassified instances are not included in the calculation.
        System.out.println("fMeasure = " + eval.fMeasure(1));
        * Returns the estimated error rate or the root mean squared error (if the class is numeric).
        System.out.println("Error Rate = " + eval.errorRate());
        //the confusion matrix
        System.out.println(eval.toMatrixString("=== Overall Confusion Matrix ===\n"));
    *
    *
    * */


}
