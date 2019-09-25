package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class OtadMessageHeaderTest extends BaseTestCase {


    @Test
    public void testSerializeOutputResponseAckNoMessageType() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        OtadMessageHeader header = new OtadMessageHeader(false, OtadMessageType.LoadImage);
        header.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(0x05, Unsigned.getUnsignedByte(byteBuffer));
    }

    @Test
    public void testSerializeInputCommandAckNoMessageType() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{0x00});
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        assertTrue(header.isCommand());
        assertTrue(header.isAck());
        assertEquals(OtadMessageType.NullMessage, header.getMessageType());
    }

    @Test
    public void testSerializeInputResponseAckNoMessageType() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0x80});
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        assertFalse(header.isCommand());
        assertTrue(header.isAck());
        assertEquals(OtadMessageType.NullMessage, header.getMessageType());
    }

    @Test
    public void testSerializeInputCommandNackNoMessageType() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0x40});
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        assertTrue(header.isCommand());
        assertFalse(header.isAck());
        assertEquals(OtadMessageType.NullMessage, header.getMessageType());
    }

    @Test
    public void testSerializeInputResponseNackNoMessageType() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0xc0});
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        assertFalse(header.isCommand());
        assertFalse(header.isAck());
        assertEquals(OtadMessageType.NullMessage, header.getMessageType());
    }

    @Test
    public void testSerializeInputResponseNackMessageType() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0xd5});
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        assertFalse(header.isCommand());
        assertFalse(header.isAck());
        assertEquals(ErrorCode.UnexpectedCodeBlock, header.getErrorCode());
        assertEquals(OtadMessageType.LoadImage, header.getMessageType());
    }
}
