package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class ExtendedAlarmValueTest extends BaseTestCase {
    private static final Float FLOAT_VALUE = 1.2f;
    private static final Float HYST_VALUE = 0.3f;
    private static final Integer SET_TIME_SECONDS = 20;
    private static final Integer CLEAR_SECONDS = 3;
    

    @Test
    public void shouldDeserializeDisabledAlarmValueFromOutputBuffer() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes("03 01 00");
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        ExtendedAlarmValue alarmValue = ExtendedAlarmValue.alarmValueFromByteBuffer(byteBuffer, GdnDataType.Float);
        
        assertTrue(alarmValue instanceof DisabledAlarmValue);
        assertEquals(AlarmType.Low, alarmValue.getAlarmType());
        assertFalse(alarmValue.isEnabled());
    }

    
    @Test
    public void shouldDeserializeEnabledAlarmValueFromOutputBuffer() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes("0F 01 01 3F 99 99 9A 3E 99 99 9A 00 14 00 03 00 03");
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        ExtendedAlarmValue alarmValue = ExtendedAlarmValue.alarmValueFromByteBuffer(byteBuffer, GdnDataType.Float);
        
        assertTrue(alarmValue instanceof EnabledAlarmValue);
        assertEquals(AlarmType.Low, alarmValue.getAlarmType());
        assertTrue(alarmValue.isEnabled());
        assertEquals(FLOAT_VALUE, ((Number)alarmValue.getLimitValue().getValue()).floatValue(), 0.01f);
        assertEquals(HYST_VALUE, ((Number)alarmValue.getHysteresisValue().getValue()).floatValue(), 0.01f);
        assertEquals(SET_TIME_SECONDS, alarmValue.getSetTimeSeconds());
        assertEquals(CLEAR_SECONDS, alarmValue.getClearTimeSeconds());
    }    
}
