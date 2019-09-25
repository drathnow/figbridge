package zedi.pacbridge.app.auth;

import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.utl.SiteAddress;


public interface AuthenticationDelegate {

    public static final String JNDI_NAME = "java:global/AuthenticationDelegate";

    public Device deviceForNuid(String identifier);
    public String systemId();
    public boolean hasOutgoingDataRequests(SiteAddress siteAddress);
    public abstract String serverName();

}