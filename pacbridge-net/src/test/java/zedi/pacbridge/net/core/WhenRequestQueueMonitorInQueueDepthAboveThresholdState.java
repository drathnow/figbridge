package zedi.pacbridge.net.core;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.core.RequestQueueMonitor.AddDispatchersState;
import zedi.pacbridge.net.core.RequestQueueMonitor.MonitoringQueueDepthState;
import zedi.pacbridge.net.core.RequestQueueMonitor.QueueDepthAboveThresholdState;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;

public class WhenRequestQueueMonitorInQueueDepthAboveThresholdState extends BaseTestCase {

    private final static int QUEUE_DEPTH_HYSTERISIS = 4;
    private final static long SANITY_TIME_MILLISECONDS = 2000;
    
    @Mock
    private RequestQueue<?> requestQueue;
    
    @Mock
    private RequestQueueMonitorHelper monitorEventHandler;
    
    @Mock
    private SystemTime systemTime;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void shouldShouldReturnToMonitorStateIfQueueLengthDropsBelowHysteresis() throws Exception {
        when(requestQueue.size())
            .thenReturn(QUEUE_DEPTH_HYSTERISIS);

        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, monitorEventHandler);
        queueMonitor.setSystemTime(systemTime);

        QueueDepthAboveThresholdState state = queueMonitor.new QueueDepthAboveThresholdState();
        state.doAction();
        
        assertTrue(queueMonitor.getCurrentState() instanceof MonitoringQueueDepthState);
    }
    
    @Test
    public void shouldChangeToAddDispatcherStateWhenQueueDepthAboveThresholdForSanityTime() throws Exception {
        long now = System.currentTimeMillis();
        when(requestQueue.size())
            .thenReturn(QUEUE_DEPTH_HYSTERISIS+1);

        when(systemTime.getCurrentTime())
            .thenReturn(now)
            .thenReturn(now + SANITY_TIME_MILLISECONDS+1);
        
        RequestQueueMonitor queueMonitor = new RequestQueueMonitor(requestQueue, monitorEventHandler);
        queueMonitor.setSystemTime(systemTime);

        QueueDepthAboveThresholdState state = queueMonitor.new QueueDepthAboveThresholdState();
        state.doAction();
        
        assertTrue(queueMonitor.getCurrentState() instanceof AddDispatchersState);
    }
    

}
