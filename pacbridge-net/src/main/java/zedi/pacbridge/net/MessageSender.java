package zedi.pacbridge.net;

import zedi.pacbridge.utl.SiteAddress;

public interface MessageSender {
    public void sendMessage(SiteAddress siteAddress, Message message);
}
