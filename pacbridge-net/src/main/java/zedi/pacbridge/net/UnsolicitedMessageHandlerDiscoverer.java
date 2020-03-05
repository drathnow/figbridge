package zedi.pacbridge.net;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.annotations.HandleUnsolicitedMessage;
import zedi.pacbridge.net.annotations.UnsolicitedMessageHandler;
import zedi.pacbridge.utl.AnnotationDiscoveryHelper;
import zedi.pacbridge.utl.ReflectionHelper;
import zedi.pacbridge.utl.SiteAddress;

public class UnsolicitedMessageHandlerDiscoverer {
    private static final Logger logger = LoggerFactory.getLogger(UnsolicitedMessageHandlerDiscoverer.class.getName());
    private static Map<String, Class<?>> unsolicitedMsgHandlerClasses = new TreeMap<String, Class<?>>();
    
    static {
        AnnotationDiscoveryHelper helper = new AnnotationDiscoveryHelper();
        Set<Class<?>> classes = helper.classesWithAnnotation(UnsolicitedMessageHandler.class, "zedi.pacbridge");
        Class<?>[] params = new Class<?>[]{SiteAddress.class, Message.class};
        for (Class<?> clazz : classes) {
            if (ReflectionHelper.methodWithAnnotationAndParameterTypesFromClass(HandleUnsolicitedMessage.class, params, clazz) == null)
                logger.warn("Class " + clazz.getName() + " has @UnsolicitedMessageHandler annotation but is missing a @HandleUnsolicitedMessage method or method has the wrong signature");
            else {
                UnsolicitedMessageHandler annotation = clazz.getAnnotation(UnsolicitedMessageHandler.class);
                unsolicitedMsgHandlerClasses.put(annotation.forNetworkType().toUpperCase(), (Class<?>)clazz);
            }
        }
    }
    
	public Object handlerForNetworkTypeName(String networkTypeName) {
        try {
            Class<?> clazz = unsolicitedMsgHandlerClasses.get(networkTypeName.toUpperCase());
            return clazz == null ? null : clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate UnsolicitedMessageHandler annotated class.", e);
        }
    }
}
