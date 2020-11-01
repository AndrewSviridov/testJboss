package test.droolsTest.droolsUtils;

import javafx.util.Pair;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;
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
import rule.KnowledgeBaseWeka;
import test.droolsTest.ClassForGlobal;
import test.droolsTest.ClassResultObject;
import test.droolsTest.weka_algoritms.IHandlerAlgorithm;
import test.droolsTest.weka_algoritms.part.HandlerPart;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//прогонка данных по правилам от алгоритма
public class Experiment1 {

    private Instances train;
    private Instances test;
    private HashMap<String, String> map;
    private String nameClassForDRL;
    private String pathForLog;
    private KnowledgeBaseWeka knowledgeBaseWeka = null;

    private ArrayList<ClassResultObject> resultObjectArrayList = null;

    // private  List<String> listForAssessment = null;

    private IHandlerAlgorithm handlerAlgorithm;

    public Experiment1(Instances train, Instances test, HashMap<String, String> map, String nameClassForDRL, String pathForLog, IHandlerAlgorithm handlerAlgorithm) {
        this.train = train;
        this.test = test;
        this.map = map;
        this.nameClassForDRL = nameClassForDRL;
        this.pathForLog = pathForLog;
        this.handlerAlgorithm = handlerAlgorithm;
        // this.knowledgeBaseWeka = knowledgeBaseWeka;
    }

    public void init() throws Exception {

        knowledgeBaseWeka = handlerAlgorithm.getRules(new Pair<>(train, map));

        String drlInMemory = DroolsHelper.createDRLFileInMemory(train, knowledgeBaseWeka, map, nameClassForDRL);

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

        builder.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (builder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + builder.getResults().toString());
        }


        KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
        FactType ft = kc.getKieBase("rules").getFactType("org.kie1", nameClassForDRL);


        KieSession kSession = kc.getKieBase("rules").newKieSession();

        //todo разобраться с логирами

        // Set up listeners.
        //       kSession.addEventListener(new DebugAgendaEventListener());
        kSession.addEventListener(new DebugRuleRuntimeEventListener());

// Set up a file-based audit logger.
        //    KieRuntimeLogger logger = KieServices.get().getLoggers().newFileLogger( kSession, "./target/helloworld" );

// Set up a ThreadedFileLogger so that the audit view reflects events while debugging.
        // KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( kSession, "./target/helloworld", 1000 );

        //-----------------------------------------------------------------------------------

        // setup the debug listeners
        kSession.addEventListener(new DebugAgendaEventListener());
        // kSession.addEventListener( new DebugWorkingMemoryEventListener() );
        // setup the audit logging
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, pathForLog);

        List<ClassForGlobal> list = new ArrayList<>();
        kSession.setGlobal("list", list);


        resultObjectArrayList = new ArrayList<>();
        // listForAssessment = new ArrayList<>();
        Object fcObject = null;
        try {

            int numAttr = test.numAttributes();// - 1;
            for (int i = 0; i < test.numInstances(); i++) {
                fcObject = ft.newInstance();

                for (int j = 0; j < numAttr; j++) {

                    if (map.containsKey(test.attribute(j).name())) {

                        if (map.get(test.attribute(j).name()).equals("String")) {
                            ft.set(fcObject, test.attribute(j).name(), test.instance(i).stringValue(j));
                        } else {

                            Object valueAttr = DroolsHelper.getValueAttr(map.get(test.attribute(j).name()), test.instance(i).value(j));
                            ft.set(fcObject, test.attribute(j).name(), valueAttr);

                        }

                    }

                }

                kSession.insert(fcObject);

                //todo разобраться с global, list=outputGlobalList ???
                int numberFire = kSession.fireAllRules();
                //List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");
                List<ClassForGlobal> outputGlobalList = new ArrayList<>();
                outputGlobalList.addAll(list);
                StringBuilder stringBuilder = new StringBuilder();
                int n = 0;
                for (ClassForGlobal it : outputGlobalList) {
                    stringBuilder.append(it).append(" ");
                    stringBuilder.append(n).append(" \n");
                    n++;
                }

                resultObjectArrayList.add(new ClassResultObject(outputGlobalList, fcObject, numberFire, ft));

                list.clear();
                kSession.setGlobal("list", list);
                //  listForAssessment.add("сколько сработало правил ЭС " + numberFire + "\n на какой факт они сработали\n" + fcObject.toString() + "\n какие данные дали правила\n" + stringBuilder.toString());
                //   System.out.println("сколько сработало правил ЭС " + numberFire + "\n на какой факт они сработали\n" + fcObject.toString() + "\n какие данные дали правила\n" + stringBuilder.toString());


            }

        } catch (InstantiationException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        }

        logger.close();
        kSession.dispose();

    }


    ///------------------------------------------------------------------------------------------------------------------------


    public Instances getTrain() {
        return train;
    }

    public void setTrain(Instances train) {
        this.train = train;
    }

    public Instances getTest() {
        return test;
    }

    public void setTest(Instances test) {
        this.test = test;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public String getNameClassForDRL() {
        return nameClassForDRL;
    }

    public void setNameClassForDRL(String nameClassForDRL) {
        this.nameClassForDRL = nameClassForDRL;
    }

    public String getPathForLog() {
        return pathForLog;
    }

    public void setPathForLog(String pathForLog) {
        this.pathForLog = pathForLog;
    }

    public KnowledgeBaseWeka getKnowledgeBaseWeka() {
        return knowledgeBaseWeka;
    }

    public void setKnowledgeBaseWeka(KnowledgeBaseWeka knowledgeBaseWeka) {
        this.knowledgeBaseWeka = knowledgeBaseWeka;
    }

    public ArrayList<ClassResultObject> getResultObjectArrayList() {
        return resultObjectArrayList;
    }

    public void setResultObjectArrayList(ArrayList<ClassResultObject> resultObjectArrayList) {
        this.resultObjectArrayList = resultObjectArrayList;
    }

    public IHandlerAlgorithm getHandlerAlgorithm() {
        return handlerAlgorithm;
    }

    public void setHandlerAlgorithm(IHandlerAlgorithm handlerAlgorithm) {
        this.handlerAlgorithm = handlerAlgorithm;
    }
}
