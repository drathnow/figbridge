package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Asynchronous;

import org.junit.Test;

public class ReflectionHelperTest {

    
    @Test
    public void shouldFindMethodWithAnnotationAndParameterTypes() throws Exception {
        Class<?>[] params = new Class<?>[]{String.class, Integer.class, Long.class};
        Method method = ReflectionHelper.methodWithAnnotationAndParameterTypesFromClass(Foo.class, params, MyClass.class);
        assertNotNull(method);
        assertEquals("testMethod", method.getName());
    }
    
//    @Test
//    public void shouldReturnTrueIfClassHasAnnotation() throws Exception {
//        assertTrue(ReflectionHelper.classHasTypeAnnotation(MyClass.class, Asynchronous.class));
//        assertFalse(ReflectionHelper.classHasTypeAnnotation(MyClass.class, Inject.class));
//    }
//    
    @Test
    public void shouldApplyPropertiesToObject() throws Exception {
        Map<String, Object> properties = new TreeMap<String, Object>();
        properties.put("Foo", "Hello");
        properties.put("Bar", 345);
        MyClass instance = new MyClass();
        ReflectionHelper helper = new ReflectionHelper();
        helper.applyPropertiesToObject(instance, properties);
        assertEquals("Hello", instance.foo);
        assertEquals(345, instance.bar);
    }
    
    @Test
    public void shouldCreateNewInstanceOfClassByClassWithArgsConstructor() throws Exception {
        ReflectionHelper helper = new ReflectionHelper();
        Object[] args = new Object[] {"Hello World", 1};
        MyClass instance = helper.<MyClass>newInstanceOfClassWithConstructorArgs(MyClass.class, args);
        assertEquals("Hello World", instance.foo);
        assertEquals(1, instance.bar);
    }
    
    @Test
    public void shouldCreateNewInstanceOfClassByNameWithArgsConstructor() throws Exception {
        ReflectionHelper helper = new ReflectionHelper();
        Object[] args = new Object[] {"Hello World", 1};
        MyClass instance = helper.<MyClass>newInstanceOfClassWithNameWithConstructorArgs(MyClass.class.getName(), args);
        assertEquals("Hello World", instance.foo);
        assertEquals(1, instance.bar);
    }
    
    @Test
    public void shouldCreateNewInstanceOfClassClassWithNoArgConstructor() throws Exception {
        ReflectionHelper helper = new ReflectionHelper();
        MyClass instance = helper.<MyClass>newInstanceOfClass(MyClass.class);
        assertEquals("Null", instance.foo);
        assertEquals(Integer.MAX_VALUE, instance.bar);
    }

    @Test
    public void shouldCreateNewInstanceOfClassByNameWithNoArgConstructor() throws Exception {
        ReflectionHelper helper = new ReflectionHelper();
        MyClass instance = helper.<MyClass>newInstanceOfClassWithName(MyClass.class.getName());
        assertEquals("Null", instance.foo);
        assertEquals(Integer.MAX_VALUE, instance.bar);
    }

    @Asynchronous
    public static class MyClass {
        
        public String foo;
        public int bar;
        
        public MyClass() {
            foo = "Null";
            bar = Integer.MAX_VALUE;
        }
        
        public MyClass(String foo, Integer bar) {
            this.foo = foo;
            this.bar = bar;
        }
        
        @Foo
        public void testMethod(String foo, Integer bar, Long spooge) {
            
        }
        
        public void setFoo(String s) {
            this.foo = s;
        }
        
        public void setBar(int bar) {
            this.bar = bar;
        }
    }
}
