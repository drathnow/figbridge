package zedi.pacbridge.utl;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;


public class ReflectionHelper {

    @SuppressWarnings("unchecked")
    public <T> T newInstanceOfClassWithNameWithConstructorArgs(String className, Object[] constructorArgs) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Class<T> clazz = (Class<T>)Class.forName(className);
        return newInstanceOfClassWithConstructorArgs(clazz, constructorArgs);
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstanceOfClassWithName(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<T> clazz = (Class<T>)Class.forName(className);
        return newInstanceOfClass(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstanceOfClassWithConstructorArgs(Class<T> clazz, Object[] constructorArgs) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<T>[] argClasses = new Class[constructorArgs.length];
        for (int i = 0; i < argClasses.length; i++)
            argClasses[i] = (Class<T>)constructorArgs[i].getClass();
        Constructor<T> constructor = clazz.getConstructor(argClasses);
        return (T)constructor.newInstance(constructorArgs);
    }

    public <T> T newInstanceOfClass(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        return (T)clazz.newInstance();
    }

    public void applyPropertiesToObject(Object object, Map<String, Object> properties) throws IllegalAccessException, InvocationTargetException {
        for (String key : properties.keySet())
            BeanUtils.setProperty(object, Introspector.decapitalize(key), properties.get(key));
    }

    public static Method methodWithAnnotationFromObject(Class<? extends Annotation> annotationClass, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }
    
    public static Method methodWithAnnotationAndParameterTypesFromClass(Class<? extends Annotation> annotationClass, Class<?>[] parameterTypes, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                Class<?>[] types = method.getParameterTypes();
                if (types.length == parameterTypes.length)
                    if (isMatchingParameters(types, parameterTypes) == false)
                        continue;
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }
    
    public static Method methodWithNameFromClass(String name, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }
    
    public static Field[] fieldsWithNameForClass(Class<?> clazz, String aRegularExpression) {
        Field[] fields = clazz.getDeclaredFields();
        Pattern pattern = Pattern.compile(aRegularExpression);
        ArrayList<Field> result = new ArrayList<Field>();
        for (int i = 0; i < fields.length; i++) {
            Matcher matcher = pattern.matcher(fields[i].getName());
            if (matcher.matches())
                result.add(fields[i]);
        }
        return (Field[])result.toArray(new Field[result.size()]);
    }

    public static boolean classHasTypeAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return clazz.getAnnotation(annotationClass) != null;
    }
    
    public static boolean hasNoArgsConstructor(Class<?> clazz) {
        try {
            clazz.getConstructor((Class<?>[])null);
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    public static boolean classImplementsInterface(Class<?> clazz, Class<?> interfaceClass) {
        if (clazz == null)
            return false;
        for (Class<?> iClass : clazz.getInterfaces())
            if (iClass == interfaceClass || classImplementsInterface(iClass, interfaceClass))
                return true;
        return classImplementsInterface(clazz.getSuperclass(), interfaceClass);
    }
    
    private static boolean isMatchingParameters(Class<?>[] types, Class<?>[] parameterTypes) {
        if (types.length == parameterTypes.length) {
            for (int i = 0; i < types.length; i++)
                if (types[i] != parameterTypes[i])
                    return false;
            return true;
        }
        return false;
    }

    public static void setObjectsFieldWithValue(Object object, String name, Object value) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = fieldsWithNameForClass(object.getClass(), name);
        if (fields.length == 0)
            throw new IllegalArgumentException(object.getClass().getName() + " does not have a field named '" + name + "'");
        fields[0].setAccessible(true);
        fields[0].set(object, value);
    }


}
