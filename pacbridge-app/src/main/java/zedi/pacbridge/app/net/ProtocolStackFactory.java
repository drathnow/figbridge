package zedi.pacbridge.app.net;

import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;


public interface ProtocolStackFactory {
    public ProtocolStack newProtocolStack(ProtocolConfig protocolConfig, SiteAddress siteAddress, ThreadContext astRequester, NetworkAdapter networkAdapter, PropertyBag propertyBag);
}