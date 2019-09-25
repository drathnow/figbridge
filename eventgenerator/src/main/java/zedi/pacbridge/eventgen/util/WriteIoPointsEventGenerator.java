package zedi.pacbridge.eventgen.util;

import java.text.MessageFormat;

import javax.inject.Inject;

import org.jdom2.Element;

import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.zios.WriteIOPointsEvent;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.values.ZapDataType;

public class WriteIoPointsEventGenerator {

    public static final String WRITE_IO_POINTS_EVENT_XML_FMT = 
            "<Event name='WriteIOPoints' qualifier='ZIOS'>"
          + "    <EventId>" + StaticEventGenerator.nextEventId() + "</EventId>"
          + "    <Nuid>{0}</Nuid>"
          + "    <WriteIOPoints>"
          + "        <WriteValue>"
          + "            <Index>100</Index>"
          + "            <DataType>" + ZapDataType.Float.getName() + "</DataType>"
          + "            <Value>1.2</Value>"
          + "        </WriteValue>"
          + "        <WriteValue>"
          + "            <Index>200</Index>"
          + "            <DataType>" + ZapDataType.Float.getName() + "</DataType>"
          + "            <Value>2.3</Value>"
          + "        </WriteValue>"
          + "    </WriteIOPoints>"
          + "</Event>";

    private DeviceCache deviceCache;
    
    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public WriteIoPointsEventGenerator(DeviceCache deviceCache) {
        this.deviceCache = deviceCache;
    }

    public WriteIOPointsEvent eventForSiteAddress(SiteAddress siteAddress) throws Exception {
        String xmlString = MessageFormat.format(WRITE_IO_POINTS_EVENT_XML_FMT, siteAddress.getAddress());
        Element element = JDomUtilities.elementForXmlString(xmlString);
        return WriteIOPointsEvent.writeIoPointsEventForElement(element, deviceCache);
    }
    
}
