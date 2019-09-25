package zedi.pacbridge.net.logging;

import org.slf4j.MDC;

import zedi.pacbridge.utl.SiteAddress;

public class LoggingContext {
    public static final String SITE_ADDRESS_KEY = "siteAddress";
    public static final String NETWORK_NUMBER_KEY = "networkNumber";
    
    private SiteAddress siteAddress;
    private String contextString;
    
    public LoggingContext(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(siteAddress.getAddress());
        stringBuilder.append("/");
        stringBuilder.append(siteAddress.getNetworkNumber());
        this.contextString = stringBuilder.toString();
    }

    public void setupContext() {
        MDC.put(SITE_ADDRESS_KEY, contextString);    
    }
    
    public void clearContext() {
        MDC.remove("siteAddress");
    }

    public SiteAddress siteAddress() {
        return siteAddress;
    }
}
