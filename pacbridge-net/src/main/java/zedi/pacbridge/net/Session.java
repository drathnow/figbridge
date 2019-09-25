package zedi.pacbridge.net;

import java.io.IOException;

public interface Session extends Comparable<Session> {
    public Integer getSessionId();
    public void sendMessage(Message message, long timeoutMilliseconds) throws IOException;
    public void setMessageListener(MessageListener messageListener);
    public void close();
    public boolean isClosed();
    public Integer nextSequenceNumber();
}
