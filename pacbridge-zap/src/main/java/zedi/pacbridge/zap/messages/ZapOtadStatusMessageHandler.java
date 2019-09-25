package zedi.pacbridge.zap.messages;

import zedi.pacbridge.utl.SiteAddress;

public interface ZapOtadStatusMessageHandler {
    public static final String JNDI_NAME = "java:global/OtadStatusMessageHandler";
    public boolean didProcessStatusUpdateMessage(SiteAddress siteAddress, OtadStatusMessage updateMessage);
}
