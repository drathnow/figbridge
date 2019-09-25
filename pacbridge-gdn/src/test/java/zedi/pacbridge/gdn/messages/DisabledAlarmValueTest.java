package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.test.BaseTestCase;

public class DisabledAlarmValueTest extends BaseTestCase {
    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        DisabledAlarmValue alarmValue = new DisabledAlarmValue(AlarmType.High);
        assertEquals(ExtendedAlarmValue.FIXED_SIZE, alarmValue.size());

        alarmValue.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(3, byteBuffer.get());
        assertEquals(AlarmType.High.getTypeNumber().byteValue(), byteBuffer.get());
        assertEquals(0, byteBuffer.get());
    }
}
