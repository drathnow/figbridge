package zedi.pacbridge.app.events.zios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.app.events.WriteValueElement;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;
import zedi.pacbridge.zap.messages.WriteValue;
import zedi.pacbridge.zap.values.ZapValue;

public class WriteIOPointsEvent extends DeviceEvent implements ControlEvent {

    private List<WriteValueElement> writeValueElements = new ArrayList<WriteValueElement>();
    
    public WriteIOPointsEvent(List<WriteValueElement> writeValueElements, Long eventId, SiteAddress siteAddress, String firmwareVersion) {
        super(ZiosEventName.WriteIOPoints, eventId, siteAddress, firmwareVersion);
        this.writeValueElements = writeValueElements;
    }
    
    @Override
    public String asXmlString() {
        Element element =  super.rootElement();
        Element subElement = new Element(getEventName().getName());
        element.addContent(subElement);
        for (Iterator<WriteValueElement> iter = writeValueElements.iterator(); iter.hasNext(); )
            subElement.addContent(iter.next().asElement());
        return JDomUtilities.xmlStringForElement(element);
    }
    
    public List<WriteValueElement> writeValueElements() {
        return Collections.unmodifiableList(writeValueElements);
    }
    
    public void handle(OutgoingRequestService controlService) {
        List<WriteValue> writeValues = new ArrayList<>();
        for (Iterator<WriteValueElement> iter = writeValueElements.iterator(); iter.hasNext(); ) {
            WriteValueElement wve = iter.next();
            WriteValue writeValue = new WriteValue(wve.getIndex(), (ZapValue)wve.getValue());
            writeValues.add(writeValue);
        }
        WriteIoPointsControl control = new WriteIoPointsControl(writeValues, getEventId());
        ControlRequest controlRequest = new ControlRequest(getSiteAddress(), getEventId(), control);
        controlService.queueOutgoingRequest(controlRequest);
    }
    
    public static WriteIOPointsEvent writeIoPointsEventForElement(Element element, DeviceCache deviceCache) throws InvalidEventFormatException {
        List<WriteValueElement> writeValueElements = new ArrayList<WriteValueElement>();

        if (element.getChildText(EVENT_ID_TAG) == null)
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
        Element subElement = element.getChild(ZiosEventName.WriteIOPoints.getName());
        List<Element> elms = subElement.getChildren(WriteValueElement.ROOT_ELEMENT_NAME);
        for (Iterator<Element> iter = elms.iterator(); iter.hasNext(); )
            writeValueElements.add(WriteValueElement.writeValueForElement(iter.next(), dataTypeFactory));
        return new WriteIOPointsEvent(writeValueElements, eventId, siteAddress, firmware);    
    }
}
