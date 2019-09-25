package zedi.pacbridge.app.events.zios;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.OtadStatus;

public class OtadStatusUpdateEvent extends ZiosEvent {
    public static final String IP_ADDRESS_TAG = "IpAddress";
    public static final String EVENT_ID_TAG = "EventId";
    public static final String STATUS_TAG = "Status";
    public static final String OPTIONAL_DATA_TAG = "OptionalData";

    private SiteAddress siteAddress;
    private OtadStatus otadStatus;
    private String optionalData;
    
    public OtadStatusUpdateEvent(long eventId, SiteAddress siteAddress, OtadStatus otadStatus, String optionalData) {
        super(ZiosEventName.OtadStatus, eventId);
        this.siteAddress = siteAddress;
        this.otadStatus = otadStatus;
        this.optionalData = optionalData;
    }

    @Override
    public String asXmlString() {
        Element root = super.rootElement()
                .addContent(new Element(NUID_TAG).setText(siteAddress.getAddress()))
                .addContent(new Element(NETWORK_NUMBER_TAG).setText(siteAddress.getNetworkNumber().toString()));
        Element eventElement = new Element(getEventName().getName());
        eventElement.addContent(new Element(STATUS_TAG).setText(otadStatus.getName()));
        if (optionalData != null)
            eventElement.addContent(new Element(OPTIONAL_DATA_TAG).setText(optionalData));
        root.addContent(eventElement);
        return JDomUtilities.xmlStringForElement(root);
    }
}
