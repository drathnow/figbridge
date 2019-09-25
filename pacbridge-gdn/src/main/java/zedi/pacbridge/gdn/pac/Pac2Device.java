package zedi.pacbridge.gdn.pac;



public class Pac2Device extends AbstractPacDevice {

    static final long serialVersionUID = 10001L;

    private Pac2Device() {
    }
    
    public Pac2Device(int aSoftwareVersion) {
        super(aSoftwareVersion);
    }

    public boolean supportsOTAD() {
        return (softwareVersion < 270) ? false : true;
    }
    
    public int hardwareVersion() {
        return PAC_HW_PAC2;
    }

    public boolean supportsIoRefresh() {
        return (softwareVersion < 280) ? false : true;
    }

    public boolean supportsRtuEventReports() {
        if (softwareVersion < 270)
            return false;
        else if (softwareVersion >= 280)
            return true;
        return false;
    }

    public boolean supportsSiteIoPointMonitoring() {
        return true;
    }

    public boolean supportsLowPowerMode() {
        return true;
    }
    
    public boolean supportsDeadBandAlarms() {
        return false;
    }

    public boolean supportsDataUnavailableAlarms() {
        return (softwareVersion < 270) ? false : true;
    }

    public boolean supportsBlobIoPoints() {
        if (softwareVersion < 270)
            return false;
        else if (softwareVersion > 272)
            return true;
        return false;
    }

    public boolean supportsSetAlarmsMessage() {
        return (softwareVersion >= 272) ? true : false;
    }

    public boolean supportsDynamicDataReporting() {
        return false;
    }

    public int numberOfScheduledEvents() {
        return PacDevice.BASE_COUNT;
    }

    public boolean supportsContextMessages() {
        return (softwareVersion > 272) ? true : false;
    }

    public boolean supportsEventSchedules() {
        return (softwareVersion >= 272) ? true : false;
    }

    public boolean supportsPacConsole() {
        return (softwareVersion > 270) ? true : false;
    }

    public boolean supportsPacRunLevel() {
        return (softwareVersion >= 272) ? true : false;
    }

    public boolean supportsSiteAlarms() {
        return (softwareVersion >= 280) ? true : false;
    }

    public String propertyNamePrefix() {
        return "pac2Device";
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public boolean supportsExtendedAlarms() {
        return false;
    }
}
