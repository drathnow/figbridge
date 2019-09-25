package zedi.pacbridge.app.events.zios;

import org.jdom2.Element;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.ScrubControl;

public class ScrubEvent  extends DeviceEvent implements ControlEvent {
    private static final String IO_POINTS_TAG = "IOPoints";
    private static final String REPORTS_TAG = "Reports";
    private static final String EVENTS_TAG = "Events";
    private static final String ALL_TAG = "All";

    
    private Long eventId;
    private SiteAddress siteAddress;
    private boolean scrubIoPoints;
    private boolean scrubReports;
    private boolean scrubEvents;
    private boolean scrubAll;
    
    public ScrubEvent(Long eventId, 
                      SiteAddress siteAddress,
                      String firmwareVersion,
                      boolean scrubIoPoints, 
                      boolean scrubReports, 
                      boolean scrubEvents, 
                      boolean scrubAll) {
        super(ZiosEventName.Scrub, eventId, siteAddress, firmwareVersion);
        this.eventId = eventId;
        this.siteAddress = siteAddress;
        this.scrubIoPoints = scrubIoPoints;
        this.scrubReports = scrubReports;
        this.scrubEvents = scrubEvents;
        this.scrubAll = scrubAll;
    }

    @Override
    public String asXmlString() {
        Element element =  super.rootElement();
        Element subElement = new Element(getEventName().getName());
        if (scrubIoPoints)
            subElement.addContent(new Element(IO_POINTS_TAG));
        if (scrubReports)
            subElement.addContent(new Element(REPORTS_TAG));
        if (scrubEvents)
            subElement.addContent(new Element(EVENTS_TAG));
        if (scrubAll)
            subElement.addContent(new Element(ALL_TAG));
        element.addContent(subElement);
        return JDomUtilities.xmlStringForElement(element);
    }

    @Override
    public void handle(OutgoingRequestService outgoingRequestService) {
        int scrubOptions = 0;
        scrubOptions |= scrubIoPoints ? ScrubControl.MSG_SCRUB_IO_POINTS.intValue() : 0;
        scrubOptions |= scrubReports ? ScrubControl.MSG_SCRUB_REPORTS.intValue() : 0;
        scrubOptions |= scrubEvents ? ScrubControl.MSG_SCRUB_EVENTS.intValue() : 0;
        scrubOptions |= scrubAll ? ScrubControl.MSG_SCRUB_ALL.intValue() : 0;
        ScrubControl control = new ScrubControl(eventId, scrubOptions);
        ControlRequest controlRequest = new ControlRequest(siteAddress, eventId, control);
        outgoingRequestService.queueOutgoingRequest(controlRequest);
    }

    public static ScrubEvent scrubEventForElement(Element element, DeviceCache deviceCache) throws InvalidEventFormatException {
        String firmwareVersion = null;
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
        SiteAddress siteAddress = new NuidSiteAddress(nuid, correctedNetworkNumber(device.getNetworkNumber()));
        Element scrubElement = element.getChild(ZiosEventName.Scrub.getName());
        
        return new ScrubEvent(eventId, 
                              siteAddress, 
                              firmwareVersion, 
                              scrubElement.getChild(IO_POINTS_TAG) != null, 
                              scrubElement.getChild(REPORTS_TAG) != null, 
                              scrubElement.getChild(EVENTS_TAG) != null, 
                              scrubElement.getChild(ALL_TAG) != null);
    }
}
