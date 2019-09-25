package zedi.pacbridge.net;

import zedi.pacbridge.utl.SiteAddress;

/**
 * A <code>SessionlessMessageHandler</code> is used to handle a message destined for a non-existant session.
 * A {@link SessionManager} will pass a message to these object in the event it cannot find a session to handle
 * a message that contains a session id in its header.  
 *
 */
public interface SessionlessMessageHandler {
    
    /**
     * Invoked by a {@link SessionManagers} to handle a message that was not handled by any other session.
     * @param siteAddress - the site that generated the message 
     * @param message the message to handle
     * @param session a fully configured session object
     */
    public void handleMessageForSession(SiteAddress siteAddress, Message message, Session session);
}
