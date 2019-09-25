package zedi.pacbridge.app.blocks;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.utl.ReflectionHelper;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;


public class ProtocolStackBuilder {
    private Map<String, Object> propertiesMap;
    private Class<?> protocolStackClass;
    private ReflectionHelper reflectionHelper;

    ProtocolStackBuilder(Class<? extends ProtocolStack> protocolStackClass, ReflectionHelper reflectionHelper, Map<String, Object> propertiesMap) {
        this.protocolStackClass = protocolStackClass;
        this.reflectionHelper = reflectionHelper;
        this.propertiesMap = propertiesMap;
    }

    public ProtocolStackBuilder(Class<? extends ProtocolStack> protocolStackClass, Map<String, Object> propertiesMap) {
        this(protocolStackClass, new ReflectionHelper(), propertiesMap);
    }
    
    public ProtocolStack newProtocolStack(ThreadContext requester) {
        try {
            //
            // Find any ProtocolStack implemenations with a protocol annotation
            // containing the specified name
            //
            ProtocolStack protocolStack = (ProtocolStack)protocolStackClass.newInstance();
            
            //
            // If we have properties, apply them
            //
            if (propertiesMap != null)
                reflectionHelper.applyPropertiesToObject(protocolStack, propertiesMap);
            if (requester != null)
                injectAstRequesterIfRequested(protocolStack, requester);
            return protocolStack;
        } catch (Exception e) {
            throw new RuntimeException("Unable to build ProtocolStack", e);
        }
    }
    
    private static void injectAstRequesterIfRequested(Object instance, ThreadContext requester) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method[] methods = instance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ThreadContextHandler) {
                    method.invoke(instance, requester);
                    return;
                }
            }
        }
    }
}
