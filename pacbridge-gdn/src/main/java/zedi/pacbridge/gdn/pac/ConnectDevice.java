package zedi.pacbridge.gdn.pac;

import java.io.Serializable;


public class ConnectDevice extends Pac4Device implements Serializable {
    
    static final long serialVersionUID = 10001L;
    
    public ConnectDevice(int softwareVersion) {
        super(softwareVersion);
    }

    public String propertyNamePrefix() {
        return "connectDevice";
    }

    @Override
    public int hardwareVersion() {
        return PAC_HW_CONNECT;
    }
    
    @Override
    public boolean supportsEvents() {
        return (softwareVersion >= 600);
    }

    @Override
    public boolean supportsExtendedAlarms() {
        return (softwareVersion >= 600);
    }

    @Override
    public boolean isConnectDevice() {
        return true;
    }
}
