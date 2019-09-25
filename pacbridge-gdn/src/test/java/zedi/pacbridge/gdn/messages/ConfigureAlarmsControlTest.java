package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnByte;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnDiscrete;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.gdn.GdnInteger;
import zedi.pacbridge.gdn.GdnLong;
import zedi.pacbridge.gdn.GdnUnsignedByte;
import zedi.pacbridge.gdn.GdnUnsignedInteger;
import zedi.pacbridge.gdn.GdnUnsignedLong;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.test.BaseTestCase;

public class ConfigureAlarmsControlTest extends BaseTestCase {
    private static final Integer INDEX = 122;
    private static final byte BYTE_MESSAGE[] = new byte[]{0x00, INDEX.byteValue(), (byte)GdnDataType.NUMBER_FOR_TYPE_DISCRETE, 0x01,
                                                            0x04, AlarmType.HighHigh.getTypeNumber().byteValue(), 0x01, 0x00};
    private static final GdnDiscrete DISCRETE_VALUE = new GdnDiscrete(1);
    private static final GdnByte BYTE_VALUE = new GdnByte(-1);
    private static final GdnUnsignedByte UBYTE_VALUE = new GdnUnsignedByte(255);
    private static final GdnInteger INT_VALUE = new GdnInteger(-1);
    private static final GdnUnsignedInteger UINT_VALUE = new GdnUnsignedInteger(65535);
    private static final GdnFloat FLOAT_VALUE = new GdnFloat(2.333f);
    private static final GdnLong LONG_VALUE = new GdnLong(-1);
    private static final GdnUnsignedLong ULONG_VALUE = new GdnUnsignedLong(GdnUnsignedLong.MAX_VALUE);

    @Test
    public void shouldCalculateSize() {
        List<StandardAlarmValue> alarmValues = new ArrayList<>();
        Integer value = new Integer(65);
        alarmValues.add(new StandardAlarmValue(AlarmType.HighHigh, new GdnLong(value), true));
        alarmValues.add(new StandardAlarmValue(AlarmType.High, null, false));
        alarmValues.add(new StandardAlarmValue(AlarmType.LowLow, new GdnLong(value), true));
        alarmValues.add(new StandardAlarmValue(AlarmType.Low, null, false));
        alarmValues.add(new StandardAlarmValue(AlarmType.DataUnavailable, null, true));
        
        ConfigureAlarmsControl alarmsControl = new ConfigureAlarmsControl(INDEX, GdnDataType.Long, alarmValues);
        assertEquals(27, alarmsControl.size().intValue());
    }

    @Test
    public void testMessageNumber() {
        ConfigureAlarmsControl alarmsControl = new ConfigureAlarmsControl(INDEX, GdnDataType.Long, null);
        assertEquals(GdnMessageType.ConfigureAlarms, alarmsControl.messageType());
    }

    @Test
    public void shouldDeserialzeFromByteBuffer() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(BYTE_MESSAGE);
        ConfigureAlarmsControl alarmsControl = ConfigureAlarmsControl.configureAlarmsControlFromByteBuffer(byteBuffer);
        assertEquals(INDEX, alarmsControl.getIndex());
        assertEquals(1, alarmsControl.getAlarmValues().size());
        StandardAlarmValue alarmValue = alarmsControl.getAlarmValues().get(0);
        assertEquals(AlarmType.HighHigh, alarmValue.getAlarmType());
        assertTrue(alarmValue.isEnabled());
        assertEquals(0, ((Number)alarmValue.getLimitValue().getValue()).intValue());
    }

    @Test
    public void testSerializeDiscreteOutput() throws IOException {
        checkAlarmValues(DISCRETE_VALUE);
    }

    @Test
    public void testSerializeByteOutput() throws IOException {
        checkAlarmValues(BYTE_VALUE);
    }

    @Test
    public void testSerializeUnsignedByteOutput() throws IOException {
        checkAlarmValues(UBYTE_VALUE);
    }

    @Test
    public void testSerializeIntegerOutput() throws IOException {
        checkAlarmValues(INT_VALUE);
    }

    @Test
    public void testSerializeUnsignedIntegerOutput() throws IOException {
        checkAlarmValues(UINT_VALUE);
    }

    @Test
    public void testSerializeFloatOutput() throws IOException {
        checkAlarmValues(FLOAT_VALUE);
    }

    @Test
    public void testSerializeLongOutput() throws IOException {
        checkAlarmValues(LONG_VALUE);
    }

    @Test
    public void testSerializeUnsignedLongOutput() throws IOException {
        checkAlarmValues(ULONG_VALUE);
    }

    protected void checkAlarmValues(GdnValue<?> theValue) throws IOException {
        List<StandardAlarmValue> alarmValues = new ArrayList<>();
        alarmValues.add(new StandardAlarmValue(AlarmType.HighHigh, theValue, true));
        alarmValues.add(new StandardAlarmValue(AlarmType.High, theValue, false));
        alarmValues.add(new StandardAlarmValue(AlarmType.LowLow, theValue, true));
        alarmValues.add(new StandardAlarmValue(AlarmType.Low, theValue, false));
        alarmValues.add(new StandardAlarmValue(AlarmType.HighHigh, theValue, true));

        ConfigureAlarmsControl control = new ConfigureAlarmsControl(INDEX, theValue.dataType(), alarmValues);
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        
        control.serialize(byteBuffer);
        byteBuffer.flip();
        
        ConfigureAlarmsControl newControl = ConfigureAlarmsControl.configureAlarmsControlFromByteBuffer(byteBuffer); 
                
        assertEquals(INDEX, newControl.getIndex());
        assertEquals(control.getDataType(), newControl.getDataType());
        assertEquals(5, control.getAlarmValues().size());
        
        StandardAlarmValue alarmValue;
        alarmValue = alarmValueWithType(AlarmType.HighHigh, control.getAlarmValues());
        assertEquals(theValue.getValue(), alarmValue.getLimitValue().getValue());
        assertTrue(alarmValue.isEnabled());
        
        alarmValue = alarmValueWithType(AlarmType.High, control.getAlarmValues());
        assertFalse(alarmValue.isEnabled());
        
        alarmValue = alarmValueWithType(AlarmType.LowLow, control.getAlarmValues());
        assertEquals(theValue.getValue(), alarmValue.getLimitValue().getValue());
        assertTrue(alarmValue.isEnabled());

        alarmValue = alarmValueWithType(AlarmType.Low, control.getAlarmValues());
        assertFalse(alarmValue.isEnabled());
    }

    private static StandardAlarmValue alarmValueWithType(AlarmType alarmType, List<StandardAlarmValue> alarmValues) {
        for (StandardAlarmValue value : alarmValues)
            if (value.getAlarmType() == alarmType)
                return value;
        return null;
    }
}
