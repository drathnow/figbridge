package zedi.pacbridge.msg;

import javax.jms.Session;

public interface JmsTransactable {
    public void setSession(Session session);
}
