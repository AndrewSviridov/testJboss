package TestsWeka;

import TestsWeka.classes.FooClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Test1 {

    class Foo {
        String m() {
            return "fooclass";
        }
    }

    class Bar {
        String m() {
            return "bar";
        }
    }


    @Test
    public void run() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
/*
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
*/


        Class<?> newClass = new ByteBuddy()
                .redefine(FooClass.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello test1Class"))
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN
                )
                .defineConstructor(Modifier.PUBLIC)
                .withParameters(String.class)
                .intercept(MethodCall.invoke(Object.class.getConstructor())
                        .andThen(FieldAccessor.ofField("myField")
                                .setsArgumentAt(0)))
                .defineMethod(
                        "getMyField",
                        String.class,
                        Modifier.PUBLIC
                )
                .intercept(FieldAccessor.ofField("myField"))
                .defineMethod(
                        "setMyField",
                        void.class,
                        Modifier.PUBLIC
                )
                .withParameters(String.class)
                .intercept(FieldAccessor.ofField("myField").setsArgumentAt(0))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();

        Object obj = newClass.getConstructor(String.class)
                .newInstance("Hello, HAL. Do you read me, HAL?");
        System.out.println(obj);

        //Foo newClass=new Foo();
        //FooClass obj=new FooClass("");
        Method getMyField = newClass.getDeclaredMethod("getMyField", String.class);
        System.out.println((String) getMyField.invoke(obj));

        Method setMyField = newClass.getDeclaredMethod("setMyField", String.class);
        setMyField.invoke(obj, "Set My Field");
        System.out.println((String) getMyField.invoke(obj));
/*
        try {
            createByteClass.getBuilder().make().saveIn(new File("src/main/java/test/DroolsTest/createClass/" + "TestMessage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

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

        kSession.insert(obj);
        kSession.fireAllRules();
        Object gdffdg = kSession.getGlobal("out");
        System.out.println(gdffdg.toString());

    }


    @Test
    public void testAddFieldWithByteBuddy() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> newClass = new ByteBuddy().rebase(FooClass.class)
                .defineField("foo", String.class, Visibility.PRIVATE)
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN
                )
                .defineConstructor(Modifier.PUBLIC)
                .withParameters(String.class)
                .intercept(MethodCall.invoke(Object.class.getConstructor())
                        .andThen(FieldAccessor.ofField("myField")
                                .setsArgumentAt(0)))
                .defineMethod(
                        "getMyField",
                        String.class,
                        Modifier.PUBLIC
                )
                .intercept(FieldAccessor.ofField("myField"))
                .defineMethod(
                        "setMyField",
                        void.class,
                        Modifier.PUBLIC
                )
                .withParameters(String.class)
                .intercept(FieldAccessor.ofField("myField").setsArgumentAt(0))


                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();


        Object obj = newClass.getConstructor(String.class)
                .newInstance("Hello, HAL. Do you read me, HAL?");
        System.out.println(obj);

        //Foo newClass=new Foo();
        //FooClass obj=new FooClass("");
        Method getMyField = newClass.getDeclaredMethod("getMyField", String.class);
        System.out.println((String) getMyField.invoke(obj));

        Method setMyField = newClass.getDeclaredMethod("setMyField", String.class);
        setMyField.invoke(obj, "Set My Field");
        System.out.println((String) getMyField.invoke(obj));
/*
        try {
            createByteClass.getBuilder().make().saveIn(new File("src/main/java/test/DroolsTest/createClass/" + "TestMessage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

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

        kSession.insert(obj);
        kSession.fireAllRules();
        Object gdffdg = kSession.getGlobal("out");
        System.out.println(gdffdg.toString());


    }

    @Test
    public void testAddFieldWithByteBuddy2() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> newClass = new ByteBuddy().rebase(FooClass.class)
                .defineField("foo", String.class, Visibility.PRIVATE)
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();


    }


    @Test
    public void testDeclare() throws IllegalAccessException, InstantiationException {

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

        FactType testClass = kContainer.getKieBase().getFactType("TestsWeka", "TestClass");

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
                "global java.io.PrintStream out \n\n" +
                        "declare TestClass\n" +
                        "  myField : String\n" +
                        "end\n\n" +
                        "rule \"rule 1\" when \n" +
                        "    m : TestClass( );\n" +
                        "then \n" +
                        "    out.println( m.getField()); \n" +
                        "end \n" +
                        "rule \"rule 2\" when \n" +
                        "    TestClass( myField == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                        "then \n" +
                        "    insert( new TestClass(\"Dave. I read you.\" ) ); \n" +
                        "end";

        return s;
    }


    private static String getMyRule() {
        String s = "" +
                "package TestsWeka; \n\n" +
                "import TestsWeka.classes.FooClass; \n\n" +
                "global java.io.PrintStream out \n\n" +
                "rule \"rule 1\" when \n" +
                "    m : FooClass( );\n" +
                "then \n" +
                "    out.println( m.getField()); \n" +
                "end \n" +
                "rule \"rule 2\" when \n" +
                "    TestMessage( myField == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                "then \n" +
                "    insert( new FooClass(\"Dave. I read you.\" ) ); \n" +
                "end";

        return s;
    }


}
