package zedi.pacbridge.gdn.messages;

import zedi.pacbridge.net.Message;


public interface GdnMessage extends Message {
    public GdnMessageType messageType();
}
