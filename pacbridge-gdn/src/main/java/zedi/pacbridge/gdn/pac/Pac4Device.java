package zedi.pacbridge.gdn.pac;




public class Pac4Device extends AbstractPacDevice {

    static final long serialVersionUID = 10001L;

    protected Pac4Device() {
    }
    
    protected Pac4Device(int aSoftwareVersion) {
        super(aSoftwareVersion);
    }

    public int numberOfScheduledEvents() {
        return PacDevice.BASE_COUNT;
    }

    public int hardwareVersion() {
        return PAC_HW_PAC4;
    }
    
    public boolean supportsPacRunLevel() {
        return true;
    }

    public boolean supportsBlobIoPoints() {
        return true;
    }
    
    public boolean supportsSiteIoPointMonitoring() {
        return true;
    }    

    public boolean supportsContextMessages() {
        return true;
    }
    
    public boolean supportsLowPowerMode() {
        return true;
    }
    
    public boolean supportsDataUnavailableAlarms() {
        return true;
    }

    public boolean supportsDeadBandAlarms() {
        return false;
    }

    public boolean supportsDynamicDataReporting() {
        return false;
    }

    public boolean supportsEventSchedules() {
        return true;    }

    public boolean supportsIoRefresh() {
        return true;
    }

    public boolean supportsOTAD() {
        return true;
    }

    public boolean supportsPacConsole() {
        return true;
    }

    public boolean supportsRtuEventReports() {
        return true;
    }

    public boolean supportsSetAlarmsMessage() {
        return true;
    }

    public boolean supportsSiteAlarms() {
        return true;
    }

    public String propertyNamePrefix() {
        return "pac4Device";
    }

    @Override
    public boolean supportsEvents() {
        return (softwareVersion >= 600);
    }

    @Override
    public boolean supportsExtendedAlarms() {
        return (softwareVersion >= 600);
    }
}
