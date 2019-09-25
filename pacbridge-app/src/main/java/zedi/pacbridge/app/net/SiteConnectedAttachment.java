package zedi.pacbridge.app.net;

import zedi.pacbridge.utl.SiteAddress;

public class SiteConnectedAttachment {
    private SiteAddress siteAddress;
    private String ipAddress;
    private String bridgeInstance;
    private String firmwareVersion;
    
    public SiteConnectedAttachment(SiteAddress siteAddress, String bridgeInstance, String ipAddress, String firmwareVersion) {
        this.siteAddress = siteAddress;
        this.bridgeInstance = bridgeInstance;
        this.ipAddress = ipAddress;
        this.firmwareVersion = firmwareVersion;
    }

    public SiteAddress getSiteAddress() {
        return siteAddress;
    }

    public String getBridgeInstance() {
        return bridgeInstance;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
}
