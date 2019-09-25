package zedi.pacbridge.app.events;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.test.BaseTestCase;

public class BridgeContextTest extends BaseTestCase {

    @Mock
    private OutgoingRequestService outgoingRequestService;
    
    @Test
    public void shouldGetOutgoingRequestServiceAndPassToControlEvent() throws Exception {
        ControlEvent controlEvent = mock(ControlEvent.class);
        
        BridgeContext bridgeContext = new BridgeContext(outgoingRequestService);
        
        bridgeContext.handle(controlEvent);
        
        verify(controlEvent).handle(outgoingRequestService);
    }
}