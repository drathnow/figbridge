package zedi.pacbridge.net;

import zedi.pacbridge.utl.SiteAddress;

public interface UnsolicitedMessageHandler {
    public void handleUnsolicitedMessage(SiteAddress siteAddress, Message message, ResponseSender responseSender);
}
