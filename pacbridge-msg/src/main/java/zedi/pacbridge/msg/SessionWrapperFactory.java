package zedi.pacbridge.msg;

import javax.jms.Session;

class SessionWrapperFactory {
    public SessionWrapper newSessionWrapper(Session session) {
        return new SessionWrapper(session);
    }
}
