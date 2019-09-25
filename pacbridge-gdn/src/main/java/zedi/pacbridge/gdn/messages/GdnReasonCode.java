package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class GdnReasonCode extends NamedType implements Serializable {

    public static final GdnReasonCode Unknown = new GdnReasonCode("unknown", -1);
    public static final GdnReasonCode Scheduled = new GdnReasonCode("scheduled", 0);
    public static final GdnReasonCode DemandPoll = new GdnReasonCode("demand", 1);
    public static final GdnReasonCode IOModify = new GdnReasonCode("ioModify", 2);
    public static final GdnReasonCode IOWrite = new GdnReasonCode("ioWrite", 3);
    public static final GdnReasonCode AlarmModify = new GdnReasonCode("alarmModify", 4);
    public static final GdnReasonCode AlarmTrigger = new GdnReasonCode("alarmTrigger", 5);
    public static final GdnReasonCode Reserved = new GdnReasonCode("reserved", 31);
    
    private static final long serialVersionUID = 1001;

    private GdnReasonCode(String name, int typeNumber) {
        super(name, typeNumber);
    }
    
    public static GdnReasonCode reascodeForReasonNumber(int reasonNumber) {
        if (reasonNumber == Unknown.getNumber())
            return Unknown;
        if (reasonNumber == Scheduled.getNumber())
            return Scheduled;
        if (reasonNumber == DemandPoll.getNumber())
            return DemandPoll;
        if (reasonNumber == IOModify.getNumber())
            return IOModify;
        if (reasonNumber == IOWrite.getNumber())
            return IOWrite;
        if (reasonNumber == AlarmModify.getNumber())
            return AlarmModify;
        if (reasonNumber == AlarmTrigger.getNumber())
            return AlarmTrigger;
        if (reasonNumber == Reserved.getNumber())
            return Reserved;
        return null;
    }
    
    public static GdnReasonCode reasonCodeForName(String name) {
        if (Unknown.getName().equalsIgnoreCase(name))
            return Unknown;
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
        return null;
    }
}
