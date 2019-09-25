package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ResendMessageRequestTest {

    private static final int MESSAGE_ID = 1;
    
    @Test
    public void shouldSetupHeaderCorrectly() {
        ResendMessageRequest request = new ResendMessageRequest(MESSAGE_ID);
        assertFalse(request.isResendSegmentRequest());
    }

}
