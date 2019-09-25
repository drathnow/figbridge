package zedi.pacbridge.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import zedi.pacbridge.net.annotations.HandleMessageForSession;
import zedi.pacbridge.utl.ReflectionHelper;
import zedi.pacbridge.utl.SiteAddress;

public class SessionlessMessageHandlerAdapter {
    private Object implemenation;
    private Method implemenationMethod;
    
    public SessionlessMessageHandlerAdapter(SessionlessMessageHandler implemenation) {
        this.implemenation = implemenation;
        this.implemenationMethod = ReflectionHelper.methodWithNameFromClass("handleMessageForSession", implemenation.getClass());
    }

    public SessionlessMessageHandlerAdapter(Object implemenation) {
        this.implemenation = implemenation;
        this.implemenationMethod = ReflectionHelper.methodWithAnnotationFromObject(HandleMessageForSession.class, implemenation.getClass());
        if (this.implemenationMethod == null)
            throw new IllegalArgumentException("Specified object does not contain a method with @HandleMessageForSession annotation");
        Class<?>[] parameterTypes = this.implemenationMethod.getParameterTypes();
        if (parameterTypes.length != 3 || anyParameterTypeIsWrong(parameterTypes))
            throw new IllegalArgumentException("HandleMessageForSession method has incorrect signature. It must accept'Message' and 'Session' arguments.");
        this.implemenationMethod.setAccessible(true);
    }


    public void invoke(SiteAddress siteAddress, Message message, Session session) {
        try {
            implemenationMethod.invoke(implemenation,siteAddress, message, session);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Unable to invoke SessionlessMessageHandler handler method", e);
        }
    }
    
    private boolean anyParameterTypeIsWrong(Class<?>[] parameterTypes) {
        return parameterTypes[0] != SiteAddress.class || parameterTypes[1] != Message.class || parameterTypes[2] != Session.class;
    }
}
