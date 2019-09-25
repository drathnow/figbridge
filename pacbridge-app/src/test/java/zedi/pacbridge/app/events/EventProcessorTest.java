package zedi.pacbridge.app.events;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;

public class EventProcessorTest extends BaseTestCase {
    @Mock
    private BridgeContext bridgeContext;
    
    @Test
    public void shouldHandleAnyAndAllExceptions() throws Exception {
        HandleableEvent event = mock(HandleableEvent.class);
        RuntimeException toBeThrown = new RuntimeException();
        doThrow(toBeThrown).when(event).handle(bridgeContext);
        EventProcessor eventProcessor = new EventProcessor(bridgeContext);
        try{
            eventProcessor.processEvent(event);
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void shouldPassBridgeContextToEvent() throws Exception {
        HandleableEvent event = mock(HandleableEvent.class);
        EventProcessor eventProcessor = new EventProcessor(bridgeContext);
        eventProcessor.processEvent(event);
        verify(event).handle(bridgeContext);
    }
}
