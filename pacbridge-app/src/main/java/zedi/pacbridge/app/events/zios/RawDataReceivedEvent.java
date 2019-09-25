package zedi.pacbridge.app.events.zios;

import org.jdom2.Element;

import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.utl.JDomUtilities;

public class RawDataReceivedEvent extends ZiosEvent {
    public static final String ROOT_ELEMENT_NAME = "SiteReport";
    
    private SiteReport siteReport;
    
    public RawDataReceivedEvent(SiteReport siteReport) {
        super(ZiosEventName.RawDataReceived);
    }

    @Override
    public String asXmlString() {
        Element element =  super.rootElement();
        element.addContent(new Element(NUID_TAG).setText(siteReport.getNuid()));
        Element subElement = new Element(getEventName().getName());
        subElement.addContent(new Element(ROOT_ELEMENT_NAME).setText(siteReport.asXmlString()));
        element.addContent(subElement);
        return JDomUtilities.xmlStringForElement(element);
    }
}
