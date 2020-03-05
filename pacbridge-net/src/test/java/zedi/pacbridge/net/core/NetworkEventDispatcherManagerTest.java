package zedi.pacbridge.net.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class NetworkEventDispatcherManagerTest extends BaseTestCase {

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRestartDispatcher() throws Exception {
        EventWorkerThreadPool workerThreadPool = mock(EventWorkerThreadPool.class);
        NetworkEventDispatcher dispatcher1 = mock(NetworkEventDispatcher.class);
        NetworkEventDispatcher dispatcher2 = mock(NetworkEventDispatcher.class);
        NetworkEventDispatcher dispatcher3 = mock(NetworkEventDispatcher.class);
        NetworkEventDispatcher dispatcher4 = mock(NetworkEventDispatcher.class);

        NetworkEventDispatcherFactory factory = mock(NetworkEventDispatcherFactory.class);
        DispatcherThreadPool threadPool = mock(DispatcherThreadPool.class);
        
        when(factory.newNetworkEventDispatcher(any(RequestQueue.class), eq(workerThreadPool)))
            .thenReturn(dispatcher1)
            .thenReturn(dispatcher2)
            .thenReturn(dispatcher3)
            .thenReturn(dispatcher4);
        
        when(threadPool.getMaximumPoolSize())
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE);
        
        when(threadPool.getCorePoolSize())
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE);
        
        when(threadPool.getPoolSize())
            .thenReturn(0)
            .thenReturn(1)
            .thenReturn(2)
            .thenReturn(3)
            .thenReturn(3);
        
        NetworkEventDispatcherManager manager = new NetworkEventDispatcherManager(threadPool, factory, workerThreadPool);
        for (int i = 0; i < DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE; i++)
            manager.startNewDispatcher();
        verify(factory, times(4)).newNetworkEventDispatcher(any(RequestQueue.class), eq(workerThreadPool));
        verify(threadPool, times(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)).startDispatcher(any(NetworkEventDispatcher.class));
        assertEquals(0, manager.getAddedDispatcherCount());
        
        manager.dispatcherTerminated(dispatcher4, new IOException());
        verify(threadPool, times(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE+1)).startDispatcher(any(NetworkEventDispatcher.class));
    }

    @Test
    public void shouldStartCoreDispatchers() throws Exception {
        EventWorkerThreadPool workerThreadPool = mock(EventWorkerThreadPool.class);
        DispatcherThreadPool threadPool = mock(DispatcherThreadPool.class);
        when(threadPool.getMaximumPoolSize())
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE);

        when(threadPool.getCorePoolSize())
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE);

        when(threadPool.getPoolSize())
            .thenReturn(0)
            .thenReturn(1)
            .thenReturn(2)
            .thenReturn(3);
        
        NetworkEventDispatcherManager manager = new NetworkEventDispatcherManager(threadPool, new NetworkEventDispatcherFactory(), workerThreadPool);
        for (int i = 0; i < DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE; i++)
            manager.startNewDispatcher();
        verify(threadPool, times(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)).startDispatcher(any(NetworkEventDispatcher.class));
        assertEquals(0, manager.getAddedDispatcherCount());
    }    

    @Test
    public void shouldStartCoreThenTemporaryDispatchers() throws Exception {
        DispatcherThreadPool threadPool = mock(DispatcherThreadPool.class);
        when(threadPool.getMaximumPoolSize())
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE);
    
        when(threadPool.getCorePoolSize())
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE)
            .thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE);

        when(threadPool.getPoolSize())
            .thenReturn(0)
            .thenReturn(1)
            .thenReturn(2)
            .thenReturn(3)
            .thenReturn(4);
        
        NetworkEventDispatcherManager manager = new NetworkEventDispatcherManager(threadPool, new NetworkEventDispatcherFactory(), null);
        for (int i = 0; i < DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE; i++)
            manager.startNewDispatcher();
        assertEquals(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE, manager.getCoreDispatcherCount());
        assertEquals(0, manager.getAddedDispatcherCount());
        
        manager.startNewDispatcher();
        assertEquals(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE, manager.getCoreDispatcherCount());
        assertEquals(1, manager.getAddedDispatcherCount());
        verify(threadPool, times(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE+1)).startDispatcher(any(NetworkEventDispatcher.class));
    }    
    
    @Test
    public void shouldNotStartMoreDispatchersThanMaxPoolSize() throws Exception {
        DispatcherThreadPool threadPool = mock(DispatcherThreadPool.class);
        when(threadPool.getPoolSize()).thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE);
        NetworkEventDispatcherManager manager = new NetworkEventDispatcherManager(threadPool, new NetworkEventDispatcherFactory(), null);
        manager.startNewDispatcher();
        verify(threadPool, never()).startDispatcher(any(NetworkEventDispatcher.class));
    }

    @Test
    public void shouldStartNewDispatchers() throws Exception {
        DispatcherThreadPool threadPool = mock(DispatcherThreadPool.class);
        when(threadPool.getMaximumPoolSize()).thenReturn(DispatcherThreadPool.DEFAULT_MAX_POOL_SIZE);
        when(threadPool.getCorePoolSize()).thenReturn(DispatcherThreadPool.DEFAULT_CORE_POOL_SIZE);
        when(threadPool.getPoolSize()).thenReturn(0);
        NetworkEventDispatcherManager manager = new NetworkEventDispatcherManager(threadPool, new NetworkEventDispatcherFactory(), null);
        manager.startNewDispatcher();
        verify(threadPool).startDispatcher(any(NetworkEventDispatcher.class));
    }
}
