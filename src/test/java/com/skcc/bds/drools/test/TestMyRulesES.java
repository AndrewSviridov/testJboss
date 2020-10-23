package com.skcc.bds.drools.test;

import org.drools.core.common.EventFactHandle;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.utils.KieHelper;
import test.droolsTest.ClassForGlobal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class TestMyRulesES {

    @Test
    public void test() {
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
                        "rule \"Rule_1\"\n" +
                        "    when\n" +
                        "        _expassessment( _arealounge/_bathfullness<1.5 && _purposebath==\"место отдыха\" ) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_1\"));//то комната отдыха слишком мала\n" +
                        "end \n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "rule \"Rule_2\"\n" +
                        "    when\n" +
                        "        _expassessment( _areasteamroom/_bathfullness<=1.5 && _purposebath==\"для мытья\" ) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_2\"));//то мойка сликлм мала\n" +
                        "end \n" +
                        "\n" +
                        "rule \"Rule_3\"\n" +
                        "    when\n" +
                        "        _expassessment( _wallthickness<=15 && _typeusebath==\"круглогодичное\" ) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"7\",\"\",\"Rule_3\"));//баня холодная\n" +
                        "end \n" +
                        "\n" +
                        "\n" +
                        "rule \"Rule_4\"\n" +
                        "    when\n" +
                        "        _expassessment( _wallthickness==15 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"true\" && _floorinsulation==\"false\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_4\"));//нужно утеплить пол оценка +2\n" +
                        "end \n" +
                        "\n" +
                        "rule \"Rule_5\"\n" +
                        "    when\n" +
                        "        _expassessment( _wallthickness>=20 && _typeusebath==\"круглогодичное\" && _ceilinginsulation==\"false\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"3\",\"\",\"Rule_5\"));//нужно утеплить потолок оценка +3\n" +
                        "end \n" +
                        "\n" +
                        "rule \"Rule_6\"\n" +
                        "    when\n" +
                        "        _expassessment( _constructionbudget==\"от 10 до 200\" && _buildingarea==\"от 20 до более\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_6\"));//слишком маленький бюджет +4\n" +
                        "end \n" +
                        "\n" +
                        "rule \"Rule_7\"\n" +
                        "    when\n" +
                        "        _expassessment( _constructionbudget==\"от 10 до 200\" && _material==\"дуб\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_7\"));//слишком маленький бюджет +4\n" +
                        "end \n" +
                        "\n" +
                        "\n" +
                        "rule \"Rule_8\"\n" +
                        "    when\n" +
                        "        _expassessment( _constructionbudget==\"от 200 до 400\" && _material==\"дуб\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_8\"));//слишком маленький бюджет +4\n" +
                        "end \n" +
                        "\n" +
                        "rule \"Rule_9\"\n" +
                        "    when\n" +
                        "        _expassessment( _constructionbudget==\"от 200 до 400\"  && _buildingarea==\"от 20 до более\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"4\",\"\",\"Rule_9\"));//слишком маленький бюджет +4\n" +
                        "end \n" +
                        "\n" +
                        "\n" +
                        "rule \"Rule_10\"\n" +
                        "    when\n" +
                        "        _expassessment( _constructionbudget==\"от 200 до 400\"  && _buildingarea==\"от 20 до более\" && _material!=\"дуб\" && _averagenumbersquaremetersperperso>6) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_10\"));//жно построить меньшую дешевую баню +1\n" +
                        "end \n" +
                        "\n" +
                        "rule \"Rule_11\"\n" +
                        "    when\n" +
                        "        _expassessment( _areasteamroom==0 && _sinkarea==0) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"10\",\"\",\"Rule_11\"));// то это не баня +10\n" +
                        "end \n" +
                        "\n" +
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
                        "end \n" +
                        "rule \"Rule_14_test\"\n" +
                        "    when\n" +
                        "        _expassessment( (_arealounge/null)<1.5 ) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"2\",\"\",\"Rule_14_test\"));//то комната отдыха слишком мала\n" +
                        "end \n";


        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();


        kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
        KieModuleModel km = ks.newKieModuleModel();
        km.newKieBaseModel("rules")
                .addPackage("org.kie1");
        kfs.writeKModuleXML(km.toXML());

        km.newKieBaseModel("rules").setDefault(true);

        KieResources kr = ks.getResources();
        Resource r1 = kr.newByteArrayResource(pkg1.getBytes())
                .setResourceType(ResourceType.DRL)
                .setSourcePath("org/kie1/myRules.drl");

        kfs.write(r1);

        KieBuilder builder = ks.newKieBuilder(kfs);
        builder.buildAll();

        assertEquals(Collections.emptyList(), builder.getResults().getMessages(Level.ERROR));

        KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
        FactType ft = kc.getKieBase("rules").getFactType("org.kie1", "_expassessment");

        assertNotNull(ft);
        assertNotNull(ft.getFactClass());
        // assertEquals("org.kie1", ft.getFactClass().getSuperclass().getName());


        KieSession kSession = kc.getKieBase("rules").newKieSession();

        // setup the debug listeners
        kSession.addEventListener(new DebugAgendaEventListener());
        //  kSession.addEventListener( new DebugWorkingMemoryEventListener() );
// setup the audit logging
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, "src/main/resources/log/TestMyRulesESLog");

        List<ClassForGlobal> list = new ArrayList<>();
        kSession.setGlobal("list", list);

        Object fcObject = null;
        try {
            fcObject = ft.newInstance();
            System.out.println();
//(14+8)/5
            ft.set(fcObject, "_arealounge", 14L);
            ft.set(fcObject, "_sinkarea", 0L);

            //assertEquals("bar", type.get(fcObject, "name"));
        } catch (InstantiationException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }

        kSession.insert(fcObject);
        kSession.fireAllRules();
        List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");


        logger.close();
        kSession.dispose();

        Long sumRules = 0L;
        for (ClassForGlobal item : outputGlobalList) {
            // sumRules = sumRules + Long.parseLong(item.getThenPart());
            System.out.println(item.toString());
        }

    }


}
