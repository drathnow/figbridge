package zedi.pacbridge.app.events;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.util.EventDocumentFactory;


@Stateless
public class EventParser {
    private static Logger logger = LoggerFactory.getLogger(EventParser.class.getName());
    
    public static final String INVALID_XML_ERROR = "Invalid event XML";
    public static final String MSG_KEY = "errorMessage";
    public static final String EVENT_XML_KEY = "evenXml";
    
    private EventFactoryFacade eventFactory;
    private EventXmlValidator eventXmlValidator;
    private EventDocumentFactory documentFactory;
    
    @Inject
    public EventParser(EventFactoryFacade eventFactory, EventXmlValidator eventXmlValidator, EventDocumentFactory documentFactory) {
        this.eventFactory = eventFactory;
        this.eventXmlValidator = eventXmlValidator;
        this.documentFactory = documentFactory;
    }

    public EventParser() {
    }
    
    public HandleableEvent eventForXmlEventString(String xmlEventString) {
        HandleableEvent event = null;
        if (eventXmlValidator.isValidXml(xmlEventString)) {
            Element element = rootElementForXmlString(xmlEventString);
            try {
                event = eventFactory.eventForElement(element);
            } catch (Exception e) {
                logger.error("Unable convert XML to Event", e);
                logger.error("Problem XML:/n" + xmlEventString);
            }
        } else
            logger.error("XML Event String does not validate: " 
                        + xmlEventString
                        + "\nReason: "
                        + eventXmlValidator.getLastError());
        return event;
    }
    
    private Element rootElementForXmlString(String xmlString) {
        try {
            return documentFactory.documentForXmlString(xmlString).getRootElement();
        } catch (JDOMException | IOException e) {
            throw new RuntimeException("Unable to create document from XML string: " + xmlString, e);
        }
    }
}
