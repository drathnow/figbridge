package zedi.pacbridge.net.auth;

import zedi.pacbridge.utl.SiteAddress;

public interface AuthenticationDelegate {
    public static final String JNDI_NAME = "java:global/AuthenticationDelegate";
    public Integer systemId();
    public boolean hasOutgoingDataRequests(SiteAddress siteAddress);
}