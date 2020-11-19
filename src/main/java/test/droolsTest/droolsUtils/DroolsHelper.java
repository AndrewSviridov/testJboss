package test.droolsTest.droolsUtils;

import javafx.util.Pair;
import myDBWeka.myDB_InstanceQuery;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import test.droolsTest.ClassForGlobal;
import test.droolsTest.ClassResultObject;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.*;

public class DroolsHelper {


    public static String Rules =
            "package org.kie1; \n" +
                    "\n" +
                    "import test.droolsTest.ClassForGlobal; \n" +
                    "\n" +
                    "global java.util.List list; \n" +
                    "\n" +
                    "dialect  \"mvel\" \n" +
                    "\n" +
                    "\n" +
                    "declare _expassessment\n" +
                    "\n" +// todo переделать под int, нужно изменить процедуру в postgres. Чтобы record_id int был
                    "record_id : String\n" +
                    "_purposebath : String\n" +
                    "_numberwindows : Integer\n" +
                    "_wallthickness : Integer\n" +
                    "_bathfullness : Integer\n" +
                    "_expassessment : String\n" +
                    "_ceilinginsulation : String\n" +
                    "_areasteamroom : Integer\n" +
                    "_terracearea : Integer\n" +
                    "_assessment : String\n" +
                    "_floorinsulation : String\n" +
                    "_constructionbudget : String\n" +
                    "_buildingarea : String\n" +
                    "_arealounge : Integer\n" +
                    "_averagenumbersquaremetersperperso : Integer\n" +
                    "_material : String\n" +
                    "_sinkarea : Integer\n" +
                    "_typeusebath : String\n" +
                    "\n" +
                    "end\n" +
                    "\n" +
                    "\n" +
                    "//---------------------------------------------------------------------------------------\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                              /*  "rule \"Rule_1\"\n" +
                                "    when\n" +
                                "        _expassessment( _arealounge/_bathfullness<1.5 && _purposebath==\"место отдыха\" ) \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_1\"));//то комната отдыха слишком мала\n" +
                                "end \n" +*/
                    "\n" +
                    "\n" +
                    "\n" +
                                /*"rule \"Rule_2\"\n" +
                                "    when\n" +
                                "        _expassessment( _areasteamroom/_bathfullness<=1.5 && _purposebath==\"для мытья\" ) \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_2\"));//то мойка сликлм мала\n" +
                                "end \n" +*/
                    "\n" +
                    "rule \"Rule_3\"\n" +
                    "    when\n" +
                    "      $fact :  _expassessment( _wallthickness<=15 && _typeusebath==\"круглогодичное\" ) \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"7\",\"\",\"Rule_3\"));//баня холодная\n" +
                    // "       System.out.println(\"Rule_3\");" +
                    "end \n" +
                    "\n" +
                    "\n" +
                    "rule \"Rule_4\"\n" +
                    "    when\n" +
                    "     $fact :   _expassessment( _wallthickness==15 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"true\" && _floorinsulation==\"false\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_4\"));//нужно утеплить пол оценка +2\n" +
                    //   "       System.out.println(\"Rule_4\");" +
                    "end \n" +
                    "\n" +
                    "rule \"Rule_5\"\n" +
                    "    when\n" +
                    "   $fact :     _expassessment( _wallthickness>=20 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"false\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"3\",\"\",\"Rule_5\"));//нужно утеплить потолок оценка +3\n" +
                    //   "       System.out.println(\"Rule_5\");" +
                    "end \n" +
                    "\n" +
                    "rule \"Rule_6\"\n" +
                    "    when\n" +
                    "        _expassessment( _constructionbudget==\"от 100 до 200\" && _buildingarea==\"от 20 до более\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_6\"));//слишком маленький бюджет +4\n" +
                    //   "       System.out.println(\"Rule_6\");" +
                    "end \n" +
                    "\n" +
                    "rule \"Rule_7\"\n" +
                    "    when\n" +
                    "        _expassessment( _constructionbudget==\"от 100 до 200\" && _material==\"дуб\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_7\"));//слишком маленький бюджет +4\n" +
                    //   "       System.out.println(\"Rule_7\");" +
                    "end \n" +
                    "\n" +
                    "\n" +
                    "rule \"Rule_8\"\n" +
                    "    when\n" +
                    "        _expassessment( _constructionbudget==\"от 200 до 400\" && _material==\"дуб\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_8\"));//слишком маленький бюджет +4\n" +
                    //   "       System.out.println(\"Rule_8\");" +
                    "end \n" +
                    "\n" +
                    "rule \"Rule_9\"\n" +
                    "    when\n" +
                    "        _expassessment( _constructionbudget==\"от 200 до 400\"  && _buildingarea==\"от 20 до более\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_9\"));//слишком маленький бюджет +4\n" +
                    //  "       System.out.println(\"Rule_9\");" +
                    "end \n" +
                    "\n" +
                    "\n" +
                    "rule \"Rule_10\"\n" +
                    "    when\n" +
                    "        _expassessment( _constructionbudget==\"от 200 до 400\"  && _buildingarea==\"от 20 до более\" && _material!=\"дуб\" && _averagenumbersquaremetersperperso>6) \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_10\"));//жно построить меньшую дешевую баню +1\n" +
                    //  "       System.out.println(\"Rule_10\");" +
                    "end \n" +
                    "\n" +
                    "rule \"Rule_11\"\n" +
                    "    when\n" +
                    "        _expassessment( _areasteamroom==0 && _sinkarea==0) \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"10\",\"\",\"Rule_11\"));// то это не баня +10\n" +
                    // "       System.out.println(\"Rule_11\");" +
                    "end \n" +
                    "\n"; /*+
                                "rule \"Rule_12\"\n" +
                                "    when\n" +
                                "        _expassessment( ((_arealounge+_sinkarea)/5-_numberwindows)<-2) \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_12\"));// то окон мало +2\n" +
                                "end \n" +
                                "\n" +
                                "rule \"Rule_13\"\n" +
                                "    when\n" +
                                "        _expassessment( ((_arealounge+_sinkarea)/5-_numberwindows)>2) \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_13\"));// то окон слишком много +1\n" +
                                "end \n";
                                */


    public static Pair<Instances, HashMap<String, String>> getInstancesFromDB(Connection connection, String[] paramsReq, String pathProps) {

        Instances data = null;
        HashMap<String, String> map = null;
        try {

            String result = "";
            CallableStatement callableStatement =
                    connection.prepareCall("{? = call pivotcode2(?,?,?,?,?)}");

            // output
            callableStatement.registerOutParameter(1, Types.VARCHAR);

            // input
            int i = 2;
            for (String it : paramsReq) {
                callableStatement.setString(i, it);
                i++;
            }
        /*
        callableStatement.setString(2, "public.view03");
        callableStatement.setString(3, "record_id");
        callableStatement.setString(4, "namefields");
        callableStatement.setString(5, "fieldvalue");
        callableStatement.setString(6, "type_collum");
        */

            // Run  function
            callableStatement.execute();

            // Get result
            result = callableStatement.getString(1);

            //System.out.println(result + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //InstanceQuery

            myDB_InstanceQuery query = new myDB_InstanceQuery();

            query.setM_Connection(connection);
            // System.out.println(query.customPropsFileTipText());
            query.setCustomPropsFile(new File(pathProps));
            query.setQuery(result);

            data = query.retrieveInstances();

            map = query.getStringClassMapAttributes();


        } catch (Exception e) {
            e.printStackTrace();
        }

        Pair<Instances, HashMap<String, String>> pair = new Pair<>(data, map);
        return pair;
    }

    public static Object getValueAttr(String nameClass, Double data) {
        switch (nameClass) {
            case "String":
                return String.valueOf(data);


            case "java.math.BigDecimal":
                return java.math.BigDecimal.valueOf(data);

            case "Boolean":
                return Boolean.valueOf(String.valueOf(data));

            case "Integer":
                return data.intValue();

            case "Long":
                return data.longValue();

            case "Float":
                return data.floatValue();
            case "Double":
                return data;
            // todo сделать остальные типы
            default:
                return String.valueOf(data);
        }
    }

    public static String readMetadata(String type) {

// сделал криво, как из файла анстроек примерное преобразование нашел. Сначала думал сделать по сайту
        //https://www.cis.upenn.edu/~bcpierce/courses/629/jdkdocs/guide/jdbc/getstart/mapping.doc.html
        switch (type.toLowerCase()) {
            case "char":
            case "varchar":
            case "longvarchar":
            case "text":
                return "String";

            case "numeric":
            case "decimal":
                return "java.math.BigDecimal";//

            case "bit":
                return "Boolean";

            case "tinyint":
            case "smallint":
            case "integer":
            case "int2":
            case "int4":
            case "oid":
                return "Integer";

            case "bigint":
                return "Long";
            case "real":
            case "float4":
                return "Float";


            case "float":
            case "float8":
            case "double":
            case "int8":
                return "Double";
            // todo сделать остальные типы
            default:
                return "String";
        }


    }

    //data.classAttribute().name() == > namesDeclareClass
    public static String createDRLFileInMemory(Instances data, KnowledgeBaseWeka knowledgeBaseWeka, HashMap<String, String> attributesClasses, String namesDeclareClass) {

        StringBuilder stringBuilder = new StringBuilder();
        //todo или из проперти или динамически
        stringBuilder
                .append("package org.kie1; \n\n ")
                .append("import test.droolsTest.ClassForGlobal; \n\n")
                .append("global java.util.List list; \n\n")
                .append("dialect  \"mvel\" \n\n")
                .append("\n" +
                        "\n" +
                        "declare " + namesDeclareClass)
                .append("\n\n");

//---------------------------------------------- создание класса
        Set<Map.Entry<String, String>> entrySet = attributesClasses.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            stringBuilder.append(entry.getKey() + " : " + entry.getValue() + "\n");

            //do something with the key and value
        }

        stringBuilder.append("\nend\n\n");


        for (int i = 0; i < knowledgeBaseWeka.getRuleForWekaArrayList().size(); i++) {
            stringBuilder.append("rule Rule_" + i + " when\n");
            stringBuilder.append(data.classAttribute().name() + "(");
            for (int j = 0; j < knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().size(); j++) {
                ConditionForWeka conditionForWeka = knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getList().get(j);

                if (attributesClasses.containsKey(conditionForWeka.getField())) {
                    //System.out.println(attributesClasses.get(conditionForWeka.getField()));
                    if (attributesClasses.get(conditionForWeka.getField()) == "String") {
                        stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + "\"" + conditionForWeka.getValue() + "\"");
                        stringBuilder.append(" && ");
                        continue;
                    }
                }

                stringBuilder.append(conditionForWeka.getField() + ConditionForWeka.Operator.fromValue(conditionForWeka.getOperator().getValue()) + conditionForWeka.getValue());

                stringBuilder.append(" && ");
            }
            stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length());
            stringBuilder.append(") \n");
            stringBuilder.append("then \n");
            stringBuilder.append("list.add(new ClassForGlobal(");
            stringBuilder.append("\"" + knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getThenPart() + "\",");
            stringBuilder.append("\"" + knowledgeBaseWeka.getRuleForWekaArrayList().get(i).getInfo() + "\",");
            stringBuilder.append("\"Rule_" + i + "\"));");

            stringBuilder.append("\nend;\n\n");

        }


        // System.out.println(stringBuilder.toString() + "\n\n");
        return stringBuilder.toString() + "\n\n";


    }


    public static void saveToExcel(List<Map<String, String>> records) throws IOException {
        saveToExcel(records, null);
    }

    public static void saveToExcel(List<Map<String, String>> records, String pathFile) throws IOException {


        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("List1");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            //  Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            //headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            //headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            headerStyle.setFont(font);


/*        List<FactField> ftFields = ft.getFields();
        for (FactField f:ftFields) {
                Cell headerCell = header.createCell(s++);
                headerCell.setCellValue(f.getName());
                headerCell.setCellStyle(headerStyle);
        }

 */
         /*       headerCell = header.createCell(1);
                headerCell.setCellValue("Age");
                headerCell.setCellStyle(headerStyle);
*/
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            int i = 0;
            for (Map<String, String> item : records) {
                if (i == 0) {
                    Row header = sheet.createRow(0);

                    int s = 0;
                    for (String it : item.keySet()) {
                        Cell headerCell = header.createCell(s++);
                        headerCell.setCellValue(it);
                        headerCell.setCellStyle(headerStyle);
                    }
                    i++;
                    continue;
                }
                Row row = sheet.createRow(i++);
                int j = 0;
                for (String it : item.values()) {

                    Cell cell = row.createCell(j++);
                    cell.setCellValue(it);
                    cell.setCellStyle(style);
                }
            }
/*
                cell = row.createCell(1);
                cell.setCellValue(20);
                cell.setCellStyle(style);
*/
            String fileLocation = null;
            String path = null;
            if (pathFile == null) {
                File currDir = new File(".");
                path = currDir.getAbsolutePath();
                fileLocation = path.substring(0, path.length() - 1) + "CommonTest1.xlsx";
            } else {
                fileLocation = pathFile;
            }
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
        } finally {
            if (workbook != null) {

                workbook.close();

            }
        }


    }


    public static String getStatisticSimple(Instances train, Instances test, Classifier cls) throws Exception {

//The kappa statistic measures the agreement of
//prediction with the true class –
//1.0 signifies complete agreement.

        //Статистика каппа измеряет согласие
        //предсказание с истинным классом -
        //1.0 означает полное согласие.
//https://www.researchgate.net/post/can_i_do_kappa_statistics_metric_for_find_the_best_model_among_3_different_models
//https://stackoverflow.com/questions/48720739/cohens-kappa-and-kappa-statistic-in-weka

        Evaluation eval = new Evaluation(train);


        //Notice we build the classifier with the training dataset
        //we initialize evaluation with the training dataset and then
        //evaluate using the test dataset

        //test dataset for evaluation
        //now evaluate model
        //eval.evaluateModel(tree, testDataset);
        eval.evaluateModel(cls, test);

        StringBuilder str = new StringBuilder();
    /*    System.out.println(eval.toSummaryString("Evaluation results:\n", false));

        System.out.println("Correct % = " + eval.pctCorrect());
        System.out.println("Incorrect % = " + eval.pctIncorrect());
        System.out.println("AUC = " + eval.areaUnderROC(1));
        System.out.println("kappa = " + eval.kappa());
        System.out.println("MAE = " + eval.meanAbsoluteError());
        System.out.println("RMSE = " + eval.rootMeanSquaredError());
        System.out.println("RAE = " + eval.relativeAbsoluteError());
        System.out.println("RRSE = " + eval.rootRelativeSquaredError());
        System.out.println("Precision = " + eval.precision(1));
        System.out.println("Recall = " + eval.recall(1));
        System.out.println("fMeasure = " + eval.fMeasure(1));
        System.out.println("Error Rate = " + eval.errorRate());
        //the confusion matrix
        System.out.println(eval.toMatrixString("=== Overall Confusion Matrix ===\n"));
      */

        str.append("Correct % = " + eval.pctCorrect() + "\n");
        str.append("Incorrect % = " + eval.pctIncorrect() + "\n");
        str.append("AUC = " + eval.areaUnderROC(1) + "\n");
        str.append("kappa = " + eval.kappa() + "\n");
        str.append("MAE = " + eval.meanAbsoluteError() + "\n");
        str.append("RMSE = " + eval.rootMeanSquaredError() + "\n");
        str.append("RAE = " + eval.relativeAbsoluteError() + "\n");
        str.append("RRSE = " + eval.rootRelativeSquaredError() + "\n");
        str.append("Precision = " + eval.precision(1) + "\n");
        str.append("Recall = " + eval.recall(1) + "\n");
        str.append("fMeasure = " + eval.fMeasure(1) + "\n");
        str.append("Error Rate = " + eval.errorRate() + "\n");
        str.append(eval.toMatrixString("=== Overall Confusion Matrix ===\n"));
        str.append(eval.toSummaryString() + "\n");
        return str.toString();

    }


    public static Pair<Instances, Instances> splitInstances(Instances data, double splitNumber) {

        Instances partLow = new Instances(data);
        Instances partHight = new Instances(data);

        for (int i = 0; i < partLow.numInstances(); i++) {
            if (partLow.instance(i).classValue() <= splitNumber) {
                partLow.remove(i);
            }
        }

        for (int i = 0; i < partHight.numInstances(); i++) {
            if (partHight.instance(i).classValue() >= splitNumber) {
                partHight.remove(i);
            }
        }


        Pair<Instances, Instances> pair = new Pair<>(partLow, partHight);
        return pair;
    }

    // делим делим правила по пороговому значению
    public static Pair<List<RuleForWeka>, List<RuleForWeka>> splitPorogGreneratedRules(KnowledgeBaseWeka KB, double splitNumber) {

        ArrayList<RuleForWeka> listLow = new ArrayList<>();
        ArrayList<RuleForWeka> listHigh = new ArrayList<>();

        for (RuleForWeka it : KB.getRuleForWekaArrayList()) {
            if (Double.parseDouble(it.getThenPart()) <= splitNumber) {
                listLow.add(it);
            } else {
                listHigh.add(it);
            }
        }

        return new Pair(listLow, listHigh);
    }


    // делим на правила которые меньше всего повлияли на оценку факта ошибку
    public static Pair<List<ClassForGlobal>, List<ClassForGlobal>> getLowAndHighRule(ClassResultObject resultObject) {
        List<ClassForGlobal> listLow = new ArrayList<>(resultObject.getOutputGlobalList());
        List<ClassForGlobal> listHigh = new ArrayList<>();

        for (ClassForGlobal it : listLow) {
            if (Double.parseDouble(it.getThenPart()) <= resultObject.getAssessment()) {
                listLow.remove(it);
            } else {
                listHigh.add(it);
            }
        }

        return new Pair(listLow, listHigh);
    }


    public static void saveToARFF(Instances data, String path) {
        try {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);//set the dataset we want to convert
            //and save as ARFF
            saver.setFile(new File(path));
            saver.writeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void getROC(Instances ttain, Instances test, Classifier cl, Integer indexAttr) throws Exception {
        // load data
        // Instances data = ConverterUtils.DataSource.read(args[0]);
        // data.setClassIndex(data.numAttributes() - 1);

        // evaluate classifier
        // Classifier cl = new NaiveBayes();
        Evaluation eval = new Evaluation(ttain);
        // eval.crossValidateModel(cl, data, 10, new Random(1));
        eval.evaluateModel(cl, test);
        // generate curve
        ThresholdCurve tc = new ThresholdCurve();
        int classIndex = indexAttr;
        Instances curve = tc.getCurve(eval.predictions(), classIndex);

        // plot curve
        ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
        tvp.setROCString("(Area under ROC = " +
                Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
        tvp.setName(curve.relationName());
        PlotData2D plotdata = new PlotData2D(curve);
        plotdata.setPlotName(curve.relationName());
        plotdata.addInstanceNumberAttribute();
        // specify which points are connected
        boolean[] cp = new boolean[curve.numInstances()];
        for (int n = 1; n < cp.length; n++)
            cp[n] = true;
        plotdata.setConnectPoints(cp);
        // add plot
        tvp.addPlot(plotdata);

        // display curve
        final JFrame jf = new JFrame("WEKA ROC: " + tvp.getName());
        jf.setSize(500, 400);
        jf.getContentPane().setLayout(new BorderLayout());
        jf.getContentPane().add(tvp, BorderLayout.CENTER);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setVisible(true);
    }


}
