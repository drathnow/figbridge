package zedi.pacbridge.gdn;

import java.io.Serializable;

/**
 * AlarmType defines the types of alarms that can be set on a PAC.  These values are set on
 * the server side and sent to the PAC.  The alarm statuses sent to the server from the PAC are
 * define in the <code>AlarmStatus</code> enumeration.
 *
 */
public class AlarmType implements Serializable {
    public static final AlarmType Low = new AlarmType("low", 1);
    public static final AlarmType LowLow = new AlarmType("lowLow", 2);
    public static final AlarmType High = new AlarmType("high", 3);
    public static final AlarmType HighHigh = new AlarmType("highHigh", 4);
    public static final AlarmType Hysterisis = new AlarmType("hysteresis", 5);
    public static final AlarmType DataUnavailable = new AlarmType("dataUnavailable", 6);
    public static final AlarmType DynamicReporting = new AlarmType("dynamicReporting", 129);

    private String name;
    private Integer typeNumber;

    private AlarmType(String name, Integer typeNumber) {
        this.typeNumber = typeNumber;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getTypeNumber() {
        return typeNumber;
    }

    @Override
    public String toString() {
        return getName() + " (" + getTypeNumber() + ")";
    }

    public static AlarmType[] allAlarmTypes() {
        AlarmType[] types = new AlarmType[6];
        types[0] = AlarmType.Low;
        types[1] = AlarmType.LowLow;
        types[2] = AlarmType.High;
        types[3] = AlarmType.HighHigh;
        types[4] = AlarmType.Hysterisis;
        types[5] = AlarmType.DataUnavailable;
        return types;
    }

    public static AlarmType alarmTypeForTypeNumber(int typeNumber) {
        if (typeNumber == Low.typeNumber)
            return Low;
        if (typeNumber == LowLow.typeNumber)
            return LowLow;
        if (typeNumber == High.typeNumber)
            return High;
        if (typeNumber == HighHigh.typeNumber)
            return HighHigh;
        if (typeNumber == Hysterisis.typeNumber)
            return Hysterisis;
        if (typeNumber == DataUnavailable.typeNumber)
            return DataUnavailable;
        if (typeNumber == DynamicReporting.typeNumber)
            return DynamicReporting;
        return null;
    }

    public static AlarmType alarmTypeForName(String name) {
        if (Low.name.equals(name))
            return Low;
        if (LowLow.name.equals(name))
            return LowLow;
        if (High.name.equals(name))
            return High;
        if (HighHigh.name.equals(name))
            return HighHigh;
        if (Hysterisis.name.equals(name))
            return Hysterisis;
        if (DataUnavailable.name.equals(name))
            return DataUnavailable;
        if (DynamicReporting.name.equals(name))
            return DynamicReporting;
        return null;
    }
}
