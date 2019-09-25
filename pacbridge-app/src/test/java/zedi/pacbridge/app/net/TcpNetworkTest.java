package zedi.pacbridge.app.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.ListenerRegistrationAgent;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TcpNetwork.class)
public class TcpNetworkTest extends BaseTestCase {
    private static final String REMOTE_ADDRESS = "2.3.4.5";
    private static final String LISTENING_ADDRESS = "1.2.3.4";
    private static final Integer NETWORK_NUMBER = 42; 
    private static final Integer CONNECTION_QUEUE_LIMIT = 500;
    private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress(REMOTE_ADDRESS, 6000);
    
    @Mock
    private ListenerRegistrationAgent registrationAgent;
    @Mock
    private ConnectionManager connectionManager;
    @Mock
    private TcpListenerFactory listenerFactory;
    @Mock
    private PropertyBag propertyBag;
    @Mock
    private OutgoingRequest outgoingRequest;
    @Mock
    private ConnectionRequestHandler connectionRequestStrategy;
    
    
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void shouldCollectInfoFromCollectionManagers() throws Exception {
        ConnectionInfoCollector collector = mock(ConnectionInfoCollector.class);

        TcpNetwork network = new TcpNetwork(NETWORK_NUMBER, LISTENING_ADDRESS, listenerFactory, connectionRequestStrategy, connectionManager, propertyBag);
        network.connectionInfo(collector);
        
        verify(connectionManager).collectConnectionInfo(collector);
    }

    @Test
    public void shouldRegisterListenerWithListeningAddress() throws IOException {
        AcceptHandler acceptHandler = mock(AcceptHandler.class);
        
        given(listenerFactory.getConnectionQueueLimit()).willReturn(CONNECTION_QUEUE_LIMIT);
        given(listenerFactory.getListeningAddress()).willReturn(SOCKET_ADDRESS);
        
        TcpNetwork network = new TcpNetwork(NETWORK_NUMBER, LISTENING_ADDRESS, listenerFactory, connectionRequestStrategy, connectionManager, propertyBag);
        given(listenerFactory.getTcpAcceptHandlerForNetwork(network, null)).willReturn(acceptHandler);
        
        network.start(registrationAgent, null);
        
        verify(registrationAgent).registerListener(SOCKET_ADDRESS, acceptHandler, CONNECTION_QUEUE_LIMIT);
    }
        
    @Test
    public void shouldHandleConnectionRequest() throws Exception {
        SocketChannelWrapper socketChannel = mock(SocketChannelWrapper.class);
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        ThreadContext astRequester = mock(ThreadContext.class);

        TcpNetwork network = new TcpNetwork(NETWORK_NUMBER, LISTENING_ADDRESS, listenerFactory, connectionRequestStrategy, connectionManager, propertyBag);
        network.handleConnectionRequest(socketChannel, dispatcherKey, astRequester);

        verify(connectionRequestStrategy).handleConnectionRequest(socketChannel, dispatcherKey, astRequester);
    }
}
