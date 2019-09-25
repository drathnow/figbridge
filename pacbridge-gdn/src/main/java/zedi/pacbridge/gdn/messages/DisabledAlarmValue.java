package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.gdn.AlarmType;

public class DisabledAlarmValue extends ExtendedAlarmValue implements AlarmValue, Serializable {
    private static final long serialVersionUID = 1001L;

    public DisabledAlarmValue(AlarmType alarmType) {
        super(alarmType, false, null, null, VARIABLE_FIXED_SIZE, FIXED_SIZE);
    }

}
