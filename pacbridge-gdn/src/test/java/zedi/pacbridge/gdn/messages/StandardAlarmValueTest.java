package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;

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
import zedi.pacbridge.utl.io.Unsigned;

public class StandardAlarmValueTest extends BaseTestCase {

    protected static final byte ENABLED_BYTE_MESSAGE[] = new byte[]{0x04, AlarmType.High.getTypeNumber().byteValue(), 0x01, 0x00};
    protected static final byte DISABLED_BYTE_MESSAGE[] = new byte[]{0x04, AlarmType.High.getTypeNumber().byteValue(), 0x00, 0x00};
    protected static final GdnValue<?> GDN_DISCRETE_VALUE = new GdnDiscrete(1);
    protected static final GdnValue<?> GDN_BYTE_VALUE = new GdnByte(-1);
    protected static final GdnValue<?> GDN_UBYTE_VALUE = new GdnUnsignedByte(255);
    protected static final GdnValue<?> GDN_INT_VALUE = new GdnInteger(-1);
    protected static final GdnValue<?> GDN_UINT_VALUE = new GdnUnsignedInteger(65535);
    protected static final GdnValue<?> GDN_FLOAT_VALUE = new GdnFloat((float)2.333);
    protected static final GdnValue<?> GDN_LONG_VALUE = new GdnLong(-1);
    protected static final GdnValue<?> GDN_ULONG_VALUE = new GdnUnsignedLong((long)Integer.MAX_VALUE);
    protected static final String LOW_ALARM_VALUE_STRING = "1,1,2.5";
    protected static final String DNA_ALARM_VALUE_STRING = "6,0,2.5";

    @Test
    public void shouldSerializeOutputForDisabled() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        StandardAlarmValue alarmValue = new StandardAlarmValue(AlarmType.High, null, false);
        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(StandardAlarmValue.FIXED_SIZE, byteBuffer.remaining());
        assertEquals(StandardAlarmValue.FIXED_SIZE, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.High.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(0, Unsigned.getUnsignedByte(byteBuffer));
    }
    
    @Test
    public void shouldSerializeOutputForDataUnavailableEnabled() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        StandardAlarmValue alarmValue = new StandardAlarmValue(AlarmType.DataUnavailable, null, true);
        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(StandardAlarmValue.FIXED_SIZE, byteBuffer.remaining());
        assertEquals(StandardAlarmValue.FIXED_SIZE, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.DataUnavailable.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(1, Unsigned.getUnsignedByte(byteBuffer));
    }

    @Test
    public void shouldSerializeOutputForDataUnavailableDisabled() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        StandardAlarmValue alarmValue = new StandardAlarmValue(AlarmType.DataUnavailable, null, false);
        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(StandardAlarmValue.FIXED_SIZE, byteBuffer.remaining());
        assertEquals(StandardAlarmValue.FIXED_SIZE, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.DataUnavailable.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(0, Unsigned.getUnsignedByte(byteBuffer));
    }

    @Test
    public void testSerializeInputForEnabled() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(ENABLED_BYTE_MESSAGE);
        StandardAlarmValue alarmValue = StandardAlarmValue.standardAlarmValueFromByteBuffer(byteBuffer, GdnDataType.Discrete);
        assertEquals(AlarmType.High, alarmValue.getAlarmType());
        assertTrue(alarmValue.isEnabled());
        assertEquals(GdnDataType.Discrete, alarmValue.getLimitValue().dataType());
        assertEquals(0, ((Number)alarmValue.getLimitValue().getValue()).intValue());
    }

    @Test
    public void testSerializeInputForDisabled() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(DISABLED_BYTE_MESSAGE);
        StandardAlarmValue alarmValue = StandardAlarmValue.standardAlarmValueFromByteBuffer(byteBuffer, GdnDataType.Discrete);
        assertEquals(AlarmType.High, alarmValue.getAlarmType());
        assertFalse(alarmValue.isEnabled());
    }

    @Test
    public void testSerializeNoDataAlarmValue() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        StandardAlarmValue alarmValue = new StandardAlarmValue(AlarmType.DataUnavailable, null, true);
        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(StandardAlarmValue.FIXED_SIZE, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.DataUnavailable.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(1, Unsigned.getUnsignedByte(byteBuffer));
    }

    @Test
    public void testSerializeValue() throws IOException {
        checkSerializeValueToOutputStream(GDN_DISCRETE_VALUE);
        checkSerializeValueToOutputStream(GDN_BYTE_VALUE);
        checkSerializeValueToOutputStream(GDN_UBYTE_VALUE);
        checkSerializeValueToOutputStream(GDN_INT_VALUE);
        checkSerializeValueToOutputStream(GDN_UINT_VALUE);
        checkSerializeValueToOutputStream(GDN_UINT_VALUE);
        checkSerializeValueToOutputStream(GDN_LONG_VALUE);
        checkSerializeValueToOutputStream(GDN_ULONG_VALUE);
        checkSerializeValueToOutputStream(GDN_FLOAT_VALUE);
    }

    private void checkSerializeValueToOutputStream(GdnValue<?> gdnValue) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        StandardAlarmValue alarmValue = new StandardAlarmValue(AlarmType.High, gdnValue, true);
        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(StandardAlarmValue.FIXED_SIZE + gdnValue.serializedSize(), byteBuffer.remaining());
        assertEquals(StandardAlarmValue.FIXED_SIZE + gdnValue.serializedSize(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(AlarmType.High.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
    }    
}
