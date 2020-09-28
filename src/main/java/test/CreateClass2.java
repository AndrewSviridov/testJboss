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

public class CreateClass2 {
    /*
      public CreateClass(DynamicType.Builder<Object> builder) {
          this.builder = builder;
      }
  */
    private DynamicType.Builder<Object> builder = new ByteBuddy().subclass(Object.class);

    public DynamicType.Builder<Object> getBuilder() {
        return builder;
    }

    public void setBuilder(DynamicType.Builder<Object> builder) {
        this.builder = builder;
    }


    public void addField(DynamicType.Builder<Object> objectBuilder, String nameField, Class nameClass) {

        objectBuilder.defineField(
                nameField,
                nameClass,
                Visibility.PRIVATE,
                FieldManifestation.PLAIN
        );

    }

    public void addMethod(DynamicType.Builder<Object> objectBuilder, String nameMethod, Class nameClass, String withNameField) {
        objectBuilder.defineMethod(
                nameMethod,
                nameClass,
                Modifier.PUBLIC
        ).intercept(FieldAccessor.ofField(withNameField));

    }

    //HashMap<String,Class> mapField=new HashMap<>();

    public void createClassBuilder(String nameClass) {
        DynamicType.Builder<Object> builder = new ByteBuddy()
                .subclass(
                        Object.class
                )
                .name(nameClass);

    }


    public void addConstructor(DynamicType.Builder<Object> objectBuilder, HashMap<String, Type> stringTypeHashMap) throws NoSuchMethodException {

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

    }


    public void createClass(String nameClass) throws ClassNotFoundException, NoSuchMethodException {

        HashMap<String, Type> stringTypeHashMap = new HashMap<String, Type>();
        stringTypeHashMap.put("field1", Class.forName("java.lang.String"));
        stringTypeHashMap.put("field2", Class.forName("java.lang.Integer"));

        createClassBuilder("TestClass");
        addField(builder, "field1", Class.forName("java.lang.String"));
        addField(builder, "field2", Class.forName("java.lang.Integer"));

        addMethod(builder, "getField1", Class.forName("java.lang.String"), "field1");
        addMethod(builder, "getField12", Class.forName("java.lang.Integer"), "field2");


    }


}
