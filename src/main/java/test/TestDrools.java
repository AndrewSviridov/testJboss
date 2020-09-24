package test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static net.bytebuddy.matcher.ElementMatchers.named;


public class TestDrools {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
/*
        Class<?> dynamicType = new ByteBuddy();
        dynamicType.asSubclass(Object.class);
        dynamicType.getMethod(ElementMatchers.named("toString"))
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(TestDrools.class.getClassLoader())
                .getLoaded();


        DynamicType.Unloaded unloadedType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.isToString())
                .intercept(FixedValue.value("Hello World ByteBuddy!"))
                .make();

        Class<?> dynamicType = unloadedType.load(TestDrools.class.getClassLoader()).getLoaded();

        System.out.println(dynamicType.newInstance().toString());
*/

        Class<?> newClass = new ByteBuddy()
                .subclass(
                        Object.class,
                        ConstructorStrategy.Default.NO_CONSTRUCTORS
                )
                .name("NewClass")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello NewClass"))
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
                .newInstance("Get My Field");
        System.out.println(obj.toString());


        Class<?> type = new ByteBuddy()
                .subclass(newClass)
                .name("MyClassName")
                .defineField("newField1", String.class, Modifier.PUBLIC)
                .defineField("newField2", String.class, Modifier.PUBLIC)
                .make()
                .load(
                        obj.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Object obj2 = type.getConstructor(String.class)
                .newInstance("new Field");
        System.out.println(obj2.toString());
//-----------------------------------------------------



        String toString = new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .make()
                .load(TestDrools.class.getClassLoader())
                .getLoaded()
                .newInstance() // Java reflection API
                .toString();

        System.out.println(toString);

        String toString2 = new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .method(named("toString")).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(TestDrools.class.getClassLoader())
                .getLoaded()
                .newInstance()
                .toString();
        System.out.println(toString2);

/*
        String r = new ByteBuddy()
                .subclass(Foo.class)
                .method(named("sayHelloFoo")
                        .and(isDeclaredBy(Foo.class)
                                .and(returns(String.class))))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance()
                .sayHelloFoo();
*/



        /*
         */

        String myRule = "import hellodrools.Message rule \"Hello World 2\" when message:Message (type==\"Test\") then System.out.println(\"Test, Drools!\"); end";

        Resource myResource = ResourceFactory.newReaderResource(new StringReader(myRule));


        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write("src/main/resources/simple.drl",
                kieServices.getResources().newReaderResource(new StringReader(myRule)));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();

     /*   KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/org/kie/example5/HAL5.drl", getRule());

        KieBuilder kb = ks.newKieBuilder(kfs);

        ks.newKieBuilder(kieFileSystem).buildAll();
        kbuilder.add(myResource, ResourceType.DRL);
*/

/*
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write("src/main/resources/rule.drl", drl);
        kieServices.newKieBuilder(kieFileSystem).buildAll();

        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        StatelessKieSession statelessKieSession = kieContainer.getKieBase().newStatelessKieSession();

        AlertDecision alertDecision = new AlertDecision();
        statelessKieSession.getGlobals().set("alertDecision", alertDecision);
        statelessKieSession.execute(event);
*/

        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession();

        Item item = new Item("A", 123.0, 234.0);
        System.out.println("Item Category: " + item.getCategory());
//2) Provide information to the Rule Engine Context
        kSession.insert(item);
//3) Execute the rules that are matching
        int fired = kSession.fireAllRules();
        System.out.println("Number of Rules executed = " + fired);
        System.out.println("Item Category: " + item.getCategory());
    }


}
