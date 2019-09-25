package zedi.pacbridge.net.tcp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.TransportAdapter;
import zedi.pacbridge.net.annotations.TransportAdapterClosed;
import zedi.pacbridge.net.annotations.TransportAdapterConnectFailed;
import zedi.pacbridge.net.annotations.TransportAdapterConnected;
import zedi.pacbridge.net.annotations.TransportAdapterConnecting;
import zedi.pacbridge.net.annotations.TransportAdapterReceivedData;
import zedi.pacbridge.net.annotations.TransportAdapterWritingData;
import zedi.pacbridge.utl.ReflectionHelper;

public class TransportAdapterEventListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TransportAdapterEventListenerAdapter.class.getName());
    
    private Object eventListener;
    private Method closeEventMethod;
    private Method connectedEventMethod;
    private Method connectingEventMethod;
    private Method connectFailedEventMethod;
    private Method receivedDataEventMethod;
    private TransportAdapter adapter;

    private Method writingDataEventMethod;

    public TransportAdapterEventListenerAdapter(Object eventListener, TransportAdapter adapter) {
        this.adapter = adapter;
        this.eventListener = eventListener;
        closeEventMethod = eventMethodWithAnnotation(TransportAdapterClosed.class, eventListener.getClass());
        connectedEventMethod = eventMethodWithAnnotation(TransportAdapterConnected.class, eventListener.getClass());
        connectingEventMethod = eventMethodWithAnnotation(TransportAdapterConnecting.class, eventListener.getClass());
        connectFailedEventMethod = eventMethodWithAnnotation(TransportAdapterConnectFailed.class, eventListener.getClass());
        receivedDataEventMethod = dataEventMethodWithAnnotation(TransportAdapterReceivedData.class, eventListener.getClass());
        writingDataEventMethod = dataEventMethodWithAnnotation(TransportAdapterWritingData.class, eventListener.getClass());
        
        if (closeEventMethod == null && connectedEventMethod == null && receivedDataEventMethod == null && writingDataEventMethod == null)
            throw new IllegalArgumentException("No event methods specified in TransportAdapter event listener object");
    }

    public void postCloseEvent() {
        invokeMethodOnListenerObject("TransportAdapterClosed", closeEventMethod, adapter, eventListener);
    }
    
    public void postConnectedEvent() {
        invokeMethodOnListenerObject("TransportAdapterConnected", connectedEventMethod, adapter, eventListener);
    }
    
    public void postConnectingEvent() {
        invokeMethodOnListenerObject("TransportAdapterConnecting", connectingEventMethod, adapter, eventListener);
    }

    public void postConnectFailedEvent() {
        invokeMethodOnListenerObject("TransportAdapterConnectFailed", connectFailedEventMethod, adapter, eventListener);
    }

    public void postWritingDataEvent(ByteBuffer byteBuffer) {
        invokeMethodOnListenerObject("TransportAdapterWritingData", writingDataEventMethod, adapter, byteBuffer, eventListener);
    }

    public void postReceivedDataEvent(ByteBuffer byteBuffer) {
        invokeMethodOnListenerObject("TransportAdapterReceivedData", receivedDataEventMethod, adapter, byteBuffer, eventListener);
    }
    
    private static void invokeMethodOnListenerObject(String name, Method method, TransportAdapter adapter, ByteBuffer byteBuffer, Object listenerObject) { 
        if (method != null) {
            try {
                method.invoke(listenerObject, adapter, byteBuffer);
            } catch (Exception e) {
                logger.error("Unexpected exception raised from " + name + " event method", e);
            }
        }
    }
    
    private static void invokeMethodOnListenerObject(String name, Method method, TransportAdapter adapter, Object listenerObject) { 
        if (method != null) {
            try {
                method.invoke(listenerObject, adapter);
            } catch (Exception e) {
                logger.error("Unexpected exception raised from " + name + " event method", e);
            }
        }
    }

    private static Method dataEventMethodWithAnnotation(Class<? extends Annotation> annotationClass, Class<?> objectClass) {
        Method eventMethod = ReflectionHelper.methodWithAnnotationFromObject(annotationClass, objectClass);
        if (eventMethod == null)
            return null;
        if (eventMethod.getParameterTypes().length == 0)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method specified is missing required parameters (TransportAdapter and ByteBuffer");
        if (eventMethod.getParameterTypes().length < 2)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method specified can only contain 1 parameter");
        if (eventMethod.getParameterTypes()[0] != TransportAdapter.class)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method must specify TransportAdapter as first parameter");
        if (eventMethod.getParameterTypes()[1] != ByteBuffer.class)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method must specify ByteBuffer as second parameter");
        if (eventMethod.isAccessible() == false)
            eventMethod.setAccessible(true);
        return eventMethod;
    }
    
    private static Method eventMethodWithAnnotation(Class<? extends Annotation> annotationClass, Class<?> objectClass) {
        Method eventMethod = ReflectionHelper.methodWithAnnotationFromObject(annotationClass, objectClass);
        if (eventMethod == null)
            return null;
        if (eventMethod.getParameterTypes().length == 0)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method specified is missing TransportAdapter parameter");
        if (eventMethod.getParameterTypes().length > 1)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method specified can only contain 1 parameter");
        if (eventMethod.getParameterTypes()[0] != TransportAdapter.class)
            throw new IllegalArgumentException(annotationClass.getSimpleName() + " method must specify can only contain 1 parameter of type TransportAdapter");
        if (eventMethod.isAccessible() == false)
            eventMethod.setAccessible(true);
        return eventMethod;
    }
}
