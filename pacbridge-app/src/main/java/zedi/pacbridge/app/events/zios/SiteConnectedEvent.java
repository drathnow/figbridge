package zedi.pacbridge.app.events.zios;

import java.text.ParseException;
import java.util.Date;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;

public class SiteConnectedEvent extends SiteConnectionEvent {
    public static final String FIRMWARE_VERSION_TAG = "FirmwareVersion";
    
    private String firmwareVersion;

    public SiteConnectedEvent(SiteAddress siteAddress, String bridgeInstanceName, String ipAddress, String firmwareVersion) {
        this(siteAddress, bridgeInstanceName, ipAddress, new Date(), firmwareVersion);
    }

    SiteConnectedEvent(SiteAddress siteAddress, String bridgeInstanceName, String ipAddress, Date timestamp, String firmwareVersion) {
        super(ZiosEventName.SiteConnected, siteAddress, bridgeInstanceName, ipAddress, timestamp);
        this.firmwareVersion = firmwareVersion;
    }
    
    public SiteConnectedEvent(Element element) throws ParseException {
        super(ZiosEventName.SiteConnected, element);
    }

    @Override
    public Long getUniqueId() {
        return null;
    }

    @Override
    public String asXmlString() {
        Element root = rootElement();
        if (firmwareVersion != null) {
            Element element = root.getChild(getEventName().getName());
            element.addContent(new Element(FIRMWARE_VERSION_TAG).setText(firmwareVersion));
        }
        return JDomUtilities.xmlStringForElement(root);
    }
    
    public static SiteConnectedEvent siteConnectedEventForElement(Element element) throws ParseException {
        return new SiteConnectedEvent(element);
    }
}
