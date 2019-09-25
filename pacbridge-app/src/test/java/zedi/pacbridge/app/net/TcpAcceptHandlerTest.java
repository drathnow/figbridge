package zedi.pacbridge.app.net;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;

public class TcpAcceptHandlerTest extends BaseTestCase {

    @Mock
    private NetworkEventDispatcherManager dispatcherManager;
    @Mock
    private SocketChannel socketChannel;
    @Mock
    private Network network;
    @Mock
    private ConnectionRequestFactory connectionRequestFactory;
    @Mock
    private NotificationCenter notificationCenter;
    @Mock
    private ConnectionRequest connectionRequest;
    
    @Test
    public void shouldCreateConnectionRequestAndPassToNetworkDispatcherManager() {
    	
    	given(connectionRequestFactory.newConnectionRequest(network, socketChannel, notificationCenter)).willReturn(connectionRequest);
    	
        TcpAcceptHandler handler = new TcpAcceptHandler(network, notificationCenter, connectionRequestFactory, dispatcherManager);
        handler.handleAcceptForSocketChannel(socketChannel);
        
        verify(connectionRequestFactory).newConnectionRequest(eq(network), eq(socketChannel), eq(notificationCenter));
        verify(dispatcherManager).queueDispatcherRequest(any(ConnectionRequest.class));
    }

}
