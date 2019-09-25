package zedi.pacbridge.app.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConnectionRequest.class)
public class ConnectionRequestTest extends BaseTestCase {
    @Mock
    private ConnectionRequestHandler connectionRequestHandler;
    @Mock
    private SocketChannel socketChannel;
    @Mock
    private NotificationCenter notificationCenter;
    
    @Test
    public void shouldHandleRequest() throws Exception {
        long now = System.currentTimeMillis();
        long then = now + 1000L;
        Long diffTime = then - now;
        SystemTime systemTime = mock(SystemTime.class);
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        ThreadContext astRequester = mock(ThreadContext.class);
        SocketChannelWrapper channelWrapper = mock(SocketChannelWrapper.class);

        given(systemTime.getCurrentTime())
            .willReturn(now)
            .willReturn(then);
        whenNew(SocketChannelWrapper.class)
            .withArguments(socketChannel)
            .thenReturn(channelWrapper);
        
        ConnectionRequest connectionRequest = new ConnectionRequest(connectionRequestHandler, socketChannel, notificationCenter, systemTime);
        connectionRequest.handleRequest(dispatcherKey, astRequester);
        
        verifyNew(SocketChannelWrapper.class).withArguments(socketChannel);
        verify(notificationCenter).postNotificationAsync(ConnectionRequest.CONNECTION_REQUEST_COMPLETED_NOTIFICATION, diffTime);
        verify(dispatcherKey).registerChannel(channelWrapper);
        verify(connectionRequestHandler).handleConnectionRequest(channelWrapper, dispatcherKey, astRequester);
    }
   
}
