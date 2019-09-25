package zedi.pacbridge.net;


public interface MessageReceiver {
    void handleMessageForSessionId(Message message, Integer sessionId);
    void handleMessage(Message message);
}
