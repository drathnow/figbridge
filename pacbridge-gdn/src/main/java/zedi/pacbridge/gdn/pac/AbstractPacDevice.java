package zedi.pacbridge.gdn.pac;


public abstract class AbstractPacDevice extends PacDevice {

    protected AbstractPacDevice() {
    }
    
    protected AbstractPacDevice(int softwareVersion) {
        super(softwareVersion);
    }
    
    @Override
    public boolean isConnectDevice() {
        return false;
    }
    
    public boolean supportsHighFrequencyData() {
        return false;
    }
    
}
