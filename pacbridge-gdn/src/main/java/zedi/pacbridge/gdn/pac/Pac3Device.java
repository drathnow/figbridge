package zedi.pacbridge.gdn.pac;



public class Pac3Device extends AbstractPacDevice {

    static final long serialVersionUID = 10001L;

    public Pac3Device(int aSoftwareVersion) {
        super(aSoftwareVersion);
    }

    public int hardwareVersion() {
        return PAC_HW_PAC3;
    }
    
    public boolean supportsOTAD() {
        return true;
    }

    public boolean supportsIoRefresh() {
        return true;
    }

    public boolean supportsRtuEventReports() {
        return false;
    }
    
    public boolean supportsSiteIoPointMonitoring() {
        return true;
    }
    
    public boolean supportsLowPowerMode() {
        return true;
    }    

    public boolean supportsDeadBandAlarms() {
        return true;
    }

    public boolean supportsDataUnavailableAlarms() {
        return true;
    }
    
    public boolean supportsPacConsole() {
        return true;
    }

    public boolean supportsBlobIoPoints() {
        return false;
    }

    public boolean supportsSetAlarmsMessage() {
        return true;
    }

    public boolean supportsDynamicDataReporting() {
        return true;
    }
    
    public int numberOfScheduledEvents() {
        return PacDevice.EXTENDED_COUNT;
    }

    public boolean supportsContextMessages() {
        return true;
    }

    public boolean supportsEventSchedules() {
        return true;
    }

    public boolean supportsPacRunLevel() {
        return false;
    }

    public boolean supportsSiteAlarms() {
        return true;
    }

    public String propertyNamePrefix() {
        return "pac3Device";
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
