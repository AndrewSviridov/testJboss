package test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewClassExample2 {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        ArrayList<Type> arrayList = new ArrayList<Type>();
        arrayList.add(Integer.class);
        arrayList.add(String.class);
        // List<Type> typeList=new List<Type>() ;
        HashMap<String, Class<?>> stringTypeHashMap = new HashMap<String, Class<?>>();
        stringTypeHashMap.put("field1", Class.forName("java.lang.String"));
        stringTypeHashMap.put("field2", Class.forName("java.lang.Integer"));

        CreateClass createClass = new CreateClass();
        // DynamicType.Builder<Object> builderTest = createClass.createClass("testNameClass");
        // createClass.createClass("testNameClass");

        Class<?> classBuilderTest = createClass.createClass("testNameClass").make().load(NewClassExample2.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        //  Class<?>[] array = (Class<?>[]) stringTypeHashMap.values().toArray();
        Class<?>[] arr = {Class.forName("java.lang.String"), Class.forName("java.lang.Integer")};


        //classBuilderTest.newInstance();

        //  classBuilderTest wef = new classBuilderTest("dfdf", 55);

        Object obj = null;
        obj = classBuilderTest.newInstance();
        System.out.println(obj.toString());


        CreateClass2 createClass2 = new CreateClass2();
        createClass2.createClass("testNameClass");
        Class<?> classBuilderTest2 = createClass2.getBuilder().make().load(NewClassExample2.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        Object obj2 = classBuilderTest2.newInstance();
        System.out.println(obj2.toString());

        System.out.println("------------------------------------------------");

        DynamicType.Builder<Object> builder = new ByteBuddy()
                .subclass(
                        Object.class,
                        ConstructorStrategy.Default.NO_CONSTRUCTORS
                )
                .name("NewClass")
                .defineField(
                        "myField",
                        String.class,
                        Visibility.PRIVATE,
                        FieldManifestation.PLAIN
                )
                .defineField(
                        "fieldConstructString1",
                        String.class,
                        Visibility.PUBLIC,
                        FieldManifestation.PLAIN
                )
                .defineField(
                        "fieldConstructInt2",
                        Integer.class,
                        Visibility.PUBLIC,
                        FieldManifestation.PLAIN
                )
                .defineConstructor(Modifier.PUBLIC)
                .withParameters(arrayList)
                .intercept(MethodCall.invoke(Object.class.getConstructor())
                        .andThen(FieldAccessor.ofField("fieldConstructInt1")
                                .setsArgumentAt(0)).andThen(FieldAccessor.ofField("fieldConstructInt2")
                                .setsArgumentAt(1)))
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
                .intercept(FieldAccessor.ofField("myField").setsArgumentAt(0));

        //  DynamicType.Unloaded<Object> newClass2 = new ByteBuddy().

        // Class cls = Class.forName("String");
        // Object obj = cls.newInstance();

        try {
            System.out.println("The first time calls forName:");
            Class c = Class.forName("java.lang.String");
            Object a = c.newInstance();
            System.out.println("The second time calls forName:");
            // Class c1 = Class.forName("com.xyzws.AClass");
            System.out.println(a.getClass().toString());


            builder.defineField(
                    "addedField1",
                    c,
                    Visibility.PRIVATE,
                    FieldManifestation.PLAIN
            );

            builder.defineMethod(
                    "getMyField222",
                    String.class,
                    Modifier.PUBLIC
            ).intercept(FieldAccessor.ofField("myField"));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        builder.defineMethod(
                "getaddedField1",
                String.class,
                Modifier.PUBLIC
        ).intercept(FieldAccessor.ofField("addedField1"));

        builder.make().load(NewClassExample2.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();


        //  public void refineTest() {
        DynamicType.Builder builderSub = new ByteBuddy().subclass(Foo.class);
        DynamicType.Builder builderRedefine = new ByteBuddy().redefine(Foo.class);
        DynamicType.Builder builderRebase = new ByteBuddy().rebase(Foo.class);
        System.out.println(builderSub);
        System.out.println(builderRedefine);
        System.out.println(builderRebase);
        //}

        System.out.println("-----------------------------------------------------------------");










   /*
        builder.make()
                .load(classLoader, loader.resolveStrategy(features.mockedType, classLoader, name.startsWith(CODEGEN_PACKAGE)))
                .getLoaded();
*/
/*

        Class<?> dynamicType = newClass.load(NewClassExample2.class.getClassLoader())
                .getLoaded();
*/

    }
}
