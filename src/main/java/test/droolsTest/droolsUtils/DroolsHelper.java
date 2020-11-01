package test.droolsTest.droolsUtils;

import javafx.util.Pair;
import myDBWeka.myDB_InstanceQuery;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import weka.core.Instances;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
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
                    "        _expassessment( _wallthickness<=15 && _typeusebath==\"круглогодичное\" ) \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"7\",\"\",\"Rule_3\"));//баня холодная\n" +
                    // "       System.out.println(\"Rule_3\");" +
                    "end \n" +
                    "\n" +
                    "\n" +
                    "rule \"Rule_4\"\n" +
                    "    when\n" +
                    "        _expassessment( _wallthickness==15 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"true\" && _floorinsulation==\"false\") \n" +
                    "    then\n" +
                    "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_4\"));//нужно утеплить пол оценка +2\n" +
                    //   "       System.out.println(\"Rule_4\");" +
                    "end \n" +
                    "\n" +
                    "rule \"Rule_5\"\n" +
                    "    when\n" +
                    "        _expassessment( _wallthickness>=20 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"false\") \n" +
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

}
