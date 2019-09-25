package zedi.pacbridge.app.controls;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.text.MessageFormat;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.app.net.OutgoingRequestSessionListener;
import zedi.pacbridge.app.net.SiteConnector;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public class OutgoingRequestManagerDelegateTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 42;
    private static final Integer MAX_SESSION_COUNT = 5;

    private SiteAddress siteAddress = new NuidSiteAddress("foo", NETWORK_NUMBER); 

    @Mock
    private OutgoingRequestManager outgoingRequestManager;
    @Mock
    private SessionsManager sessionsManager;
    @Mock
    private OutgoingRequestQueue outgoingRequestQueue;
    @Mock
    private ControlRequestProgressListener requestProgressListener;
    @Mock
    private NetworkService networkService;

    @Test
    public void whenRemovingASessionShouldStartNextQueuedRequest() throws Exception {
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestSession outgoingRequestSession = mock(OutgoingRequestSession.class);
        Connection connection = mock(Connection.class);
        SiteConnector connector = mock(SiteConnector.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(networkService.isValidNetworkNumber(NETWORK_NUMBER)).willReturn(true);
        given(networkService.maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER)).willReturn(MAX_SESSION_COUNT);
        given(sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER)).willReturn(MAX_SESSION_COUNT-1);
        given(networkService.siteConnectorForNetworkNumber(NETWORK_NUMBER)).willReturn(connector);
        given(connector.connectionForSiteAddress(siteAddress)).willReturn(connection);
        given(connection.outgoingRequestSessionForOutgoingRequest(outgoingRequest)).willReturn(outgoingRequestSession);
        given(connection.getMaxSessionLimit()).willReturn(MAX_SESSION_COUNT);
        
        InOrder order = inOrder(outgoingRequest, networkService, sessionsManager, networkService, connection, connector, outgoingRequestSession, outgoingRequestQueue);
        ArgumentCaptor<OutgoingRequestSessionListener> arg = ArgumentCaptor.forClass(OutgoingRequestSessionListener.class);
        
        OutgoingRequestManagerDelegate delegate = new OutgoingRequestManagerDelegate(outgoingRequestManager, sessionsManager, networkService, requestProgressListener);
        
        delegate.startOutgoingRequest(outgoingRequest);

        order.verify(outgoingRequest).getSiteAddress();
        order.verify(networkService).isValidNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER);
        order.verify(sessionsManager).numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).siteConnectorForNetworkNumber(NETWORK_NUMBER);
        order.verify(connector).connectionForSiteAddress(siteAddress);
        order.verify(connection, times(2)).getMaxSessionLimit();
        order.verify(sessionsManager).numberOfSessionForSiteAddress(siteAddress);
        order.verify(connection).outgoingRequestSessionForOutgoingRequest(outgoingRequest);
        order.verify(outgoingRequestSession).setOutgoingRequestSessionListener(arg.capture());
        order.verify(sessionsManager).addOutgoingRequestSession(outgoingRequestSession);
        order.verify(outgoingRequestSession).start();
        
        verifyNoMoreInteractions(outgoingRequest, networkService, sessionsManager, networkService, connection, connector, requestProgressListener, outgoingRequestQueue);
        
        arg.getValue().sessionClosed(outgoingRequestSession);        
    }
    
    @Test
    public void whenStartingOutoingRequestShouldStartRequestIfMaxSessionIsZerio() throws Exception {
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestSession outgoingRequestSession = mock(OutgoingRequestSession.class);
        Connection connection = mock(Connection.class);
        SiteConnector connector = mock(SiteConnector.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(networkService.isValidNetworkNumber(NETWORK_NUMBER)).willReturn(true);
        given(networkService.maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER)).willReturn(0);
        given(sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER)).willReturn(0);
        given(networkService.siteConnectorForNetworkNumber(NETWORK_NUMBER)).willReturn(connector);
        given(connector.connectionForSiteAddress(siteAddress)).willReturn(connection);
        given(connection.outgoingRequestSessionForOutgoingRequest(outgoingRequest)).willReturn(outgoingRequestSession);
        given(connection.getMaxSessionLimit()).willReturn(MAX_SESSION_COUNT);
        
        InOrder order = inOrder(outgoingRequest, networkService, sessionsManager, connection, connector, outgoingRequestSession, outgoingRequestQueue);
        ArgumentCaptor<OutgoingRequestSessionListener> arg = ArgumentCaptor.forClass(OutgoingRequestSessionListener.class);
        
        OutgoingRequestManagerDelegate delegate = new OutgoingRequestManagerDelegate(outgoingRequestManager, sessionsManager, networkService, requestProgressListener);
        
        delegate.startOutgoingRequest(outgoingRequest);

        order.verify(outgoingRequest).getSiteAddress();
        order.verify(networkService).isValidNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER);
        order.verify(sessionsManager, never()).numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).siteConnectorForNetworkNumber(NETWORK_NUMBER);
        order.verify(connector).connectionForSiteAddress(siteAddress);
        order.verify(connection, times(2)).getMaxSessionLimit();
        order.verify(sessionsManager).numberOfSessionForSiteAddress(siteAddress);
        order.verify(connection).outgoingRequestSessionForOutgoingRequest(outgoingRequest);
        order.verify(outgoingRequestSession).setOutgoingRequestSessionListener(arg.capture());
        order.verify(sessionsManager).addOutgoingRequestSession(outgoingRequestSession);
        order.verify(outgoingRequestSession).start();
        
        verifyNoMoreInteractions(outgoingRequest, networkService, sessionsManager, networkService, connection, connector, requestProgressListener, outgoingRequestQueue);
        
        arg.getValue().sessionClosed(outgoingRequestSession);
    }
    
    @Test
    public void whenStratingOutoingRequestShouldRemoveOutgoingRequestFromCacheIfNetworkNumberIsInvalid() throws Exception {
        String expectMmessage = MessageFormat.format(OutgoingRequestManagerDelegate.NETWORK_NUMBER_ERROR, siteAddress.getNetworkNumber());
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        SiteConnector connector = mock(SiteConnector.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(networkService.isValidNetworkNumber(NETWORK_NUMBER)).willReturn(false);
        
        InOrder order = inOrder(outgoingRequest, networkService, sessionsManager, networkService, connector, requestProgressListener, outgoingRequestQueue);
        
        OutgoingRequestManagerDelegate delegate = new OutgoingRequestManagerDelegate(null, sessionsManager, networkService, requestProgressListener);
        
        delegate.startOutgoingRequest(outgoingRequest);

        order.verify(outgoingRequest).getSiteAddress();
        order.verify(networkService).isValidNetworkNumber(NETWORK_NUMBER);
        order.verify(requestProgressListener).requestProcessingAborted(outgoingRequest, ControlStatus.FAILURE, expectMmessage, null);
        
        verifyNoMoreInteractions(outgoingRequest, networkService, sessionsManager, networkService, connector, requestProgressListener, outgoingRequestQueue);
    }
    
    @Test
    public void whenStratingOutoingRequestShouldDoNothingWithRequestIfDeviceNotConnected() throws Exception {
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        SiteConnector connector = mock(SiteConnector.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(networkService.isValidNetworkNumber(NETWORK_NUMBER)).willReturn(true);
        given(networkService.maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER)).willReturn(MAX_SESSION_COUNT);
        given(sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER)).willReturn(MAX_SESSION_COUNT-1);
        given(networkService.siteConnectorForNetworkNumber(NETWORK_NUMBER)).willReturn(connector);
        given(connector.connectionForSiteAddress(siteAddress)).willReturn(null);
        
        InOrder order = inOrder(outgoingRequest, networkService, sessionsManager, networkService, connector, outgoingRequestQueue);
        
        OutgoingRequestManagerDelegate delegate = new OutgoingRequestManagerDelegate(null, sessionsManager, networkService, requestProgressListener);
        
        delegate.startOutgoingRequest(outgoingRequest);

        order.verify(outgoingRequest).getSiteAddress();
        order.verify(networkService).isValidNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER);
        order.verify(sessionsManager).numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).siteConnectorForNetworkNumber(NETWORK_NUMBER);
        order.verify(connector).connectionForSiteAddress(siteAddress);
        
        verifyNoMoreInteractions(outgoingRequest, networkService, sessionsManager, networkService, connector, requestProgressListener, outgoingRequestQueue);
    }
    
    @Test
    public void whenStratingOutoingRequestShouldStartOutgoingRequestIfDeviceConnected() throws Exception {
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestSession outgoingRequestSession = mock(OutgoingRequestSession.class);
        Connection connection = mock(Connection.class);
        SiteConnector connector = mock(SiteConnector.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(networkService.isValidNetworkNumber(NETWORK_NUMBER)).willReturn(true);
        given(networkService.maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER)).willReturn(MAX_SESSION_COUNT);
        given(sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER)).willReturn(MAX_SESSION_COUNT-1);
        given(networkService.siteConnectorForNetworkNumber(NETWORK_NUMBER)).willReturn(connector);
        given(connector.connectionForSiteAddress(siteAddress)).willReturn(connection);
        given(connection.outgoingRequestSessionForOutgoingRequest(outgoingRequest)).willReturn(outgoingRequestSession);
        given(connection.getMaxSessionLimit()).willReturn(MAX_SESSION_COUNT);
        
        InOrder order = inOrder(outgoingRequest, networkService, sessionsManager, networkService, connection, connector, outgoingRequestSession, outgoingRequestQueue);
        ArgumentCaptor<OutgoingRequestSessionListener> arg = ArgumentCaptor.forClass(OutgoingRequestSessionListener.class);
        
        OutgoingRequestManagerDelegate delegate = new OutgoingRequestManagerDelegate(outgoingRequestManager, sessionsManager, networkService, requestProgressListener);
        
        delegate.startOutgoingRequest(outgoingRequest);

        order.verify(outgoingRequest).getSiteAddress();
        order.verify(networkService).isValidNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER);
        order.verify(sessionsManager).numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER);
        order.verify(networkService).siteConnectorForNetworkNumber(NETWORK_NUMBER);
        order.verify(connector).connectionForSiteAddress(siteAddress);
        order.verify(connection, times(2)).getMaxSessionLimit();
        order.verify(sessionsManager).numberOfSessionForSiteAddress(siteAddress);
        order.verify(connection).outgoingRequestSessionForOutgoingRequest(outgoingRequest);
        order.verify(outgoingRequestSession).setOutgoingRequestSessionListener(arg.capture());
        order.verify(sessionsManager).addOutgoingRequestSession(outgoingRequestSession);
        order.verify(outgoingRequestSession).start();
        
        verifyNoMoreInteractions(outgoingRequest, networkService, sessionsManager, networkService, connection, connector, requestProgressListener, outgoingRequestQueue);
        
        arg.getValue().sessionClosed(outgoingRequestSession);
    }
    
}