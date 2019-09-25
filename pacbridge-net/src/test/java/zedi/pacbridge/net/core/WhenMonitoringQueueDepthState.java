package zedi.pacbridge.net.core;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.core.RequestQueueMonitor.MonitoringQueueDepthState;
import zedi.pacbridge.net.core.RequestQueueMonitor.QueueDepthAboveThresholdState;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;


public class WhenMonitoringQueueDepthState extends BaseTestCase {

    private final static int CRITICAL_QUEUE_DEPTH_THRESHOLD = 10;
    private final static int QUEUE_DEPTH_HYSTERISIS = 4;
    
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
    public void shouldReturnToMonitorQueueDepthStateWhenQueueDepthDropsBackToThreshold() throws Exception {
        
        when(queueMonitorHelper.getCriticalQueueDepthThreshold()).thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD);
        when(queueMonitorHelper.getQueueDepthHysteresis()).thenReturn(QUEUE_DEPTH_HYSTERISIS);
        
        when(requestQueue.size())
            .thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD-1)
            .thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD)
            .thenReturn(QUEUE_DEPTH_HYSTERISIS);
        
        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, queueMonitorHelper);
        queueMonitor.setSystemTime(systemTime);
        
        queueMonitor.shouldExitAfterMainLoop();
        assertTrue(queueMonitor.getCurrentState() instanceof MonitoringQueueDepthState);
    
        queueMonitor.shouldExitAfterMainLoop();
        assertTrue(queueMonitor.getCurrentState() instanceof QueueDepthAboveThresholdState);
            
        queueMonitor.shouldExitAfterMainLoop();
        assertTrue(queueMonitor.getCurrentState() instanceof MonitoringQueueDepthState);
    }
    
    @Test
    public void shouldMonitorQueueStateUntilAboveThreshold() throws Exception {
        when(queueMonitorHelper.getCriticalQueueDepthThreshold()).thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD);
        when(requestQueue.size())
            .thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD-1)
            .thenReturn(CRITICAL_QUEUE_DEPTH_THRESHOLD);
        
        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, queueMonitorHelper);
        queueMonitor.setSystemTime(systemTime);
        
        queueMonitor.shouldExitAfterMainLoop();
        assertTrue(queueMonitor.getCurrentState() instanceof MonitoringQueueDepthState);

        queueMonitor.shouldExitAfterMainLoop();
        assertTrue(queueMonitor.getCurrentState() instanceof QueueDepthAboveThresholdState);
    }
}
