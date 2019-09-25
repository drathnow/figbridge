package zedi.pacbridge.test;

import java.lang.reflect.Field;

public class InjectorHelper {

	public static void injectObject(Object targetObject, String fieldName, Object valueToInject) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = targetObject.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(targetObject, valueToInject);
	}
}
