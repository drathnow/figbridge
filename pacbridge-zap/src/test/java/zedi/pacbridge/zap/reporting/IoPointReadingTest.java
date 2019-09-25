package zedi.pacbridge.zap.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapValue;
import zedi.pacbridge.zap.values.ZapValueDeserializer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReadingFlags.class, IoPointReading.class, ZapValueDeserializer.class})
public class IoPointReadingTest extends BaseTestCase {

    private static final ZapDataType DATA_TYPE = ZapDataType.UnsignedInteger;
    private static final ZapAlarmStatus ALARM_STATUS = ZapAlarmStatus.High;

    @Test
    public void shouldDeserializeEmptyValue() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ReadingFlags flags = mock(ReadingFlags.class);
        
        mockStatic(ReadingFlags.class);
        mockStatic(ZapValueDeserializer.class);
        
        given(ReadingFlags.readingFlagsFromByteBuffer(byteBuffer)).willReturn(flags);
        given(flags.isNullValue()).willReturn(false);
        given(flags.isEmptyValue()).willReturn(true);
        given(flags.alarmStatus()).willReturn(ALARM_STATUS);
        
        IoPointReading reading = IoPointReading.ioPointReadingFromByteBuffer(DATA_TYPE, byteBuffer);
        
        assertFalse(reading.isNullValue());
        assertTrue(reading.isEmptyValue());
        
        verifyStatic(ReadingFlags.class);
        ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
        
        verifyStatic(ZapValueDeserializer.class, never());
        ZapValueDeserializer.valueFromByteBuffer(DATA_TYPE, byteBuffer);
    }
    
    @Test
    public void shouldDeserializeNullValue() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ReadingFlags flags = mock(ReadingFlags.class);

        mockStatic(ReadingFlags.class);
        mockStatic(ZapValueDeserializer.class);
        
        given(ReadingFlags.readingFlagsFromByteBuffer(byteBuffer)).willReturn(flags);
        given(flags.isNullValue()).willReturn(true);
        given(flags.isEmptyValue()).willReturn(false);
        given(flags.alarmStatus()).willReturn(ALARM_STATUS);
        
        IoPointReading reading = IoPointReading.ioPointReadingFromByteBuffer(DATA_TYPE, byteBuffer);
        
        assertTrue(reading.isNullValue());
        assertFalse(reading.isEmptyValue());
        assertEquals(ALARM_STATUS, reading.alarmStatus());
        
        verifyStatic(ReadingFlags.class);
        ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
        
        verifyStatic(ZapValueDeserializer.class, never());
        ZapValueDeserializer.valueFromByteBuffer(DATA_TYPE, byteBuffer);
    }
    
    @Test
    public void shouldDeserializeFullValue() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ReadingFlags flags = mock(ReadingFlags.class);
        ZapValue value = mock(ZapValue.class);

        mockStatic(ReadingFlags.class);
        mockStatic(ZapValueDeserializer.class);
        
        given(ReadingFlags.readingFlagsFromByteBuffer(byteBuffer)).willReturn(flags);
        given(flags.isNullValue()).willReturn(false);
        given(flags.isEmptyValue()).willReturn(false);
        given(flags.alarmStatus()).willReturn(ALARM_STATUS);
        given(ZapValueDeserializer.valueFromByteBuffer(DATA_TYPE, byteBuffer)).willReturn(value);
        
        IoPointReading reading = IoPointReading.ioPointReadingFromByteBuffer(DATA_TYPE, byteBuffer);
        
        assertFalse(reading.isNullValue());
        assertFalse(reading.isEmptyValue());
        assertEquals(ALARM_STATUS, reading.alarmStatus());
     
        verifyStatic(ReadingFlags.class);
        ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
    }
}
