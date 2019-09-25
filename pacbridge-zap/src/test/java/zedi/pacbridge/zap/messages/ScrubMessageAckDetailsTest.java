package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ScrubMessageAckDetailsTest extends BaseTestCase {

    @Test
    public void shouldIndicateIOPointsFailed() throws Exception {
        ScrubControlAckDetails ackDetails = new ScrubControlAckDetails(1, ScrubControlAckDetails.MSG_ERR_SCRUB_IO_POINTS);
        assertFalse(ackDetails.isSuccessful());
        assertTrue(ackDetails.didIoPointsFail());
        assertFalse(ackDetails.didEventsFail());
        assertFalse(ackDetails.didPortsFail());
        assertFalse(ackDetails.didReportsFail());
    }
    
    @Test
    public void shouldIndicateEventsFailed() throws Exception {
        ScrubControlAckDetails ackDetails = new ScrubControlAckDetails(1, ScrubControlAckDetails.MSG_ERR_SCRUB_EVENTS);
        assertFalse(ackDetails.isSuccessful());
        assertTrue(ackDetails.didEventsFail());
        assertFalse(ackDetails.didIoPointsFail());
        assertFalse(ackDetails.didPortsFail());
        assertFalse(ackDetails.didReportsFail());
    }

    @Test
    public void shouldIndicatePortsFailed() throws Exception {
        ScrubControlAckDetails ackDetails = new ScrubControlAckDetails(1, ScrubControlAckDetails.MSG_ERR_SCRUB_PORTS);
        assertFalse(ackDetails.isSuccessful());
        assertTrue(ackDetails.didPortsFail());
        assertFalse(ackDetails.didIoPointsFail());
        assertFalse(ackDetails.didEventsFail());
        assertFalse(ackDetails.didReportsFail());
    }

    @Test
    public void shouldIndicateReportsFailed() throws Exception {
        ScrubControlAckDetails ackDetails = new ScrubControlAckDetails(1, ScrubControlAckDetails.MSG_ERR_SCRUB_REPORTS);
        assertFalse(ackDetails.isSuccessful());
        assertTrue(ackDetails.didReportsFail());
        assertFalse(ackDetails.didIoPointsFail());
        assertFalse(ackDetails.didEventsFail());
        assertFalse(ackDetails.didPortsFail());
    }
}
