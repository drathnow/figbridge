package zedi.pacbridge.msg;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.FigBridgeThreadFactory;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.ThreadSleeper;

/**
 * A ReconnectionRunner is a Runnable object that is started when a JMS server loss is detected.
 * A ReconnectionRunner is typically stated by an {@link zedi.pacbridge.msg.ExceptionListener}.
 * Because a single ExceptionListern object is used for all conenctions created by the JMS subsystem,
 * it's possible that more than one ReconnectionRunner could be started.  To avoid multiple 
 * ReconnectionRunners from attempting to reconnect, a {@link java.util.concurrent.locks.Lock} must
 * first be aquired by a <code>ReconnectionRunner</code>.  They do this by calling {@link java.util.concurrent.locks.Lock.tryLock}
 * and if it returns true, then it holds the lock and attempts to reconnect.  If   {@link java.util.concurrent.locks.Lock.tryLock}
 * returns false, no futher action is taken and the thread exits.
 * 
 * The <code>ReconnectionRunner</code> is responsible for posting notification to the {@zedi.pacbridge.utl.NotificationCenter}
 * to indicate the status of the JMS sever connection. Refer to {@link zedi.pacbridge.msg.JmsCenter.CONNECTION_LOST_NOTIFICATION}
 * and {@link zedi.pacbridge.msg.JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION} 
 *
 */
class ReconnectionRunner implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ReconnectionRunner.class.getName());

    public static final String THREAD_NAME = "JMS Reconnection Runner";
    public static final long RECONNECTION_INTERVAL_SECONDS = 15;

    private Thread thread;
    private ThreadSleeper threadSleeper;
    private ThreadFactory threadFactory;
    private Lock reconnectionLock;
    private JmsServerReconnector reconnector;
    private JMSException causeException;
    private NotificationCenter notificationCenter;

    ReconnectionRunner(JMSException causeException, JmsServerReconnector reconnector, Lock reconnectionLock, NotificationCenter notificationCenter) {
        this (causeException, new ThreadSleeper(), new FigBridgeThreadFactory(), reconnector, reconnectionLock, notificationCenter);
    }
    
    ReconnectionRunner(JMSException causeException, 
                       ThreadSleeper threadSleeper, 
                       ThreadFactory threadFactory, 
                       JmsServerReconnector reconnector, 
                       Lock reconnectionLock, 
                       NotificationCenter notificationCenter) {
        this.threadSleeper = threadSleeper;
        this.threadFactory = threadFactory;
        this.reconnectionLock = reconnectionLock;
        this.reconnector = reconnector;
        this.causeException = causeException;
        this.notificationCenter = notificationCenter;
    }

    void start() {
        if (thread == null) {
            thread = threadFactory.newThread(this);
            thread.setName(THREAD_NAME);
            thread.start();
        }
    }

    @Override
    public void run() {
        if (reconnectionLock.tryLock()) {
            logDetails();
            logger.info("JMS Reconnection runner started");
            try {
                notificationCenter.postNotification(JmsCenter.CONNECTION_LOST_NOTIFICATION);
                while (reconnector.isConnectionReestabilshed() == false)
                    hangOut();
                logger.info("Connection to JMS serevr reestablished.");
                notificationCenter.postNotification(JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION);
            } finally {
                reconnectionLock.unlock();
            }
        }
    }
    
    private void logDetails() {
        logger.error("Connection to JMS server lost", causeException);
        if (causeException.getCause() != null)
            logger.error("Caused by", causeException.getCause());
        if (causeException.getLinkedException() != null)
            logger.error("Linked exception", causeException.getLinkedException());
    }

    private void hangOut() {
        try {
            threadSleeper.sleep(RECONNECTION_INTERVAL_SECONDS * 1000);
        } catch (InterruptedException e) {
        }
    }
}
