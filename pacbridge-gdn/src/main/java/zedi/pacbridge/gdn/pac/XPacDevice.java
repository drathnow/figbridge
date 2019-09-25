package zedi.pacbridge.gdn.pac;

import java.io.Serializable;


public class XPacDevice extends Pac4Device implements Serializable {
    
    static final long serialVersionUID = 10001L;
    
    public XPacDevice(int softwareVersion) {
        super(softwareVersion);
    }

    public String propertyNamePrefix() {
        return "XPac";
    }

    @Override
    public int hardwareVersion() {
        return PAC_HW_XPAC;
    }
    
    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public boolean supportsExtendedAlarms() {
        return true;
    }

    @Override
    public boolean supportsOTAD() {
        return false;
    }
    
    @Override
    public boolean isConnectDevice() {
        return false;
    }
}
