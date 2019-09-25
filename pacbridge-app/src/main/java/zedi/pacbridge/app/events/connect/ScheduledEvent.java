package zedi.pacbridge.app.events.connect;

import org.jdom2.Element;

import zedi.pacbridge.gdn.messages.NextEventTime;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.strategies.DueTimeStrategy;
import zedi.pacbridge.utl.strategies.NextDueTimeStrategy;

public class ScheduledEvent implements NextEventTime {
    public static final Long AWAKE_WINDOW_INMILLIS = 30000L;

    public static final String ROOT_ELEMENT_NAME = "DeviceEventBasic";
    public static final String START_TIME_TAG = "StartTime";
    public static final String INTERVAL_TAG = "Interval";
    
    private DueTimeStrategy dueTimeStrategy;
    private SystemTime systemTime;
    private Long startTime;
    private Integer interval;
    
    ScheduledEvent(NextDueTimeStrategy dueTimeStrategy) {
        this.dueTimeStrategy = dueTimeStrategy;
        this.systemTime = new SystemTime();
    }

    public ScheduledEvent(Long startTime, Integer intervalSeconds) {
        this(new NextDueTimeStrategy(startTime, intervalSeconds, AWAKE_WINDOW_INMILLIS));
    }
    
    /**
     * Calculates the next event time from the current system time.
     *
     * @return long - the next due time in milliseconds. Utilities.DISTANT_FUTURE_INMILLIS
     * if no future event is due. If the next awake time
     * is within AWAKE_WINDOW_INMILLIS of the current time, then the current time
     * is returned.
     */
    @Override
    public Long nextEventTime() {
        return dueTimeStrategy.nextDueTimeFromTime(systemTime.getCurrentTime());
    }
    
    public Element asElement() {
        return new Element(ROOT_ELEMENT_NAME)
                        .addContent(new Element(START_TIME_TAG).setText(Long.toString(startTime/1000L)))
                        .addContent(new Element(INTERVAL_TAG).setText(interval.toString()));
    }
    
    void setSystemTime(SystemTime systemTime) {
        this.systemTime = systemTime;
    }
    
    public static ScheduledEvent deviceEventForElement(Element element) {
        Long startTime = Long.valueOf(element.getChildText(START_TIME_TAG)) * 1000L;
        Integer interval = Integer.valueOf(element.getChildText(INTERVAL_TAG));
        NextDueTimeStrategy dueTimeStrategy = new NextDueTimeStrategy(startTime, interval, AWAKE_WINDOW_INMILLIS);
        return new ScheduledEvent(dueTimeStrategy);
    }
}
