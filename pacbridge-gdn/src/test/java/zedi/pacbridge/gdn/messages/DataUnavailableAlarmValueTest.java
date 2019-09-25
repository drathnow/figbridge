package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.test.BaseTestCase;

public class DataUnavailableAlarmValueTest extends BaseTestCase {

    @Test
    public void shouldSerializeEnabledValueToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        DataUnavailableAlarmValue alarmValue = new DataUnavailableAlarmValue(true);
        assertEquals(ExtendedAlarmValue.FIXED_SIZE, alarmValue.size());

        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(3, byteBuffer.get());
        assertEquals(AlarmType.DataUnavailable.getTypeNumber().byteValue(), byteBuffer.get());
        assertEquals(1, byteBuffer.get());
    }

    @Test
    public void shouldSerializeDisabledValueToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        DataUnavailableAlarmValue alarmValue = new DataUnavailableAlarmValue(false);
        assertEquals(ExtendedAlarmValue.FIXED_SIZE, alarmValue.size());

        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(3, byteBuffer.get());
        assertEquals(AlarmType.DataUnavailable.getTypeNumber().byteValue(), byteBuffer.get());
        assertEquals(0, byteBuffer.get());
    }
}
