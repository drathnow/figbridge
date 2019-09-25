package zedi.pacbridge.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import zedi.pacbridge.net.annotations.HandleUnsolicitedMessage;
import zedi.pacbridge.utl.ReflectionHelper;
import zedi.pacbridge.utl.SiteAddress;

public class UnsolicitedMessageHandlerAdapter {
    private Object implemenation;
    private Method implemenationMethod;

    public UnsolicitedMessageHandlerAdapter(UnsolicitedMessageHandler implemenation) {
        this.implemenation = implemenation;
        this.implemenationMethod = ReflectionHelper.methodWithNameFromClass("handleUnsolicitedMessage", implemenation.getClass());
    }
    
    public UnsolicitedMessageHandlerAdapter(Object implemenation) {
        this.implemenation = implemenation;
        this.implemenationMethod = ReflectionHelper.methodWithAnnotationFromObject(HandleUnsolicitedMessage.class, implemenation.getClass());
        if (this.implemenationMethod == null)
            throw new IllegalArgumentException("Specified object does not contain a method with @HandleUnsolicitedMessage annotation");
        Class<?>[] parameterTypes = this.implemenationMethod.getParameterTypes();
        if (parameterTypes.length != 3 || parameterTypes[0] != SiteAddress.class || parameterTypes[1] != Message.class || parameterTypes[2] != ResponseSender.class)
            throw new IllegalArgumentException("HandleUnsolicitedMessage method has incorrect signature. It must accept a 'SiteAddress', 'Message' and 'ResponseSender' arguments.");
        this.implemenationMethod.setAccessible(true);
    }
    
    public void invoke(SiteAddress siteAddress, Message message, ResponseSender responseSender) {
        try {
            implemenationMethod.invoke(implemenation, siteAddress, message, responseSender);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Unable to invoke UnsolicitedMessageHandler handler method", e);
        }
    }
}
