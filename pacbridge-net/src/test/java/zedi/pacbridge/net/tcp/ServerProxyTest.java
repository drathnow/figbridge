package zedi.pacbridge.net.tcp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;

import org.junit.Test;

import zedi.pacbridge.net.core.AcceptHandler;


public class ServerProxyTest {

    private final static InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("1.2.3.4", 100);
    private final static int CONNECTION_QUEUE_LIMIT = 2;
    
    @Test
    public void shouldQueueShutdownRequest() throws Exception {
        TcpServerRequestFactory factory = mock(TcpServerRequestFactory.class);
        ShutdownServerRequest request = mock(ShutdownServerRequest.class);
        
        when(factory.newShutdownServerRequest()).thenReturn(request);
        
        ServerProxy proxy = new ServerProxy(factory);
        
        proxy.shutdown();
        
        verify(request).shutdown();
    }
    
    @Test
    public void shouldQueueStartListeningRequest() throws Exception {
        TcpServerRequestFactory factory = mock(TcpServerRequestFactory.class);
        StartListeningServerRequest request = mock(StartListeningServerRequest.class);
        
        when(factory.newStartListeningRequest()).thenReturn(request);
        
        ServerProxy proxy = new ServerProxy(factory);
        
        proxy.startListening();
        
        verify(request).startListening();
    }
    
    @Test
    public void shouldQueueStopListeningRequest() throws Exception {
        TcpServerRequestFactory factory = mock(TcpServerRequestFactory.class);
        StopListeningServerRequest request = mock(StopListeningServerRequest.class);
        
        when(factory.newStopListeningRequest()).thenReturn(request);
        
        ServerProxy proxy = new ServerProxy(factory);
        
        proxy.stopListening();
        
        verify(request).stopListening();
    }
    
    @Test
    public void shouldQueueRegisterListerRequest() throws Exception {
        TcpServerRequestFactory factory = mock(TcpServerRequestFactory.class);
        AcceptHandler acceptHandler = mock(AcceptHandler.class);
        RegisterListenerRequest request = mock(RegisterListenerRequest.class);
        
        when(factory.newRegisterListenerRequest()).thenReturn(request);
        
        ServerProxy proxy = new ServerProxy(factory);
        
        proxy.registerListener(SOCKET_ADDRESS, acceptHandler, CONNECTION_QUEUE_LIMIT);
        
        verify(request).registerListener(SOCKET_ADDRESS, acceptHandler, CONNECTION_QUEUE_LIMIT);
    }
}
