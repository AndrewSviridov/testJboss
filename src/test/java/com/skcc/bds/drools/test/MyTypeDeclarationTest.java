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
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;
import test.droolsTest.ClassForGlobal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class MyTypeDeclarationTest {

    @Test
    public void testCrossPackageDeclares() {
        String pkg1 = "package org.kie1; \n" +
                "\n" +
                " import test.droolsTest.ClassForGlobal; \n" +
                "\n" +
                "global java.util.List list; \n" +
                "\n" +
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
                "rule Rule_0 when\n" +
                "_expassessment(_averagenumbersquaremetersperperso<=1 && _numberwindows>4) \n" +
                "then \n" +
                "list.add(new ClassForGlobal(6,(13.0),Rule_0));\n" +
                "end;";


        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.generateAndWritePomXML(ks.newReleaseId("test", "foo", "1.0"));
        KieModuleModel km = ks.newKieModuleModel();
        km.newKieBaseModel("rules")
                .addPackage("org.kie1");

        kfs.writeKModuleXML(km.toXML());

        KieResources kr = ks.getResources();
        Resource r1 = kr.newByteArrayResource(pkg1.getBytes())
                .setResourceType(ResourceType.DRL)
                .setSourcePath("org/kie1/p1.drl");


        kfs.write(r1);


        KieBuilder builder = ks.newKieBuilder(kfs);
        builder.buildAll();

        assertEquals(Collections.emptyList(), builder.getResults().getMessages(Level.ERROR));

        KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
        FactType ft = kc.getKieBase("rules").getFactType("org.kie1", "_expassessment");

        assertNotNull(ft);
        assertNotNull(ft.getFactClass());
        //      assertEquals( "org.drools.compiler.test1.Parent", ft.getFactClass().getSuperclass().getName() );

        KieSession kSession = kc.newKieSession();
        List<ClassForGlobal> list = new ArrayList<>();
        kSession.setGlobal("list", list);

        // вставить
        //kSession.insert(new test.Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        kSession.fireAllRules();
        List<ClassForGlobal> outputGlobalList = (List<ClassForGlobal>) kSession.getGlobal("list");

    }

    private static String getMyRuleDeclare() {
        String s = "package org.kie1; \n\n" +
                "global java.io.PrintStream out \n\n" +
                "declare TestClass\n" +
                "  myField : String\n" +
                "end\n\n";

        return s;
    }

    @Test
    public void testMultipleAnnotationDeclarations() {
        String str1 = "";
        str1 += "package org.kie1 " +
                "" +
                "declare Foo \n" +
                "    name : String " +
                "    age : int " +
                "end ";

        String str2 = "";
        str2 += "package org.kie1; \n" +
                "\n" +
                "import test.droolsTest.ClassForGlobal; \n" +
                "\n" +
                "global java.util.List list; \n" +
                "\n" +
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
                "rule Rule_0 when\n" +
                "_expassessment( _averagenumbersquaremetersperperso<=1 && _numberwindows>4); \n" +
                "then \n" +
                "list.add(new ClassForGlobal(6,(13.0),Rule_0));\n" +
                "end;";

        String str3 = "";
        str3 += "package org.kie3 " +
                "" +
                "declare org.kie1.Foo " +
                "    @propertyReactive " +
                "end ";

        String str4 = "" +
                "package org.kie4; " +
                "import org.kie1.Foo; " +
                "" +
                "rule Check " +
                "when " +
                " $f : Foo( name == 'bar' ) " +
                "then " +
                " modify( $f ) { setAge( 99 ); } " +
                " System.out.println('test');" +
                "end ";

        KieHelper helper = new KieHelper();
        helper.addContent(str2, ResourceType.DRL);

        List<Message> msg = helper.verify().getMessages(Level.ERROR);
        System.out.println(msg);
        assertEquals(0, msg.size());

        KieBase kieBase = helper.build();
        FactType type = kieBase.getFactType("org.kie1", "_expassessment");
//        assertEquals( 2, type.getFields().size() );
/*
        Object foo = null;
        try {
            foo = type.newInstance();
            type.set( foo, "myField", "bar" );
            assertEquals( "bar", type.get( foo, "myField" ) );
        } catch ( InstantiationException e ) {
            fail( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            fail( e.getMessage() );
        }

        KieSession session = kieBase.newKieSession();
        FactHandle handle = session.insert( foo );
        int n = session.fireAllRules( 5 );
*/

        /*
        assertTrue( handle instanceof EventFactHandle );
        assertEquals( 1, n );
        assertEquals( 99, type.get( foo, "age" ) );
  */
    }


}
