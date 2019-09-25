package zedi.pacbridge.gdn.messages;

import zedi.pacbridge.gdn.GdnAlarmStatus;

public class AlarmStatusFactory {

    public GdnAlarmStatus alarmStatusForAlarmStatusNumber(int alarmStatusNumber) {
        return GdnAlarmStatus.alarmStatusForAlarmStatusNumber(alarmStatusNumber);
    }
}
