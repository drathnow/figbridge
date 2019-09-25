package zedi.pacbridge.msg;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.msg.annotations.JmsImplementation;
import zedi.pacbridge.utl.AnnotationDiscoveryHelper;
import zedi.pacbridge.utl.ReflectionHelper;
import zedi.pacbridge.utl.StringUtilities;

@ApplicationScoped
public class JmsImplementationBuilder {
    private static Logger logger = LoggerFactory.getLogger(JmsImplementationBuilder.class.getName());

    private ReflectionHelper reflectionHelper;
    private Map<String, String> implementationMap;
    private AnnotationDiscoveryHelper annotationDiscoveryHelper;

    JmsImplementationBuilder(AnnotationDiscoveryHelper annotationDiscoveryHelper, ReflectionHelper reflectionHelper) {
        this.annotationDiscoveryHelper = annotationDiscoveryHelper;
        this.reflectionHelper = reflectionHelper;
    }
    
    public JmsImplementationBuilder() {
        this(new AnnotationDiscoveryHelper(), new ReflectionHelper());
    }

    public JmsImplementationBuilder usingImplementationMap(Map<String, String> implementationMap) {
        this.implementationMap = implementationMap;
        return this;
    }

    @SuppressWarnings("unchecked")
    public JmsImplementor buildJmsImplementor() {
        String name = implementationMap.get("name");
        JmsImplementor implementor = newInstanceOfImplementorWithName(name);
        try {
            Map<String, Object> properties = BeanUtils.describe(implementor);
            for (String key : implementationMap.keySet())
                properties.put(Introspector.decapitalize(key), objectForString(implementationMap.get(key)));
            BeanUtils.populate(implementor, properties);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to create instance of JMS Implemenation class " + implementor.getClass().getName(), e);
        }
        return implementor;
    }

    private Object objectForString(String string) {
        if (StringUtilities.isNumericString(string))
            return new Integer(string);
        return string;
    }

    @SuppressWarnings("unchecked")
    private JmsImplementor newInstanceOfImplementorWithName(String name) {
        Set<Class<?>> classes = annotationDiscoveryHelper.classesWithAnnotation(JmsImplementation.class, "zedi.pacbridge");
        if (classes.isEmpty() == false) {
            for (Class<?> clazz : classes) {
                if (ReflectionHelper.classImplementsInterface(clazz, JmsImplementor.class) == false)
                    logger.error("Discovered class " + clazz.getName() + " annotated as a JmsImplemenation but it does not implement the JmsImplementor interface");
                else {
                    JmsImplementation annotation = clazz.getAnnotation(JmsImplementation.class);
                    if (annotation.name().equals(name)) {
                        try {
                            return reflectionHelper.<JmsImplementor>newInstanceOfClass((Class<JmsImplementor>)clazz);
                        } catch (Exception e) {
                            throw new RuntimeException("Unable to create instance of JMS Implemenation class " + clazz.getName(), e);
                        }
                    }
                }
            }
        } 
        return null;
    }
}
