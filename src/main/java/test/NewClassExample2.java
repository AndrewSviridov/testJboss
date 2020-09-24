package test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Modifier;

public class NewClassExample2 {
    public static void main(String[] args) throws NoSuchMethodException {
        DynamicType.Unloaded<Object> newClass = new ByteBuddy()
                .subclass(
                        Object.class,
                        ConstructorStrategy.Default.NO_CONSTRUCTORS
                )
                .name("de.detim.workshop.NewClass")
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
                .make();

        //  DynamicType.Unloaded<Object> newClass2 = new ByteBuddy().

        newClass.include()


        Class<?> dynamicType = newClass.load(NewClassExample2.class.getClassLoader())
                .getLoaded();


    }
}
