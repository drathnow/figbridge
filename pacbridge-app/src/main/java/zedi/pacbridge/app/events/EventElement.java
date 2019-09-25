package zedi.pacbridge.app.events;

import org.jdom2.Element;


public abstract class EventElement {

    protected static String requiredValueFromElement(String elementName, Element element) throws InvalidEventFormatException {
        String string = element.getChildText(elementName);
        if (string == null)
            throw new InvalidEventFormatException("Event does not contain required element: " + elementName);
        return string;
    }
    
}