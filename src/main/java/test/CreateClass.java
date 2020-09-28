package test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateClass {
  /*
    public CreateClass(DynamicType.Builder<Object> builder) {
        this.builder = builder;
    }
*/
  /*
    DynamicType.Builder<Object> builder = new ByteBuddy() .subclass(Object.class);

    public DynamicType.Builder<Object> getBuilder() {
        return builder;
    }

    public void setBuilder(DynamicType.Builder<Object> builder) {
        this.builder = builder;
    }
*/

    public DynamicType.Builder<Object> addField(DynamicType.Builder<Object> objectBuilder, String nameField, Class nameClass) {

        objectBuilder.defineField(
                nameField,
                nameClass,
                Visibility.PRIVATE,
                FieldManifestation.PLAIN
        );
        return objectBuilder;
    }

    public DynamicType.Builder<Object> addMethod(DynamicType.Builder<Object> objectBuilder, String nameMethod, Class nameClass, String withNameField) {
        objectBuilder.defineMethod(
                nameMethod,
                nameClass,
                Modifier.PUBLIC
        ).intercept(FieldAccessor.ofField(withNameField));
        return objectBuilder;
    }

    //HashMap<String,Class> mapField=new HashMap<>();

    public DynamicType.Builder<Object> createClassBuilder(String nameClass) {
        DynamicType.Builder<Object> builder = new ByteBuddy()
                .subclass(
                        Object.class
                )
                .name(nameClass);
        return builder;
    }


    public DynamicType.Builder<Object> addConstructor(DynamicType.Builder<Object> objectBuilder, HashMap<String, Type> stringTypeHashMap) throws NoSuchMethodException {

        //.intercept(
        MethodCall methodCall = MethodCall.invoke(Object.class.getConstructor());

        for (HashMap.Entry<String, Type> item : stringTypeHashMap.entrySet()) {
            int i = 0;
            methodCall.andThen(FieldAccessor.ofField(item.getKey())
                    .setsArgumentAt(i++));
            // System.out.printf("Key: %d  Value: %s \n", item.getKey(), item.getValue());
        }

    /*    ArrayList<Type> list = new ArrayList<Type>(map.values());
        for (String s : list) {
            System.out.println(s);
        }
      */
        objectBuilder.defineConstructor(Modifier.PUBLIC)
                .withParameters(new ArrayList<Type>(stringTypeHashMap.values())).intercept(methodCall);
/*
            methodCall.andThen(FieldAccessor.ofField("fieldConstructInt2")
                    .setsArgumentAt(0));
        methodCall.andThen(FieldAccessor.ofField("fieldConstructInt2")
                .setsArgumentAt(0));
*/


        //    .withParameters(new ArrayList<Type>(){})
        return objectBuilder;
    }


    public DynamicType.Builder<Object> createClass(String nameClass) throws ClassNotFoundException, NoSuchMethodException {

        HashMap<String, Type> stringTypeHashMap = new HashMap<String, Type>();
        stringTypeHashMap.put("field1", Class.forName("java.lang.String"));
        stringTypeHashMap.put("field2", Class.forName("java.lang.Integer"));

        DynamicType.Builder<Object> testClass = createClassBuilder("TestClass");
        testClass = addField(testClass, "field1", Class.forName("java.lang.String"));
        testClass = addField(testClass, "field2", Class.forName("java.lang.Integer"));
        testClass = addMethod(testClass, "getField1", Class.forName("java.lang.String"), "field1");
        testClass = addMethod(testClass, "getField12", Class.forName("java.lang.Integer"), "field2");

        return testClass;
    }


}
