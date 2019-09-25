package zedi.pacbridge.net;

import java.io.IOException;

public interface MessageTransmitter {
    MessageTracker transmitMessageForSession(Message message, Session session) throws IOException;
    MessageTracker transmitMessage(Message message) throws IOException;
}
