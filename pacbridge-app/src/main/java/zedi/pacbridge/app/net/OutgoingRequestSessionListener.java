package zedi.pacbridge.app.net;


/**
 * The {@code OutgoingRequestSessionListener} intface defines a protocol used by an {@code OutgoingRequestSession}
 * to communicate close events to a client. Implementors of this class should take caution when handling events
 * from {@code OutgoingRequestSession}.  Implemenations should complete work as fast as possible and not take out
 * locks or do any other activity to stall the calling thread.
 */
public interface OutgoingRequestSessionListener {
    public void sessionClosed(OutgoingRequestSession session);
}
