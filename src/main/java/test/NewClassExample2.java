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
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Modifier;

public class NewClassExample2 {


    public void addMethod() {

    }

    public void addField() {

    }

    public void createClass() {

    }


    public static void main(String[] args) throws NoSuchMethodException {
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
                .intercept(FieldAccessor.ofField("myField").setsArgumentAt(0));

        //  DynamicType.Unloaded<Object> newClass2 = new ByteBuddy().

        builder.defineField(
                "addedField1",
                String.class,
                Visibility.PRIVATE,
                FieldManifestation.PLAIN
        );

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
