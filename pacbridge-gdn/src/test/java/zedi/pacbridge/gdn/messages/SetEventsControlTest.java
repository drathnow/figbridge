package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class SetEventsControlTest extends BaseTestCase {

    private static final Integer EVENT_INDEX_1 = 1;
    private static final EventAction ACTION_TYPE_1 = EventAction.Report;
    private static final Integer ACTION_TYPE_NUMBER_1 = ACTION_TYPE_1.getActionNumber();
    private static final Integer PARAM1_1 = 3;
    private static final Integer PARAM2_1 = 2;
    private static final Date START_TIME_1 = new Date(100000);
    private static final Integer INTERVAL_1 = 10;
    private static final Integer DURATION_1 = 20;

    private static final Integer EVENT_INDEX_2 = 2;
    private static final EventAction ACTION_TYPE_2 = EventAction.Poll;
    private static final Integer PARAM1_2 = 44;
    private static final Integer PARAM2_2 = 949;
    private static final Date START_TIME_2 = new Date(200000);
    private static final Integer INTERVAL_2 = 5;
    private static final Integer DURATION_2 = 80;


    @Test
    public void testDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.put((byte)0);
        byteBuffer.putShort((short)1);
        byteBuffer.putShort(EVENT_INDEX_1.shortValue());
        byteBuffer.putShort(ACTION_TYPE_NUMBER_1.shortValue());
        byteBuffer.putShort(PARAM1_1.shortValue());
        byteBuffer.putShort(PARAM2_1.shortValue());
        byteBuffer.putInt((int)(START_TIME_1.getTime() / 1000L));
        byteBuffer.putInt(INTERVAL_1);
        byteBuffer.putInt(DURATION_1);
        byteBuffer.flip();
        
        SetEventsControl control = SetEventsControl.setEventsControlFromByteBuffer(byteBuffer);
        
        assertEquals(1, control.getEvents().size());
    }

    @Test
    public void testSerialize() throws IOException {
        DeviceEvent event1 = new DeviceEvent(ACTION_TYPE_1,
                                             START_TIME_1,
                                             INTERVAL_1,
                                             DURATION_1,
                                             EVENT_INDEX_1,
                                             PARAM1_1,
                                             PARAM2_1);

        DeviceEvent event2 = new DeviceEvent(ACTION_TYPE_2,
                                             START_TIME_2,
                                             INTERVAL_2,
                                             DURATION_2,
                                             EVENT_INDEX_2,
                                             PARAM1_2,
                                             PARAM2_2);

        List<DeviceEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        SetEventsControl setEventMessage = new SetEventsControl(events);
        setEventMessage.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(0, Unsigned.getUnsignedByte(byteBuffer)); // version
        assertEquals(2, Unsigned.getUnsignedShort(byteBuffer)); // count
        
        assertEquals(EVENT_INDEX_1.shortValue(), byteBuffer.getShort());
        assertEquals(ACTION_TYPE_1.getActionNumber().shortValue(), byteBuffer.getShort());
        assertEquals(PARAM1_1.shortValue(), byteBuffer.getShort());
        assertEquals(PARAM2_1.shortValue(), byteBuffer.getShort());
        assertEquals(START_TIME_1.getTime(), byteBuffer.getInt() * 1000L);
        assertEquals(INTERVAL_1.shortValue(), byteBuffer.getInt());
        assertEquals(DURATION_1.shortValue(), byteBuffer.getInt());

        assertEquals(EVENT_INDEX_2.shortValue(), byteBuffer.getShort());
        assertEquals(ACTION_TYPE_2.getActionNumber().shortValue(), byteBuffer.getShort());
        assertEquals(PARAM1_2.shortValue(), byteBuffer.getShort());
        assertEquals(PARAM2_2.shortValue(), byteBuffer.getShort());
        assertEquals(START_TIME_2.getTime(), byteBuffer.getInt() * 1000L);
        assertEquals(INTERVAL_2.shortValue(), byteBuffer.getInt());
        assertEquals(DURATION_2.shortValue(), byteBuffer.getInt());
    }
}
