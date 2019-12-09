package zedi.pacbridge.net.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.core.ChannelFactory;
import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.concurrent.DetachedTaskRunner;

public class ServerTaskTest extends BaseTestCase {
    @Mock
    private ServerHelper serverHelper;
    @Mock
    private ThreadFactory threadFactory;
    @Mock
    private ChannelFactory channelFactory;
    @Mock
    private RequestQueue<ServerRequest> requestQueue;
    @Mock
    private ServerSocketChannel serverSocketChannel;
   
    
    @Test
    public void shouldExitIfTooManySelectExceptionsHappen() throws Exception {
        doThrow(new IOException()).when(serverHelper).doSelect(anyLong());
        
        ServerTask serverTask = new ServerTask(serverHelper, threadFactory, requestQueue);
        for (int i = 0; i < ServerTask.ERROR_COUNT_THRESHOLD; i++)
            assertFalse(serverTask.shouldExitAfterMainLoop());
        assertTrue(serverTask.shouldExitAfterMainLoop());
    } 
    
    @Test
    public void shouldStart() throws Exception {
        Thread thread = mock(Thread.class);
        
        given(threadFactory.newThread(any(DetachedTaskRunner.class))).willReturn(thread);
        
        ServerTask serverTask = new ServerTask(serverHelper, threadFactory, requestQueue);
        serverTask.start();
        
        verify(threadFactory).newThread(any(DetachedTaskRunner.class));
        assertEquals(ServerTask.TASK_NAME, thread.getName());
        verify(thread).start();
    }
    
    @Test
    public void shouldDoSelect() throws IOException {
        ServerTask serverTask = new ServerTask(serverHelper, threadFactory, requestQueue);
        assertFalse(serverTask.shouldExitAfterMainLoop());
        verify(serverHelper).doSelect(ServerTask.SELECT_TIMEOUT_MILLISECONDS);
    }
}
