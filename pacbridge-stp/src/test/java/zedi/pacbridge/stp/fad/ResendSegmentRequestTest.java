package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResendSegmentRequestTest {

    private static final int MESSAGE_ID = 1;
    private static final int SEGMENT_ID = 2;
    
    @Test
    public void shouldBuildHeaderCorrectly() {
        ResendSegmentRequest request = new ResendSegmentRequest(MESSAGE_ID, SEGMENT_ID);
        assertTrue(request.isResendSegmentRequest());
    }

}
