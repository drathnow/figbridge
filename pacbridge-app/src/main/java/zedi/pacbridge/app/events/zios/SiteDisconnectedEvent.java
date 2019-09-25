package zedi.pacbridge.app.events.zios;

import java.text.ParseException;
import java.util.Date;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;

public class SiteDisconnectedEvent extends SiteConnectionEvent {
    public static final String BYTES_RECEIVED_TAG = "BytesReceived";
    public static final String BYTES_TRANSMITTED_TAG = "BytesTransmitted";
    
    private Integer bytesReceived;
    private Integer bytesTransmitted;

    SiteDisconnectedEvent(SiteAddress siteAddress, String bridgeInstanceName, String ipAddress, Integer bytesReceived, Integer bytesTransmitted, Date timestamp) {
        super(ZiosEventName.SiteDisconnected, siteAddress, bridgeInstanceName, ipAddress, timestamp);
        this.bytesReceived = bytesReceived;
        this.bytesTransmitted = bytesTransmitted;
    }
    
    public SiteDisconnectedEvent(SiteAddress siteAddress, String bridgeInstanceName, String ipAddress, Integer bytesReceived, Integer bytesTransmitted) {
        super(ZiosEventName.SiteDisconnected, siteAddress, bridgeInstanceName, ipAddress, new Date());
        this.bytesReceived = bytesReceived;
        this.bytesTransmitted = bytesTransmitted;
    }
    
    public SiteDisconnectedEvent(Element element) throws ParseException {
        super(ZiosEventName.SiteDisconnected, element);
        bytesReceived = Integer.parseInt(element.getChild(ZiosEventName.SiteDisconnected.getName()).getChildText(BYTES_RECEIVED_TAG));
        bytesTransmitted = Integer.parseInt(element.getChild(ZiosEventName.SiteDisconnected.getName()).getChildText(BYTES_TRANSMITTED_TAG));
    }

    @Override
    public Long getUniqueId() {
        return null;
    }

    public Integer getBytesReceived() {
        return bytesReceived;
    }
    
    public Integer getBytesTransmitted() {
        return bytesTransmitted;
    }
    
    @Override
    public String asXmlString() {
        Element root = super.rootElement();
        Element eventElement = root.getChild(ZiosEventName.SiteDisconnected.getName());
        eventElement.addContent(new Element(BYTES_RECEIVED_TAG).setText(Integer.toString(bytesReceived)));
        eventElement.addContent(new Element(BYTES_TRANSMITTED_TAG).setText(Integer.toString(bytesTransmitted)));
        return JDomUtilities.xmlStringForElement(root);
    }
}
