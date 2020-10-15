package TestsWeka;

import TestsWeka.classes.FooClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Test2 {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        testAddFieldWithByteBuddy2();
    }


    public static void testAddFieldWithByteBuddy2() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> newClass = new ByteBuddy().rebase(FooClass.class)
                .defineField("foo", String.class, Visibility.PRIVATE)
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();


    }

}
