package zedi.pacbridge.eventgen.util;

import org.junit.Test;

import zedi.pacbridge.app.events.zios.ConfigureEvent;
import zedi.pacbridge.eventgenerator.JmsPublisherBaseTest;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

public class ConfigureIoPointsEventGeneratorTest extends JmsPublisherBaseTest {
    @Test
    public void shouldDisplayXml() throws Exception {
        FieldTypeLibrary typeLibrary = new SimpleFieldTypeLibrary();
        SiteAddress siteAddress = new IpSiteAddress("1.2.3.4", 17);
        ConfigureIoPointsEventGenerator generator = new ConfigureIoPointsEventGenerator(typeLibrary);
        ConfigureEvent event = generator.eventForSiteAddress(siteAddress);
        System.out.println("XML: " + event.asXmlString());
    }
}
