package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.reporting.ResponseStatus;

public class AckMessageTest extends BaseTestCase {
    
    private static final Short SEQ_NO = 42;
    
    @Test
    public void shouldReturnProtocolErrorFlags() throws Exception {
        ProtocolErrorDetails details = new ProtocolErrorDetails(ProtocolErrorType.InvalidSessionId);
        
        AckMessage ackMessage = new AckMessage(SEQ_NO.intValue(), ZapMessageType.BundledReport, details);
        AckMessage.Flags flags = ackMessage.flags();
        
        assertTrue(flags.isAdditionalDetailsSet());
        assertTrue(flags.isProtocolErrorSet());
    }

    @Test
    public void shouldReturnAdditionalDetailsFlags() throws Exception {
        BundledReportAckDetails details = new BundledReportAckDetails();
        details.addReportStatus(1, ResponseStatus.OK);
        
        AckMessage ackMessage = new AckMessage(SEQ_NO.intValue(), ZapMessageType.BundledReport, details);
        AckMessage.Flags flags = ackMessage.flags();
        
        assertTrue(flags.isAdditionalDetailsSet());
        assertFalse(flags.isProtocolErrorSet());
    }

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{0x00, 0x00, 0x04, 0x00, 0x04, 0x00, 0x0A});
        AckMessage message = AckMessage.ackMessageForByteBuffer(byteBuffer);
        assertEquals(ZapMessageType.BundledReport, message.getAckedMessageType());
        assertEquals(10, message.sequenceNumber().intValue());
        
        System.out.println("ACK: " + message.toString());
    }
    
    @Test
    public void shouldSerializeWithAdditionalDetails() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        BundledReportAckDetails details = new BundledReportAckDetails();
        details.addReportStatus(1, ResponseStatus.OK);
        
        AckMessage ackMessage = new AckMessage(SEQ_NO.intValue(), ZapMessageType.BundledReport, details);
        ackMessage.serialize(byteBuffer);
        byteBuffer.flip();
        
        int expectedSize = AckMessage.FIXED_SIZE + details.size();
        
        assertEquals(expectedSize, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals((byte)1, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals((short)ZapMessageType.BUNDLED_REPORT_NUMBER, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals((short)SEQ_NO, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(details.size().shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals((byte)AckDetailsType.BUNDLED_REPORT_ACK_NUMBER, byteBuffer.get());
        assertEquals(1, byteBuffer.getShort());
        assertEquals(ResponseStatus.OK, ResponseStatus.reportStatusForNumber(byteBuffer.getInt()));
    }

    @Test
    public void shouldSerializeWithoutAdditionalDetails() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        AckMessage ackMessage = new AckMessage(42, ZapMessageType.BundledReport);
        ackMessage.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(AckMessage.FIXED_SIZE.byteValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals((byte)0, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals((short)ZapMessageType.BUNDLED_REPORT_NUMBER, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals((short)42, Unsigned.getUnsignedShort(byteBuffer));
    }
}
