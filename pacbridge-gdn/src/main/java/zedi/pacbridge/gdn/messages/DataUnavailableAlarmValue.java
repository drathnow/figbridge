package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.gdn.AlarmType;

public class DataUnavailableAlarmValue extends ExtendedAlarmValue implements AlarmValue, Serializable {
    private static final long serialVersionUID = 1001L;
    
    public DataUnavailableAlarmValue(boolean enabled) {
        super(AlarmType.DataUnavailable, enabled, null, null, null, null);
    }
}
