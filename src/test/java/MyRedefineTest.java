import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;
import test.CreateClass;
import test.CreateClass3;
import test.NewClassExample2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static junit.framework.TestCase.assertNotNull;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static org.junit.Assert.assertEquals;

/**
 * @author zhangwu
 * @version 1.0.0
 * @date 2019-01-11-11:30
 */
public class MyRedefineTest {

    class Foo {
 /*       String field1 = "123";

        public String getField1() {
            return field1;
        }

        @Override
        public String toString() {
            return "Foo{" +
                    "field1='" + field1 + '\'' +
                    '}';
        }

  */
    }

    class Bar {
        String field1 = "456";

        public String getField1() {
            return field1;
        }

        @Override
        public String toString() {
            return "Bar{" +
                    "field1='" + field1 + '\'' +
                    '}';
        }
    }

    /**
     * com.undergrowth.buddy.RedefineTest$Foo	foo bar m	java.lang.String com.undergrowth.buddy.RedefineTest$Foo.m()
     */
    @Test
    public void redefineTest() {
        ByteBuddyAgent.install();
        Foo foo = new Foo();
        //System.out.println(Foo.class.getName() + "\t" + foo.m());
        System.out.println(foo.toString());
        new ByteBuddy()
                .redefine(Foo.class)
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN)

                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        // System.out.println(foo.m());
        for (Method method :
                foo.getClass().getDeclaredMethods()) {
            System.out.println(method.getName() + "\t" + method);
        }
    }

    /**
     * com.undergrowth.buddy.RedefineTest$Foo	foo bar m	java.lang.String com.undergrowth.buddy.RedefineTest$Foo.m()
     */
    @Test
    public void rebaseTest() {
        ByteBuddyAgent.install();
        Foo foo = new Foo();
        //System.out.println(Foo.class.getName() + "\t" + foo.m());
        new ByteBuddy()
                .rebase(Bar.class)
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN)
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        // System.out.println(foo.m());
        for (Method method :
                foo.getClass().getDeclaredMethods()) {
            System.out.println(method.getName() + "\t" + method);
        }
    }

    @Test
    public void testMy() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Class<?> newClass = new ByteBuddy()
                .subclass(
                        Object.class,
                        ConstructorStrategy.Default.NO_CONSTRUCTORS
                )
                .name("de.detim.workshop.NewClass")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello NewClass"))
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PUBLIC,
                        FieldManifestation.PLAIN
                )
                .defineField(
                        "myField2",
                        Integer.class,
                        Visibility.PUBLIC,
                        FieldManifestation.PLAIN
                )
                .defineConstructor(Modifier.PUBLIC)
                .withParameters(String.class, Integer.class)
                .intercept(MethodCall.invoke(Object.class.getConstructor())
                        .andThen(FieldAccessor.ofField("myField")
                                .setsArgumentAt(0)).andThen(FieldAccessor.ofField("myField2")
                                .setsArgumentAt(1)))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();

        HashMap<String, Class<?>> stringTypeHashMap = new HashMap<String, Class<?>>();
        stringTypeHashMap.put("field1", Class.forName("java.lang.String"));
        stringTypeHashMap.put("field2", Class.forName("java.lang.Integer"));

        ArrayList<Class<?>> typeArrayList = new ArrayList<Class<?>>(stringTypeHashMap.values());
   /*     Class<?> gfdg = stringTypeHashMap.values().toArray();
        for (int i = 0; i <stringTypeHashMap.values() ; i++) {

        }
     */
        Class<?>[] arr = {Class.forName("java.lang.String"), Class.forName("java.lang.Integer")};
        Object obj = newClass.getConstructor(arr)
                .newInstance("Get My Field", 55);
        System.out.println(obj);
    }


    @Test
    public void testMyWithoutConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Class<?> newClass = new ByteBuddy()
                .subclass(
                        Object.class
                )
                .name("de.detim.workshop.NewClass")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello NewClass"))
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PUBLIC,
                        FieldManifestation.PLAIN
                )
                .defineField(
                        "myField2",
                        Integer.class,
                        Visibility.PUBLIC,
                        FieldManifestation.PLAIN
                )

                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();


        Object obj = newClass.newInstance();
        System.out.println(obj);
    }

    @Test
    public void testMyCreateClass3() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchFieldException {
        CreateClass3 createClass3 = new CreateClass3("NameTestClass");

        createClass3.addField("field1", Class.forName("java.lang.String"));
        createClass3.addField("field2", Class.forName("java.lang.Integer"));

        createClass3.addGetMethod("getField1", Class.forName("java.lang.String"), "field1");
        createClass3.addGetMethod("getField2", Class.forName("java.lang.Integer"), "field2");

        createClass3.addSetMethod("setField1", Class.forName("java.lang.String"), "field1");
        createClass3.addSetMethod("setField2", Class.forName("java.lang.Integer"), "field2");

        createClass3.getBuilder().defineMethod(("method"), void.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE);

        //Class<?> type = builder.make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();

        Class<?> classBuilderTest3 = createClass3.getBuilder().make().load(createClass3.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();


        Method m = classBuilderTest3.getDeclaredMethod("setField1", String.class);

        Method m2 = classBuilderTest3.getDeclaredMethod("setField1", String.class);


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

        m.invoke(obj3, "testString");
        //assertNotNull(classBuilderTest3.getDeclaredField("getField1"));

        System.out.println(obj3.toString());
    }

    @Test
    public void testMyCreateClass() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        CreateClass createClass = new CreateClass();

        DynamicType.Builder<Object> bulder = createClass.createClass("NameTestClass");

        //Class<?> type = builder.make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        //写入到本地目录
        try {

            bulder.make().saveIn(new File("src/main/java/target/classes1"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class<?> classBuilderTest3 = bulder.make().load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        Object obj3 = classBuilderTest3.newInstance();
        System.out.println(obj3.toString());
    }


    @Test
    public void testMyConstructor2() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        DynamicType.Builder<Object> builder = new ByteBuddy()
                .subclass(
                        Object.class
                )
                .name("NewClass").defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN
                );
        System.out.println("---------");
        builder.defineMethod(
                "getMyField",
                String.class,
                Modifier.PUBLIC
        )
                .intercept(FieldAccessor.ofField("myField"));
        System.out.println("---------");
        builder.defineMethod(
                "setMyField",
                void.class,
                Modifier.PUBLIC
        )
                .withParameters(String.class)
                .intercept(FieldAccessor.ofField("myField").setsArgumentAt(0));

        Class<?> test = builder.make().load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();


        Object obj = test.newInstance();
        System.out.println(obj.toString());

    }


    @Test
    public void testMyRedefine() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        ByteBuddyAgent.install();
        Class<?> builder = new ByteBuddy()
                .subclass(
                        Object.class
                )
                .name("NewClass").defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN
                ).make().load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();


        Object obj = builder.newInstance();
        System.out.println(obj.toString());
/*
        new ByteBuddy()
                .redefine(builder.class)
                .defineField(
                        "myField2",
                        Integer.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN
                )
                .make()
                .load(RedefineTest.Foo .class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        Object obj2 = builder.newInstance();
        System.out.println(obj2.toString());
*/
    }




}