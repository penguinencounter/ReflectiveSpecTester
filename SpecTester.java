import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Compile separately for increased performance.
 * @param <T>
 */
public class SpecTester<T> {
    public enum Access {
        PRIVATE(false, Modifier.PRIVATE, "private"),
        PROTECTED(false, Modifier.PROTECTED, "protected"),
        PACKAGE_PRIVATE(true, Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC, "package-private"),
        PUBLIC(false, Modifier.PUBLIC, "public"),
        ACCESSIBLE(true, Modifier.PROTECTED | Modifier.PRIVATE, "not private"),
        INACCESSIBLE(false, Modifier.PROTECTED | Modifier.PRIVATE, "private");
        public final boolean inverted;
        public final int modifier;
        public final String niceName;

        Access(boolean inverted, int modifier, String niceName) {
            this.inverted = inverted;
            this.modifier = modifier;
            this.niceName = niceName;
        }

        public boolean matches(int modifiers) {
            return inverted ^ ((modifiers & modifier) != 0);
        }
    }

    private final Class<T> target;
    private final Consumer<String> fail;

    public SpecTester(Class<T> target, Consumer<String> fail) {
        this.target = target;
        this.fail = fail;
    }

    private Method findMethod(String name, Class<?>... types) {
        Method[] declList = target.getDeclaredMethods();
        try {
            return target.getDeclaredMethod(name, types);
        } catch (NoSuchMethodException e) {
            // Check if there are overloads
            for (Method decl : declList) {
                if (decl.getName().equals(name)) {
                    // Get the method list
                    Class<?>[] paramList = decl.getParameterTypes();
                    fail.accept(
                            "The " + name + " method has the wrong arguments: expected "
                                    + Arrays.toString(types) + " but got " + Arrays.toString(paramList)
                    );
                    return null;
                }
            }
            fail.accept("The " + name + " method is missing");
            return null;
        }
    }

    public void methodSpec(String name, Access access, Class<?> returnType, Class<?>... argTypes) {
        Method located = findMethod(name, argTypes);
        if (located == null) return;
        Class<?> returns = located.getReturnType();
        if (returns != returnType) {
            fail.accept("The " + name + " method has the wrong return type: expected " + returnType + " but got " + returns);
        }
        if (!access.matches(target.getModifiers())) {
            fail.accept("The " + name + " method should be " + access.niceName);
        }
    }

    public void constructorSpec(Access access, Class<?>... types) {
        try {
            Constructor<T> ctor = target.getDeclaredConstructor(types);
            if (!access.matches(ctor.getModifiers())) {
                fail.accept("The " + target.getName() + " constructor should be " + access.niceName);
            }
        } catch (NoSuchMethodException e) {
            fail.accept("The " + target.getName() + " class is missing a constructor with arguments " + Arrays.toString(types));
        }
    }

    public void fieldSpec(String fieldName, Access access, Class<?> fieldType) {
        try {
            Field field = target.getDeclaredField(fieldName);
            if (field.getType() != fieldType) {
                fail.accept("The " + fieldName + " field has the wrong type");
            }
            if (!access.matches(field.getModifiers())) {
                fail.accept("The " + fieldName + " field should be " + access.niceName);
            }
        } catch (NoSuchFieldException e) {
            fail.accept("The " + fieldName + " field is missing");
        }
    }
}
