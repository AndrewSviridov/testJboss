package TestsWeka;

import TestsWeka.classes.FooClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Test2 {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        // testAddFieldWithByteBuddy2();
        testDeclare();
    }


    public static void testAddFieldWithByteBuddy2() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> newClass = new ByteBuddy().rebase(FooClass.class)
                .defineField("foo", String.class, Visibility.PRIVATE)
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();


    }


    public static void testDeclare() throws IllegalAccessException, InstantiationException {


        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
        KieModuleModel km = ks.newKieModuleModel();
        km.newKieBaseModel("rules")
                .addPackage("org.drools.compiler.test1");
        kfs.writeKModuleXML(km.toXML());

        KieResources kr = ks.getResources();
        Resource r1 = kr.newByteArrayResource(getMyRuleDeclare().getBytes())
                .setResourceType(ResourceType.DRL)
                .setSourcePath("org/drools/compiler/test1/p1.drl");


        kfs.write(r1);


        KieBuilder builder = ks.newKieBuilder(kfs);
        builder.buildAll();

        assertEquals(Collections.emptyList(), builder.getResults().getMessages(Message.Level.ERROR));

        KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());

        KieSession kSession = kc.newKieSession();
        kSession.setGlobal("out", System.out);


        FactType ft = kc.getKieBase("rules").getFactType("org.drools.compiler.test1", "TestClass");

        assertNotNull(ft);
        assertNotNull(ft.getFactClass());

        Object first = ft.newInstance();
        ft.set(first, "myField", "Hello, HAL. Do you read me, HAL?!");

        kSession.insert(first);
        kSession.fireAllRules();
        Object outObj = kSession.getGlobal("out");
        System.out.println(outObj.toString());

        // assertEquals( "org.drools.compiler.test1.Parent", ft.getFactClass().getSuperclass().getName() );

//        KieSession ksession = kc.newKieSession();
//        ksession.insert("test");
//        ksession.fireAllRules();

    /*

        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/HAL5.drl", getMyRuleDeclare());

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        KieSession kSession = kContainer.newKieSession();
        kSession.setGlobal("out", System.out);

        FactType testClass =  kContainer.getKieBase().getFactType("TestsWeka", "TestClass");

        Object first = testClass.newInstance();
        testClass.set(first, "myField", "Hello, HAL. Do you read me, HAL?!");

        kSession.insert(first);
        kSession.fireAllRules();
        Object gdffdg = kSession.getGlobal("out");
        System.out.println(gdffdg.toString());

*/
    }

//package test.droolsTest.createClass.TestMessage

    private static String getMyRuleDeclare() {
        String s = "package org.drools.compiler.test1;\n\n" +
                "global java.io.PrintStream out \n\n" +
                "declare TestClass\n" +
                "  myField : String\n" +
                "end\n\n";

        return s;
    }


}
