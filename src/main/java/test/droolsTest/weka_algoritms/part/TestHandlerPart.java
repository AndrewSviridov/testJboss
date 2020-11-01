package test.droolsTest.weka_algoritms.part;

import DAO.C3POData;
import javafx.util.Pair;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
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
import test.droolsTest.ClassResultObject;
import test.droolsTest.droolsUtils.DroolsHelper;
import test.droolsTest.weka_algoritms.JRIP.HandlerJRIP;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

public class TestHandlerPart {

    public static void main(String[] args) throws Exception {
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

        System.out.println(pair.getKey().numAttributes() - 1);
        pair.getKey().setClassIndex(attrClass.index());

        test1(pair, nameClass, pathForLog);
    }

    public static void test1(Pair<Instances, HashMap<String, String>> pair, String nameClassForDRL, String pathForLog) throws Exception {
        HandlerPart handlerPart = new HandlerPart();
        KnowledgeBaseWeka knowledgeBaseWeka = handlerPart.getRules(pair);

        //https://stackoverflow.com/questions/14682057/java-weka-how-to-specify-split-percentage
        pair.getKey().randomize(new java.util.Random(0));
        int trainSize = (int) Math.round(pair.getKey().numInstances() * 0.8);
        int testSize = pair.getKey().numInstances() - trainSize;
        Instances train = new Instances(pair.getKey(), 0, trainSize);
        Instances test = new Instances(pair.getKey(), trainSize, testSize);

// todo передавать в параметрах
        String drlInMemory = DroolsHelper.createDRLFileInMemory(train, knowledgeBaseWeka, pair.getValue(), nameClassForDRL);


        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();


        kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
        KieModuleModel km = ks.newKieModuleModel();
        km.newKieBaseModel("rules")
                .addPackage("org.kie1");
        kfs.writeKModuleXML(km.toXML());

        km.newKieBaseModel("rules").setDefault(true);

        KieResources kr = ks.getResources();
        Resource r1 = kr.newByteArrayResource(drlInMemory.getBytes())//pkg1.getBytes()
                .setResourceType(ResourceType.DRL)
                .setSourcePath("org/kie1/myRules.drl");

        kfs.write(r1);

        KieBuilder builder = ks.newKieBuilder(kfs);

        /*
        builder.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (builder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + builder.getResults().toString());
        }
*/
        // assertEquals(Collections.emptyList(), builder.getResults().getMessages(Message.Level.ERROR));

        KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
        FactType ft = kc.getKieBase("rules").getFactType("org.kie1", nameClassForDRL);


        KieSession kSession = kc.getKieBase("rules").newKieSession();

        // Set up listeners.
        //       kSession.addEventListener(new DebugAgendaEventListener());
        kSession.addEventListener(new DebugRuleRuntimeEventListener());

// Set up a file-based audit logger.
        //            KieRuntimeLogger logger = KieServices.get().getLoggers().newFileLogger( ksession, "./target/helloworld" );

// Set up a ThreadedFileLogger so that the audit view reflects events while debugging.
        //          KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( ksession, "./target/helloworld", 1000 );

        //-----------------------------------------------------------------------------------

        // setup the debug listeners
        //   kSession.addEventListener(new DebugAgendaEventListener());
        //  kSession.addEventListener( new DebugWorkingMemoryEventListener() );
// setup the audit logging
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, pathForLog);

        List<ClassForGlobal> list = new ArrayList<>();
        kSession.setGlobal("list", list);


        //-------------------------------------------------------------------------------------------------------------------------
/*
        // output evaluation
        System.out.println();
        System.out.println("=== Setup ===");
       // System.out.println("Classifier: " + classifierName.getClass().getName() + " " + Utils.joinOptions(base.getOptions()));
        System.out.println("Dataset: " + pair.getKey().relationName());
        System.out.println();

        // output predictions
        System.out.println("# - actual - predicted - error - distribution - token");
        for (int i = 0; i < pair.getKey().numInstances(); i++)
        {
        //    double pred = base.classifyInstance(data.getKey().instance(i));
            double actual = pair.getKey().instance(i).classValue();
          //  double[] dist = base.distributionForInstance(data.getKey().instance(i));

         //   if (pred != actual)
            {
                System.out.print((i+1));
                System.out.print(" - ");
                System.out.print(pair.getKey().instance(i).toString(pair.getKey().classIndex()));
                System.out.print(" - ");
                //System.out.print(data.getKey().classAttribute().value((int) pred));
                System.out.print(" - ");
           //     if (pred != data.getKey().instance(i).classValue())
               //     System.out.print("yes");
             //   else
                 //   System.out.print("no");
                System.out.print(" - ");
               // System.out.print(Utils.arrayToString(dist));
                System.out.print(" - ");
                pair.getKey().instance(i).enumerateAttributes().toString();
                System.out.println();
            }
        }

       // System.out.println(eval.toSummaryString());
        //System.out.println(eval.toClassDetailsString());
        //System.out.println(eval.toMatrixString());

        System.out.println("------------------------------------------------------------------------------");
       */
        //-------------------------------------------------------------------------------------------------------------------------


        ArrayList<ClassResultObject> resultObjectArrayList = new ArrayList<>();

        List<String> listForAssessment = new ArrayList<>();
        List<Object> ListObject = new ArrayList<>();
        List<Map<String, String>> ListMapObject = new ArrayList<>();
        Object fcObject = null;
        try {

            int numAttr = test.numAttributes() - 1;
            for (int i = 0; i < test.numInstances(); i++) {
                fcObject = ft.newInstance();

                //System.out.println(data.checkForStringAttributes());
                for (int j = 0; j < numAttr; j++) {

                    //for(int g=1;g<=pair.getKey().getValue().getMetaData().getColumnCount();g++) {
                    //    if (test.attribute(j).name().equals(pair.getKey().getValue().getMetaData().getColumnName(g))) {
                    //pair.getKey().getValue().getObject(j)
                    //  ft.set(fcObject,  test.attribute(j).name(),  test.instance(i).value());
                    //  stringClassMapAttributes.put(instances.attribute(i).name(), readMetadata(rs.getMetaData().getColumnTypeName(j)));
                    //            }
                    // }
                    //    pair.getKey().getValue().getMetaData().
                    if (pair.getValue().containsKey(test.attribute(j).name())) {

                        if (pair.getValue().get(test.attribute(j).name()).equals("String")) {
                            ft.set(fcObject, test.attribute(j).name(), test.instance(i).stringValue(j));
                        } else {
                            //  Class<?> ergegr = Class.forName("java.lang."+pair.getValue().get(test.attribute(j).name()));

                            //  Object ergeq = Class.forName("java.lang."+pair.getValue().get(test.attribute(j).name())  test.instance(i).stringValue(j));

                            //ergegr.newInstance().getClass()
                            //     System.out.println();


                            // Class.forName(pair.getValue().get(test.attribute(j).name());
                            Object valueAttr = DroolsHelper.getValueAttr(pair.getValue().get(test.attribute(j).name()), test.instance(i).value(j));
                            ft.set(fcObject, test.attribute(j).name(), valueAttr);
                            //   System.out.println();
                        }

                    }
//((Integer) test.instance(i).stringValue(j))

//                    ft.set(fcObject,  test.attribute(j).name(),  test.instance(i).value());
                }

                kSession.insert(fcObject);
                //System.out.println(outputGlobalList);
                int numberFire = kSession.fireAllRules();
                List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");
                StringBuilder stringBuilder = new StringBuilder();
                int n = 0;
                for (ClassForGlobal it : outputGlobalList) {
                    stringBuilder.append(it + " ");
                    stringBuilder.append(n + " \n");
                    n++;
                }
                //ClassResultObject resultObject=new ClassResultObject(outputGlobalList,fcObject,numberFire);
                resultObjectArrayList.add(new ClassResultObject(outputGlobalList, fcObject, numberFire, ft));

                list.clear();
                kSession.setGlobal("list", list);
                listForAssessment.add("сколько сработало правил ЭС " + numberFire + "\n на какой факт они сработали\n" + fcObject.toString() + "\n какие данные дали правила\n" + stringBuilder.toString());
                System.out.println("сколько сработало правил ЭС " + numberFire + "\n на какой факт они сработали\n" + fcObject.toString() + "\n какие данные дали правила\n" + stringBuilder.toString());


            }


            System.out.println();
          /*      int numAttr = data.numAttributes() - 1;
                System.out.println(data.getKey().checkForStringAttributes());
                for (int i = 0; i < numAttr; i++) {

                double pred = cls.classifyInstance(data.getKey().instance(i));
                double[] dist = cls.distributionForInstance(data.getKey().instance(i));
                System.out.print((i+1));
                System.out.print(" - ");
                System.out.print(data.getKey().instance(i).toString(data.getKey().classIndex()));
                System.out.print(" - ");
                System.out.print(data.getKey().classAttribute().value((int) pred));
                System.out.print(" - ");
                if (pred != data.instance(i).classValue())
                    System.out.print("yes");
                else
                    System.out.print("no");
                System.out.print(" - ");
                System.out.print(Utils.arrayToString(dist));
                System.out.println();
            }
*/
            /*
            fcObject = ft.newInstance();

                ft.set(fcObject, it.getField(), Long.valueOf(it.getValue()));


                List<FactField> ftFields = ft.getFields();

                TreeMap<String, String> mapFields = new TreeMap<>();
                for (FactField f : ftFields) {

                }
                */
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
/*                    Object gg = ft.get(fcObject, f.getName());


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
*/
            System.out.println();
//(14+8)/5
// assertEquals("bar", type.get(fcObject, "name"));
        } catch (InstantiationException e) {
            // fail(e.getMessage());
        } catch (IllegalAccessException e) {
            //fail(e.getMessage());
        }
/*

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

*/
        logger.close();
        kSession.dispose();
/*
                System.out.println("список объектов дельта");
                for (Object item : ListObject) {
                        // sumRules = sumRules + Long.parseLong(item.getThenPart());
                        System.out.println(item.toString());
                }
*/


        System.out.println();
    }

}
