package zedi.pacbridge.msg;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;

import zedi.pacbridge.utl.NotificationCenter;
class ExceptionListener implements javax.jms.ExceptionListener {

    private Lock reconnectionLock;
    private JmsServerReconnector reconnector;
    private NotificationCenter notificationCenter;

    ExceptionListener(JmsServerReconnector reconnector, Lock reconnectionLock, NotificationCenter notificationCenter) {
        this.reconnectionLock = reconnectionLock;
        this.reconnector = reconnector;
        this.notificationCenter = notificationCenter;
    }

    public ExceptionListener(JmsServerReconnector reconnector, NotificationCenter notificationCenter) {
        this(reconnector, new ReentrantLock(), notificationCenter);
    }
    
    @Override
    public void onException(JMSException exception) {
        ReconnectionRunner reconnectionRunner = new ReconnectionRunner(exception, reconnector, reconnectionLock, notificationCenter);
        reconnectionRunner.start();
    }

}
