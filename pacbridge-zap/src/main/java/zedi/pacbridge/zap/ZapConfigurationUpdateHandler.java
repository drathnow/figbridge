package zedi.pacbridge.zap;

import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.ConfigureUpdateMessage;

public interface ZapConfigurationUpdateHandler {
    public static final String JNDI_NAME = "java:global/ConfigurationUpdateHandler";
    
    public boolean didProcessConfigurationUpdate(SiteAddress siteAddress, ConfigureUpdateMessage updateMessage);

}
