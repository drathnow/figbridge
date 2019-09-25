package zedi.pacbridge.app.net;

import java.net.InetSocketAddress;
import java.util.List;

import zedi.pacbridge.net.ListenerStatus;
import zedi.pacbridge.net.core.ListenerRegistrationAgent;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.PropertyBag;

public interface Network extends SiteConnector, ConnectionRequestHandler {
    public Integer getNumber();
    public Integer maxOutgoingRequestsSessions(); 
    public void start(ListenerRegistrationAgent registrationAgent, NotificationCenter notificationCenter);
    public ListenerStatus getListenerStatus();
    public PropertyBag getPropertyBag();
    public InetSocketAddress listeningAddress();
    public String typeName();
    public Integer currentConnectionCount();
    public boolean isStarted();
    public <T> List<T> connectionInfo(ConnectionInfoCollector<T> collector);
    public void removeConnectionWithSiteAddress(NuidSiteAddress siteAddress);
}
