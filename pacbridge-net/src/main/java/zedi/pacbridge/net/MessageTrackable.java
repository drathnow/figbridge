package zedi.pacbridge.net;

/**
 * Implementors of the <code>MessageTrackable</code> interface can provide clients with a way to track messages.  The
 * interface provides a single method that will return a <code>MessageTracker</code> object that represents an 
 * object that can give the status of the last message transmitted.
 */
public interface MessageTrackable {
    public MessageTracker lastMessageTracker();
    public boolean supportsMessageTracking();
}
