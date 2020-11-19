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
import rule.ConditionForWeka;
import rule.KnowledgeBaseWeka;
import rule.RuleForWeka;
import test.droolsTest.ClassForGlobal;
import test.droolsTest.ClassResultObject;
import test.droolsTest.weka_algoritms.JRIP.HandlerJRIP;
import test.droolsTest.weka_algoritms.part.HandlerPart;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//прогонка данных по правилам от человека
public class Experiment3 {

    private Instances train;
    private Instances test;
    private HashMap<String, String> map;
    private String nameClassForDRL;
    private String pathForLog;

    private ArrayList<ClassResultObject> resultObjectArrayList = null;


    public Experiment3(Instances train, Instances test, HashMap<String, String> map, String nameClassForDRL, String pathForLog) {
        this.train = train;
        this.test = test;
        this.map = map;
        this.nameClassForDRL = nameClassForDRL;
        this.pathForLog = pathForLog;

    }

    public void init() throws Exception {


        String drlInMemory = DroolsHelper.Rules;

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

            int numAttr = test.numAttributes(); //- 1;
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
                List<ClassForGlobal> outputGlobalList = new ArrayList<>(list);
       /*         StringBuilder stringBuilder = new StringBuilder();
                int n = 0;
                for (ClassForGlobal it : outputGlobalList) {
                    stringBuilder.append(it).append(" ");
                    stringBuilder.append(n).append(" \n");
                    n++;
                }
*/
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
//-------------------------------------------------------------------------------------

        KnowledgeBaseWeka KBHuman = new KnowledgeBaseWeka();

        RuleForWeka ruleForWeka1 = new RuleForWeka();
        ConditionForWeka condition1 = new ConditionForWeka();
        condition1.setField("_wallthickness");
        condition1.setOperator(ConditionForWeka.Operator.fromValue("<="));
        condition1.setValue("15");
        condition1.setTypeClass("Integer");

        ruleForWeka1.getList().add(condition1);
        ConditionForWeka condition2 = new ConditionForWeka();
        condition2.setField("_typeusebath");
        condition2.setOperator(ConditionForWeka.Operator.fromValue("=="));
        condition2.setValue("круглогодичное");
        condition2.setTypeClass("String");
        ruleForWeka1.getList().add(condition2);
        ruleForWeka1.setNameRule("Rule_3");
        ruleForWeka1.setThenPart("7");
        ruleForWeka1.setInfo("");

        KBHuman.getRuleForWekaArrayList().add(ruleForWeka1);
      /*  newCond.setField(arrPartCon2[0].trim());
        newCond.setOperator(ConditionForWeka.Operator.EQUAL_TO);
        newCond.setOperator(ConditionForWeka.Operator.fromValue(sign));
        newCond.setValue(arr22[0].trim());
        newCond.setTypeClass(pair.getValue().get(arrPartCon2[0].trim()));

        rule.setThenPart(arrkk[0].trim());
        rule.setInfo(arrkk[1].trim());

        ruleForWeka1.
        */
        //-----------------------------------------------------------------

        ClassResultObject testInstance = null;
        ArrayList<ClassResultObject> podhod = new ArrayList<>();

        //нашел факт и набор правил под него  у которых оценка больше 5 например
        for (ClassResultObject it : resultObjectArrayList) {
            if (it.getAssessment() >= 5.0 && it.getOutputGlobalList().size() > 2) {
                podhod.add(it);
            }
          /*  if (it.getFactType().get(it.getFcObject(),"record_id").toString().equals("203")){
                testInstance=it;
            }

           */
        }

        ClassResultObject classResultObject1 = podhod.get(0);

        // получаем пару списков правил которые меньше всего повлияли и те которые больше
        Pair<List<ClassForGlobal>, List<ClassForGlobal>> result1 = DroolsHelper.getLowAndHighRule(classResultObject1);

        HandlerPart handlerPart = new HandlerPart();

        KnowledgeBaseWeka knowledgeBaseWeka = handlerPart.getRules(new Pair<>(train, map));

        HandlerJRIP handlerJRIP = new HandlerJRIP();
        KnowledgeBaseWeka knowledgeBaseWekaJRIP = handlerJRIP.getRules(new Pair<>(train, map));
//knowledgeBaseWeka.getRuleForWekaArrayList().get(0).

        Pair<List<RuleForWeka>, List<RuleForWeka>> result2 = DroolsHelper.splitPorogGreneratedRules(knowledgeBaseWekaJRIP, 5.0);

        //сделаю список кондишенов, условий.  это и будут вершины которые мы взяли из правила который имее наиболее большое влияние (оценку)
        ArrayList<ConditionForWeka> conditionForWekaArrayList = new ArrayList<>();
        for (RuleForWeka it : KBHuman.getRuleForWekaArrayList()) {
// вообще тут нужно по всем правилам пройтись которые подняли оценку ( ну или можно взять наиболее)
            // подумаем как будто мы взяли наиболее которое повлияло
            if (it.getNameRule().equals(result1.getKey().get(0).getNameRule())) {
                conditionForWekaArrayList.addAll(it.getList());
            }
        }


        //-------------------------------------------


        //-----------------------------------------
        ArrayList<RuleForWeka> Rule_conditionForWekaArrayListContainsLst = new ArrayList<>();
        ArrayList<RuleForWeka> Rule_conditionForWekaArrayListNOTContainsList = new ArrayList<>();
        for (RuleForWeka it : result2.getKey()) {
            // сделал упрощение до одной вершины

            if (it.getList().contains(conditionForWekaArrayList.get(1))) {
                Rule_conditionForWekaArrayListContainsLst.add(it);
            } else {

                Rule_conditionForWekaArrayListNOTContainsList.add(it);
            }
        }


        ArrayList<ConditionForWeka> anotherConditionInRueWith_conditionForWekaArrayList = new ArrayList<>();
        for (RuleForWeka it : Rule_conditionForWekaArrayListContainsLst) {

            for (ConditionForWeka con : it.getList()) {

                for (ConditionForWeka conInner : conditionForWekaArrayList) {
                    if (!con.equals(conInner)) {
                        if (!anotherConditionInRueWith_conditionForWekaArrayList.contains(con))
                            anotherConditionInRueWith_conditionForWekaArrayList.add(con);
                    }
                }

            }

        }


        //5 шаг
        //  Pair<Instances,Instances> resultSplit= DroolsHelper.splitInstances(test,5.0);

/*
        ArrayList< Pair<List<ClassForGlobal>, List<ClassForGlobal>>> podhod_lowAndHight=new ArrayList<>();
        // получаем пару списков правил которые меньше всего повлияли и те которые больше
        for (ClassResultObject it:podhod) {
            podhod_lowAndHight.add(DroolsHelper.getLowAndHighRule(it));
        }



//нужно обойти множество  it.getValue(), те вершины что сильно повлияли на оценку и взять их со значением
        // найти правила из сформированных по алгоритму и меньше порогового значения (resultSplit.getKeys())
        for (Pair<List<ClassForGlobal>, List<ClassForGlobal>> it:podhod_lowAndHight) {
            for (ClassForGlobal currentVershinWithZnach :it.getValue()) {

            }
        }
*/
        //mapFields.put("idПрецедента", itAlgoritm.getFactType().get(itAlgoritm.getFcObject(), "record_id").toString());
        // делим правила по порогу
        //   Pair<List<ClassForGlobal>, List<ClassForGlobal>> lowAndHight = DroolsHelper.getLowAndHighRule(testInstance);
        /**/


//убираем правила которые меньше всего повлияли на оценку // это low часть
        //DroolsHelper.getLowAndHighRule()
        System.out.println();
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

    public ArrayList<ClassResultObject> getResultObjectArrayList() {
        return resultObjectArrayList;
    }

    public void setResultObjectArrayList(ArrayList<ClassResultObject> resultObjectArrayList) {
        this.resultObjectArrayList = resultObjectArrayList;
    }
}
