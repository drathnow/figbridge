package zedi.pacbridge.zap.messages;

import zedi.pacbridge.utl.NamedType;

public class TimedEventType extends NamedType {
    private static final long serialVersionUID = 1001L;
    public static final int POLL_EVENT_NUMBER = 1;
    public static final int RESERVED_EVENT_NUMBER = 2;
    public static final int REPORT_EVENT_NUMBER = 3;
    public static final int REBOOT_EVENT_NUMBER = 4;
    public static final int TIMESYNCH_EVEN_NUMBER = 5;
    public static final int NETSTATUS_EVENT_NUMBER = 6;
        
    public static final TimedEventType Poll = new TimedEventType("Poll", POLL_EVENT_NUMBER);
    public static final TimedEventType Reserved = new TimedEventType("Reserved", RESERVED_EVENT_NUMBER);
    public static final TimedEventType Report = new TimedEventType("Report", REPORT_EVENT_NUMBER);
    public static final TimedEventType Reboot = new TimedEventType("Reboot", REBOOT_EVENT_NUMBER);
    public static final TimedEventType TimeSync = new TimedEventType("TimeSync", TIMESYNCH_EVEN_NUMBER);
    public static final TimedEventType NetworkStatus = new TimedEventType("NetworkStatus", NETSTATUS_EVENT_NUMBER);
    
    private TimedEventType(String name, Integer number) {
        super(name, number);
    }
    
    public static final TimedEventType timedEventTypeForNumber(Integer number) {
        switch (number) {
            case POLL_EVENT_NUMBER : return Poll;
            case RESERVED_EVENT_NUMBER : return Reserved;
            case REPORT_EVENT_NUMBER : return Report;
            case REBOOT_EVENT_NUMBER : return Reboot;
            case TIMESYNCH_EVEN_NUMBER : return TimeSync;
            case NETSTATUS_EVENT_NUMBER : return NetworkStatus;
        }
        throw new IllegalArgumentException("Unknown timed event type number specified");
    }
    
}
