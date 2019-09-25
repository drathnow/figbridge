package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import zedi.pacbridge.net.ReasonCode;
import zedi.pacbridge.utl.NamedType;


public class ZapReasonCode extends NamedType implements ReasonCode, Serializable {
    private static final long serialVersionUID = 1001;

    static final String RESERVED_NAME = "Reserved";
    static final String ALARM_TRIGGER_NAME = "AlarmTrigger";
    static final String ALARM_MODIFY_NAME = "AlarmModify";
    static final String IO_WRITE_NAME = "IOWrite";
    static final String IO_MODIFY_NAME = "IOModify";
    static final String DEMAND_POLL_NAME = "Demand";
    static final String SCHEDULED_NAME = "Scheduled";
    static final String UNKNOWN_NAME = "Unknown";

    static final int UNKNOWN_NUMBER = -1;
    static final int SCHEDULED_NUMBER = 0;
    static final int DEMAND_POLL_NUMBER = 1;
    static final int IO_MODIFY_NUMBER = 2;
    static final int IO_WRITE_NUMBER = 3;
    static final int ALARM_MODIFY_NUMBER = 4;
    static final int ALARM_TRIGGER_NUMBER = 5;
    static final int RESERVED_NUMBER = 31;
        
    public static final ZapReasonCode Unknown = new ZapReasonCode(UNKNOWN_NAME, UNKNOWN_NUMBER, false);
    public static final ZapReasonCode Scheduled = new ZapReasonCode(SCHEDULED_NAME, SCHEDULED_NUMBER, false);
    public static final ZapReasonCode DemandPoll = new ZapReasonCode(DEMAND_POLL_NAME, DEMAND_POLL_NUMBER, true);
    public static final ZapReasonCode IOModify = new ZapReasonCode(IO_MODIFY_NAME, IO_MODIFY_NUMBER, true);
    public static final ZapReasonCode IOWrite = new ZapReasonCode(IO_WRITE_NAME, IO_WRITE_NUMBER, true);
    public static final ZapReasonCode AlarmModify = new ZapReasonCode(ALARM_MODIFY_NAME, ALARM_MODIFY_NUMBER, true);
    public static final ZapReasonCode AlarmTrigger = new ZapReasonCode(ALARM_TRIGGER_NAME, ALARM_TRIGGER_NUMBER, true);
    public static final ZapReasonCode Reserved = new ZapReasonCode(RESERVED_NAME, RESERVED_NUMBER, false);

    private boolean highPriority;
    
    private ZapReasonCode(String name, int typeNumber, boolean isHighPriority) {
        super(name, typeNumber);
        this.highPriority = isHighPriority;
    }

    @Override
    public boolean isHighPriority() {
        return highPriority;
    }

    public static ZapReasonCode reasonCodeForReasonNumber(int reasonNumber) {
        switch (reasonNumber) {
            case SCHEDULED_NUMBER :
                return Scheduled;
            case DEMAND_POLL_NUMBER :
                return DemandPoll;
            case IO_MODIFY_NUMBER :
                return IOModify;
            case IO_WRITE_NUMBER :
                return IOWrite;
            case ALARM_MODIFY_NUMBER :
                return AlarmModify;
            case ALARM_TRIGGER_NUMBER :
                return AlarmTrigger;
            case RESERVED_NUMBER :
                return Reserved;
        }
        return Unknown;
    }

    public static ZapReasonCode reasonCodeForName(String name) {
        if (Scheduled.getName().equals(name))
            return Scheduled;
        if (DemandPoll.getName().equals(name))
            return DemandPoll;
        if (IOModify.getName().equals(name))
            return IOModify;
        if (Scheduled.getName().equals(name))
            return Scheduled;
        if (IOWrite.getName().equals(name))
            return IOWrite;
        if (AlarmModify.getName().equals(name))
            return AlarmModify;
        if (AlarmTrigger.getName().equals(name))
            return AlarmTrigger;
        if (Reserved.getName().equals(name))
            return Reserved;
        return Unknown;
    }
}
