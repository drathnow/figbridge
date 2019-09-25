package zedi.pacbridge.net.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.net.ConnectEventHandler;
import zedi.pacbridge.net.ReadEventHandler;
import zedi.pacbridge.net.WriteEventHandler;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.test.mocknet.MockSelectionKey;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.ThreadContext;

public class NetworkEventDispatcherTest extends BaseTestCase {
    private final static int OPERATIONS_MASK = 0xFC;
    
    @Mock
    private ChannelHelperFactory channelHelperFactory;
    @Mock
    private RequestQueue<DispatcherRequest> dispatcherRequestQueue;
    @Mock
    private ContextCommandQueue contextCommandQueue;
    @Mock
    private EventWorkerThreadPool eventWorkerThreadPool;
    @Mock
    private SystemTime systemTime;
    @Mock
    private LoggingContext loggingContext;
    
    @Test
    public void shouldNotSpendTooMuchTimeProcessingContextRequests() throws Exception {
        Selector selector = mock(Selector.class);
        ContextCommandQueue myContextCommandQueue = new ContextCommandQueue();

        ContextCommand tooLongCommand = new ContextCommand() {
                @Override
                public void execute() {
                    try {
                        Thread.sleep(NetworkEventDispatcher.DEFAULT_CONTEXT_COMMAND_PROCTIME_MILLIS + 10);
                    } catch (InterruptedException e) {
                    }
                }
            };
            
        ContextCommand otherCommand = mock(ContextCommand.class);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, myContextCommandQueue);
        dispatcher.queueContextCommand(tooLongCommand);
        dispatcher.shouldExitAfterMainLoop();
        
        verify(otherCommand, never()).execute();
    }
    
    @Test
    public void shouldShutdownWhenShutdownDelaySecondsExpires() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        ChannelHelper channelManager = mock(ChannelHelper.class);
        Selector selector = mock(Selector.class);
        SelectionKey key = mock(SelectionKey.class);
        
        Set<SelectionKey> keys = new HashSet<SelectionKey>();
        keys.add(key);
        
        long now = System.currentTimeMillis();
        when(systemTime.getCurrentTime())
            .thenReturn(now)
            .thenReturn(now)
            .thenReturn(now+(NetworkEventDispatcher.DEFAULT_SHUTDOWN_DELAY_SECONDS*1000)+1);
        
        when(selector.select(100)).thenReturn(0);
        when(selector.keys())
            .thenReturn(keys)
            .thenReturn(keys);
        when(channelHelperFactory.newChannelHelperWithSelector(selector)).thenReturn(channelManager);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        dispatcher.setSystemTime(systemTime);
        
        dispatcher.shutdown();
        
        assertFalse(dispatcher.shouldExitAfterMainLoop());
        
        assertTrue(dispatcher.shouldExitAfterMainLoop());
        
        verify(dispatcherRequestQueue, never()).nextRequest();
    }
    
    @Test
    public void shouldProcessContextCommandRequests() throws Exception {
        Selector selector = mock(Selector.class);
        ContextCommand command1 = mock(ContextCommand.class);
        ContextCommand command2 = mock(ContextCommand.class);
        
        given(contextCommandQueue.isEmpty())
            .willReturn(false)
            .willReturn(false)
            .willReturn(true);
        given(contextCommandQueue.nextDueCommand())
            .willReturn(command1)
            .willReturn(command2)
            .willReturn(null);
        
        InOrder inOrder = inOrder(command1, command2);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);

        dispatcher.queueContextCommand(command1);
        dispatcher.queueContextCommand(command2);
        dispatcher.shouldExitAfterMainLoop();
        
        verify(contextCommandQueue).queueCommand(command1);
        inOrder.verify(command1).execute();

        dispatcher.shouldExitAfterMainLoop();
        verify(contextCommandQueue).queueCommand(command2);
        inOrder.verify(command2).execute();
    }
    
    @Test
    public void shouldCleanUpSelector() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        SelectableChannel channel = SocketChannel.open();
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        Selector selector = mock(Selector.class);
        MockSelectionKey selectionKey = new MockSelectionKey();

        Set<SelectionKey> keys = new HashSet<SelectionKey>();
        keys.add(selectionKey);
     
        when(selector.keys()).thenReturn(keys);
        when(selector.isOpen()).thenReturn(true);
        selectionKey.attach(dispatcherKey);
        selectionKey.channel = channel;
        selectionKey.interestOps = OPERATIONS_MASK;

        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        
        dispatcher.executionTerminating();
        
        assertTrue(selectionKey.wasCancelled);
        verify(selector).selectNow();
        verify(selector).close();
    }
    
    @Test
    public void shouldReassignChannelsAndRequestsWhenShutdown() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        SelectableChannel channel = SocketChannel.open();
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        Selector selector = mock(Selector.class);
        MockSelectionKey selectionKey = new MockSelectionKey();

        Set<SelectionKey> keys = new HashSet<SelectionKey>();
        keys.add(selectionKey);
     
        when(selector.keys()).thenReturn(keys);
        when(selector.isOpen()).thenReturn(true);
        selectionKey.attach(dispatcherKey);
        selectionKey.channel = channel;
        selectionKey.interestOps = OPERATIONS_MASK;

        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        
        dispatcher.executionTerminating();
        
        assertTrue(selectionKey.wasCancelled);
        verify(selector).selectNow();
        verify(selector).close();
    }
    
    @Test
    public void shouldShutdownNicely() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        ChannelHelper channelManager = mock(ChannelHelper.class);
        Selector selector = mock(Selector.class);
        SelectionKey key = mock(SelectionKey.class);
        
        Set<SelectionKey> keys = new HashSet<SelectionKey>();
        keys.add(key);
        
        when(selector.select(100)).thenReturn(0);
        when(selector.keys())
            .thenReturn(keys)
            .thenReturn(Collections.<SelectionKey>emptySet());
        when(channelHelperFactory.newChannelHelperWithSelector(selector)).thenReturn(channelManager);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        
        dispatcher.shutdown();
        
        assertFalse(dispatcher.shouldExitAfterMainLoop());
        assertTrue(dispatcher.shouldExitAfterMainLoop());
        
        verify(dispatcherRequestQueue, never()).nextRequest();
    }
    
    @Test
    public void shouldShouldHandleNewChannelManagerRequests() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        Selector selector = mock(Selector.class);
        ChannelHelper channelHelper = mock(ChannelHelper.class);
        DispatcherRequest request = mock(DispatcherRequest.class);
        
        when(selector.select(100)).thenReturn(0);
        when(channelHelperFactory.newChannelHelperWithSelector(selector)).thenReturn(channelHelper);
        when(dispatcherRequestQueue.nextRequest())
            .thenReturn(request)
            .thenReturn(null);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        assertFalse(dispatcher.shouldExitAfterMainLoop());
        
        verify(request).handleRequest(any(DispatcherKey.class), any(ThreadContext.class));
    }
    
    @Test
    public void shouldNotCheckForChannelManagerRequestsIfRefuseNewChannelRequestsTrue() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        ChannelHelper channelManager = mock(ChannelHelper.class);
        Selector selector = mock(Selector.class);
        when(selector.select(100)).thenReturn(0);
        when(channelHelperFactory.newChannelHelperWithSelector(selector)).thenReturn(channelManager);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        dispatcher.shutdown();
        assertTrue(dispatcher.shouldExitAfterMainLoop());
        
        verify(dispatcherRequestQueue, never()).nextRequest();
    }
    
    @Test
    public void shouldHandleEventForConnectOperation() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        Selector selector = mock(Selector.class);
        ConnectEventHandler eventHandler = mock(ConnectEventHandler.class);
        SelectionKey selectionKey = mock(SelectionKey.class);
        
        Set<SelectionKey> selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKey);

        selectionKey.attach(eventHandler);
        
        when(eventHandler.loggingContext()).thenReturn(loggingContext);
        when(selector.select(100)).thenReturn(1);
        when(selector.selectedKeys()).thenReturn(selectedKeys);
        when(selectionKey.readyOps())
            .thenReturn(SelectionKey.OP_CONNECT)
            .thenReturn(SelectionKey.OP_CONNECT)
            .thenReturn(SelectionKey.OP_CONNECT)
            .thenReturn(SelectionKey.OP_CONNECT);
        when(selectionKey.interestOps())
            .thenReturn(SelectionKey.OP_CONNECT);
        when(selectionKey.isValid()).thenReturn(true);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        assertFalse(dispatcher.shouldExitAfterMainLoop());

        verify(selector).select(100);
        verify(selector).selectedKeys();
        verify(selectionKey, times(4)).isValid();
        verify(selector).selectedKeys();
        verify(loggingContext).setupContext();
        verify(eventHandler).handleConnect();
        verify(loggingContext).clearContext();
    }
    
    @Test
    public void shouldHandleEventForWriteOperation() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        Selector selector = mock(Selector.class);
        WriteEventHandler eventHandler = mock(WriteEventHandler.class);
        SelectionKey selectionKey = mock(SelectionKey.class);
        
        Set<SelectionKey> selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKey);

        selectionKey.attach(eventHandler);

        when(eventHandler.loggingContext()).thenReturn(loggingContext);
        when(selector.select(100)).thenReturn(1);
        when(selector.selectedKeys()).thenReturn(selectedKeys);
        when(selectionKey.readyOps())
            .thenReturn(SelectionKey.OP_WRITE)
            .thenReturn(SelectionKey.OP_WRITE)
            .thenReturn(SelectionKey.OP_WRITE)
            .thenReturn(SelectionKey.OP_WRITE);
        when(selectionKey.interestOps())
            .thenReturn(SelectionKey.OP_WRITE);
        when(selectionKey.isValid()).thenReturn(true);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        assertFalse(dispatcher.shouldExitAfterMainLoop());

        verify(selector).select(100);
        verify(selector).selectedKeys();
        verify(selectionKey, times(4)).isValid();
        verify(selector).selectedKeys();
        verify(loggingContext).setupContext();
        verify(eventHandler).handleWrite();
        verify(loggingContext).clearContext();
    }
    
    @Test
    public void shouldHandleEventForReadOperation() throws Exception {
        given(contextCommandQueue.isEmpty()).willReturn(true);
        Selector selector = mock(Selector.class);
        ReadEventHandler eventHandler = mock(ReadEventHandler.class);
        SelectionKey selectionKey = mock(SelectionKey.class);
        
        Set<SelectionKey> selectedKeys = new HashSet<SelectionKey>();
        selectedKeys.add(selectionKey);

        selectionKey.attach(eventHandler);
        
        when(eventHandler.loggingContext()).thenReturn(loggingContext);
        when(selector.select(100)).thenReturn(1);
        when(selector.selectedKeys()).thenReturn(selectedKeys);
        when(selectionKey.readyOps())
            .thenReturn(SelectionKey.OP_READ)
            .thenReturn(SelectionKey.OP_READ)
            .thenReturn(SelectionKey.OP_READ)
            .thenReturn(SelectionKey.OP_READ);
        when(selectionKey.interestOps())
            .thenReturn(SelectionKey.OP_READ);
        when(selectionKey.isValid()).thenReturn(true);
        
        NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(selector, channelHelperFactory, dispatcherRequestQueue, contextCommandQueue);
        
        assertFalse(dispatcher.shouldExitAfterMainLoop());

        verify(selector).select(100);
        verify(selector).selectedKeys();
        verify(selectionKey, times(4)).isValid();
        verify(selector).selectedKeys();
        verify(loggingContext).setupContext();
        verify(eventHandler).handleRead();
        verify(loggingContext).clearContext();
    }
}
