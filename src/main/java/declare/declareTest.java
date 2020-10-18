package declare;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class declareTest {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        testDeclare();
    }


    public static void testDeclare() throws IllegalAccessException, InstantiationException {

        // DroolsHelper.getInstance().loadGav("com.myspace", "flink_rule", "LATEST");

        ReleaseIdImpl releaseId = new ReleaseIdImpl("testJboss", "", "1.0");
    /*    ks = KieServices.Factory.get();
        container = ks.newKieContainer(releaseId);
        scanner = ks.newKieScanner(container);
        scanner.addListener(new KieScannerEventListener() {
      */
        //KieServices ks = KieServices.Factory.get();
        KieServices ks = KieServices.Factory.get();

        //   ks.newReleaseId("com.myspace", "flink_rule", "LATEST");
        KieRepository kr = ks.getRepository();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/HAL5.drl", getMyRuleDeclare());

        KieBuilder kb = ks.newKieBuilder(kfs);
        System.out.println(kb.getKieModule().getReleaseId());
        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        System.out.println(kr.getDefaultReleaseId());


        /*
        *             DroolsHelper.getInstance().loadGav("com.myspace", "flink_rule", "LATEST");
            FactType factType = DroolsHelper.getInstance().getFactType("com.myspace.flink_rule", "approve");
        * */
        System.out.println(ks.newKieClasspathContainer().getReleaseId());

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        KieSession kSession = kContainer.newKieSession();
        kSession.setGlobal("out", System.out);

        FactType testClass = kContainer.getKieBase().getFactType("com.myspace", "TestClass");

        Object first = testClass.newInstance();
        testClass.set(first, "myField", "Hello, HAL. Do you read me, HAL?!");

        kSession.insert(first);
        kSession.fireAllRules();
        Object gdffdg = kSession.getGlobal("out");
        System.out.println(gdffdg.toString());

    }

//package test.droolsTest.createClass.TestMessage

    private static String getMyRuleDeclare() {
        String s =
                "declare TestClass\n" +
                        "  myField : String\n" +
                        "end\n\n";

        return s;
    }


}
