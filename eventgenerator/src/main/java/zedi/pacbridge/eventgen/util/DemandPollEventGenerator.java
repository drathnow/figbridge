package zedi.pacbridge.eventgen.util;

import java.text.MessageFormat;

import javax.inject.Inject;

import org.jdom2.Element;

import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.zios.DemandPollEvent;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;

public class DemandPollEventGenerator {
    public static final Integer DEFAULT_POLLSET = 0;
    public static final Integer DEFAULT_INDEX = 0;
    
    public static final String DEMAND_POLL_EVENT_PID_XML_FMT = 
                    "<Event name='DemandPoll' qualifier='ZIOS'>"
                  + "    <EventId>" + StaticEventGenerator.nextEventId() + "</EventId>"
                  + "    <Nuid>{0}</Nuid>"
                  + "    <DemandPoll>"
                  + "        <Index>{1, number, integer}</Index>"
                  + "        <PollsetNumber>{2, number, integer}</PollsetNumber>"
                  + "    </DemandPoll>"
                  + "</Event>";
        
    private DeviceCache deviceCache;
    
    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public DemandPollEventGenerator(DeviceCache deviceCache) {
        this.deviceCache = deviceCache;
    }
    
    public DemandPollEvent eventForSiteAddress(SiteAddress siteAddress) throws Exception {
        return demandPollEventForSiteAddress(siteAddress, DEFAULT_INDEX, DEFAULT_POLLSET);
    }
    
    public DemandPollEvent demandPollEventForSiteAddress(SiteAddress siteAddress, Integer index, Integer pollsetNumber) throws Exception {
        String xmlString = MessageFormat.format(DEMAND_POLL_EVENT_PID_XML_FMT, siteAddress.getAddress(), index, pollsetNumber);
        Element element = JDomUtilities.elementForXmlString(xmlString);
        return DemandPollEvent.demandPollEventEventForElement(element, deviceCache);
    }
}
