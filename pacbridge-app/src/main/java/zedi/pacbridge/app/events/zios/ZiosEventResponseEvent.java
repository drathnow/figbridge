package zedi.pacbridge.app.events.zios;

import java.io.Serializable;

import org.jdom2.Element;

import zedi.pacbridge.app.events.EventName;
import zedi.pacbridge.app.events.EventResponseEvent;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.utl.JDomUtilities;

public class ZiosEventResponseEvent extends ZiosEvent implements EventResponseEvent, Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String ROOT_ELEMENT_NAME = "EventResponse";
    public static final String STATUS_TAG = "Status";
    public static final String MESSAGE_TAG = "Message";
    public static final String RESPONSE_TO_TAG = "ResponseTo";
    
    private EventStatus eventStatus;
    private EventName responseToEventName;
    private String statusMessage;
    private EventData eventData;
    private String nuid;
    
    private ZiosEventResponseEvent(Element element) {
        super(ZiosEventName.EventResponse, element);
    }
    
    public ZiosEventResponseEvent(Long eventId, EventStatus eventStatus, String nuid) {
        this(eventId, eventStatus, nuid, null, null);
    }
    
    public ZiosEventResponseEvent(Long eventId, EventStatus eventStatus, String nuid, EventName responseToEventName) {
        this(eventId, eventStatus, nuid, responseToEventName, null, null);
    }

    public ZiosEventResponseEvent(Long eventId, EventStatus eventStatus, String nuid, String statusMessage, EventData eventData) {
        this(eventId, eventStatus, nuid, null, statusMessage, eventData);
    }

    public ZiosEventResponseEvent(Long eventId, EventStatus eventStatus, String nuid, EventName responseToEventName, String statusMessage, EventData eventData) {
        super(ZiosEventName.EventResponse, eventId);
        this.eventStatus = eventStatus;
        this.nuid = nuid;
        this.responseToEventName = responseToEventName;
        this.statusMessage = statusMessage;
        this.eventData = eventData;
    }

    @Override
    public String asXmlString() {
        Element rootElement = rootElement();
        rootElement.addContent(new Element(NUID_TAG).setText(nuid));
        Element eventElement = new Element(ROOT_ELEMENT_NAME);
        rootElement.addContent(eventElement);
        
        Element statusElement = new Element(STATUS_TAG).setText(eventStatus.getName());
        Element messageElement = new Element(MESSAGE_TAG);

        if (statusMessage != null)
            messageElement.setText(statusMessage);

        if (responseToEventName != null) {
            Element responseToElement = new Element(RESPONSE_TO_TAG);
            responseToElement.setText(responseToEventName.getName());
            eventElement.addContent(responseToElement);
        }
        
        eventElement.addContent(statusElement);
        eventElement.addContent(messageElement);
        if (eventData != null)
            eventElement.addContent(eventData.asElement());
        
        return JDomUtilities.xmlStringForElement(rootElement);
    }

}