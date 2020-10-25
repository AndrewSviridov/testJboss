package test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class NewClassExample {

    public static void main(String[] args)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
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
        System.out.println(obj);

        Method getMyField = newClass.getDeclaredMethod("getMyField", String.class);
        System.out.println((String) getMyField.invoke(obj));

        Method setMyField = newClass.getDeclaredMethod("setMyField", String.class);
        setMyField.invoke(obj, "Set My Field");
        System.out.println((String) getMyField.invoke(obj));

        MyClassFactory factory = new ByteBuddy()
                .subclass(MyClassFactory.class)
                .method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class)))
                .intercept(MethodDelegation.toConstructor(newClass))
                .make()
                .load(newClass.getClassLoader())
                .getLoaded()
                .newInstance();

        Object obj2 = factory.makeInstance("ABC");
        System.out.println((String) getMyField.invoke(obj2));
    }

}
