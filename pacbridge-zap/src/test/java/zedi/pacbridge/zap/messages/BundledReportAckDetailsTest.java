package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.reporting.ResponseStatus;

public class BundledReportAckDetailsTest extends BaseTestCase {

    private static final Integer REPORT_ID1 = 11;
    private static final Integer REPORT_ID2 = 12;
    private static final Integer REPORT_ID3 = 13;

    @Test
    public void shouldSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        BundledReportAckDetails ack = new BundledReportAckDetails();
        ack.addReportStatus(REPORT_ID1, ResponseStatus.OK);
        ack.addReportStatus(REPORT_ID2, ResponseStatus.TransientError);
        ack.addReportStatus(REPORT_ID3, ResponseStatus.PermanentError);
        
        int expectedSize = 3*BundledReportAckDetails.ACK_SIZE.intValue()+3;
        assertEquals(expectedSize, ack.size().intValue());
        ack.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(expectedSize, byteBuffer.getShort());
        assertEquals(AckDetailsType.BundledReportAck.getNumber().byteValue(), byteBuffer.get());
        assertEquals(3, byteBuffer.getShort());
        assertEquals(REPORT_ID1.intValue(), byteBuffer.getInt());
        assertEquals(ResponseStatus.OK.getNumber().intValue(), byteBuffer.getShort());
        assertEquals(REPORT_ID2.intValue(), byteBuffer.getInt());
        assertEquals(ResponseStatus.TransientError.getNumber().intValue(), byteBuffer.getShort());
        assertEquals(REPORT_ID3.intValue(), byteBuffer.getInt());
        assertEquals(ResponseStatus.PermanentError.getNumber().intValue(), byteBuffer.getShort());
    }
}
