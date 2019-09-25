package zedi.pacbridge.app.util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.controls.ControlRequestProgressListener;
import zedi.pacbridge.app.controls.ControlResponseStrategyFactory;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.controls.OutgoingRequestManager;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.app.publishers.EventPublisher;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

@Stateless
@EJB(name = LookupHelper.JNDI_NAME, beanInterface=LookupHelper.class)
public class LookupHelper {
    public static final String JNDI_NAME = "java:global/LookupHelper";
    
    @Inject Instance<NotificationCenter> notificationCenterInstance;
    @Inject Instance<RequestProgressListener> listenerInstance;
    @Inject Instance<NetworkService> networkServiceInstance;
    @Inject Instance<FieldTypeLibrary> fieldTypeLibraryInstance;
    @Inject Instance<ControlResponseStrategyFactory> controlResponseStrategyFactoryInstance;
    @Inject Instance<ControlRequestProgressListener> controlRequestProgressListenerInstance;
    @Inject Instance<AuthenticationDelegate> authenticationDelegateInstance;
    @Inject Instance<OutgoingRequestCache> outgoingRequestCacheInstance;
    @Inject Instance<OutgoingRequestManager> outgoingRequestManagerInstance;
    @Inject Instance<DeviceCache> deviceCacheInstance;
    @Inject Instance<EventPublisher> eventPublisherInstance;
    
    public NotificationCenter getNotificationCenter() {
        return notificationCenterInstance.get();
    }
    
    public EventHandler getEventPublisher() {
        return eventPublisherInstance.get();
    }
    
    public DeviceCache getDeviceCache() {
        return deviceCacheInstance.get();
    }
    
//    public DeviceRepository getDeviceRepository() {
//        return deviceRepositoryInstance.get();
//    }
    
    public RequestProgressListener getRequestProgressListener() {
        return listenerInstance.get();
    }
    
    public NetworkService getNetworkService() {
        return networkServiceInstance.get();
    }
    
    public FieldTypeLibrary getFieldTypeLibrary() {
        return fieldTypeLibraryInstance.get();
    }
    
    public ControlResponseStrategyFactory getControlResponseStrategyFactory() {
        return controlResponseStrategyFactoryInstance.get();
    }
    
    public AuthenticationDelegate getAuthenticationDelegate() {
        return authenticationDelegateInstance.get();
    }

    public OutgoingRequestCache getOutgoingRequestCache() {
        return outgoingRequestCacheInstance.get();
    }
    
    public OutgoingRequestManager getOutgoingRequestManager() {
        return outgoingRequestManagerInstance.get();
    }
}
