package zedi.pacbridge.app.events.zios;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureEvent extends DeviceEvent implements ControlEvent {
    public static final String ROOT_ELEMENT_NAME = "Configure";
    public static final String OBJECT_TAG = "object";
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigureEvent.class.getName());

    private ObjectType objectType;
    private List<Action> actions;
        
    public ConfigureEvent(Long eventId, SiteAddress siteAddress, String firmwareVersion, ObjectType objectType, List<Action> actions) {
        super(ZiosEventName.Configure, eventId, siteAddress, firmwareVersion);
        this.objectType = objectType;
        this.actions = actions;
    }
    
    public ConfigureEvent(Long eventId, SiteAddress siteAddress, ObjectType objectType, List<Action> actions) {
        this(eventId, siteAddress, null, objectType, actions);
    }

    public Element asElement() {
        Element element = new Element(ROOT_ELEMENT_NAME);
        element.setAttribute(OBJECT_TAG, objectType.getName());
        for (Action action : actions)
            element.addContent(action.asElement());
        return rootElement().addContent(element);
    }

    public List<Action> getActions() {
        return new ArrayList<Action>(actions);
    }
    
    public ObjectType getObjectType() {
        return objectType;
    }
        
    @Override
    public Long getUniqueId() {
        return null;
    }

    @Override
    public String asXmlString() {
        return JDomUtilities.xmlStringForElement(asElement());
    }

    @Override
    public void handle(OutgoingRequestService requestService) {
        ConfigureControl control = new ConfigureControl(getEventId(), objectType, actions);
        ControlRequest controlRequest = new ControlRequest(getSiteAddress(), getEventId(), control);
        requestService.queueOutgoingRequest(controlRequest);
    }
    
    public static ConfigureEvent configureEventForElement(Element element, FieldTypeLibrary library, DeviceCache deviceCache) throws InvalidEventFormatException {
        String eventIdString = element.getChildText(EVENT_ID_TAG);
        if (eventIdString == null)
            throw new InvalidEventFormatException("Event is missing an EventId");
        Long eventId = Long.valueOf(element.getChildText(EVENT_ID_TAG));
        String nuid = element.getChildText(NUID_TAG);
        if (nuid == null)
            throw new InvalidEventFormatException("Event is missing an NUID");
        Device device = deviceCache.deviceForNetworkUnitId(nuid);
        if (device == null) {
            device = new Device(nuid, 17);
        }
        String firmware = device.getFirmwareVersion();
        SiteAddress siteAddress = new NuidSiteAddress(nuid, correctedNetworkNumber(device.getNetworkNumber()));
        Element configureElement = element.getChild(ZiosEventName.Configure.getName());
        String objectName = configureElement.getAttributeValue(OBJECT_TAG);
        ObjectType objectType;
        if ((objectType = ObjectType.objectTypeForName(objectName)) == null)
            throw new InvalidEventFormatException("Invalid object type specified for Configure event: '" + objectName + "'");
        List<Element> actionElements = configureElement.getChildren(Action.ROOT_ELEMENT_NAME);
        List<Action> actions = new ArrayList<Action>();
        for (Element actionElement : actionElements) {
            try {
                actions.add(Action.actionFromElement(actionElement, library));
            } catch (ParseException e) {
                logger.error("Unable to parse Action element with name '" 
                             + element.getName() 
                             + "': " 
                             + e.toString());
            }
        }
        
        return new ConfigureEvent(eventId, siteAddress, firmware, objectType, actions);
    }
}
