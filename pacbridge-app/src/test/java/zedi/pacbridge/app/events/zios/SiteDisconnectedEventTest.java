package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.annotations.SampleEventXML;
import zedi.pacbridge.utl.JDomUtilities;

public class SiteDisconnectedEventTest extends ZiosEventTestCase {
    
    private static final String TIMESTAMP_STRING = "2015-06-24T15:32:28Z";
    private static final String BRIDGE_INSTANCE = "Bridge123";
    private static final String IP_ADDRESS = "1.2.3.4";
    private static final String BYTES_RCV = "100";
    private static final String BYTES_TRX = "200";

    public static final String XML_EVENT = 
            "<Event name='SiteDisconnected' qualifier='ZIOS'>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <SiteDisconnected>"
          + "       <BridgeInstance>" + BRIDGE_INSTANCE + "</BridgeInstance>"
          + "       <IpAddress>" + IP_ADDRESS + "</IpAddress>"
          + "       <BytesReceived>" + BYTES_RCV + "</BytesReceived>"
          + "       <BytesTransmitted>" + BYTES_TRX + "</BytesTransmitted>"
          + "       <TimestampUtc>" + TIMESTAMP_STRING + "</TimestampUtc>"
          + "    </SiteDisconnected>"
          + "</Event>";
    
    @Test
    public void shouldFormatDate() throws Exception {
        Date timestamp = SiteConnectionEvent.dateFormat.parse(TIMESTAMP_STRING);
        SiteDisconnectedEvent event = new SiteDisconnectedEvent(SITE_ADDRESS, BRIDGE_INSTANCE, IP_ADDRESS, Integer.parseInt(BYTES_RCV), Integer.parseInt(BYTES_TRX), timestamp);
        String xmlString = event.asXmlString();
        assertIsValidXml(xmlString);
        Element element = JDomUtilities.elementForXmlString(xmlString);
        String timestampString = element.getChild(ZiosEventName.SiteDisconnected.getName()).getChildText(SiteConnectionEvent.TIMESTAMP_TAG);
        assertEquals(TIMESTAMP_STRING, timestampString);
    }
    
    @Test
    public void shouldBeValidStaticXML() throws Exception {
        assertIsValidXml(XML_EVENT);
    }
    
    @Test
    public void testAsXmlString() throws Exception {
        SiteDisconnectedEvent event = new SiteDisconnectedEvent(SITE_ADDRESS, BRIDGE_INSTANCE, IP_ADDRESS, Integer.parseInt(BYTES_RCV), Integer.parseInt(BYTES_TRX));
        assertIsValidXml(event.asXmlString());
    }
    
    @SampleEventXML
    public static String sampleXml() {
        return XML_EVENT;
    }
}
