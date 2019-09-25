package zedi.pacbridge.app.events.connect;

import org.jdom2.Element;

import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;


public class DemandPollEvent extends SiteEvent implements ControlEvent {
    public static final String INDEX_TAG = "Index";
    public static final String POLLSET_NUMBER_TAG = "PollsetNumber";
    
    private Integer index;
    private Integer pollsetNumber;
    
    public DemandPollEvent(Integer index, Integer pollsetNumber, Long eventId, SiteAddress siteAddress, Integer firmwareVersion, String serialNumber) {
        super(ConnectEventName.DemandPoll, eventId, siteAddress, firmwareVersion, serialNumber);
        this.index = index;
        this.pollsetNumber = pollsetNumber;
    }
    
    public DemandPollEvent(Element rootElement) throws InvalidEventFormatException {
        super(ConnectEventName.DemandPoll, rootElement);
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
    }

}