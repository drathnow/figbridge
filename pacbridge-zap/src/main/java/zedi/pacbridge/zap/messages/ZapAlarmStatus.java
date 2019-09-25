package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import zedi.pacbridge.net.AlarmStatus;
import zedi.pacbridge.utl.NamedType;



public class ZapAlarmStatus extends NamedType implements AlarmStatus, Serializable {

    public static final ZapAlarmStatus OK = new ZapAlarmStatus("OK", 0);
    public static final ZapAlarmStatus Low = new ZapAlarmStatus("Low", 1);
    public static final ZapAlarmStatus LowLow = new ZapAlarmStatus("LowLow", 2);
    public static final ZapAlarmStatus High = new ZapAlarmStatus("High", 3);
    public static final ZapAlarmStatus HighHigh = new ZapAlarmStatus("HighHigh", 4);
    public static final ZapAlarmStatus RTUTimeout = new ZapAlarmStatus("RtuTimeout", 5);
    public static final ZapAlarmStatus RTUError = new ZapAlarmStatus("RtuError", 6);
    public static final ZapAlarmStatus RTUBackoff = new ZapAlarmStatus("RtuBackoff", 7);
    public static final ZapAlarmStatus NoData = new ZapAlarmStatus("NoData", 8);
    public static final ZapAlarmStatus OutOfRangeLow = new ZapAlarmStatus("OutOfRangeLow", 9);
    public static final ZapAlarmStatus OutOfRangeHigh = new ZapAlarmStatus("OutOfRangeHigh", 10);
    public static final ZapAlarmStatus RTUOverflow = new ZapAlarmStatus("RtuOverflow", 12);
    public static final ZapAlarmStatus Reserved = new ZapAlarmStatus("reserved", 13);
    public static final ZapAlarmStatus History = new ZapAlarmStatus("History", 14);
    public static final ZapAlarmStatus Empty = new ZapAlarmStatus("Empty", -1);

    static final long serialVersionUID = 1001;
    
    private ZapAlarmStatus(String name, Integer typeNumber) {
        super(name, typeNumber);
    }

    public boolean isDataUnavailable() {
        return getNumber() == RTUOverflow.getNumber() 
                || getNumber() == RTUBackoff.getNumber()
                || getNumber() == RTUError.getNumber()
                || getNumber() == NoData.getNumber()
                || getNumber() == RTUTimeout.getNumber();
    }
        
    public static ZapAlarmStatus alarmStatusForAlarmStatusNumber(int alarmStatusNumber) {
        if (alarmStatusNumber == OK.getNumber())
            return OK;
        if (alarmStatusNumber == Low.getNumber())
            return Low;
        if (alarmStatusNumber == LowLow.getNumber())
            return LowLow;
        if (alarmStatusNumber == High.getNumber())
            return High;
        if (alarmStatusNumber == HighHigh.getNumber())
            return HighHigh;
        if (alarmStatusNumber == RTUTimeout.getNumber())
            return RTUTimeout;
        if (alarmStatusNumber == RTUError.getNumber())
            return RTUError;
        if (alarmStatusNumber == RTUBackoff.getNumber())
            return RTUBackoff;
        if (alarmStatusNumber == NoData.getNumber())
            return NoData;
        if (alarmStatusNumber == OutOfRangeLow.getNumber())
            return OutOfRangeLow;
        if (alarmStatusNumber == OutOfRangeHigh.getNumber())
            return OutOfRangeHigh;
        if (alarmStatusNumber == RTUOverflow.getNumber())
            return RTUOverflow;
        if (alarmStatusNumber == Reserved.getNumber())
            return Reserved;
        if (alarmStatusNumber == History.getNumber())
            return History;
        if (alarmStatusNumber == Empty.getNumber())
            return Empty;
        return null;
    }
    
    public static ZapAlarmStatus alarmStatusForName(String name) {
        if (OK.getName().equals(name))
            return OK;
        if (Low.getName().equals(name))
            return Low;
        if (LowLow.getName().equals(name))
            return LowLow;
        if (High.getName().equals(name))
            return High;
        if (HighHigh.getName().equals(name))
            return HighHigh;
        if (RTUTimeout.getName().equals(name))
            return RTUTimeout;
        if (RTUError.getName().equals(name))
            return RTUError;
        if (RTUBackoff.getName().equals(name))
            return RTUBackoff;
        if (NoData.getName().equals(name))
            return NoData;
        if (OutOfRangeLow.getName().equals(name))
            return OutOfRangeLow;
        if (OutOfRangeHigh.getName().equals(name))
            return OutOfRangeHigh;
        if (RTUOverflow.getName().equals(name))
            return RTUOverflow;
        if (Reserved.getName().equals(name))
            return Reserved;
        if (History.getName().equals(name))
            return History;
        if (Empty.getName().equals(name))
            return Empty;
        return null;
    }

}