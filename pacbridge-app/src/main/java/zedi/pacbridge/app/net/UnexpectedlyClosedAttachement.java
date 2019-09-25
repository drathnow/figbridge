package zedi.pacbridge.app.net;

import java.net.SocketAddress;

import zedi.pacbridge.utl.SiteAddress;

public class UnexpectedlyClosedAttachement {
    private Exception exception;
    private SiteAddress siteAddress;
    private SocketAddress socketAddress;
    private String message;
    
    public UnexpectedlyClosedAttachement(Exception exception, SiteAddress siteAddress, SocketAddress address, String message) {
        this.exception = exception;
        this.siteAddress = siteAddress;
        this.socketAddress = address;
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public SiteAddress getSiteAddress() {
        return siteAddress;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public String getMessage() {
        return message;
    }
}
