package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import zedi.pacbridge.utl.annotations.AnnotatedTestClass;
import zedi.pacbridge.utl.annotations.TestAnnotation;

public class AnnotationDiscoveryHelperTest {

    @Test
    public void shouldFindAnnotatedClasses() {
        AnnotationDiscoveryHelper helper = new AnnotationDiscoveryHelper();
        Set<Class<?>> classes = helper.classesWithAnnotation(TestAnnotation.class, "zedi.pacbridge");
        assertEquals(1, classes.size());
        assertTrue(classes.contains(AnnotatedTestClass.class));
    }

}
