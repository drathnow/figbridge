package zedi.pacbridge.net;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceSession implements Session, Comparable<Session>, Serializable {
    private static final long serialVersionUID = 1001L;
    private static Logger logger = LoggerFactory.getLogger(DeviceSession.class.getName());
    
    private Integer sessionId;
    private int nextSequenceNumber;
    private SessionManager sessionLayer;
    private BlockingQueue<Message> messageQueue;
    private MessageListenerProxy messageListenerProxy;
    
    DeviceSession(Integer sessionId, SessionManager sessionLayer, LinkedBlockingDeque<Message> messageQueue) {
        if (sessionId.intValue() <= 0)
            throw new IllegalArgumentException("Invalid value for sessionId '" + sessionId + ". SessionId must be greater than zero");
        this.sessionId = sessionId;
        this.sessionLayer = sessionLayer;
        this.messageQueue = messageQueue;
        this.nextSequenceNumber = 1;
    }

    
    public DeviceSession(Integer sessionId, SessionManager sessionLayer) {
        this(sessionId, sessionLayer, new LinkedBlockingDeque<Message>());
    }
    
    @Override
    public Integer nextSequenceNumber() {
        return nextSequenceNumber++;
    }
    
    public Integer getSessionId() {
        return sessionId;
    }
    
    public void sendMessage(Message message, long timeoutMilliseconds) throws IOException {
        throwIfClosed();
        if (logger.isTraceEnabled()) {
            logger.trace("Trx: SesId: " 
                          + sessionId
                          + ", message: "
                          + message.toString());
        }
        sessionLayer.sendMessageForSession(message, this);
    }

    /**
     * Sets a MessageListener for this session.  When calling this method, callers should be aware that if any messages
     * are queued for the session, they will be immediately delivered before the call returns.  The MessageListener
     * can only be set once for the life of any session.
     * 
     * @param messageListener
     */
    public void setMessageListener(MessageListener messageListener) {
        if (this.messageListenerProxy != null)
            throw new RuntimeException("MessageListener can only be set once.");
        this.messageListenerProxy = new MessageListenerProxy(messageListener);
        while (messageQueue.size() > 0)
            messageListener.handleMessage(messageQueue.poll());
    }
    
    public void close() {
        if (sessionLayer != null)
            sessionLayer.closeSession(this);
        clear();
    }

    public boolean isClosed() {
        return sessionLayer == null;
    }
    
    @Override
    public int compareTo(Session otherSession) {
        return sessionId.compareTo(otherSession.getSessionId());
    }
    
    private void throwIfClosed() throws ClosedSessionException {
        if (sessionLayer == null)
            throw new ClosedSessionException();
    }
    
    void handleRecievedMessage(Message message) {
        if (logger.isTraceEnabled()) {
            logger.trace("Rcv: SesId: " 
                          + sessionId
                          + ", message: "
                          + message.toString());
        }
        if (messageListenerProxy != null)
            messageListenerProxy.handleMessage(message);
        else
            if (messageQueue.offer((Message)message) == false)
                logger.error("Unable to queue message for session. No capacity left in message qeueue");
    }
    
    void clear() {
        sessionLayer = null;
        messageListenerProxy = null;
        messageQueue.clear();
    }
}
