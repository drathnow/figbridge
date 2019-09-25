package zedi.pacbridge.app.auth.zap;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.utl.SiteAddress;

@Stateless
@EJB(name = AuthenticationDelegate.JNDI_NAME, beanInterface = AuthenticationDelegate.class)
public class ZapAuthenticationDelegate implements AuthenticationDelegate {
    private static final Logger logger = LoggerFactory.getLogger(ZapAuthenticationDelegate.class.getName());
    private OutgoingRequestCache requestCache;
    private DeviceCache deviceCache;
    
    public ZapAuthenticationDelegate() {
    }

    @Inject
    public ZapAuthenticationDelegate(OutgoingRequestCache requestCache, DeviceCache deviceCache) {
        this.requestCache = requestCache;
        this.deviceCache = deviceCache;
    }

    @Override
    public Device deviceForNuid(String nuid) {
        return deviceCache.deviceForNetworkUnitId(nuid);
    }
    
    @Override
    public String serverName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Unable to get localhost name", e);
            return "Don't know";
        }
    }
    
    @Override
    public String systemId() {
        return "1";
    }

    @Override
    public boolean hasOutgoingDataRequests(SiteAddress siteAddress) {
        return requestCache.hasOutgoingRequests(siteAddress);
    }
}