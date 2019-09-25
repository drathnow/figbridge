package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.test.BaseTestCase;

public class EnabledAlarmValueTest extends BaseTestCase {
    private static final Float FLOAT_VALUE = 1.2f;
    private static final Float HYST_VALUE = 0.3f;
    private static final Integer SET_TIME_SECONDS = 20;
    private static final Integer CLEAR_SECONDS = 3;
    
    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        EnabledAlarmValue alarmValue = new EnabledAlarmValue(AlarmType.Low, 
                                                                new GdnFloat(FLOAT_VALUE), 
                                                                new GdnFloat(HYST_VALUE), 
                                                                SET_TIME_SECONDS, 
                                                                CLEAR_SECONDS);
        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        
        EnabledAlarmValue result = (EnabledAlarmValue)ExtendedAlarmValue.alarmValueFromByteBuffer(byteBuffer, GdnDataType.Float);
        assertEquals(AlarmType.Low, result.getAlarmType());
        assertTrue(result.isEnabled());
        assertEquals(FLOAT_VALUE, ((Number)result.getLimitValue().getValue()).floatValue(), 0.01f);
        assertEquals(HYST_VALUE, ((Number)result.getHysteresisValue().getValue()).floatValue(), 0.01f);
        assertEquals(SET_TIME_SECONDS, result.getSetTimeSeconds());
        assertEquals(CLEAR_SECONDS, result.getClearTimeSeconds());
    }
}