package test.droolsTest.createClass;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

import java.lang.reflect.Modifier;

public class CreateByteClass {
    private DynamicType.Builder<Object> builder;


    public CreateByteClass(String nameClass) {
        this.builder = new ByteBuddy().subclass(Object.class).name(nameClass);
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

    public void addProperty(String nameField, Class nameClass) {
        builder = builder.defineField(
                nameField,
                nameClass,
                Visibility.PRIVATE,
                FieldManifestation.PLAIN
        ).defineMethod(
                "get" + nameField,
                nameClass,
                Modifier.PUBLIC
        ).intercept(FieldAccessor.ofField(nameField)).defineMethod(
                "set" + nameField,
                void.class,
                Modifier.PUBLIC
        )
                .withParameters(nameClass)
                .intercept(FieldAccessor.ofField(nameField).setsArgumentAt(0));
    }

}
