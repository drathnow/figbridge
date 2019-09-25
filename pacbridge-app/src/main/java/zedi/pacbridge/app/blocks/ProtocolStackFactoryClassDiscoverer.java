package zedi.pacbridge.app.blocks;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import zedi.pacbridge.app.net.ProtocolStackFactory;
import zedi.pacbridge.net.annotations.Protocol;
import zedi.pacbridge.utl.AnnotationDiscoveryHelper;
import zedi.pacbridge.utl.ReflectionHelper;


@SuppressWarnings("unchecked")
public class ProtocolStackFactoryClassDiscoverer {

    private static Map<String, Class<? extends ProtocolStackFactory>> protocolStackFactoryClasses = new TreeMap<>();

    static {
        AnnotationDiscoveryHelper helper = new AnnotationDiscoveryHelper();
        Set<Class<?>> classes = helper.classesWithAnnotation(Protocol.class, "zedi.pacbridge");
        for (Class<?> clazz : classes) {
            if (clazz != null) {
                if (ReflectionHelper.classImplementsInterface(clazz, ProtocolStackFactory.class)) {
                    Protocol annotation = clazz.getAnnotation(Protocol.class);
                    protocolStackFactoryClasses.put(annotation.name().toUpperCase(), (Class<? extends ProtocolStackFactory>)clazz);
                } else 
                    throw new RuntimeException("Class annoated with @Protocol does not implement ProtocolStackFactory: " + clazz.getName());
            }
        }
    }

    public Class<? extends ProtocolStackFactory> protocolStackFactoryClassForProtocolName(String protocolName) {
        return protocolStackFactoryClasses.get(protocolName.toUpperCase());
    }
}
