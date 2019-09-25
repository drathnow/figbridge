package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnValue;

public class EnabledAlarmValue extends ExtendedAlarmValue implements AlarmValue, Serializable {
    private static final long serialVersionUID = 1001L;

    public EnabledAlarmValue(AlarmType alarmType, GdnValue<?> limitValue, GdnValue<?> hysteresisValue) {
        this(alarmType, limitValue, hysteresisValue, 0, 0);
    }
    
    public EnabledAlarmValue(AlarmType alarmType, 
                                GdnValue<?> limitValue,
                                GdnValue<?> hysteresisValue, 
                                Integer setTimeSeconds, 
                                Integer clearTimeSeconds) {
        super(alarmType, true, limitValue, hysteresisValue, setTimeSeconds, clearTimeSeconds);
    }
}
