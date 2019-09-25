package zedi.pacbridge.app.net;

import zedi.pacbridge.utl.SiteAddress;

public class SiteDisconnectedAttachment extends SiteConnectedAttachment {
    private int bytesReceived;
    private int byteTransmitted;
    
    public SiteDisconnectedAttachment(SiteAddress siteAddress, String bridgeInstanceName, String ipAddress, int bytesReceived, int byteTransmitted) {
        super(siteAddress, bridgeInstanceName, ipAddress, null);
        this.bytesReceived = bytesReceived;
        this.byteTransmitted = byteTransmitted;
    }

    public int getBytesReceived() {
        return bytesReceived;
    }

    public int getByteTransmitted() {
        return byteTransmitted;
    }
}
