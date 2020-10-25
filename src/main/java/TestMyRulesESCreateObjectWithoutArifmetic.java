import myDBWeka.myDB_InstanceQuery;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import test.droolsTest.ClassForGlobal;
import test.droolsTest.weka_algoritms.part.HandlerPartCreateObject;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.*;

public class TestMyRulesESCreateObjectWithoutArifmetic {

        public static void main(String[] args) throws Exception {

                String pkg1 =
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
                                "\n" +
                                "record_id : String\n" +
                                "_arealounge : Long\n" +
                                "_areasteamroom : Long\n" +
                                "_assessment : String\n" +
                                "_averagenumbersquaremetersperperso : Long\n" +
                                "_bathfullness : Long\n" +
                                "_buildingarea : String\n" +
                                "_ceilinginsulation : String\n" +
                                "_constructionbudget : String\n" +
                                "_expassessment : String\n" +
                                "_floorinsulation : String\n" +
                                "_material : String\n" +
                                "_numberwindows : Long\n" +
                                "_purposebath : String\n" +
                                "_sinkarea : Long\n" +
                                "_terracearea : Long\n" +
                                "_typeusebath : String\n" +
                                "_wallthickness : Long\n" +
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
                                "       System.out.println(\"Rule_3\");" +
                                "end \n" +
                                "\n" +
                                "\n" +
                                "rule \"Rule_4\"\n" +
                                "    when\n" +
                                "        _expassessment( _wallthickness==15 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"true\" && _floorinsulation==\"false\") \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_4\"));//нужно утеплить пол оценка +2\n" +
                                "       System.out.println(\"Rule_4\");" +
                                "end \n" +
                                "\n" +
                                "rule \"Rule_5\"\n" +
                                "    when\n" +
                                "        _expassessment( _wallthickness>=20 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"false\") \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"3\",\"\",\"Rule_5\"));//нужно утеплить потолок оценка +3\n" +
                                "       System.out.println(\"Rule_5\");" +
                                "end \n" +
                                "\n" +
                                "rule \"Rule_6\"\n" +
                                "    when\n" +
                                "        _expassessment( _constructionbudget==\"от 100 до 200\" && _buildingarea==\"от 20 до более\") \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_6\"));//слишком маленький бюджет +4\n" +
                                "       System.out.println(\"Rule_6\");" +
                                "end \n" +
                                "\n" +
                                "rule \"Rule_7\"\n" +
                                "    when\n" +
                                "        _expassessment( _constructionbudget==\"от 100 до 200\" && _material==\"дуб\") \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_7\"));//слишком маленький бюджет +4\n" +
                                "       System.out.println(\"Rule_7\");" +
                                "end \n" +
                                "\n" +
                                "\n" +
                                "rule \"Rule_8\"\n" +
                                "    when\n" +
                                "        _expassessment( _constructionbudget==\"от 200 до 400\" && _material==\"дуб\") \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_8\"));//слишком маленький бюджет +4\n" +
                                "       System.out.println(\"Rule_8\");" +
                                "end \n" +
                                "\n" +
                                "rule \"Rule_9\"\n" +
                                "    when\n" +
                                "        _expassessment( _constructionbudget==\"от 200 до 400\"  && _buildingarea==\"от 20 до более\") \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_9\"));//слишком маленький бюджет +4\n" +
                                "       System.out.println(\"Rule_9\");" +
                                "end \n" +
                                "\n" +
                                "\n" +
                                "rule \"Rule_10\"\n" +
                                "    when\n" +
                                "        _expassessment( _constructionbudget==\"от 200 до 400\"  && _buildingarea==\"от 20 до более\" && _material!=\"дуб\" && _averagenumbersquaremetersperperso>6) \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_10\"));//жно построить меньшую дешевую баню +1\n" +
                                "       System.out.println(\"Rule_10\");" +
                                "end \n" +
                                "\n" +
                                "rule \"Rule_11\"\n" +
                                "    when\n" +
                                "        _expassessment( _areasteamroom==0 && _sinkarea==0) \n" +
                                "    then\n" +
                                "       list.add(new ClassForGlobal(\"10\",\"\",\"Rule_11\"));// то это не баня +10\n" +
                                "       System.out.println(\"Rule_11\");" +
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

            /*
                            "rule \"Rule_14_test\"\n" +
                            "    when\n" +
                            "        _expassessment( _arealounge/_sinkarea<1.5 ) \n" +
                            "    then\n" +
                            "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_14_test\"));//то комната отдыха слишком мала\n" +
                            "end \n";
*/
                System.out.println(pkg1);

                byte[] bytes = pkg1.getBytes(Charset.defaultCharset().displayName());//"windows-1251"

                //   pkg1 = new String(bytes, StandardCharsets.UTF_8);

                //  assertEquals(rawString, utf8EncodedString);

                Connection connection = C3POData.getDataSource().getConnection();

                String result = "";
                CallableStatement callableStatement = connection.prepareCall("{? = call pivotcode2(?,?,?,?,?)}");
                //  CallableStatement callableStatement = connection.prepareCall(runFunction);

                //----------------------------------

                // output
                callableStatement.registerOutParameter(1, Types.VARCHAR);

                // input
                callableStatement.setString(2, "public.view03");
                callableStatement.setString(3, "record_id");
                callableStatement.setString(4, "namefields");
                callableStatement.setString(5, "fieldvalue");
                callableStatement.setString(6, "type_collum");

                // Run hello() function
                callableStatement.execute();

                // Get result
                result = callableStatement.getString(1);

                System.out.println(result + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                //InstanceQuery


                myDB_InstanceQuery query = new myDB_InstanceQuery();

                query.setM_Connection(connection);
                System.out.println(query.customPropsFileTipText());
                query.setCustomPropsFile(new File("C:\\Users\\Andrew\\wekafiles\\props\\postgres\\DatabaseUtils.props"));
                query.setQuery(result);

                Instances data = query.retrieveInstances();


          /*  Instance inst;
            int count = 0;
            while ((inst = loader.getNextInstance(structure)) != null) {
                data.add(inst);
                count++;
                if ((count % 100) == 0)
                    System.out.println(count + " rows read so far.");
            }*/


                System.out.println(data.toSummaryString());
                //System.out.println(data);

                Attribute attrClass = data.attribute("_expassessment");


                //set class index to the last attribute
                // data.setClassIndex(data.numAttributes() - 1);
                System.out.println(data.numAttributes() - 1);
                data.setClassIndex(attrClass.index());


                HandlerPartCreateObject handlerPartCreateObject = new HandlerPartCreateObject();

                KnowledgeBaseWeka knowledgeBaseWeka = handlerPartCreateObject.getRules(data);

                //---------------------------------------------------------------------------


                KieServices ks = KieServices.Factory.get();
                KieFileSystem kfs = ks.newKieFileSystem();


                kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
                KieModuleModel km = ks.newKieModuleModel();
                km.newKieBaseModel("rules")
                        .addPackage("org.kie1");
                kfs.writeKModuleXML(km.toXML());

                km.newKieBaseModel("rules").setDefault(true);

                KieResources kr = ks.getResources();
                Resource r1 = kr.newByteArrayResource(pkg1.getBytes(StandardCharsets.UTF_8))//pkg1.getBytes()
                        .setResourceType(ResourceType.DRL)
                        .setSourcePath("org/kie1/myRules.drl");

                kfs.write(r1);

                KieBuilder builder = ks.newKieBuilder(kfs);
                builder.buildAll();

                // assertEquals(Collections.emptyList(), builder.getResults().getMessages(Message.Level.ERROR));

                KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
                FactType ft = kc.getKieBase("rules").getFactType("org.kie1", "_expassessment");

                //assertNotNull(ft);
                //assertNotNull(ft.getFactClass());
                // assertEquals("org.kie1", ft.getFactClass().getSuperclass().getName());


                KieSession kSession = kc.getKieBase("rules").newKieSession();

                // Set up listeners.
                kSession.addEventListener(new DebugAgendaEventListener());
                kSession.addEventListener(new DebugRuleRuntimeEventListener());

// Set up a file-based audit logger.
                //            KieRuntimeLogger logger = KieServices.get().getLoggers().newFileLogger( ksession, "./target/helloworld" );

// Set up a ThreadedFileLogger so that the audit view reflects events while debugging.
                //          KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( ksession, "./target/helloworld", 1000 );

                //-----------------------------------------------------------------------------------

                // setup the debug listeners
                kSession.addEventListener(new DebugAgendaEventListener());
                //  kSession.addEventListener( new DebugWorkingMemoryEventListener() );
// setup the audit logging
                KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, "src/main/resources/log/TestMyRulesESCreateObjectWithoutArifmeticLog");

                List<ClassForGlobal> list = new ArrayList<>();
                kSession.setGlobal("list", list);


                List<Object> ListObject = new ArrayList<>();
                List<Map<String, String>> ListMapObject = new ArrayList<>();
                Object fcObject = null;
                try {

                        System.out.println();
//(14+8)/5

                        for (RuleForWeka item : knowledgeBaseWeka.getRuleForWekaArrayList()) {
                                fcObject = ft.newInstance();

                                System.out.println("before " + fcObject);
                                for (ConditionForWeka it : item.getList()) {
                                        if (it.getTypeClass().equals("java.lang.String")) {

                                                //       byte[] bytes = pkg1.getBytes(Charset.defaultCharset().displayName());//"windows-1251"

                                                //   pkg1 = new String(bytes, StandardCharsets.UTF_8);

                                                //  assertEquals(rawString, utf8EncodedString);
                                                //"windows-1251"
                                                //String val=new String( it.getValue().toString().getBytes(Charset.defaultCharset().displayName()), StandardCharsets.UTF_8);
                                                //String val=new String( it.getValue().toString().getBytes(Charset.defaultCharset().displayName()), StandardCharsets.UTF_8);
                                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                                if (new String("круглогодичное".getBytes(Charset.defaultCharset().displayName()), StandardCharsets.UTF_8).equals(it.getValue())) {
                                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!yes");
                                                }
                                                if (new String("круглогодичное".getBytes(Charset.defaultCharset().displayName()), StandardCharsets.UTF_8).equals(new String(it.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))) {
                                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!yes");
                                                }
                                                if (new String("круглогодичное".getBytes(StandardCharsets.UTF_8), "CP1251").equals(new String(it.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))) {
                                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!yes2");
                                                }
                                                if (new String("круглогодичное".getBytes("CP1251"), StandardCharsets.UTF_8).equals(new String(it.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))) {
                                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!yes2");
                                                }
                                                if ("круглогодичное".equals(new String(it.getValue().getBytes("windows-1251"), StandardCharsets.UTF_8))) {
                                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!yes2");
                                                }
                                                if ("круглогодичное".equals(new String(it.getValue().getBytes("windows-1251"), StandardCharsets.UTF_8))) {
                                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!yes2");
                                                }
                                                System.out.println(Charset.defaultCharset().displayName());
                                                System.out.println(it.getValue());
                                                System.out.println(new String(it.getValue().getBytes(Charset.defaultCharset().displayName()), StandardCharsets.UTF_8));

                                                System.out.println(new String(it.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                                                String val = it.getValue();
                                                System.out.println("1 " + it.getField() + " _ " + val + " _ " + it.getTypeClass());
                                                ft.set(fcObject, it.getField(), val);
                                        } else {
                                                System.out.println("2 " + it.getField() + " _ " + it.getValue() + " _ " + it.getTypeClass());
                                                System.out.println("3 " + it.getValue() + "!!!!!!!");
                                                if (it.getValue().equals("0") || Long.valueOf(it.getValue()).equals(0L)) {
                                                        //  ft.set(fcObject, it.getField(), null);
                                                        System.out.println("\n" + fcObject + " \n" + it.getField() + " не может быть 0 " + it.getValue());
                                                } else {
                                                        ft.set(fcObject, it.getField(), Long.valueOf(it.getValue()));
                                                }

                                        }
                                        // it.getField()
                                }

                                List<FactField> ftFields = ft.getFields();
                                System.out.println(ftFields.toString());
                                TreeMap<String, String> mapFields = new TreeMap<>();
                                for (FactField f : ftFields) {
                                 /*       System.out.println(f.getIndex());
                                        System.out.println(f.getMetaData());
                                        System.out.println("!! "+f.getName());
                                        System.out.println(f.getType());
                                        System.out.println("!!2 "+ft.get(fcObject,f.getName()));

                                  */
/*
                                        if (f.getType().equals(Long.class) && f.get(fcObject).equals(0)){
                                                ft.set(fcObject,f.getName(),null);
                                        }

 */
                                        //

                                        //                             System.out.println(fcObject.toString());
                                        Object gg = ft.get(fcObject, f.getName());


                                        if (gg == null) {
                                                gg = "null";
                                        }
                                        mapFields.put(f.getName(), gg.toString());
                                }
                                ListMapObject.add(mapFields);
                                System.out.println();

                                //List<String> actualFieldNames = getFieldNames(fields);
                                System.out.println("after " + fcObject);
                                ListObject.add(fcObject);
                        }

                        System.out.println();
//(14+8)/5
// assertEquals("bar", type.get(fcObject, "name"));
                } catch (InstantiationException e) {
                        // fail(e.getMessage());
                } catch (IllegalAccessException e) {
                        //fail(e.getMessage());
                }


                List<String> listForAssessment = new ArrayList<>();


                System.out.println(ListObject.size() + " records + delta");

                for (Object item : ListObject) {
                        kSession.insert(item);
                        //System.out.println(outputGlobalList);
                        int numberFire = kSession.fireAllRules();
                        List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");
                        StringBuilder stringBuilder = new StringBuilder();
                        int i = 0;
                        for (ClassForGlobal it : outputGlobalList) {
                                stringBuilder.append(it + " ");
                                stringBuilder.append(i + " \n");
                                i++;
                        }
                        list.clear();
                        kSession.setGlobal("list", list);
                        listForAssessment.add("сколько сработало правил ЭС " + numberFire + "\n на какой факт они сработали\n" + item.toString() + "\n какие данные дали правила\n" + stringBuilder.toString());
                        System.out.println("сколько сработало правил ЭС " + numberFire + "\n на какой факт они сработали\n" + item.toString() + "\n какие данные дали правила\n" + stringBuilder.toString());
                }


                logger.close();
                kSession.dispose();
/*
                System.out.println("список объектов дельта");
                for (Object item : ListObject) {
                        // sumRules = sumRules + Long.parseLong(item.getThenPart());
                        System.out.println(item.toString());
                }
*/
                //-----------------------------------------------------------------------------

                Workbook workbook = new XSSFWorkbook();
                try {
                        Sheet sheet = workbook.createSheet("Persons");
                        sheet.setColumnWidth(0, 6000);
                        sheet.setColumnWidth(1, 4000);

                        //  Row header = sheet.createRow(0);

                        CellStyle headerStyle = workbook.createCellStyle();
                        //headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                        //headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
                        font.setFontName("Arial");
                        font.setFontHeightInPoints((short) 16);
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
                        for (Map<String, String> item : ListMapObject) {
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
                        File currDir = new File(".");
                        String path = currDir.getAbsolutePath();
                        String fileLocation = path.substring(0, path.length() - 1) + "temp2.xlsx";

                        FileOutputStream outputStream = new FileOutputStream(fileLocation);
                        workbook.write(outputStream);
                } finally {
                        if (workbook != null) {

                                workbook.close();

                        }
                }

        }


}
