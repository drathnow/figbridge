package zedi.pacbridge.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.msg.annotations.JmsImplementation;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.AnnotationDiscoveryHelper;
import zedi.pacbridge.utl.ReflectionHelper;


public class JmsImplementationBuilderTest extends BaseTestCase {
    private Map<String, String> IMPL_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("name", "JmsImplementationBuilderTestImplementation");
            put("foo", "one");
            put("bar", "123");
        }
    });

    @Mock
    private AnnotationDiscoveryHelper annotationDiscoveryHelper;
    @Mock
    private ReflectionHelper reflectionHelper;

    @Test
    public void shouldReturnConfiguredImplemenation() throws Exception {
        MyJmsImplementor implementor = new MyJmsImplementor();
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(MyJmsImplementor.class);
        given(annotationDiscoveryHelper.classesWithAnnotation(JmsImplementation.class, "zedi.pacbridge")).willReturn(classes);
        given(reflectionHelper.newInstanceOfClass(MyJmsImplementor.class)).willReturn(implementor);

        JmsImplementationBuilder builder = new JmsImplementationBuilder(annotationDiscoveryHelper, reflectionHelper);

        MyJmsImplementor result = (MyJmsImplementor)builder
                                                     .usingImplementationMap(IMPL_MAP)
                                                     .buildJmsImplementor();
        assertSame(result, implementor);
        assertEquals("one", implementor.getFoo());
        assertEquals(123, implementor.getBar().intValue());
    }
}
