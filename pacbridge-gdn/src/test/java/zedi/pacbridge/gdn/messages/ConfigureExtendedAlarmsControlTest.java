package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class ConfigureExtendedAlarmsControlTest extends BaseTestCase {
    private static final Float FLOAT_VALUE = 1.2f;
    private static final Float HYST_VALUE = 0.3f;
    private static final Integer SET_TIME_SECONDS = 20;
    private static final Integer CLEAR_SECONDS = 3;

    private static final Integer INDEX = 2;
    private static final Float HYS_VALUE = 2.3f;
    private static final Float LIMIT_VALUE = 4.3f;
    private static final GdnFloat HYS_GDN_VALUE = new GdnFloat(HYS_VALUE);
    private static final GdnFloat LIMIT_GDN_VALUE = new GdnFloat(LIMIT_VALUE);
    private static final Integer ACTION_ID = 42;
    
    @Test
    public void shouldReturnCorrectMessageType() throws Exception {
        ConfigureExtendedAlarmsControl message = new ConfigureExtendedAlarmsControl(INDEX, GdnDataType.Float, new ArrayList<ExtendedAlarmValue>());
        assertEquals(GdnMessageType.ConfigureExtendedAlarms, message.messageType());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testAddExtendedAlarmValueWithBadValue() throws Exception {
        List<ExtendedAlarmValue> alarmValues = new ArrayList<>();
        alarmValues.add(new EnabledAlarmValue(AlarmType.High, LIMIT_GDN_VALUE, HYS_GDN_VALUE));
        new ConfigureExtendedAlarmsControl(INDEX, GdnDataType.Integer, alarmValues);
    }
    
    @Test
    public void shouldDeserializeFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
     
        byteBuffer.put((byte)0);
        byteBuffer.putShort(INDEX.shortValue());
        byteBuffer.put(GdnDataType.Float.getNumber().byteValue());
        byteBuffer.put((byte)2);
        
        byteBuffer.put((byte)15);
        byteBuffer.put(AlarmType.Low.getTypeNumber().byteValue());
        byteBuffer.put((byte)1);
        byteBuffer.putFloat(FLOAT_VALUE);
        byteBuffer.putFloat(HYST_VALUE);
        byteBuffer.putShort(SET_TIME_SECONDS.shortValue());
        byteBuffer.putShort(CLEAR_SECONDS.shortValue());
        byteBuffer.putShort(ACTION_ID.shortValue());
        
        byteBuffer.put((byte)15);
        byteBuffer.put(AlarmType.High.getTypeNumber().byteValue());
        byteBuffer.put((byte)0);
        byteBuffer.putFloat(FLOAT_VALUE);
        byteBuffer.putFloat(HYST_VALUE);
        byteBuffer.putShort(SET_TIME_SECONDS.shortValue());
        byteBuffer.putShort(CLEAR_SECONDS.shortValue());
        byteBuffer.putShort(ACTION_ID.shortValue());
        byteBuffer.flip();
        
        ConfigureExtendedAlarmsControl control = ConfigureExtendedAlarmsControl.configureExtendedAlarmsControlFromByteBuffer(byteBuffer);
        
        assertEquals(INDEX, control.getIndex());
        assertEquals(GdnDataType.Float, control.getDataType());
        
        List<ExtendedAlarmValue> alarmValues = control.getExtendedAlarmValues();
        assertEquals(2, alarmValues.size());
        
        ExtendedAlarmValue lowAlarmValue = alarmValueFromList(alarmValues, AlarmType.Low);
        assertNotNull(lowAlarmValue);
        
        assertEquals(AlarmType.Low, lowAlarmValue.getAlarmType());
        assertTrue(lowAlarmValue.isEnabled());
        assertEquals(FLOAT_VALUE, ((Number)lowAlarmValue.getLimitValue().getValue()).floatValue(), 0.1f);
        assertEquals(HYST_VALUE, ((Number)lowAlarmValue.getHysteresisValue().getValue()).floatValue(), 0.1f);
        assertEquals(SET_TIME_SECONDS, lowAlarmValue.getSetTimeSeconds());
        assertEquals(CLEAR_SECONDS, lowAlarmValue.getClearTimeSeconds());

        ExtendedAlarmValue highAlarmValue = alarmValueFromList(alarmValues, AlarmType.High);
        assertNotNull(lowAlarmValue);
        
        assertEquals(AlarmType.High, highAlarmValue.getAlarmType());
        assertFalse(highAlarmValue.isEnabled());
        assertNull(highAlarmValue.getLimitValue());
        assertNull(highAlarmValue.getHysteresisValue());
    }
    
    private ExtendedAlarmValue alarmValueFromList(List<ExtendedAlarmValue> alarmValues, AlarmType alarmType) {
        for (ExtendedAlarmValue alarmValue : alarmValues) 
            if (alarmValue.getAlarmType().equals(alarmType))
                return alarmValue;
        return null;
    }
    
    @Test
    public void testSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);

        List<ExtendedAlarmValue> alarmValues = new ArrayList<>();
        alarmValues.add(new EnabledAlarmValue(AlarmType.Low, new GdnFloat(FLOAT_VALUE), new GdnFloat(HYST_VALUE), SET_TIME_SECONDS, CLEAR_SECONDS));
        alarmValues.add(new EnabledAlarmValue(AlarmType.High, new GdnFloat(FLOAT_VALUE), new GdnFloat(HYST_VALUE), SET_TIME_SECONDS, CLEAR_SECONDS));
        
        ConfigureExtendedAlarmsControl control = new ConfigureExtendedAlarmsControl(INDEX, GdnDataType.Float, alarmValues);
        control.serialize(byteBuffer);
        byteBuffer.flip();
        
        
        assertEquals(0, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(GdnDataType.Float.getNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(control.getExtendedAlarmValues().size(), Unsigned.getUnsignedByte(byteBuffer));
        
        assertEquals(17, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.Low.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(1, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(FLOAT_VALUE, byteBuffer.getFloat(), 0.1f);
        assertEquals(HYST_VALUE, byteBuffer.getFloat(), 0.1f);
        assertEquals(SET_TIME_SECONDS.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(CLEAR_SECONDS.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(0, Unsigned.getUnsignedShort(byteBuffer));
        
        assertEquals(17, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.High.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(1, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(FLOAT_VALUE, byteBuffer.getFloat(), 0.1f);
        assertEquals(HYST_VALUE, byteBuffer.getFloat(), 0.1f);
        assertEquals(SET_TIME_SECONDS.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(CLEAR_SECONDS.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(0, Unsigned.getUnsignedShort(byteBuffer));
    }
}
