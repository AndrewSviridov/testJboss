package com.skcc.bds.drools.test;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import test.droolsTest.ClassForGlobal;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class TestMyRulesESArithmeticException {


    public static class TestLong {
        public Long field1;
        public Long field2;

        public TestLong() {
        }

        public Long getField1() {
            return field1;
        }

        public void setField1(Long field1) {
            this.field1 = field1;
        }

        public Long getField2() {
            return field2;
        }

        public void setField2(Long field2) {
            this.field2 = field2;
        }
    }

    @Test
    public void testLong() throws UnsupportedEncodingException {
        String hell = "\"привет\"";
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
                        "_field1 : Long\n" +
                        "_field2 : Long\n" +
                        "_field3 : String\n" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "//---------------------------------------------------------------------------------------\n" +
                        "\n" +
                        "rule \"Rule_13\"\n" +
                        "    when\n" +
                        "        _expassessment( (_field1/_field2)>2) \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_13\"));// то окон слишком много +1\n" +
                        "end \n" +
                        "rule \"Rule_14\"\n" +
                        "    when\n" +
                        "        _expassessment( _field3==\"при\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_14\"));// то окон слишком много +1\n" +
                        "end \n" +
                        "rule \"Rule_15\"\n" +
                        "    when\n" +
                        "        _expassessment( _field3==\"привет\") \n" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_15\"));// то окон слишком много +1\n" +
                        "end \n";

        System.out.println(Charset.defaultCharset().displayName());


        byte[] bytes = pkg1.getBytes("windows-1251");

        // pkg1 = new String(bytes, StandardCharsets.UTF_8);


        System.out.println((10L / 5L));

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();


        kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
        KieModuleModel km = ks.newKieModuleModel();
        km.newKieBaseModel("rules")
                .addPackage("org.kie1");
        kfs.writeKModuleXML(km.toXML());

        km.newKieBaseModel("rules").setDefault(true);

        KieResources kr = ks.getResources();
        Resource r1 = kr.newByteArrayResource(pkg1.getBytes(StandardCharsets.UTF_8))
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
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, "src/main/resources/log/TestMyRulesESArithmeticExceptionLog");

        List<ClassForGlobal> list = new ArrayList<>();
        kSession.setGlobal("list", list);


        TestLong testLong = new TestLong();

        Object fcObject = null;
        try {
            fcObject = ft.newInstance();
            System.out.println();


            List<FactField> erg = ft.getFields();
            for (FactField f : erg) {
                System.out.println(f.getIndex());
                System.out.println(f.getMetaData());
                System.out.println(f.getName());
                System.out.println(f.getType());
                if (f.getType().equals(Long.class)) {
                    //      ft.set(fcObject,f.getName(),50L);
                }
            }
            System.out.println();

            //(14+8)/5
            //    ft.set(fcObject, "_field1", 14L);
            //  ft.set(fcObject, "_field2", 0L);
            System.out.println();
            //    ft.set(fcObject, "_field2", null);
            System.out.println();
            ft.set(fcObject, "_field3", "привет");
            //assertEquals("bar", type.get(fcObject, "name"));
            System.out.println(fcObject);
        } catch (InstantiationException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }

        kSession.insert(fcObject);
        int result = kSession.fireAllRules();
        System.out.println(result);
        List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");


        logger.close();
        kSession.dispose();

        Long sumRules = 0L;
        for (ClassForGlobal item : outputGlobalList) {
            // sumRules = sumRules + Long.parseLong(item.getThenPart());
            System.out.println(item.toString());
        }

    }


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
                        "_field1 : Long\n" +
                        "_field2 : Long\n" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "//---------------------------------------------------------------------------------------\n" +
                        "\n" +
                        "rule \"Rule_13\"\n" +
                        "    when\n" +
                        "try {" +
                        "        _expassessment( (_field1/_field2)>2) \n" +
                        "}catch (Exception e){" +
                        "     System.out.println(e.message);" +
                        "}" +
                        "    then\n" +
                        "       list.add(new ClassForGlobal(\"1\",\"\",\"Rule_13\"));// то окон слишком много +1\n" +
                        "end \n";


        System.out.println((10L / 5L));

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
        KieRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, "src/main/resources/log/TestMyRulesESArithmeticExceptionLog");

        List<ClassForGlobal> list = new ArrayList<>();
        kSession.setGlobal("list", list);

        Object fcObject = null;
        try {
            fcObject = ft.newInstance();
            System.out.println();
//(14+8)/5
            ft.set(fcObject, "_field1", 14L);
            ft.set(fcObject, "_field2", 0L);

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
