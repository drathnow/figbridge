package zedi.pacbridge.msg;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class MessageListenerContainer implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(MessageListenerContainer.class);
    
    private Destination destination;
    private String subscriptionName;
    private MessageListener messageListener;
    private MessageConsumer messageConsumer;
    private SessionWrapper sessionWrapper;
    private SessionWrapperFactory sessionWrapperFactory;
    private String messageSelector;
    private boolean transacted;

    MessageListenerContainer(MessageListener messageListener, Destination destination, boolean isTransacted, String messageSelector) {
        this(messageListener, destination, null, isTransacted, messageSelector);
    }
    
    MessageListenerContainer(MessageListener messageListener, Destination destination, String subscriptionName, boolean isTransacted, String messageSelector) {
        this.messageListener = messageListener;
        this.subscriptionName = subscriptionName;
        this.destination = destination;
        this.transacted = isTransacted;
        this.sessionWrapperFactory = new SessionWrapperFactory();
        this.messageSelector = messageSelector;
    }

    MessageListener getMessageListener() {
        return messageListener;
    }

    SessionWrapper getSessionWrapper() {
        return sessionWrapper;
    }
    
    MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }
    
    void start(Connection connection) throws JMSException {
        Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
        sessionWrapper = sessionWrapperFactory.newSessionWrapper(session);
        if (transacted) {
            if (messageListener instanceof JmsTransactable)
                ((JmsTransactable)messageListener).setSession(session);
        }
        if (subscriptionName == null)
            messageConsumer = session.createConsumer(destination, messageSelector);
        else
            messageConsumer = session.createDurableSubscriber((Topic)destination, subscriptionName);
        messageConsumer.setMessageListener(this);
    }

    void close() {
        try {
            if (messageConsumer != null)
                    messageConsumer.close();
        } catch (JMSException e) {
        }
        try {
            if (sessionWrapper != null)
                sessionWrapper.close();
        } catch (JMSException e) {
        }
        messageConsumer = null;
        sessionWrapper = null;
    }
    
    void setSessionWrapperFactory(SessionWrapperFactory sessionWrapperFactory) {
        this.sessionWrapperFactory = sessionWrapperFactory;
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            sessionWrapper.reset();
            messageListener.onMessage(message);
            if (transacted && sessionWrapper.isCommitted() == false)
                commit();
        } catch (Exception e) {
            logger.error("Unhandled exception encoutered from message listener",e);
        }
    }

    private void commit() {
        try {
            sessionWrapper.commit();
        } catch (Exception e) {
            logger.error("Unable to commit JMS transaction", e);
            if (transacted)
                rollback();
        }
    }
    
    private void rollback() {
        try {
            sessionWrapper.rollback();
        } catch (Exception e) {
            logger.error("Unable to rollback JMS transaction", e);
        }
    }
    

}
