package zedi.pacbridge.app.events.connect;

import java.io.Serializable;
import java.util.UUID;

import org.jdom2.Element;

import zedi.pacbridge.app.events.BridgeContext;
import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.HandleableEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.gdn.pac.EventSchedule;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public abstract class SiteEvent extends ConnectEvent implements Serializable, ControlEvent, HandleableEvent  {
    private static final long serialVersionUID = 10001L;    
    
    public static final String ROOT_ELEMENT_NAME = "Event";
    public static final String IP_ADDRESS_TAG = "IpAddress";
    public static final String NETWORK_NUMBER_TAG = "NetworkNumber";
    public static final String SERIAL_NUMBER_TAG = "SerialNumber";
    public static final String FIRMWARE_VERSION_TAG = "FirmwareVersion";
    public static final String EVENT_ID_TAG = "EventId";
    public static final String QUALIFIER_TAG = "qualifier";
    public static final String NAME_TAG = "name";
    public static final String UTC_TIME_TAG = "utcTime";
    
    private SiteAddress siteAddress;
    private Integer firmwareVersion;
    private String serialNumber;
    private EventSchedule<ScheduledEvent> eventSchedule;
    private Long uniqueId = UUID.randomUUID().getLeastSignificantBits();
    
    protected SiteEvent(ConnectEventName eventName, Element element) {
        super(eventName, Long.valueOf(element.getChildText(EVENT_ID_TAG)));
        String ipAddress = element.getChildText(IP_ADDRESS_TAG);
        Integer networkNumber = Integer.valueOf(element.getChildText(NETWORK_NUMBER_TAG));
        this.siteAddress = new IpSiteAddress(ipAddress, networkNumber);
        this.serialNumber = element.getChildText(SERIAL_NUMBER_TAG);
        this.firmwareVersion = Integer.valueOf(element.getChildText(FIRMWARE_VERSION_TAG));
        if (element.getChild(EventScheduleSerializer.ROOT_ELEMENT_NAME) != null)
            eventSchedule = EventScheduleSerializer.eventScheduleForElement(element.getChild(EventScheduleSerializer.ROOT_ELEMENT_NAME));
    }
    
    protected SiteEvent(ConnectEventName eventName, Long eventId, SiteAddress siteAddress, Integer firmwareVersion, String serialNumber) {
        this(eventName, eventId, siteAddress, firmwareVersion, serialNumber, null);
    }
    
    protected SiteEvent(ConnectEventName eventName, Long eventId, SiteAddress siteAddress, Integer firmwareVersion, String serialNumber, EventSchedule<ScheduledEvent> eventSchedule) {
        super(eventName, eventId);
        this.siteAddress = siteAddress;
        this.firmwareVersion = firmwareVersion;
        this.serialNumber = serialNumber;
        this.eventSchedule = eventSchedule;
    }
    
    @Override
    public void handle(BridgeContext bridgeContext) {
        bridgeContext.handle(this);
    }

    @Override
    public Long getUniqueId() {
        return uniqueId;
    }

    public boolean hasEventSchedule() {
        return eventSchedule != null;
    }
    
    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
    
    public Integer getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    
    @Override
    protected Element rootElement() {
        Element element =  super.rootElement()
                                .addContent(new Element(IP_ADDRESS_TAG).setText(getSiteAddress().getAddress()))
                                .addContent(new Element(NETWORK_NUMBER_TAG).setText(getSiteAddress().getNetworkNumber().toString()))
                                .addContent(new Element(SERIAL_NUMBER_TAG).setText(getSerialNumber()))
                                .addContent(new Element(FIRMWARE_VERSION_TAG).setText(getFirmwareVersion().toString()));
        if (eventSchedule != null)
            element.addContent(EventScheduleSerializer.elementForEventSchedule(eventSchedule));
        return element;
    }
    
    protected static Element eventElementFromRoot(ConnectEventName eventName, Element rootElement) throws InvalidEventFormatException {
        return requireElementFromRoot(eventName.getName(), rootElement);
    }
    
    protected static Element requireElementFromRoot(String elementName, Element rootElement) throws InvalidEventFormatException {
        Element element = rootElement.getChild(elementName);
        if (element == null)
            throw new InvalidEventFormatException("Event does not contain required element: " + elementName);
        return element;
    }

}
