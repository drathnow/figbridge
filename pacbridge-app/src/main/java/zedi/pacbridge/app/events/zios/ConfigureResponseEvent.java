package zedi.pacbridge.app.events.zios;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureResponseEvent extends ZiosEvent {
    public static final String ROOT_ELEMENT_NAME = "ConfigureResponse";
    public static final String OBJECT_TAG = "object";
    
    private String nuid;
    private ObjectType objectType;
    private List<Action> actions;
    
    public ConfigureResponseEvent(ObjectType objectType, Long eventId, List<Action> actions, String nuid) {
        super(ZiosEventName.ConfigureResponse, eventId);
        this.objectType = objectType;
        this.actions = actions;
        this.nuid = nuid;
    }
    
    public ObjectType getObjectType() {
        return objectType;
    }
    
    public List<Action> getActions() {
        return actions;
    }
    
    @Override
    public String asXmlString() {
        Element myElement = new Element(ROOT_ELEMENT_NAME);
        myElement.setAttribute(OBJECT_TAG, objectType.getName());
        for (Action action : actions)
            myElement.addContent(action.asElement());
        Element rootElement = rootElement();
        rootElement.addContent(new Element(NUID_TAG).setText(nuid));
        return JDomUtilities.xmlStringForElement(rootElement.addContent(myElement));
    }

    public static ConfigureResponseEvent configureResponseEventForElement(Element element, FieldTypeLibrary library) throws ParseException {
        Element configElement = element.getChild(ROOT_ELEMENT_NAME);
        Long eventId = Long.parseLong(element.getChildText(EVENT_ID_TAG));
        String nuid = element.getChildText(NUID_TAG);
        ObjectType type = ObjectType.objectTypeForName(configElement.getAttributeValue(OBJECT_TAG));
        List<Action> actions = new ArrayList<>();
        for (Element el : configElement.getChildren())
            actions.add(Action.actionFromElement(el, library));
        return new ConfigureResponseEvent(type, eventId, actions, nuid);
    }
}
