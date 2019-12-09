package zedi.pacbridge.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import zedi.pacbridge.utl.GlobalExecutor;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;

/**
 * The <code>MessageListenerManager</code> class manages message listeners registered to receive message
 * from JMS destinations.  All artifacts required to connect to the JMS server and receive messages are
 * maintained by this class.  In the event that connection to the JMS server is lost, this class will 
 * clean up those artrifacts and recreate them when the connection is restored.
 *
 */
class MessageListenerManager implements Notifiable {
    private ReentrantLock lock;
    private Connection connection;
    private JmsCenter jmsCenter;
    private List<MessageListenerContainer> messageListenerContainers = new ArrayList<MessageListenerContainer>();
    private Map<String, MessageListenerContainer> listenerClassMap = new HashMap<String, MessageListenerContainer>();
    private MessageListenerContainerFactory messageListenerContainerFactory;

    MessageListenerManager(JmsCenter jmsCenter, MessageListenerContainerFactory messageListenerContainerFactory, NotificationCenter notificationCenter) {
        this.messageListenerContainerFactory = messageListenerContainerFactory;
        this.jmsCenter = jmsCenter;
        this.lock = new ReentrantLock();
        notificationCenter.addObserver(this, JmsCenter.CONNECTION_LOST_NOTIFICATION);
        notificationCenter.addObserver(this, JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION);
        createConnection();
    }

    public MessageListenerManager(JmsCenter jmsCenter, NotificationCenter notificationCenter) {
        this(jmsCenter, new MessageListenerContainerFactory(), notificationCenter);
    }

    public void registerMessageListener(MessageListener messageListener, String destinationName, String subscriptionName, boolean isTransacted) {
        lock.lock();
        try {
            Destination destination =  jmsCenter.getDestination(destinationName);
            MessageListenerContainer container = messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, subscriptionName, isTransacted, null);
            messageListenerContainers.add(container);
            if (connection != null)
                container.start(connection);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize message listener", e);
        } finally {
            lock.unlock();
        }
    }
    
    public void registerMessageListener(MessageListener messageListener, String destinationName, boolean isTransacted) {
        lock.lock();
        try {
            Destination destination = jmsCenter.getDestination(destinationName);
            MessageListenerContainer container = messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, isTransacted, null);
            messageListenerContainers.add(container);
            if (connection != null)
                container.start(connection);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize message listener", e);
        } finally {
            lock.unlock();
        }
    }
    

    public void unregisterMessageListener(MessageListener messageListener) {
        lock.lock();
        try {
            for (Iterator<MessageListenerContainer> iterator = messageListenerContainers.iterator(); iterator.hasNext();) {
                MessageListenerContainer container = iterator.next();
                if (container.getMessageListener() == messageListener) {
                    iterator.remove();
                    container.close();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void handleNotification(Notification notification) {
        if (notification.getName().equals(JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION))
            GlobalExecutor.sharedInstance().execute(new RestartWorker());
        else if (notification.getName().equals(JmsCenter.CONNECTION_LOST_NOTIFICATION))
            GlobalExecutor.sharedInstance().execute(new CloseWorker());
    }
    

    public void registerMessageListener(String lookupName, String destinationName, boolean isTransacted) {
        DefaultMessageListener messageListener = new DefaultMessageListener(lookupName);
        lock.lock();
        try {
            Destination destination = jmsCenter.getDestination(destinationName);
            MessageListenerContainer container = messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, isTransacted, null);
            messageListenerContainers.add(container);
            listenerClassMap.put(lookupName, container);
            if (connection != null)
                container.start(connection);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize message listener", e);
        } finally {
            lock.unlock();
        }
    }

    public void unregisterMessageListenerClass(Class<? extends MessageListener> listernClass) {
        lock.lock();
        try {
            @SuppressWarnings("unlikely-arg-type")
			MessageListenerContainer container = listenerClassMap.get(listernClass);
            if (container != null)
                messageListenerContainers.remove(container);
        } finally {
            lock.unlock();
        }
    }
    
    private void closeContainers() {
        try {
            if (connection != null)
                connection.close();
        } catch (JMSException eatIt) {
        }
        connection = null;
        for (MessageListenerContainer container : messageListenerContainers)
            container.close();
    }

    private void restartContainers() {
        createConnection();
        for (MessageListenerContainer container : messageListenerContainers)
            try {
                container.start(connection);
            } catch (JMSException e) {
                throw new RuntimeException("Unable to initialize message listener", e);
            }
    }

    private void createConnection() {
        try {
            this.connection = jmsCenter.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize JMS subsystem", e);
        }
    }

    private class CloseWorker implements Runnable {
        
        @Override
        public void run() {
            lock.lock();
            try {
                closeContainers();
            } finally {
                lock.unlock();
            }
        }
    }
    
    private class RestartWorker implements Runnable {
        
        @Override
        public void run() {
            lock.lock();
            try {
                restartContainers();
            } finally {
                lock.unlock();
            }
        }
    }
}
