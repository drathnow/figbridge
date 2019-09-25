package zedi.pacbridge.net;

import zedi.pacbridge.utl.SiteAddress;

public class ConnectionInformation {

    private SiteAddress siteAddress;
    private Integer remotePortNumber;
    
    public ConnectionInformation(SiteAddress siteAddress, Integer remotePortNumber) {
        this.siteAddress = siteAddress;
        this.remotePortNumber = remotePortNumber;
    }
    
    public ConnectionInformation(SiteAddress siteAddress) {
        this(siteAddress, 0);
    }
    
    public Integer getRemotePortNumber() {
        return remotePortNumber;
    }
    
    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
}
