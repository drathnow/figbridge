package zedi.pacbridge.gdn.pac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zedi.pacbridge.gdn.messages.NextEventTime;
import zedi.pacbridge.utl.Utilities;


public class EventSchedule<TEvent extends NextEventTime> implements Serializable {
    private static final long serialVersionUID = 1001;
    public static final long NO_FUTURE_EVENT = 0x7fffffffffffffffL;
    
    private List<TEvent> deviceEvents = new ArrayList<TEvent>();
    
    public EventSchedule(List<TEvent> deviceEvents) {
        this.deviceEvents = deviceEvents;
    }

    public int size() {
        return deviceEvents.size();
    }

    public List<TEvent> events() {
        return new ArrayList<TEvent>(deviceEvents);
    }
    
    public List<TEvent> nextEventTimes() {
        return new ArrayList<TEvent>(deviceEvents);
    }

    public Long nextEventTime() {
        Iterator<TEvent> iterator = deviceEvents.iterator();
        Long nearestTime = Utilities.DISTANT_FUTURE_INMILLIS;
        while (iterator.hasNext()) {
            long eventTime = iterator.next().nextEventTime();
            if (nearestTime > eventTime)
                nearestTime = eventTime;
        }
        return nearestTime;
    }
}

