package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.Utilities;


public class DeviceEventTest extends BaseTestCase {

    public static final Integer EVENT_INDEX1 = 1;
    public static final Integer EVENT_INDEX2 = 2;
    public static final EventAction ACTION_TYPE = EventAction.Report;
    public static final Integer PARAM1 = 3;
    public static final Integer PARAM2 = 5;
    public static final Date START_TIME = new Date();
    public static final Integer INTERVAL = 10;
    public static final Integer DURATION = 20;
    
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-DDD HH:mm:ss");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
    
    public static final String EVENT_STRING1 = "" + EVENT_INDEX1 
            + ','
            + ACTION_TYPE.getActionNumber()
            + ','
            + PARAM1
            + ','
            + PARAM2
            + ','
            + (START_TIME.getTime() / 1000L)
            + ','
            + INTERVAL
            + ','
            + DURATION;
    
    public static final String EVENT_STRING2 = "" + EVENT_INDEX2 
            + ','
            + ACTION_TYPE.getActionNumber()
            + ','
            + PARAM1
            + ','
            + PARAM2
            + ','
            + (START_TIME.getTime() / 1000L)
            + ','
            + INTERVAL
            + ','
            + DURATION;
    
    
    @Test
    public void testNextEventTime() throws Exception {
        SystemTime mockSystemTime = mock(SystemTime.class);
        long currentTime = simpleDateFormat.parse("13:56:00 000").getTime();
        long expectedTime = simpleDateFormat.parse("14:00:00 000").getTime();
        long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        long interval = TimeUnit.MINUTES.toMillis(10);
        
        given(mockSystemTime.getCurrentTime()).willReturn(currentTime);

        DeviceEvent eventDetail = new DeviceEvent(null, 
                                                  new Date(startTime), 
                                                  (int)(interval/1000L), 
                                                  0, 
                                                  0, 
                                                  0, 
                                                  0);
        eventDetail.setSystemTime(mockSystemTime);
        
        eventDetail.setStartTime(new Date(startTime));
        eventDetail.setIntervalSeconds((int)(interval/1000L));
        
        long resultTime = eventDetail.nextEventTime();
        String msg = "Exp: " 
            + formatter.format(new Date(expectedTime))
            + " Result: " 
            + formatter.format(resultTime);
        assertEquals(msg, expectedTime, resultTime);
    }
    
    @Test
    public void nextEventTimeFromTimeWithExpiredDuration() throws Exception {
        long currentTime = simpleDateFormat.parse("22:49:00 000").getTime();
        long expectedTime = Utilities.DISTANT_FUTURE_INMILLIS;
        long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        long interval = TimeUnit.MINUTES.toMillis(120);
        long duration = TimeUnit.HOURS.toMillis(8);

        assertCorrectTimes(expectedTime, currentTime, startTime, interval, duration);
    }
    
    @Test
    public void nextEventTimeFromTime3() throws Exception {
        long currentTime = simpleDateFormat.parse("13:49:00 000").getTime();
        long expectedTime = simpleDateFormat.parse("14:0:00 000").getTime();
        long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        long interval = TimeUnit.MINUTES.toMillis(120);
        
        assertCorrectTimes(expectedTime, currentTime, startTime, interval);
    }
    
    @Test
    public void nextEventTimeFromTime2() throws Exception {
        long currentTime = simpleDateFormat.parse("13:49:00 000").getTime();
        long expectedTime = simpleDateFormat.parse("13:50:00 000").getTime();
        long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        long interval = TimeUnit.MINUTES.toMillis(10);
        
        assertCorrectTimes(expectedTime, currentTime, startTime, interval);
    }

    @Test
    public void nextEventTimeFromTime1() throws Exception {
        long currentTime = simpleDateFormat.parse("13:56:00 000").getTime();
        long expectedTime = simpleDateFormat.parse("14:00:00 000").getTime();
        long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        long interval = TimeUnit.MINUTES.toMillis(10);
        
        assertCorrectTimes(expectedTime, currentTime, startTime, interval);
    }
    
    private void assertCorrectTimes(long expectedTime, long currentTime, long startTime, long interval, long duration) {

        DeviceEvent eventDetail = new DeviceEvent(null, 
                                                    new Date(startTime), 
                                                    (int)(interval/1000L), 
                                                    (int)(duration/1000L), 
                                                    0, 
                                                    0, 
                                                    0);
        eventDetail.setStartTime(new Date(startTime));
        eventDetail.setIntervalSeconds((int)(interval/1000L));
        eventDetail.setDurationSeconds((int)(duration/1000L));
        
        long resultTime = eventDetail.nextEventTimeFromTime(currentTime);
        String msg = "Exp: " 
            + formatter.format(new Date(expectedTime))
            + " Result: " 
            + formatter.format(resultTime);
        assertEquals(msg, expectedTime, resultTime);
    }

    
    private void assertCorrectTimes(long expectedTime, long currentTime, long startTime, long interval) {
        assertCorrectTimes(expectedTime, currentTime, startTime, interval, 0);
    }
   
    @Test
    public void testDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.putShort(EVENT_INDEX1.shortValue());
        byteBuffer.putShort(ACTION_TYPE.getActionNumber().shortValue());
        byteBuffer.putShort(PARAM1.shortValue());
        byteBuffer.putShort(PARAM2.shortValue());
        byteBuffer.putInt((int)(START_TIME.getTime() / 1000L));
        byteBuffer.putInt(INTERVAL);
        byteBuffer.putInt(DURATION);
        byteBuffer.flip();
        
        DeviceEvent event = DeviceEvent.deviceEventFromByteBuffer(byteBuffer);
        
        assertEquals(EVENT_INDEX1, event.getEventIndex());
        assertEquals(ACTION_TYPE, event.getEventAction());
        assertEquals(PARAM1, event.getEventParameter1());
        assertEquals(PARAM2, event.getEventParameter2());
        assertEquals(START_TIME.getTime(), event.getStartTime().getTime(), 1000L);
        assertEquals(INTERVAL, event.getIntervalSeconds());
        assertEquals(DURATION, event.getDurationSeconds());
    }
    
    @Test
    public void testSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        DeviceEvent event = new DeviceEvent(ACTION_TYPE,
                                            START_TIME,
                                            INTERVAL,
                                            DURATION,
                                            EVENT_INDEX1,
                                            PARAM1,
                                            PARAM2);

        event.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(EVENT_INDEX1.shortValue(), byteBuffer.getShort());
        assertEquals(ACTION_TYPE.getActionNumber().shortValue(), byteBuffer.getShort());
        assertEquals(PARAM1.shortValue(), byteBuffer.getShort());
        assertEquals(PARAM2.shortValue(), byteBuffer.getShort());
        assertEquals(START_TIME.getTime() / 1000L, byteBuffer.getInt());
        assertEquals(INTERVAL.intValue(), byteBuffer.getInt());
        assertEquals(DURATION.intValue(), byteBuffer.getInt());
    }
    
    @Test
    public void testSerializeDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        DeviceEvent event = new DeviceEvent(ACTION_TYPE,
                                            START_TIME,
                                            INTERVAL,
                                            DURATION,
                                            EVENT_INDEX1,
                                            PARAM1,
                                            PARAM2);

        event.serialize(byteBuffer);
        byteBuffer.flip();

        DeviceEvent inputEvent = DeviceEvent.deviceEventFromByteBuffer(byteBuffer);
        assertEquals(EVENT_INDEX1, inputEvent.getEventIndex());
        assertEquals(ACTION_TYPE, inputEvent.getEventAction());
        assertEquals(PARAM1, inputEvent.getEventParameter1());
        assertEquals(PARAM2, inputEvent.getEventParameter2());
        assertEquals(START_TIME.getTime(), inputEvent.getStartTime().getTime(), 1000L);
        assertEquals(INTERVAL, inputEvent.getIntervalSeconds());
        assertEquals(DURATION, inputEvent.getDurationSeconds());
    }
}
