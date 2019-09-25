package zedi.pacbridge.zap.messages;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class OtadStatusMessageTest extends BaseTestCase {
	private static final long EVENT_ID = 5432L;
	private static final String VER = "1.2.3";
	private static final String ERR_MSG = "It broke";
	

	@Test
	public void shouldDeserializeMessageFromByteBuffer() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(256);
		byteBuffer.put(OtadStatusMessage.VERSION1.byteValue());
		byteBuffer.putLong(EVENT_ID);
        byteBuffer.put((byte)OtadStatus.OTAD_COMPLETE_NUMBER);
		byteBuffer.flip();
		
		OtadStatusMessage message = OtadStatusMessage.messageFromByteBuffer(byteBuffer);
		assertNotNull(message);
		assertEquals(EVENT_ID, message.getEventId().longValue());
		assertEquals(OtadStatus.COMPLETE, message.getOtadStatusType());
		assertNull(message.getOptionalData());
	}

	@Test
	public void shouldDeserializeCompleteStatusMessageFromByteBuffer() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(256);
		byteBuffer.put(OtadStatusMessage.VERSION1.byteValue());
		byteBuffer.putLong(EVENT_ID);
        byteBuffer.put((byte)OtadStatus.OTAD_COMPLETE_NUMBER);
		byteBuffer.put((byte)VER.length());
		byteBuffer.put(VER.getBytes());
		byteBuffer.flip();
		
		OtadStatusMessage message = OtadStatusMessage.messageFromByteBuffer(byteBuffer);
		assertNotNull(message);
		assertEquals(EVENT_ID, message.getEventId().longValue());
		assertEquals(OtadStatus.COMPLETE, message.getOtadStatusType());
		assertNotNull(message.getOptionalData());
		assertEquals(VER, new String(message.getOptionalData()));
	}

    @Test
    public void shouldDeserializeCompleteStatusMessageFromByteBufferWithNoOptionalData() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.put(OtadStatusMessage.VERSION1.byteValue());
        byteBuffer.putLong(EVENT_ID);
        byteBuffer.put((byte)OtadStatus.OTAD_COMPLETE_NUMBER);
        byteBuffer.flip();
        
        OtadStatusMessage message = OtadStatusMessage.messageFromByteBuffer(byteBuffer);
        assertNotNull(message);
        assertEquals(EVENT_ID, message.getEventId().longValue());
        assertEquals(OtadStatus.COMPLETE, message.getOtadStatusType());
        assertNull(message.getOptionalData());
    }

	@Test
	public void shouldDeserializeFailedStatusMessageFromByteBuffer() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(256);
		byteBuffer.put(OtadStatusMessage.VERSION1.byteValue());
		byteBuffer.putLong(EVENT_ID);
		byteBuffer.put((byte)OtadStatus.OTAD_FAILED_NUMBER);
		byteBuffer.put((byte)ERR_MSG.length());
		byteBuffer.put(ERR_MSG.getBytes());
		byteBuffer.flip();
		
		OtadStatusMessage message = OtadStatusMessage.messageFromByteBuffer(byteBuffer);
		assertNotNull(message);
		assertEquals(EVENT_ID, message.getEventId().longValue());
		assertEquals(OtadStatus.FAILED, message.getOtadStatusType());
		assertNotNull(message.getOptionalData());
		assertEquals(ERR_MSG, new String(message.getOptionalData()));
	}
}

