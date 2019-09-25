package zedi.pacbridge.app.events;

import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.events.zios.ZiosEventFactory;
import zedi.pacbridge.utl.JDomUtilities;

/**
 * The <code>EventFactoryFacade</code> manages EventFactories for different qualifier names.  
 *
 */
@ApplicationScoped
public class EventFactoryFacade {
    private static final Logger logger = LoggerFactory.getLogger(EventFactoryFacade.class.getName());
    private Map<String, EventFactory> eventFactoryMap;
    
    @Inject
    public EventFactoryFacade(ZiosEventFactory ziosEventParser) {
        eventFactoryMap = new TreeMap<String, EventFactory>();
        eventFactoryMap.put(EventQualifier.ZIOS.getName(), ziosEventParser);
    }
    
    public EventFactoryFacade() {
    }
    
    /**
     * Searches the internal map of EventFactories to find one with the matching qualifier
     * name from the event elements.  It then invokes the enent factory with the element and returns
     * the result. If an error occurs, the error is logged through the EventErrorHandler. 
     * <p>
     * By the time this method is called, the event element should have been validated and parsed
     * so we make the assumption that the element is valid. Therefore, for this method to work
     * the element must have an attribute named {@link Event.QUALIFIER_TAG} that is a valid 
     * {@link EventQualifier}.  This method does not check that the name is valid.  If it is not,
     * an error is logged.
     * 
     * @param element - A valid event element
     * @return Event - if it was valid and the corresponding factory was found. null if an error occurs.
     */
    public HandleableEvent eventForElement(Element element) {
        String qualifierName = element.getAttributeValue(Event.QUALIFIER_TAG);
        if (qualifierName == null)
            return null;
        EventFactory factory = eventFactoryMap.get(qualifierName);
        try {
            return factory == null ? null :  factory.eventForElement(element);
        } catch (InvalidEventFormatException e) {
            logger.error(factory.getName() 
                         + " was unable to parse the event element: " 
                         + "\n"
                         + JDomUtilities.xmlStringForElement(element)
                         + "\nReason: "
                         + e.toString());
            return null;
        }
    }
}