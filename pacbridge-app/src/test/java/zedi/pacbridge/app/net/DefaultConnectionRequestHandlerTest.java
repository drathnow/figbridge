package zedi.pacbridge.app.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DefaultConnectionRequestHandler.class)
public class DefaultConnectionRequestHandlerTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 1;
    private static final String REMOTE_ADDRESS = "2.3.4.5";
    private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress(REMOTE_ADDRESS, 6000);
    
    @Mock
    private ConnectionBuilderFactory connectionBuilderFactory;
    @Mock
    private ConnectionManager connectionManager;
    @Mock
    private ThreadLocal<ConnectionBuilder> threadLocal;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void shouldUseExistingConnectionBuilderFactory() throws Exception {
        SocketChannelWrapper socketChannel = mock(SocketChannelWrapper.class);
        Socket socket = mock(Socket.class);
        ConnectionBuilder connectionBuilder = mock(ConnectionBuilder.class);
        Connection connection = mock(Connection.class);
        IpSiteAddress siteAddress = mock(IpSiteAddress.class);
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        ThreadContext astRequester = mock(ThreadContext.class);
        ThreadLocal threadLocal = mock(ThreadLocal.class);

        whenNew(IpSiteAddress.class)
            .withArguments(REMOTE_ADDRESS, NETWORK_NUMBER)
            .thenReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(REMOTE_ADDRESS);
        given(socketChannel.socket()).willReturn(socket);
        given(socket.getRemoteSocketAddress()).willReturn(SOCKET_ADDRESS);
        given(threadLocal.get()).willReturn(connectionBuilder);
        given(connectionBuilderFactory.newConnectionBuilder()).willReturn(connectionBuilder);
        given(connectionBuilder.newConnection(siteAddress, socketChannel, dispatcherKey, astRequester)).willReturn(connection);
        
        DefaultConnectionRequestHandler strategy = new DefaultConnectionRequestHandler(threadLocal, connectionBuilderFactory, NETWORK_NUMBER, connectionManager);
        strategy.handleConnectionRequest(socketChannel, dispatcherKey, astRequester);
        
        verify(threadLocal).get();
        verify(connectionBuilderFactory, never()).newConnectionBuilder();
        verify(threadLocal, never()).set(connectionBuilder);
        verify(socketChannel).socket();
        verify(socket).getRemoteSocketAddress();
        verifyNew(IpSiteAddress.class).withArguments(REMOTE_ADDRESS, NETWORK_NUMBER);
        verify(connectionBuilder).newConnection(siteAddress, socketChannel, dispatcherKey, astRequester);
        verify(connectionManager).manageConnection(connection);
        verify(connection).start();
    }
    
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void shouldConstructNewConnectionBuilderFactoryIfOneDoesNotExistForCurrentThread() throws Exception {
        SocketChannelWrapper socketChannel = mock(SocketChannelWrapper.class);
        Socket socket = mock(Socket.class);
        ConnectionBuilder connectionBuilder = mock(ConnectionBuilder.class);
        Connection connection = mock(Connection.class);
        IpSiteAddress siteAddress = mock(IpSiteAddress.class);
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        ThreadContext astRequester = mock(ThreadContext.class);
        ThreadLocal threadLocal = mock(ThreadLocal.class);

        whenNew(IpSiteAddress.class)
            .withArguments(REMOTE_ADDRESS, NETWORK_NUMBER)
            .thenReturn(siteAddress);
        
        given(siteAddress.getAddress()).willReturn(REMOTE_ADDRESS);
        given(socketChannel.socket()).willReturn(socket);
        given(socket.getRemoteSocketAddress()).willReturn(SOCKET_ADDRESS);
        given(threadLocal.get()).willReturn(null);
        given(connectionBuilderFactory.newConnectionBuilder()).willReturn(connectionBuilder);
        given(connectionBuilder.newConnection(siteAddress, socketChannel, dispatcherKey, astRequester)).willReturn(connection);
        
        DefaultConnectionRequestHandler strategy = new DefaultConnectionRequestHandler(threadLocal, connectionBuilderFactory, NETWORK_NUMBER, connectionManager);
        strategy.handleConnectionRequest(socketChannel, dispatcherKey, astRequester);
        
        verify(threadLocal).get();
        verify(connectionBuilderFactory).newConnectionBuilder();
        verify(threadLocal).set(connectionBuilder);
        verify(socketChannel).socket();
        verify(socket).getRemoteSocketAddress();
        verify(connectionBuilderFactory).newConnectionBuilder();
        verifyNew(IpSiteAddress.class).withArguments(REMOTE_ADDRESS, NETWORK_NUMBER);
        verify(connectionBuilder).newConnection(siteAddress, socketChannel, dispatcherKey, astRequester);
        verify(connectionManager).manageConnection(connection);        
    }
}
