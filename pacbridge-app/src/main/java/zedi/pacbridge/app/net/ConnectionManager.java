package zedi.pacbridge.app.net;

import java.util.List;

import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public interface ConnectionManager {
    public void manageConnection(Connection connection);
    public Connection connectionForSiteAddress(SiteAddress siteAddress);
    public Integer currentConnectionCount();
    public <T> List<T> collectConnectionInfo(ConnectionInfoCollector<T> collector);
    public void removeConnectionWithSiteAddress(NuidSiteAddress siteAddress);
}