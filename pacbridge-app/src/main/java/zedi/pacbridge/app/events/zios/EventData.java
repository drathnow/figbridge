package zedi.pacbridge.app.events.zios;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;

public class EventData implements Serializable {
    private static final long serialVersionUID = 1001L;

    public static final String ROOT_ELEMENT_NAME = "EventData";
    public static final String PROPERTY_TAG = "Property";
    public static final String NAME_TAG = "name";
    public static final String VALUE_TAG = "value";
    
    private Map<String, String> properties;
    
    public EventData() {
        properties = new HashMap<String, String>();
    }
    
    public void addProperty(String name, String value) {
        properties.put(name, value);
    }
    
    public String asXmlString() {
        return JDomUtilities.xmlStringForElement(asElement());
    }
    
    public Element asElement() {
        Element rootElement = new Element(ROOT_ELEMENT_NAME);
        for (String key : properties.keySet()) {
            Element propertyElement = new Element(PROPERTY_TAG);
            propertyElement.setAttribute(NAME_TAG, key);
            propertyElement.setAttribute(VALUE_TAG, properties.get(key));
            rootElement.addContent(propertyElement);
        }
        return rootElement;
    }
}
