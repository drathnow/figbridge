package zedi.pacbridge.app.events.zios;

import java.io.Serializable;
import java.util.UUID;

import org.jdom2.Element;

import zedi.pacbridge.app.devices.DeviceObjectCreator;
import zedi.pacbridge.app.events.BridgeContext;
import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.HandleableEvent;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public abstract class DeviceEvent extends ZiosEvent implements Serializable, ControlEvent, HandleableEvent  {
    private static final long serialVersionUID = 10001L;    
    
    public static final String FIRMWARE_VERSION_TAG = "FirmwareVersion";
    public static final String EVENT_ID_TAG = "EventId";
    public static final String QUALIFIER_TAG = "qualifier";
    public static final String NAME_TAG = "name";
    public static final String UTC_TIME_TAG = "utcTime";
    
    private SiteAddress siteAddress;
    private String firmwareVersion;
    private Long uniqueId = UUID.randomUUID().getLeastSignificantBits();
    
    protected DeviceEvent(ZiosEventName eventName, Element element) {
        super(eventName, Long.valueOf(element.getChildText(EVENT_ID_TAG)));
        String foo;
        Integer networkNumber = 0;
        String nuid = element.getChildText(NUID_TAG);
        if ((foo = element.getChildText(NETWORK_NUMBER_TAG)) != null)
            networkNumber = Integer.valueOf(foo);
        this.siteAddress = new NuidSiteAddress(nuid, networkNumber);
        this.firmwareVersion = element.getChildText(FIRMWARE_VERSION_TAG);
    }
    
    protected DeviceEvent(ZiosEventName eventName, Long eventId, SiteAddress siteAddress, String firmwareVersion) {
        super(eventName, eventId);
        this.siteAddress = siteAddress;
        this.firmwareVersion = firmwareVersion;
    }

    protected DeviceEvent(ZiosEventName eventName, Long eventId, SiteAddress siteAddress) {
        this(eventName, eventId, siteAddress, null);
    }

    @Override
    public void handle(BridgeContext bridgeContext) {
        bridgeContext.handle(this);
    }

    @Override
    public Long getUniqueId() {
        return uniqueId;
    }

    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    protected static Integer correctedNetworkNumber(Integer networkNumber) {
    	return networkNumber == 0 ? DeviceObjectCreator.DEFAULT_NETWORK_NUMBER_PROPERTY.currentValue() : networkNumber;
    }
    
    @Override
    protected Element rootElement() {
        Element rootElement = super.rootElement();
        rootElement.addContent(new Element(NUID_TAG).setText(getSiteAddress().getAddress()));
        return rootElement;
    }
}
