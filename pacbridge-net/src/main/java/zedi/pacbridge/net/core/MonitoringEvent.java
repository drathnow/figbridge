package zedi.pacbridge.net.core;

class MonitoringEvent {
    
    public static enum EventType {ADD, TRIM} ;
    
    private EventType eventType;
    private long eventTime;
    private int coreDispatcherCount;
    private int curentDispatcherCount;

    public MonitoringEvent(EventType eventType, long eventTime, int coreDispatcherCount, int curentDispatcherCount) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.coreDispatcherCount = coreDispatcherCount;
        this.curentDispatcherCount = curentDispatcherCount;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getEventTime() {
        return eventTime;
    }

    public int getCoreDispatcherCount() {
        return coreDispatcherCount;
    }

    public int getCurentDispatcherCount() {
        return curentDispatcherCount;
    }
}
