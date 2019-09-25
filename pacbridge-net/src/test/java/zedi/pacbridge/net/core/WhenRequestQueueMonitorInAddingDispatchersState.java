package zedi.pacbridge.net.core;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.core.RequestQueueMonitor.AddDispatchersState;
import zedi.pacbridge.net.core.RequestQueueMonitor.WaitingForNewDispatcherToTakeEffectState;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;


public class WhenRequestQueueMonitorInAddingDispatchersState extends BaseTestCase {

    @Mock
    private RequestQueue<?> requestQueue;
    
    @Mock
    private RequestQueueMonitorHelper queueMonitorHelper;
    
    @Mock
    private SystemTime systemTime;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(queueMonitorHelper.getScanTimeMilliseconds()).thenReturn(0L);
    }
    @Test
    public void shouldCreateNewDispatcherAndWaitForEffect() throws Exception {
        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, queueMonitorHelper);
        queueMonitor.setSystemTime(systemTime);

        AddDispatchersState state = queueMonitor.new AddDispatchersState();
        
        state.doAction();
        verify(queueMonitorHelper).createNewWorker();
        assertTrue(queueMonitor.getCurrentState() instanceof WaitingForNewDispatcherToTakeEffectState);
    }
    
}
