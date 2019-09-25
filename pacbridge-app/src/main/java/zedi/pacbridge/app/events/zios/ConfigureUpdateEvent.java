package zedi.pacbridge.app.events.zios;

import java.util.List;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureUpdateEvent extends ZiosEvent {
    public static final String ROOT_ELEMENT_NAME = "ConfigureUpdate";
    public static final String OBJECT_TAG = "object";
    
    private SiteAddress siteAddress;
    private ObjectType objectType;
    private List<Action> actions;
        
    public ConfigureUpdateEvent(SiteAddress siteAddress,  ObjectType objectType, List<Action> actions) {
        super(ZiosEventName.ConfigureUpdate);
        this.siteAddress = siteAddress;
        this.objectType = objectType;
        this.actions = actions;
    }

    @Override
    public String asXmlString() {
        Element rootElement = rootElement();
        rootElement.addContent(new Element(ZiosEvent.NUID_TAG).setText(siteAddress.getAddress()));
        Element configElement = new Element(ROOT_ELEMENT_NAME);
        configElement.setAttribute(OBJECT_TAG, objectType.getName());
        for (Action action : actions)
            configElement.addContent(action.asElement());
        rootElement.addContent(configElement);
        return JDomUtilities.xmlStringForElement(rootElement);
    }
}