package zedi.pacbridge.app.events.connect;

import java.io.Serializable;

import org.jdom2.Element;

import zedi.pacbridge.app.events.EventResponseEvent;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.utl.JDomUtilities;

public class ConnectEventResponseEvent extends ConnectEvent implements EventResponseEvent, Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String ROOT_ELEMENT_NAME = "EventResponse";
    public static final String STATUS_TAG = "Status";
    public static final String MESSAGE_TAG = "Message";
    public static final String EVENT_DATA_TAG = "EventData";
    
    private EventStatus eventStatus;
    private String statusMessage;
    private String eventDataString;
    
    private ConnectEventResponseEvent(Element element) {
        super(ConnectEventName.EventResponse, element);
    }
    
    public ConnectEventResponseEvent(Long eventId, EventStatus eventStatus) {
        this(eventId, eventStatus, null, null);
    }
    
    public ConnectEventResponseEvent(Long eventId, EventStatus eventStatus, String statusMessage, String eventDataString) {
        super(ConnectEventName.EventResponse, eventId);
        this.eventStatus = eventStatus;
        this.statusMessage = statusMessage;
        this.eventDataString = eventDataString;
    }

    @Override
    public String asXmlString() {
        Element rootElement = rootElement();
        Element eventElement = new Element(ROOT_ELEMENT_NAME);
        rootElement.addContent(eventElement);
        
        Element statusElement = new Element(STATUS_TAG).setText(eventStatus.getName());
        Element messageElement = new Element(MESSAGE_TAG);
        Element dataElement = new Element(EVENT_DATA_TAG);

        if (statusMessage != null)
            messageElement.setText(statusMessage);
        if (eventDataString != null)
            dataElement.setText(eventDataString);
        
        eventElement.addContent(statusElement);
        eventElement.addContent(messageElement);
        eventElement.addContent(dataElement);
        
        return JDomUtilities.xmlStringForElement(rootElement);
    }

    @Override
    public Long getUniqueId() {
        // TODO Auto-generated method stub
        return null;
    }
}