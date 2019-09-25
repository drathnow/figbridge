package zedi.pacbridge.app.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.net.ActivityTrackable;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.utl.SiteAddress;

public interface Connection extends Comparable<Connection>, ActivityTrackable {
    
    /**
     * Defines the amount of time a connection has to authenticate before it will be closed.
     */
    public static final String AUTHENTICATION_TIMEOUT_PROPERTY_NAME = "connection.authTimeoutSeconds";
    public static final Integer DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS = 60;
    public static final Integer MIN_AUTHENTICATION_TIMEOUT_SECONDS = 5;
    public static final Integer MAX_AUTHENTICATION_TIMEOUT_SECONDS = 300;
    
    public static final String CONNECTION_LOST_NOTIFICATION = "ConnectionLost";
    public static final String CONNECTION_CONNECTED_NOTIFICATION = "ConnectionConnected";
    public static final String CONNECTION_CLOSED_NOTIFICATION = "ConnectionClosed";
    
    public SiteAddress getSiteAddress();
    public InetSocketAddress getRemoteAddress();
    public void addConnectionListener(Object connectionListener);
    public Session newSession();
    public OutgoingRequestSession outgoingRequestSessionForOutgoingRequest(OutgoingRequest outgoingRequest);
    public boolean isActive();
    public void close();
    public void start() throws IOException;
    public void destroy();
    public Integer getMaxSessionLimit();
    public int getBytesReceived();
    public int getBytesTransmitted();
    public Long getLastActivityTime();
    public CallCollisionHandler callCollisionHandler();
    public String getFirmwareVersion();
}