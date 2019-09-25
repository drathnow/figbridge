package zedi.pacbridge.test;

import java.lang.reflect.Field;

public class SingletonTestHelper {

    public static void replaceStaticInstance(Class<?> singletonClass, Object value) throws NoSuchFieldException {
        replaceStaticInstance(singletonClass, "sharedInstance", value);
    }

    public static void replaceStaticInstance(Class<?> singletonClass, String fieldName, Object value) throws NoSuchFieldException {
        try {
            Field field = singletonClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
            return;
        } catch (Exception ex) {
        }
        throw new NoSuchFieldException("Could set value for field " + singletonClass.getName() + "." + fieldName);
    }
    
    public static boolean isSingletonNull(Class<?> singletonClass) throws NoSuchFieldException {
        return isSingletonNull(singletonClass, "sharedInstance");
    }
    
    public static boolean isSingletonNull(Class<?> singletonClass, String fieldName) throws NoSuchFieldException {
        try {
            Field field = singletonClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return null == field.get(null);
        } catch (Exception ex) {
        }
        throw new NoSuchFieldException("Could set value for field " + singletonClass.getName() + "." + fieldName);
    }

}
