package zedi.pacbridge.gdn;

import java.io.Serializable;

/**
 * The AlarmStatus enumeration defines the alarm status sent from the PAC to the server.  These 
 * values match those on the ZedPAC and are taken from the following C include file:
 * 
 * #define GDN_P_S_NORMAL              0        // Point data is valid, no alarm status
 * #define GDN_P_S_LO                  1        // Point is below Lo threshold
 * #define GDN_P_S_LO_LO               2        // Point is below LoLo threshold
 * #define GDN_P_S_HI                  3        // Point is above Hi threshold
 * #define GDN_P_S_HI_HI               4        // Point is above HiHi threshold
 * #define GDN_P_S_RTU_TIMEOUT         5        // Current data is unavailable, rtu timeout
 * #define GDN_P_S_RTU_ERROR           6        // Current data is unavailable, rtu error
 * #define GDN_P_S_RTU_BACKOFF         7        // Current data is unavailable, rtu backoff
 * #define GDN_P_S_RTU_NODATA          8        // Current data is unavailable
 * #define GDN_P_S_OOR_LO              9        // Point is out-ot-range lo
 * #define GDN_P_S_OOR_HI              10       // Point is out-ot-range hi
 * #define GDN_P_S_DEADBAND            11       // Point has exceeded deadband level
 * #define GDN_P_S_RTU_OVERFLOW        12       // Current data is unavailable, too large
 * #define GDN_P_S_RESERVED            13
 * #define GDN_P_S_RTU_HISTORY         14       // Point is historical data entry
 *
 */
public class GdnAlarmStatus implements Serializable {

    public static final GdnAlarmStatus OK = new GdnAlarmStatus("OK", 0);
    public static final GdnAlarmStatus Low = new GdnAlarmStatus("low", 1);
    public static final GdnAlarmStatus LowLow = new GdnAlarmStatus("lowLow", 2);
    public static final GdnAlarmStatus High = new GdnAlarmStatus("high", 3);
    public static final GdnAlarmStatus HighHigh = new GdnAlarmStatus("highHigh", 4);
    public static final GdnAlarmStatus RTUTimeout = new GdnAlarmStatus("rtuTimeout", 5);
    public static final GdnAlarmStatus RTUError = new GdnAlarmStatus("rtuError", 6);
    public static final GdnAlarmStatus RTUBackoff = new GdnAlarmStatus("rtuBackoff", 7);
    public static final GdnAlarmStatus NoData = new GdnAlarmStatus("noData", 8);
    public static final GdnAlarmStatus OutOfRangeLow = new GdnAlarmStatus("outOfRangeLow", 9);
    public static final GdnAlarmStatus OutOfRangeHigh = new GdnAlarmStatus("outOfRangeHigh", 10);
    public static final GdnAlarmStatus RTUOverflow = new GdnAlarmStatus("rtuOverflow", 12);
    public static final GdnAlarmStatus Reserved = new GdnAlarmStatus("reserved", 13);
    public static final GdnAlarmStatus History = new GdnAlarmStatus("history", 14);
    public static final GdnAlarmStatus NaN = new GdnAlarmStatus("nan", -1);

    static final long serialVersionUID = 1001;
    
    private String name;
    private Integer typeNumber;
    
    private GdnAlarmStatus(String name, Integer typeNumber) {
        this.name = name;
        this.typeNumber = typeNumber;
    }

    public String getName() {
        return name;
    }

    public Integer getTypeNumber() {
        return typeNumber;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isDataUnavailable() {
        return typeNumber == RTUOverflow.getTypeNumber() 
                || typeNumber == RTUBackoff.getTypeNumber()
                || typeNumber == RTUError.getTypeNumber()
                || typeNumber == NoData.getTypeNumber()
                || typeNumber == RTUTimeout.getTypeNumber();
    }
        
    public static GdnAlarmStatus alarmStatusForAlarmStatusNumber(int alarmStatusNumber) {
        if (alarmStatusNumber == OK.getTypeNumber())
            return OK;
        if (alarmStatusNumber == Low.getTypeNumber())
            return Low;
        if (alarmStatusNumber == LowLow.getTypeNumber())
            return LowLow;
        if (alarmStatusNumber == High.getTypeNumber())
            return High;
        if (alarmStatusNumber == HighHigh.getTypeNumber())
            return HighHigh;
        if (alarmStatusNumber == RTUTimeout.getTypeNumber())
            return RTUTimeout;
        if (alarmStatusNumber == RTUError.getTypeNumber())
            return RTUError;
        if (alarmStatusNumber == RTUBackoff.getTypeNumber())
            return RTUBackoff;
        if (alarmStatusNumber == NoData.getTypeNumber())
            return NoData;
        if (alarmStatusNumber == OutOfRangeLow.getTypeNumber())
            return OutOfRangeLow;
        if (alarmStatusNumber == OutOfRangeHigh.getTypeNumber())
            return OutOfRangeHigh;
        if (alarmStatusNumber == RTUOverflow.getTypeNumber())
            return RTUOverflow;
        if (alarmStatusNumber == Reserved.getTypeNumber())
            return Reserved;
        if (alarmStatusNumber == History.getTypeNumber())
            return History;
        if (alarmStatusNumber == NaN.getTypeNumber())
            return NaN;
        return null;
    }
    
    public static GdnAlarmStatus alarmStatusForName(String name) {
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
        if (NaN.getName().equals(name))
            return NaN;
        return null;
    }

}