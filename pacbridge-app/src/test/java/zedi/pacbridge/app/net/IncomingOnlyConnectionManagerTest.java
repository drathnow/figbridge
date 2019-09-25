package zedi.pacbridge.app.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.Constants;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IncomingOnlyConnectionManager.class, SiteConnectedAttachment.class, SiteDisconnectedAttachment.class})
public class IncomingOnlyConnectionManagerTest extends BaseTestCase {
    private static final Integer RCV_BYTES = 10;
    private static final Integer TRX_BYTES = 20;
    private static final String FIRMWARE_VERSION = "V1.2.3";
    
    @Mock
    private Map<SiteAddress, Connection> connectionMap; 
    @Mock
    private ConnectionManagerHelper helper;
    @Mock
    private ConnectionGarbageCollector connectionGarbageCollector;
    @Mock
    private NotificationCenter notificationCenter;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void shouldCollectInfoOnConnections() throws Exception {
        SiteAddress addr1 = new NuidSiteAddress("1", 1);
        SiteAddress addr2= new NuidSiteAddress("2", 1);
        Connection con1 = mock(Connection.class);
        Connection con2 = mock(Connection.class);
        ConnectionInfoCollector collector = mock(ConnectionInfoCollector.class);
        Map<SiteAddress, Connection> map = new TreeMap<SiteAddress, Connection>();

        map.put(addr1, con1);
        map.put(addr2, con2);
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(map, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.collectConnectionInfo(collector);
        
        verify(collector).collectInfo(con1);
        verify(collector).collectInfo(con1);
    }
    
    @Test	
    public void shouldCloseAllConnectionWhenNetworkShutdownNotificationRecieved() throws Exception {
        Map<SiteAddress, Connection> map = new TreeMap<SiteAddress, Connection>();
        Notification notification = mock(Notification.class);
        
        Connection connection1 = mock(Connection.class);
        Connection connection2 = mock(Connection.class);
        map.put(new NuidSiteAddress("123", 1), connection1);
        map.put(new NuidSiteAddress("234", 1), connection2);
        
        assertEquals(2, map.size());
        given(notification.getName()).willReturn(NetworkService.NETWORK_SHUTTING_DOWN_NOTIFICATION_NAME);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(map, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.handleNotification(notification);
        
        verify(connection1).close();
        verify(connection2).close();
    }
    
    @Test
    public void shouldRemoveConnectionAndPostNotificationIfConnectionClosesUnexpectedly() throws Exception {
        SocketAddress socketAddress = mock(SocketAddress.class);
        String message = "FOO";
        Exception exception = mock(Exception.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        Connection connection = mock(Connection.class);
        
        given(connection.getSiteAddress()).willReturn(siteAddress);
        
        ArgumentCaptor<UnexpectedlyClosedAttachement> arg = ArgumentCaptor.forClass(UnexpectedlyClosedAttachement.class);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(connectionMap, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.handleConnectionUnexpectedlyClosedEvent(connection, exception, siteAddress, socketAddress, message);
        
        verify(connectionMap).remove(siteAddress);
        verify(connectionGarbageCollector).queueForCleanup(connection);
        verify(notificationCenter).postNotificationAsync(eq(Connection.CONNECTION_LOST_NOTIFICATION), arg.capture());
        
        assertSame(socketAddress, arg.getValue().getSocketAddress());
        assertSame(message, arg.getValue().getMessage());
        assertSame(exception, arg.getValue().getException());
        assertSame(siteAddress, arg.getValue().getSiteAddress());
    }

    @Test
    public void shouldReplaceConnectionMappingWhenIdentityChanges() throws Exception {
        Map<SiteAddress, Connection> myConnectionMap = new TreeMap<>(); 
        InetSocketAddress address = new InetSocketAddress(100);
        SiteAddress ipAddr = new IpSiteAddress("1.2.3.4", 17);
        SiteAddress nuidAddr = new NuidSiteAddress("Foo", 17);
        Connection connection = mock(Connection.class);

        myConnectionMap.put(ipAddr, connection);
        given(connection.getSiteAddress()).willReturn(nuidAddr);
        given(connection.getRemoteAddress()).willReturn(address);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(myConnectionMap, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.connectionIdentityChanged(connection);
        
        assertEquals(1, myConnectionMap.size());
        assertTrue(myConnectionMap.containsKey(nuidAddr));
        assertTrue(myConnectionMap.containsValue(connection));
    }
    
    @Test
    public void shouldRemoveAndCloseOldConnectionThenStartOutgoingConnectionsWhenIdentityChangesAndOldConnectionExists() throws Exception {
    	CallCollisionHandler callCollisionHandler = mock(CallCollisionHandler.class);
        InetSocketAddress address = new InetSocketAddress(100);
        SiteAddress siteAddress = mock(SiteAddress.class);
        Connection connection = mock(Connection.class);
        Connection existingConnection = mock(Connection.class);
        SiteConnectedAttachment attachement = mock(SiteConnectedAttachment.class);
        
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(existingConnection.getSiteAddress()).willReturn(siteAddress);
        given(connectionMap.remove(siteAddress)).willReturn(existingConnection);
        given(connection.getRemoteAddress()).willReturn(address);
        given(connection.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        given(existingConnection.callCollisionHandler()).willReturn(callCollisionHandler);
        
        whenNew(SiteConnectedAttachment.class)
            .withArguments(siteAddress, Constants.BRIDGE_NAME, address.getAddress().getHostAddress(), FIRMWARE_VERSION)
            .thenReturn(attachement);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(connectionMap, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.connectionIdentityChanged(connection);
        
        verify(connectionMap).remove(siteAddress);
        verify(callCollisionHandler).handleCallCollision(connection, existingConnection);
        verify(connectionMap).put(siteAddress, connection);
        verify(helper).queueAnyOutgoingRequestForSite(siteAddress);
        verify(helper).queueAnyOutgoingRequestForSite(siteAddress);
        verify(notificationCenter).postNotificationAsync(Connection.CONNECTION_CONNECTED_NOTIFICATION, attachement);
        verifyNew(SiteConnectedAttachment.class).withArguments(siteAddress, Constants.BRIDGE_NAME, address.getAddress().getHostAddress(), FIRMWARE_VERSION);
    }
    
    @Test
    public void shouldStartOutgoingConnectionsWhenIdentityChanges() throws Exception {
        InetSocketAddress address = new InetSocketAddress(100);
        SiteAddress siteAddress = mock(SiteAddress.class);
        Connection connection = mock(Connection.class);
        SiteConnectedAttachment attachement = mock(SiteConnectedAttachment.class);
        
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(connection.getRemoteAddress()).willReturn(address);
        given(connection.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        whenNew(SiteConnectedAttachment.class)
            .withArguments(siteAddress, Constants.BRIDGE_NAME, address.getAddress().getHostAddress(), FIRMWARE_VERSION)
            .thenReturn(attachement);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(connectionMap, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.connectionIdentityChanged(connection);
        
        verify(connectionMap).put(siteAddress, connection);
        verify(helper).queueAnyOutgoingRequestForSite(siteAddress);
        verify(helper).queueAnyOutgoingRequestForSite(siteAddress);
        verify(notificationCenter).postNotificationAsync(Connection.CONNECTION_CONNECTED_NOTIFICATION, attachement);
        verifyNew(SiteConnectedAttachment.class).withArguments(siteAddress, Constants.BRIDGE_NAME, address.getAddress().getHostAddress(), FIRMWARE_VERSION);
    }
    
    @Test
    public void shouldRemoveConnectionWhenClosed() throws Exception {
        InetSocketAddress address = new InetSocketAddress(100);
        SiteAddress siteAddress = mock(SiteAddress.class);
        Connection connection = mock(Connection.class);
        SiteDisconnectedAttachment attachement = mock(SiteDisconnectedAttachment.class);
        
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(connection.getRemoteAddress()).willReturn(address);
        given(connection.getBytesReceived()).willReturn(RCV_BYTES);
        given(connection.getBytesTransmitted()).willReturn(TRX_BYTES);
        whenNew(SiteDisconnectedAttachment.class)
            .withArguments(siteAddress, Constants.BRIDGE_NAME, address.getAddress().getHostAddress(), RCV_BYTES, TRX_BYTES)
            .thenReturn(attachement);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(connectionMap, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.connectionClosed(connection);
        
        verify(connectionMap).remove(siteAddress);
        verify(connectionGarbageCollector).queueForCleanup(connection);
        verify(notificationCenter).postNotificationAsync(Connection.CONNECTION_CLOSED_NOTIFICATION, attachement);
        verifyNew(SiteDisconnectedAttachment.class).withArguments(siteAddress, Constants.BRIDGE_NAME, address.getAddress().getHostAddress(), RCV_BYTES, TRX_BYTES);
    }
    
    @Test
    public void shouldAddConnection() throws Exception {
        InetSocketAddress address = new InetSocketAddress(100);
        SiteAddress siteAddress = mock(SiteAddress.class);
        Connection connection = mock(Connection.class);

        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(connection.getRemoteAddress()).willReturn(address);
        
        IncomingOnlyConnectionManager connectionManager = new IncomingOnlyConnectionManager(connectionMap, helper, connectionGarbageCollector, notificationCenter, 0);
        connectionManager.manageConnection(connection);
        
        verify(connection).addConnectionListener(connectionManager);
        verify(connectionMap).put(siteAddress, connection);
        verify(helper).queueAnyOutgoingRequestForSite(siteAddress);
        verify(notificationCenter, times(0)).postNotificationAsync(eq(Connection.CONNECTION_CONNECTED_NOTIFICATION), any(SiteConnectedAttachment.class));
    }
}
