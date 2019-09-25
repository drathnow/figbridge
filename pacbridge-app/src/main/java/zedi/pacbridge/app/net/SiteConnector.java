package zedi.pacbridge.app.net;

import zedi.pacbridge.utl.SiteAddress;

public interface SiteConnector {
    /**
     * Returnes a {@code Connection} object, or null if one does not exist or could not be 
     * created.  The implemenation must determine the rules for the creation of a connection object.
     * 
     * @param siteAddress
     * @return {@link Connection} or null if one does not exist or could not be created.
     */
    public Connection connectionForSiteAddress(SiteAddress siteAddress);
}
