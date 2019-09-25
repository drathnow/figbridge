package zedi.pacbridge.zap.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;

public class ReadingFlagsTest extends BaseTestCase {
    
    @Test
    public void shouldSerializeAndDeserialize() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(1);
        ReadingFlags flags = new ReadingFlags(false, false, ZapAlarmStatus.OK);
        bb.put(flags.asByteValue());
        bb.flip();
        flags = ReadingFlags.readingFlagsFromByteBuffer(bb);
        assertFalse(flags.isEmptyValue());
        assertFalse(flags.isNullValue());
        assertEquals(ZapAlarmStatus.OK, flags.alarmStatus());
    }
    
    @Test
    public void shouldMaskCorrecValue() throws Exception {
        assertEquals((byte)0x80, new ReadingFlags(true, false, null).asByteValue());
        assertEquals((byte)0x40, new ReadingFlags(false, true, null).asByteValue());
        assertEquals(ZapAlarmStatus.Low.getNumber().byteValue(), new ReadingFlags(false, false, ZapAlarmStatus.Low).asByteValue());
    }
    
    @Test
    public void shouldSerialize() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(1);
        ReadingFlags flags = new ReadingFlags(false, false, ZapAlarmStatus.High);
        bb.put(flags.asByteValue());
        bb.flip();
        assertEquals(0x03, bb.get());
    }
    
    @Test
    public void shouldDeserializeAlarmValue() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0x03});
        
        ReadingFlags flags = ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
        
        assertFalse(flags.isEmptyValue());
        assertFalse(flags.isNullValue());
        assertEquals(ZapAlarmStatus.High, flags.alarmStatus());
    }
    
    @Test
    public void shouldDeserializeEmptyValue() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0x71});
        
        ReadingFlags flags = ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
        
        assertTrue(flags.isEmptyValue());
        assertFalse(flags.isNullValue());
        assertNull(flags.alarmStatus());
    }
    
    @Test
    public void shouldDeserializeNullValue() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0x83});
        
        ReadingFlags flags = ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
        
        assertTrue(flags.isNullValue());
        assertFalse(flags.isEmptyValue());
        assertEquals(ZapAlarmStatus.High, flags.alarmStatus());
    }
}
