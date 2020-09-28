package test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateClass3 {
    private DynamicType.Builder<Object> builder;

    public CreateClass3() {
        this.builder = new ByteBuddy().subclass(Object.class);
    }


    public DynamicType.Builder<Object> getBuilder() {
        return builder;
    }

    public void setBuilder(DynamicType.Builder<Object> builder) {
        this.builder = builder;
    }


    public void addField(String nameField, Class nameClass) {

        builder = builder.defineField(
                nameField,
                nameClass,
                Visibility.PRIVATE,
                FieldManifestation.PLAIN
        );

    }

    public void addGetMethod(String nameMethod, Class nameClass, String withNameField) {
        builder = builder.defineMethod(
                nameMethod,
                nameClass,
                Modifier.PUBLIC
        ).intercept(FieldAccessor.ofField(withNameField));

    }

    public void addSetMethod(String nameMethod, Class nameClass, String withNameField) {
        builder = builder.defineMethod(
                nameMethod,
                void.class,
                Modifier.PUBLIC
        )
                .withParameters(nameClass)
                .intercept(FieldAccessor.ofField(withNameField).setsArgumentAt(0));

    }


    public void createClass(String nameClass) throws ClassNotFoundException, NoSuchMethodException {

        HashMap<String, Type> stringTypeHashMap = new HashMap<String, Type>();
        stringTypeHashMap.put("field1", Class.forName("java.lang.String"));
        stringTypeHashMap.put("field2", Class.forName("java.lang.Integer"));

        addField("field1", Class.forName("java.lang.String"));
        addField("field2", Class.forName("java.lang.Integer"));

        addGetMethod("getField1", Class.forName("java.lang.String"), "field1");
        addGetMethod("getField2", Class.forName("java.lang.Integer"), "field2");

        addSetMethod("setField1", Class.forName("java.lang.String"), "field1");
        addSetMethod("setField2", Class.forName("java.lang.Integer"), "field2");

    }


}
