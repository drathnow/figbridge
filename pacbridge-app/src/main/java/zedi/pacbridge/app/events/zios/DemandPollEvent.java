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
import zedi.pacbridge.zap.messages.DemandPollControl;

public class DemandPollEvent extends DeviceEvent implements ControlEvent {

    public static final String INDEX_TAG = "Index";
    public static final String POLLSET_NUMBER_TAG = "PollsetNumber";
    
    private Integer pollsetNumber;
    private Long index;
    
    public DemandPollEvent(Long eventId, SiteAddress siteAddress, String firmwareVersion, Long index, Integer pollsetNumber) {
        super(ZiosEventName.DemandPoll, eventId, siteAddress, firmwareVersion);
        this.index = index;
        this.pollsetNumber = pollsetNumber;
    }

    public Integer getPollsetNumber() {
        return pollsetNumber;
    }
    
    public Long getIndex() {
        return index;
    }
    
    @Override
    public String asXmlString() {
        Element element =  super.rootElement();
        Element subElement = new Element(getEventName().getName());
        subElement.addContent(new Element(INDEX_TAG).setText(index.toString()));
        subElement.addContent(new Element(POLLSET_NUMBER_TAG).setText(pollsetNumber.toString()));
        element.addContent(subElement);
        return JDomUtilities.xmlStringForElement(element);
    }

    @Override
    public void handle(OutgoingRequestService outgoingRequestService) {
        DemandPollControl control = new DemandPollControl(getEventId(), index, pollsetNumber);
        ControlRequest controlRequest = new ControlRequest(getSiteAddress(), getEventId(), control);
        outgoingRequestService.queueOutgoingRequest(controlRequest);
    }

    public static DemandPollEvent demandPollEventEventForElement(Element element, DeviceCache deviceCache) throws InvalidEventFormatException {
        String eventIdString = element.getChildText(EVENT_ID_TAG);
        if (eventIdString == null)
            throw new InvalidEventFormatException("Event is missing an EventId");
        Long eventId = Long.valueOf(element.getChildText(EVENT_ID_TAG));
        String nuid = element.getChildText(NUID_TAG);
        if (nuid == null)
            throw new InvalidEventFormatException("Event is missing an NUID");
        Device device = deviceCache.deviceForNetworkUnitId(nuid);
        if (device == null)
            device = new Device(nuid, 17);
        SiteAddress siteAddress = new NuidSiteAddress(nuid, correctedNetworkNumber(device.getNetworkNumber()));
        String firmware = device.getFirmwareVersion();
        Element subElement = element.getChild(ZiosEventName.DemandPoll.getName());
        String indexString = subElement.getChildText(INDEX_TAG);
        if (indexString == null)
            throw new InvalidEventFormatException("Event is missing an Index");
        Long index = Long.parseLong(indexString);
        String pollsetString = subElement.getChildText(POLLSET_NUMBER_TAG);
        if (pollsetString == null)
            throw new InvalidEventFormatException("Event is missing pollset ID");
        Integer pollsetNumber = Integer.parseInt(pollsetString);
        return new DemandPollEvent(eventId, siteAddress, firmware, index, pollsetNumber);
    }

}
