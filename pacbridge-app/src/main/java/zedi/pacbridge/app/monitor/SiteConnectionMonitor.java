package zedi.pacbridge.app.monitor;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import zedi.pacbridge.app.events.zios.SiteConnectedEvent;
import zedi.pacbridge.app.events.zios.SiteDisconnectedEvent;
import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.SiteConnectedAttachment;
import zedi.pacbridge.app.net.SiteDisconnectedAttachment;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.app.util.LookupHelper;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;

@Singleton
@Startup
public class SiteConnectionMonitor implements Notifiable {
    private LookupHelper lookupHelper;
    
    public SiteConnectionMonitor() {
    }

    @Inject
    public SiteConnectionMonitor(NotificationCenter notificationCenter, LookupHelper lookupHelper) {
        this.lookupHelper = lookupHelper;
        notificationCenter.addObserver(this, Connection.CONNECTION_CONNECTED_NOTIFICATION);
        notificationCenter.addObserver(this, Connection.CONNECTION_CLOSED_NOTIFICATION);
    }
    
    @Override
    public void handleNotification(Notification notification) {
        if (Connection.CONNECTION_CONNECTED_NOTIFICATION.equals(notification.getName())) {
            SiteConnectedAttachment attachment = notification.getAttachment();
            publishSiteConnectedEvent(attachment);
        } else if (Connection.CONNECTION_CLOSED_NOTIFICATION.equals(notification.getName())) {
            SiteDisconnectedAttachment attachment = notification.getAttachment();
            publishSiteDisconnectedEvent(attachment);
        }
    }
    
    
    private void publishSiteConnectedEvent(SiteConnectedAttachment attachment) {
        EventHandler eventPublisher = lookupHelper.getEventPublisher();
        SiteConnectedEvent event = new SiteConnectedEvent(attachment.getSiteAddress(), attachment.getBridgeInstance(), attachment.getIpAddress(), attachment.getFirmwareVersion());
        eventPublisher.publishEvent(event);
    }

    private void publishSiteDisconnectedEvent(SiteDisconnectedAttachment attachment) {
        EventHandler eventPublisher = lookupHelper.getEventPublisher();
        SiteDisconnectedEvent event = new SiteDisconnectedEvent(attachment.getSiteAddress(), 
                                                                attachment.getBridgeInstance(), 
                                                                attachment.getIpAddress(),
                                                                attachment.getBytesReceived(),
                                                                attachment.getByteTransmitted());
        eventPublisher.publishEvent(event);
    }
}
