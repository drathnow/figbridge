package zedi.pacbridge.net.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.ListenerStatus;
import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.ChannelFactory;
import zedi.pacbridge.test.BaseTestCase;


public class ServerHelperTest extends BaseTestCase {
    private static final int QUEUE_LIMIT = 122;
    private static final Long TIMEOUT = 10L;
    private static final InetSocketAddress socketAddress = new InetSocketAddress("localhost", 23459);
    private ServerSocketChannel serverSocketChannel;
    
    @Mock
    private Selector selector;
    @Mock
    private ChannelFactory channelFactory;
    @Mock
    private ServerSocket socket;
    @Mock
    private AcceptHandler acceptHandler;
    @Mock
    private ServerSocketChannel mockServerSocketChannel;
    @Mock
    private Selector mockSelector;
    @Mock
    private SocketChannel socketChannel;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();        
    }
    
    @Override
    public void tearDown() throws Exception {
        serverSocketChannel.close();
        selector.close();
        super.tearDown();
    }
    
    @Test
    public void shouldHandleExceptionDuringAccept() throws Exception {
        SelectionKey key1 = mock(SelectionKey.class);

        Set<SelectionKey> keys = new HashSet<SelectionKey>(Arrays.asList(new SelectionKey[]{key1}));

        given(mockSelector.selectedKeys()).willReturn(keys);
        doThrow(new IOException()).when(mockServerSocketChannel).accept();
        given(key1.channel()).willReturn(mockServerSocketChannel);
        given(mockServerSocketChannel.socket()).willReturn(socket);
        given(socket.getLocalSocketAddress()).willReturn(socketAddress);
        
        key1.attach(acceptHandler);
        
        ServerHelper server = new ServerHelper(mockSelector, channelFactory);
        server.doSelect(TIMEOUT);
        
        verify(mockSelector).select(TIMEOUT);
        verify(mockSelector).selectedKeys();
        verify(key1, times(2)).channel();
        verify(mockServerSocketChannel).socket();
        verify(socket).getLocalSocketAddress();
        verify(mockServerSocketChannel).accept();
    }
    
    @Test
    public void shouldDoSelect() throws Exception {
        SocketAddress socketAddress = mock(SocketAddress.class);
        SelectionKey key1 = mock(SelectionKey.class);
        SelectionKey key2 = mock(SelectionKey.class);

        Set<SelectionKey> keys = new HashSet<SelectionKey>(Arrays.asList(new SelectionKey[]{key1, key2}));

        given(mockSelector.selectedKeys()).willReturn(keys);
        given(mockServerSocketChannel.accept()).willReturn(socketChannel);
        given(socketChannel.getRemoteAddress()).willReturn(socketAddress);
        given(socketAddress.toString()).willReturn("1.2.3.4");
        given(key1.channel()).willReturn(mockServerSocketChannel);
        given(key2.channel()).willReturn(mockServerSocketChannel);
        given(key1.readyOps()).willReturn(SelectionKey.OP_ACCEPT);
        given(key2.readyOps()).willReturn(SelectionKey.OP_ACCEPT);
        given(key1.isValid()).willReturn(true);
        given(key2.isValid()).willReturn(true);
        
        key1.attach(acceptHandler);
        key2.attach(acceptHandler);
        
        ServerHelper server = new ServerHelper(mockSelector, channelFactory);
        server.doSelect(TIMEOUT);
        
        verify(mockSelector).select(TIMEOUT);
        verify(mockServerSocketChannel, times(2)).accept();
        verify(acceptHandler, times(2)).handleAcceptForSocketChannel(socketChannel);
        verify(key1).channel();
        verify(key2).channel();
        verify(key1).readyOps();
        verify(key2).readyOps();
        verify(key1).isValid();
        verify(key2).isValid();

    }
    
    @Test
    public void shouldStopListening() throws Exception {
        SelectionKey key1 = mock(SelectionKey.class);
        SelectionKey key2 = mock(SelectionKey.class);

        Set<SelectionKey> keys = new HashSet<SelectionKey>(Arrays.asList(new SelectionKey[]{key1, key2}));
        
        given(mockSelector.keys()).willReturn(keys);
        given(key1.channel()).willReturn(mockServerSocketChannel);
        given(key2.channel()).willReturn(mockServerSocketChannel);
        given(mockServerSocketChannel.socket()).willReturn(socket);
        given(socket.getLocalSocketAddress()).willReturn(socketAddress);
        
        ServerHelper server = new ServerHelper(mockSelector, channelFactory);
        
        server.stopListening();
        
        verify(key1).interestOps(0);
        verify(key2).interestOps(0);
    }
    
    @Test
    public void shouldStartListening() throws Exception {
        AcceptHandler acceptHandler = mock(AcceptHandler.class);
        when(channelFactory.newServerSocketChannel()).thenReturn(serverSocketChannel);
        ServerHelper server = new ServerHelper(selector, channelFactory);
        ListenerStatus listenerStatus = server.registerListener(socketAddress, acceptHandler, QUEUE_LIMIT);
        assertFalse(listenerStatus.isListening());
        assertEquals(0, selector.keys().iterator().next().interestOps());
        server.startListening();
        assertEquals(SelectionKey.OP_ACCEPT, selector.keys().iterator().next().interestOps());
        assertTrue(listenerStatus.isListening());
    }

    @Test
    public void shouldRegisterSocketForAcceptWhenListening() throws Exception {
        AcceptHandler acceptHandler = mock(AcceptHandler.class);

        when(channelFactory.newServerSocketChannel()).thenReturn(serverSocketChannel);

        ServerHelper server = new ServerHelper(selector, channelFactory);
        server.startListening();
        server.registerListener(socketAddress, acceptHandler, QUEUE_LIMIT);

        assertFalse(serverSocketChannel.isBlocking());
        Set<SelectionKey> keys = selector.keys();
        assertEquals(1, keys.size());
        assertEquals(SelectionKey.OP_ACCEPT, keys.iterator().next().interestOps());
        assertEquals(socketAddress, serverSocketChannel.socket().getLocalSocketAddress());
    }
    
    @Test
    public void shouldJustRegisterSocketWhenNotListening() throws Exception {
        AcceptHandler acceptHandler = mock(AcceptHandler.class);

        when(channelFactory.newServerSocketChannel()).thenReturn(serverSocketChannel);

        ServerHelper server = new ServerHelper(selector, channelFactory);

        server.registerListener(socketAddress, acceptHandler, QUEUE_LIMIT);

        assertFalse(serverSocketChannel.isBlocking());
        Set<SelectionKey> keys = selector.keys();
        assertEquals(1, keys.size());
        SelectionKey key = keys.iterator().next();
        assertEquals(0, key.interestOps());
        assertSame(acceptHandler, key.attachment());
        assertEquals(socketAddress, serverSocketChannel.socket().getLocalSocketAddress());
    }    
}
