package test.droolsTest;

import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.StubMethod;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import test.droolsTest.createClass.CreateByteClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DroolsTest {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {

        CreateByteClass createByteClass = new CreateByteClass("TestMessage");

        createByteClass.addField("name", Class.forName("java.lang.String"));
        createByteClass.addField("text", Class.forName("java.lang.String"));

        createByteClass.addGetMethod("getName", Class.forName("java.lang.String"), "name");
        createByteClass.addGetMethod("getText", Class.forName("java.lang.String"), "text");

        createByteClass.addSetMethod("setName", Class.forName("java.lang.String"), "name");
        createByteClass.addSetMethod("setText", Class.forName("java.lang.String"), "text");

        createByteClass.getBuilder().defineMethod(("method"), void.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE);

        //Class<?> type = builder.make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();

        Class<?> classBuilderTest3 = createByteClass.getBuilder().make().load(createByteClass.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();


        Method m = classBuilderTest3.getDeclaredMethod("setName", String.class);

        Method m2 = classBuilderTest3.getDeclaredMethod("setText", String.class);


        Method[] classBuilderTest3Methods = classBuilderTest3.getMethods();
        Method[] declaredMethods = classBuilderTest3.getDeclaredMethods();


        for (Method method : declaredMethods) {
            System.out.println(method.getName());
            System.out.println(method.toGenericString());
            System.out.println(method.getParameterTypes().getClass().toString());
            System.out.println(method.getGenericParameterTypes().length);
            System.out.println(method.getGenericReturnType().getTypeName() + " return");

            System.out.println(method.getReturnType().toString() + " returnType");

            if (method.getGenericParameterTypes().length != 0) {
                System.out.println(method.getGenericParameterTypes()[0].getTypeName() + " param");

            }
            System.out.println("----------------------------------------------");
        }


        for (Method method : classBuilderTest3Methods) {
            System.out.println(method.getName());
            // System.out.println(method.toGenericString());
        }
        System.out.println("----------------------------------------------");

        Object obj3 = classBuilderTest3.newInstance();

        //  m.invoke(obj3, "testName");
        //m2.invoke(obj3, "testText");
        //assertNotNull(classBuilderTest3.getDeclaredField("getField1"));

        System.out.println(obj3.toString());


        try {
            createByteClass.getBuilder().make().saveIn(new File("src/main/java/test/DroolsTest/createClass/" + "TestMessage"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/HAL5.drl", getMyRule());

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        KieSession kSession = kContainer.newKieSession();
        kSession.setGlobal("out", System.out);

        kSession.insert(new test.Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        kSession.fireAllRules();
        Object gdffdg = kSession.getGlobal("out");
        System.out.println(gdffdg.toString());


    }

//package test.droolsTest.createClass.TestMessage


    private static String getMyRule() {
        String s = "" +
                "package test.droolsTest.createClass \n\n" +
                "import createClass.TestMessage \n\n" +
                "global java.io.PrintStream out \n\n" +
                "rule \"rule 1\" when \n" +
                "    m : TestMessage( ) \n" +
                "then \n" +
                "    out.println( m.getName() + \": \" +  m.getText() ); \n" +
                "end \n" +
                "rule \"rule 2\" when \n" +
                "    TestMessage( text == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                "then \n" +
                "    insert( new Message(\"HAL\", \"Dave. I read you.\" ) ); \n" +
                "end";

        return s;
    }


    private static String getRule() {
        String s = "" +
                "package test \n\n" +
                "import test.Message \n\n" +
                "global java.io.PrintStream out \n\n" +
                "rule \"rule 1\" when \n" +
                "    m : Message( ) \n" +
                "then \n" +
                "    out.println( m.getName() + \": \" +  m.getText() ); \n" +
                "end \n" +
                "rule \"rule 2\" when \n" +
                "    Message( text == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                "then \n" +
                "    insert( new Message(\"HAL\", \"Dave. I read you.\" ) ); \n" +
                "end";

        return s;
    }


}
