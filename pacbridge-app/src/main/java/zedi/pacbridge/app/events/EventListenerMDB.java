package zedi.pacbridge.app.events;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class EventListenerMDB implements MessageListener {
    private static final Logger logger = Logger.getLogger(EventListenerMDB.class.getName());

    public static final String JNDI_NAME = "java:global/EventListenerMDB";
    public static final String JMS_ERROR_MSG = "Unable to retrieve text message from JMS message object";
    public static final String PARSE_ERROR_MSG = "Unable to parse XML string to event";
    public static final String XML_KEY = "xmlString";
    public static final String PARSE_ERROR_KEY = "errorMessage";

    private EventProcessor eventProcessor;
    private EventParser eventParser;
    
    @Inject
    public EventListenerMDB(EventProcessor eventProcessor, EventParser eventParser) {
        this.eventProcessor = eventProcessor;
        this.eventParser = eventParser;
    }
    
    public EventListenerMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
        HandleableEvent event = eventFromMessage(message);
        if (event != null)
            eventProcessor.processEvent(event);
    }

    private HandleableEvent eventFromMessage(Message message) {
        String xmlEventString = xmlEventStringForMessage(message);
        if (logger.isDebugEnabled()) {
            logger.debug("Received XML Event: ");
            logger.debug(xmlEventString);
        }
        try {
            HandleableEvent event = null;
            if (xmlEventString != null)
                event = eventParser.eventForXmlEventString(xmlEventString);
            return event;
        } catch (Throwable e) {
            logger.error(PARSE_ERROR_MSG + "\n" + xmlEventString, e);
            return null;
        }
    }
    
    private String xmlEventStringForMessage(Message message) {
        try {
            return ((TextMessage)message).getText();
        } catch (JMSException e) {
            logger.error(JMS_ERROR_MSG, e);
            return null;
        }
    }
}