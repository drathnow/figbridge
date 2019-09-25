package zedi.pacbridge.app.monitor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.infinispan.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.SiteConnectedAttachment;
import zedi.pacbridge.app.net.UnexpectedlyClosedAttachement;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({LostConnectionMonitor.class, LostConnectionTracker.class})
public class LostConnectionMonitorTest extends BaseTestCase {
    private static final String BRIDGE_NAME = "foo";
    private static final String IP_ADDRESS = "1.2.3.4";
    private static final Integer LOST_CONNECTION_THESHOLD_COUNT = 3; 
    private static final Integer LOST_CONNECTION_THESHOLD_MINUTES = 100;        

    @Mock
    private LostConnectionTracker tracker;
    
    @Test
    public void shouldClearAnyTrackersWhenConnectionConnected() throws Exception {
        Long now = System.currentTimeMillis();
        Cache<String, LostConnectionTracker> theCache = mock(Cache.class);
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        SystemTime systemTime = mock(SystemTime.class);
        SiteAddress siteAddress = new NuidSiteAddress(IP_ADDRESS);
        Notification notification = mock(Notification.class);
        
        SiteConnectedAttachment attachment = new SiteConnectedAttachment(siteAddress, BRIDGE_NAME, IP_ADDRESS, null);
        
        given(theCache.get(siteAddress.getAddress())).willReturn(tracker);
        given(systemTime.getCurrentTime()).willReturn(now);
        given(notification.getName()).willReturn(Connection.CONNECTION_CONNECTED_NOTIFICATION);
        given(notification.getAttachment()).willReturn(attachment);
        
        LostConnectionMonitor monitor = new LostConnectionMonitor(notificationCenter, theCache, systemTime, LOST_CONNECTION_THESHOLD_COUNT, LOST_CONNECTION_THESHOLD_MINUTES);
        verify(notificationCenter).addObserver(monitor, Connection.CONNECTION_CONNECTED_NOTIFICATION);
        monitor.handleNotification(notification);
        verify(theCache).remove(IP_ADDRESS);
    }
    
    @Test
    public void shouldHandleNotification() throws Exception {
        Long now = System.currentTimeMillis();
        Cache<String, LostConnectionTracker> theCache = mock(Cache.class);
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        SystemTime systemTime = mock(SystemTime.class);
        Exception exception = mock(Exception.class);
        SiteAddress siteAddress = new NuidSiteAddress(IP_ADDRESS);
        UnexpectedlyClosedAttachement attachment = mock(UnexpectedlyClosedAttachement.class);
        Notification notification = mock(Notification.class);
        
        given(attachment.getSiteAddress()).willReturn(siteAddress);
        given(attachment.getException()).willReturn(exception);
        given(theCache.get(siteAddress.getAddress())).willReturn(tracker);
        given(systemTime.getCurrentTime()).willReturn(now);
        given(notification.getName()).willReturn(Connection.CONNECTION_LOST_NOTIFICATION);
        given(notification.getAttachment()).willReturn(attachment);
        
        LostConnectionMonitor monitor = new LostConnectionMonitor(notificationCenter, theCache, systemTime, LOST_CONNECTION_THESHOLD_COUNT, LOST_CONNECTION_THESHOLD_MINUTES);
        verify(notificationCenter).addObserver(monitor, Connection.CONNECTION_LOST_NOTIFICATION);
        
        monitor.handleNotification(notification);
        
        verify(tracker).recordLostConnection(exception, now);
        verify(theCache).replace(IP_ADDRESS, tracker);
    }
    
    @Test
    public void shouldRecordLostConnectionWithNonExistingTracker() throws Exception {
        Long now = System.currentTimeMillis();
        Cache<String, LostConnectionTracker> theCache = mock(Cache.class);
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        SystemTime systemTime = mock(SystemTime.class);
        Exception exception = mock(Exception.class);
        SiteAddress siteAddress = new NuidSiteAddress(IP_ADDRESS);
        UnexpectedlyClosedAttachement attachment = mock(UnexpectedlyClosedAttachement.class);
        
        given(attachment.getSiteAddress()).willReturn(siteAddress);
        given(attachment.getException()).willReturn(exception);
        given(theCache.get(siteAddress.getAddress())).willReturn(null);
        given(systemTime.getCurrentTime()).willReturn(now);
        whenNew(LostConnectionTracker.class)
            .withArguments(siteAddress, LOST_CONNECTION_THESHOLD_COUNT, LOST_CONNECTION_THESHOLD_MINUTES)
            .thenReturn(tracker);
        
        LostConnectionMonitor monitor = new LostConnectionMonitor(notificationCenter, theCache, systemTime, LOST_CONNECTION_THESHOLD_COUNT, LOST_CONNECTION_THESHOLD_MINUTES);
        monitor.recordLostConnection(siteAddress, exception);
        
        verify(tracker).recordLostConnection(exception, now);
        verify(theCache).put(IP_ADDRESS, tracker);
        verify(theCache).replace(IP_ADDRESS, tracker);
    }
    
    
    @Test
    public void shouldRecordLostConnectionWithExistingTracker() throws Exception {
        Long now = System.currentTimeMillis();
        Cache<String, LostConnectionTracker> theCache = mock(Cache.class);
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        SystemTime systemTime = mock(SystemTime.class);
        Exception exception = mock(Exception.class);
        SiteAddress siteAddress = new NuidSiteAddress(IP_ADDRESS);
        UnexpectedlyClosedAttachement attachment = mock(UnexpectedlyClosedAttachement.class);
        
        given(attachment.getSiteAddress()).willReturn(siteAddress);
        given(attachment.getException()).willReturn(exception);
        given(theCache.get(siteAddress.getAddress())).willReturn(tracker);
        given(systemTime.getCurrentTime()).willReturn(now);
        
        LostConnectionMonitor monitor = new LostConnectionMonitor(notificationCenter, theCache, systemTime, LOST_CONNECTION_THESHOLD_COUNT, LOST_CONNECTION_THESHOLD_MINUTES);
        monitor.recordLostConnection(siteAddress, exception);
        
        verify(tracker).recordLostConnection(exception, now);
        verify(theCache).replace(IP_ADDRESS, tracker);
    }
    
    @Test
    public void shouldRegisterForNotificationAtConstruction() throws Exception {
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        SystemTime systemTime = mock(SystemTime.class);
        Cache<String, LostConnectionTracker> theCache = mock(Cache.class);
        
        new LostConnectionMonitor(notificationCenter, theCache, systemTime, LOST_CONNECTION_THESHOLD_COUNT, LOST_CONNECTION_THESHOLD_MINUTES);
        
        verify(notificationCenter).addObserver(any(Notifiable.class), eq(Connection.CONNECTION_LOST_NOTIFICATION));
        verify(notificationCenter).addObserver(any(Notifiable.class), eq(Connection.CONNECTION_CONNECTED_NOTIFICATION));
    }
}
