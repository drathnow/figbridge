package zedi.pacbridge.net.core;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.core.RequestQueueMonitor.AddDispatchersState;
import zedi.pacbridge.net.core.RequestQueueMonitor.MonitoringQueueDepthState;
import zedi.pacbridge.net.core.RequestQueueMonitor.WaitingForNewDispatcherToTakeEffectState;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;

public class WhenRequestQueueMonitorInWaitingForNewDispatcherToTakeEffect extends BaseTestCase {

    private final static int CRITICAL_QUEUE_DEPTH_THRESHOLD = 10;
    private final static int QUEUE_DEPTH_HYSTERISIS = 4;
    
    @Mock
    private RequestQueue<?> requestQueue;
    
    @Mock
    private RequestQueueMonitorHelper monitorEventHandler;
    
    @Mock
    private SystemTime systemTime;
        
    @Test
    public void shouldWaitForQueueToDropBelowHysteresisThenStartTrimming() throws Exception {
        long now = System.currentTimeMillis();
        
        when(monitorEventHandler.getCriticalQueueDepthThreshold()).thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD);
        when(monitorEventHandler.getQueueDepthHysteresis()).thenReturn(QUEUE_DEPTH_HYSTERISIS);
        
        when(systemTime.getCurrentTime())
            .thenReturn(now)
            .thenReturn(now+2001);
        when(requestQueue.size())
            .thenReturn(QUEUE_DEPTH_HYSTERISIS);
        
        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, monitorEventHandler);
        queueMonitor.setSystemTime(systemTime);
        
        WaitingForNewDispatcherToTakeEffectState state = queueMonitor.new WaitingForNewDispatcherToTakeEffectState();
        state.doAction();
        
        assertTrue(queueMonitor.getCurrentState() instanceof MonitoringQueueDepthState);
    }
    
    @Test
    public void shouldAddNewDispatchersIfSanityTimeExpiresAndQueueDepthDoesNotDropBelowHysteresis() throws Exception {
        long now = System.currentTimeMillis();
        when(systemTime.getCurrentTime())
            .thenReturn(now)
            .thenReturn(now+2001);
        when(requestQueue.size())
            .thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD+1);
        
        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, monitorEventHandler);
        queueMonitor.setSystemTime(systemTime);
        
        WaitingForNewDispatcherToTakeEffectState state = queueMonitor.new WaitingForNewDispatcherToTakeEffectState();
        state.doAction();
        
        assertTrue(queueMonitor.getCurrentState() instanceof AddDispatchersState);
    }
    
    @Test
    public void shouldDoNothingUntilSanityTimeExpires() throws Exception {
        long now = System.currentTimeMillis();
        when(systemTime.getCurrentTime())
            .thenReturn(now)
            .thenReturn(now);

        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, monitorEventHandler);
        queueMonitor.setSystemTime(systemTime);
        
        WaitingForNewDispatcherToTakeEffectState state = queueMonitor.new WaitingForNewDispatcherToTakeEffectState();
        state.doAction();
        
        assertTrue(queueMonitor.getCurrentState() instanceof MonitoringQueueDepthState);
    }
}
