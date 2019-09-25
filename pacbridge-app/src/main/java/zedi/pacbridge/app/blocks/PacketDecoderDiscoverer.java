package zedi.pacbridge.app.blocks;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.PacketDecoder;
import zedi.pacbridge.net.annotations.PacketLayerDecoder;
import zedi.pacbridge.utl.AnnotationDiscoveryHelper;
import zedi.pacbridge.utl.ReflectionHelper;

/**
 * Utility class that discovers all classes annotated with {@link PacketDecoder}.  Only classes
 * in {@code zedi.pacbridge}, and its sub-packages, are searched.
 */
@SuppressWarnings("unchecked")
public class PacketDecoderDiscoverer {
    private static final Logger logger = LoggerFactory.getLogger(PacketDecoderDiscoverer.class.getName());
    private static Map<String, Class<? extends PacketDecoder>> decoderClasses = new TreeMap<>();

    static {
        AnnotationDiscoveryHelper helper = new AnnotationDiscoveryHelper();
        Set<Class<?>> classes = helper.classesWithAnnotation(PacketLayerDecoder.class, "zedi.pacbridge");
        for (Class<?> clazz : classes) {
            if (ReflectionHelper.classImplementsInterface(clazz, PacketDecoder.class)) {
                PacketLayerDecoder annotation = clazz.getAnnotation(PacketLayerDecoder.class);
                decoderClasses.put(annotation.forNetworkType().toUpperCase(), (Class<? extends PacketDecoder>)clazz);
            } else
                logger.warn("Class " + clazz.getName() + " is annotated with @PacketLayerDecoder but it does not implement the PacketDecoder interface");
                
        }
    }

    public Class<? extends PacketDecoder> packetDecoderClassForName(String name) {
        return decoderClasses.get(name.toUpperCase());
    }
}
