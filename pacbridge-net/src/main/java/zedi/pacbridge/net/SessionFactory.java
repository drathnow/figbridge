package zedi.pacbridge.net;


public class SessionFactory {
    public DeviceSession newSession(Integer sessionId, SessionManager sessionManager) {
        return new DeviceSession(sessionId, sessionManager);
    }
}