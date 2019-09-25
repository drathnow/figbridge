package zedi.pacbridge.net;

/**
 * A message listener is used to notify object when a message arrives for a specific session.  Normally, MessageListeners
 * are invoked from the main bridge worker thread so anything it does should be short and quick.  It should not undertake
 * any activities that could stall the thread.  
 * <p>
 * If a MessageListener is expecting to perform activities that could stall the calling thread, then it should add that 
 * {@link zedi.pacbridge.annotations.Async} annotation.  If this annotation is present, then the MessageListener
 * will be invoked by a separate thread.
 * </p>
 * Message Listener must not throw exceptions.
 * 
 */
public interface MessageListener {
    public void handleMessage(Message message);
}
