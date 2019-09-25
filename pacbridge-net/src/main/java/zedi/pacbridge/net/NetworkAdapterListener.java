package zedi.pacbridge.net;

import java.net.SocketAddress;

import zedi.pacbridge.utl.SiteAddress;

public interface NetworkAdapterListener {
    public void handleCloseEvent();
    public void handleUnexpectedCloseEvent(Exception e, SiteAddress siteAddress, SocketAddress address, String message);
}
