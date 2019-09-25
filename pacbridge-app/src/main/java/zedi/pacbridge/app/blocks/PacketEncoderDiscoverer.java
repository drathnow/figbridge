package zedi.pacbridge.app.blocks;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.PacketEncoder;
import zedi.pacbridge.net.annotations.PacketLayerEncoder;
import zedi.pacbridge.utl.AnnotationDiscoveryHelper;
import zedi.pacbridge.utl.ReflectionHelper;

/**
 * Utility class that discovers all classes annotated with {@link PacketEncoder}.  Only classes
 * in {@code zedi.pacbridge}, and its sub-packages, are searched.
 */
@SuppressWarnings("unchecked")
public class PacketEncoderDiscoverer {
    private static final Logger logger = LoggerFactory.getLogger(PacketEncoderDiscoverer.class.getName());
    private static Map<String, Class<? extends PacketEncoder>> encoderClasses = new TreeMap<>();

    static {
        AnnotationDiscoveryHelper helper = new AnnotationDiscoveryHelper();
        Set<Class<?>> classes = helper.classesWithAnnotation(PacketLayerEncoder.class, "zedi.pacbridge");
        for (Class<?> clazz : classes) {
            if (ReflectionHelper.classImplementsInterface(clazz, PacketEncoder.class)) {
                PacketLayerEncoder annotation = clazz.getAnnotation(PacketLayerEncoder.class);
                logger.info("Adding class " + clazz.getName() + " for key " + annotation.forNetworkType().toUpperCase());
                encoderClasses.put(annotation.forNetworkType().toUpperCase(), (Class<? extends PacketEncoder>)clazz);
            }
        }
    }

    public Class<? extends PacketEncoder> packetEncoderClassForName(String name) {
        return encoderClasses.get(name.toUpperCase());
    }
}
