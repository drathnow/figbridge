package zedi.pacbridge.gdn.messages;

import zedi.pacbridge.net.Message;

public interface GdnMessageFormatter {
    public String decodedGdnMessage(Message gdnMessage);
}
