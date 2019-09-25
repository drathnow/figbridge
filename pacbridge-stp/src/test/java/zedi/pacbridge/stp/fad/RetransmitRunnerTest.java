package zedi.pacbridge.stp.fad;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

public class RetransmitRunnerTest {

    private static final int MESSAGE_ID = 2;
    
    @Mock
    private RetransmitEventHandler eventHandler;
    
    @Test
    public void shouldHandleTimeoutForInTransitMessageWithMessageId() {
        RetransmitEventHandler eventHandler = mock(RetransmitEventHandler.class);
        
        RetransmitRunner runner = new RetransmitRunner(eventHandler, MESSAGE_ID);
        runner.run();

        verify(eventHandler).retransmitMessageWithMessageId(MESSAGE_ID);
    }

}
