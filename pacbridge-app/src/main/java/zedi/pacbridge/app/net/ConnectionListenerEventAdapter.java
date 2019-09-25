package zedi.pacbridge.app.net;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.annotations.ConnectionClosed;
import zedi.pacbridge.net.annotations.ConnectionConnectFailed;
import zedi.pacbridge.net.annotations.ConnectionConnected;
import zedi.pacbridge.net.annotations.ConnectionConnecting;
import zedi.pacbridge.net.annotations.ConnectionIdentityChanged;
import zedi.pacbridge.net.annotations.ConnectionUnexpectedlyClosed;
import zedi.pacbridge.utl.ReflectionHelper;
import zedi.pacbridge.utl.SiteAddress;


public class ConnectionListenerEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionListenerEventAdapter.class.getName());

    private Method connectedEventMethod;
    private Method closedEventMethod;
    private Method connectingEventMethod;
    private Method connectFailedEventMethod;
    private Method connectionIdentityChangedMethod;
    private Method connectionUnexpectedlyClosedMethod;
    private Object eventListenerObject;
    private Connection connection;

    public ConnectionListenerEventAdapter(Connection connection, Object eventListenerObject) {
        this.eventListenerObject = eventListenerObject;
        this.connection = connection;
        connectedEventMethod = eventMethodWithAnnotation(ConnectionConnected.class, eventListenerObject.getClass());
        closedEventMethod = eventMethodWithAnnotation(ConnectionClosed.class, eventListenerObject.getClass());
        connectingEventMethod = eventMethodWithAnnotation(ConnectionConnecting.class, eventListenerObject.getClass());
        connectFailedEventMethod = eventMethodWithAnnotation(ConnectionConnectFailed.class, eventListenerObject.getClass());
        connectionIdentityChangedMethod = eventMethodWithAnnotation(ConnectionIdentityChanged.class, eventListenerObject.getClass());

        Class<?>[] parameters = new Class<?>[]{Connection.class, 
                                               Exception.class,
                                               SiteAddress.class,
                                               SocketAddress.class,
                                               String.class};
        connectionUnexpectedlyClosedMethod = eventMethodWithAnnotationAndParameters(ConnectionUnexpectedlyClosed.class, eventListenerObject.getClass(), parameters);
        
        if (connectedEventMethod == null && closedEventMethod == null && connectingEventMethod == null && connectFailedEventMethod == null && connectionIdentityChangedMethod == null)
            throw new IllegalArgumentException("No event methods specified in TransportAdapter event listener object");
    }

    public void postConnectingEvent() {
        invokeMethodOnListenerObject("ConnectionConnecting", connectingEventMethod, connection, eventListenerObject);
    }
    
    public void postConnectFailedEvent() {
        invokeMethodOnListenerObject("ConnectionConnectFailed", connectFailedEventMethod, connection, eventListenerObject);
    }

    public void postClosedEvent() {
        invokeMethodOnListenerObject("ConnectionClosed", closedEventMethod, connection, eventListenerObject);
    }

    public void postUnexpectedCloseEvent(Exception exception, SiteAddress siteAddress, SocketAddress address, String message) {
        if (connectionUnexpectedlyClosedMethod != null) {
            try {
                connectionUnexpectedlyClosedMethod.invoke(eventListenerObject, connection, exception, siteAddress, address, message);
            } catch (Exception e) {
                logger.error("Unexpected exception raised from ConnectionUnexpectedlyClosed event method", e);
            }
        }

    }
    
    public void postConnectedEvent() {
        invokeMethodOnListenerObject("ConnectionConnected", connectedEventMethod, connection, eventListenerObject);
    }

    public void postIdentityChangedEvent() {
        invokeMethodOnListenerObject("ConnectionIdentityChanged", connectionIdentityChangedMethod, connection, eventListenerObject);
    }
    
    private static void invokeMethodOnListenerObject(String name, Method method, Connection connection, Object listenerObject) {
        if (method != null) {
            try {
                method.invoke(listenerObject, connection);
            } catch (Exception e) {
                logger.error("Unexpected exception raised from " + name + " event method", e);
            }
        }
    }

    private static Method eventMethodWithAnnotationAndParameters(Class<? extends Annotation> annotationClass, Class<?> objectClass, Class<?>[] params) {
        Method eventMethod = ReflectionHelper.methodWithAnnotationFromObject(annotationClass, objectClass);
        if (eventMethod == null)
            return null;
        if (eventMethod.getParameterTypes().length != params.length)
            throw new IllegalArgumentException("Class " 
                                                + objectClass.getName() 
                                                + " with annotation " 
                                                + annotationClass.getSimpleName() 
                                                + " method specified is required parameters");
        int i = 0;
        for (Class<?> clazz : params) {
            if (clazz.equals(eventMethod.getParameterTypes()[i++]) == false)
                throw new IllegalArgumentException("Class " 
                        + objectClass.getName() 
                        + " with annotation " 
                        + annotationClass.getSimpleName() + " does not have the correct parameter order");
        }
        if (eventMethod.isAccessible() == false)
            eventMethod.setAccessible(true);
        return eventMethod;
    }

    private static Method eventMethodWithAnnotation(Class<? extends Annotation> annotationClass, Class<?> objectClass) {
        Method eventMethod = ReflectionHelper.methodWithAnnotationFromObject(annotationClass, objectClass);
        if (eventMethod == null)
            return null;
        if (eventMethod.getParameterTypes().length == 0)
            throw new IllegalArgumentException("Class " 
                                                + objectClass.getName() 
                                                + " with annotation " 
                                                + annotationClass.getSimpleName() + " method specified is missing Connection parameter");
        if (eventMethod.getParameterTypes().length > 1)
            throw new IllegalArgumentException("Class " 
                                                + objectClass.getName() 
                                                + " with annotation " 
                                                + annotationClass.getSimpleName() + " method specified can only contain 1 parameter");
        if (eventMethod.getParameterTypes()[0] != Connection.class)
            throw new IllegalArgumentException("Class " 
                                                + objectClass.getName() 
                                                + " with annotation " 
                                                +annotationClass.getSimpleName() + " method must specify can only contain 1 parameter of type Connection");
        if (eventMethod.isAccessible() == false)
            eventMethod.setAccessible(true);
        return eventMethod;
    }

}
