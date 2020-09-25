import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author zhangwu
 * @version 1.0.0
 * @date 2019-01-11-11:30
 */
public class MyRedefineTest {

    class Foo {
        String field1 = "123";

        public String getField1() {
            return field1;
        }

        @Override
        public String toString() {
            return "Foo{" +
                    "field1='" + field1 + '\'' +
                    '}';
        }
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

                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        // System.out.println(foo.m());
        for (Method method :
                foo.getClass().getDeclaredMethods()) {
            System.out.println(method.getName() + "\t" + method);
        }
    }

}