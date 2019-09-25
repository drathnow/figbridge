package zedi.pacbridge.app.events.zios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.jdom2.Element;

import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public abstract class SiteConnectionEvent extends ZiosEvent {
    public static final String BRIDGE_INSTANCE_TAG = "BridgeInstance";
    public static final String IP_ADDRESS_TAG = "IpAddress";
    public static final String TIMESTAMP_TAG = "TimestampUtc";
    
    public static final SimpleDateFormat dateFormat;
    
    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    protected SiteAddress siteAddress;
    protected String bridgeInstanceName;
    protected String ipAddress;
    protected Date timestamp;

    protected SiteConnectionEvent(ZiosEventName eventName, SiteAddress siteAddress, String bridgeInstanceName, String ipAddress, Date timestamp) {
        super(eventName);
        this.siteAddress = siteAddress;
        this.bridgeInstanceName = bridgeInstanceName;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    protected SiteConnectionEvent(ZiosEventName eventName, Element element) throws ParseException {
        super(eventName);
        String nuid = element.getChildText(NUID_TAG);
        Integer networkNumber = Integer.valueOf(element.getChildText(NETWORK_NUMBER_TAG));
        this.siteAddress = new NuidSiteAddress(nuid, networkNumber);
        this.ipAddress = element.getChild(eventName.getName()).getChildText(IP_ADDRESS_TAG);
        this.timestamp = dateFormat.parse(element.getChildText(TIMESTAMP_TAG));
    }

    public Date getTimestamp() {
        return null;
    }
    
    
    protected Element rootElement() {
        Element element = super.rootElement()
                        .addContent(new Element(NUID_TAG).setText(siteAddress.getAddress()))
                        .addContent(new Element(NETWORK_NUMBER_TAG).setText(siteAddress.getNetworkNumber().toString()));
        Element eventElement = new Element(getEventName().getName());
        eventElement.addContent(new Element(BRIDGE_INSTANCE_TAG).setText(bridgeInstanceName));
        eventElement.addContent(new Element(IP_ADDRESS_TAG).setText(ipAddress));
        eventElement.addContent(new Element(TIMESTAMP_TAG).setText(dateFormat.format(timestamp)));
        element.addContent(eventElement);
        element.removeChildren(EVENT_ID_TAG);
        return element;
    }
}
