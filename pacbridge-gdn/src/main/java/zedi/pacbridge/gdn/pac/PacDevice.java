package zedi.pacbridge.gdn.pac;


import java.io.Serializable;


public abstract class PacDevice implements Serializable {
    
    public static final int PAC_VERSION_260 = 260;
    public static final int PAC_VERSION_270 = 270;
    public static final int PAC_VERSION_280 = 280;
    public static final int PAC_VERSION_310 = 310;
    public static final int PAC_HW_PAC2 = 2;
    public static final int PAC_HW_PAC3 = 3;
    public static final int PAC_HW_PAC4 = 4;
    public static final int PAC_HW_XPAC = 5;
    public static final int PAC_HW_CONNECT = 6;
    public static final int PAC_SW_PAC2_BASE_VERSION = 200;
    public static final int PAC_SW_PAC3_BASE_VERSION = 300;
    public static final int PAC_SW_PAC4_BASE_VERSION = 400;
    public static final int PAC_SW_XPAC_BASE_VERSION = 500;
    public static final int PAC_SW_ZEDPAC_BASE_VERSION = 600;
    public static final int RUN_LEVEL_RUN_ACTIVE = 0;  
    public static final int RUN_LEVEL_RUN_IDLE = 1;
    
    protected int softwareVersion;

    public static final int BASE_COUNT = 12;

    public static final int EXTENDED_COUNT = 16;
    public static final int DEFAULT_CMD_TRACKER_TIMEOUT = 60;

    protected PacDevice() {
    }
    
    protected PacDevice(int aSoftwareVersion) {
        softwareVersion = aSoftwareVersion;
    }

    public int softwareVersion() {
        return softwareVersion;
    }
    
    public abstract String propertyNamePrefix();
    public abstract int numberOfScheduledEvents();
    public abstract boolean supportsOTAD();
    public abstract boolean supportsIoRefresh();
    public abstract boolean supportsRtuEventReports();
    public abstract boolean supportsDeadBandAlarms();
    public abstract boolean supportsDataUnavailableAlarms();
    public abstract boolean supportsBlobIoPoints();
    public abstract boolean supportsSetAlarmsMessage();
    public abstract boolean supportsDynamicDataReporting();
    public abstract boolean supportsContextMessages();
    public abstract boolean supportsEventSchedules();
    public abstract boolean supportsPacConsole();
    public abstract boolean supportsPacRunLevel();
    public abstract boolean supportsSiteAlarms();
    public abstract boolean supportsSiteIoPointMonitoring();
    public abstract boolean supportsLowPowerMode();
    public abstract boolean supportsExtendedAlarms();
    public abstract boolean supportsEvents();
    public abstract int hardwareVersion();
    public abstract boolean isConnectDevice();
    public abstract boolean supportsHighFrequencyData();

    public static PacDevice pacDeviceForSoftwareVersion(int softwareVersion) {
        switch(softwareVersion / 100) {
            case PAC_HW_PAC3 : 
                return new Pac3Device(softwareVersion);
            case PAC_HW_PAC4 :
                return new Pac4Device(softwareVersion);
            case PAC_HW_XPAC : 
                return new XPacDevice(softwareVersion);
            case PAC_HW_CONNECT :
                return new ConnectDevice(softwareVersion);
            default: return new Pac2Device(softwareVersion);
        }
    }

}
