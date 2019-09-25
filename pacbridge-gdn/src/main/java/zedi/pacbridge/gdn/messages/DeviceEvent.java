package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.Utilities;
import zedi.pacbridge.utl.io.Unsigned;


public class DeviceEvent implements Serializable, NextEventTime {
    static final long serialVersionUID = 1001;
    public static final String TAG_ELEMENT_NAME = "DeviceEvent";
    public static final String TAG_INDEX = "Index";
    public static final String TAG_EVENT_ACTION = "EventAction";
    public static final String TAG_PARAMETER1 = "Parameter1";
    public static final String TAG_PARAMETER2 = "Parameter2";
    public static final String TAG_START_TIME = "StartTime";
    public static final String TAG_INTERVAL = "Interval";
    public static final String TAG_DURATION = "Duration";
    public static final long AWAKE_WINDOW_INMILLIS = 30 * 1000;
    public static int SIZE = 20;

    private EventAction eventAction;
    private Date startTime;
    private Integer intervalSeconds;
    private Integer durationSeconds;
    private Integer eventIndex;
    private Integer eventParameter1;
    private Integer eventParameter2;
    private SystemTime systemTime;
    

    
    public DeviceEvent(EventAction eventAction, 
                        Date startTime, 
                        Integer intervalSeconds, 
                        Integer durationSeconds, 
                        Integer eventIndex, 
                        Integer eventParameter1, 
                        Integer eventParameter2) {
        this.eventAction = eventAction;
        this.startTime = startTime;
        this.intervalSeconds = intervalSeconds;
        this.durationSeconds = durationSeconds;
        this.eventIndex = eventIndex;
        this.eventParameter1 = eventParameter1;
        this.eventParameter2 = eventParameter2;
        this.systemTime = new SystemTime();
    }
    
    public Integer getEventIndex() {
        return eventIndex;
    }

    public void setEventIndex(Integer eventIndex) {
        this.eventIndex = eventIndex;
    }

    public EventAction getEventAction() {
        return eventAction;
    }

    public void setEventAction(EventAction eventAction) {
        this.eventAction = eventAction;
    }

    public Integer getEventParameter1() {
        return eventParameter1;
    }

    public void setEventParameter1(Integer eventParameter) {
        this.eventParameter1 = eventParameter;
    }

    public Integer getEventParameter2() {
        return eventParameter2;
    }

    public void setEventParameter2(Integer eventParameter) {
        this.eventParameter2 = eventParameter;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Long nextEventTime() {
        return nextEventTimeFromTime(systemTime.getCurrentTime());
    }
    
    /**
     * Calculates the next awake interval for the site.  If the next awake time
     * is within AWAKE_WINDOW_INMILLIS of the current time, then the current time
     * is returned.
     * 
     * @param baseTime
     * @return
     */
    public Long nextEventTimeFromTime(long baseTime) {
        if (startTime != null && durationHasExpired(baseTime) == false) {
            if (baseTime <= startTime.getTime() + AWAKE_WINDOW_INMILLIS)
                return startTime.getTime();
            else if (intervalSeconds != 0) {
                long intervalMilliseconds = (intervalSeconds * 1000L);
                long numberOfIntervals = ((baseTime - startTime.getTime() - 1) / intervalMilliseconds) + 1;
                long nextTime = (numberOfIntervals * intervalMilliseconds) + startTime.getTime();
                if (baseTime <= nextTime - intervalMilliseconds + DeviceEvent.AWAKE_WINDOW_INMILLIS)
                    return baseTime;
                else
                    return nextTime;
            }
        }
        return Utilities.DISTANT_FUTURE_INMILLIS;
    }
    
    private boolean durationHasExpired(long baseTime) {
        return durationSeconds == 0 ? false : (startTime.getTime() + (durationSeconds * 1000L) < baseTime);
    }

    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort(eventIndex.shortValue());
        byteBuffer.putShort(eventAction.getActionNumber().shortValue());
        byteBuffer.putShort(eventParameter1.shortValue());
        byteBuffer.putShort(eventParameter2.shortValue());
        byteBuffer.putInt((int)(startTime.getTime()/1000L));
        byteBuffer.putInt(intervalSeconds);
        byteBuffer.putInt(durationSeconds);
    }

    void setSystemTime(SystemTime systemTime) {
        this.systemTime = systemTime;
    }

    public static DeviceEvent deviceEventFromByteBuffer(ByteBuffer byteBuffer) {
        Integer eventIndex = Unsigned.getUnsignedShort(byteBuffer);
        EventAction eventAction = EventAction.eventActionForActionNumber(Unsigned.getUnsignedShort(byteBuffer));
        Integer eventParameter1 = (int)byteBuffer.getShort();
        Integer eventParameter2 = (int)byteBuffer.getShort();
        Date startTime = new Date(byteBuffer.getInt() * 1000L);
        Integer intervalSeconds = byteBuffer.getInt();
        Integer durationSeconds = byteBuffer.getInt();
        return new DeviceEvent(eventAction, startTime, intervalSeconds, durationSeconds, eventIndex, eventParameter1, eventParameter2);
    }
}
