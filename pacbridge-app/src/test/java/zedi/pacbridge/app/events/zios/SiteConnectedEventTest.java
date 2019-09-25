package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.annotations.SampleEventXML;
import zedi.pacbridge.utl.JDomUtilities;

public class SiteConnectedEventTest extends ZiosEventTestCase {
    private static final String TIMESTAMP_STRING = "2015-06-24T15:32:28Z";
    private static final String BRIDGE_INSTANCE = "Bridge123";
    private static final String IP_ADDRESS = "1.2.3.4";
    private static final String FIRMWARE_VERSION = "V1.2.3";
    
    public static final String XML_EVENT_WITHOUT_FIRMWARE = 
            "<Event name='SiteConnected' qualifier='ZIOS'>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <SiteConnected>"
          + "       <BridgeInstance>" + BRIDGE_INSTANCE + "</BridgeInstance>"
          + "       <IpAddress>" + IP_ADDRESS + "</IpAddress>"
          + "       <TimestampUtc>" + TIMESTAMP_STRING + "</TimestampUtc>"
          + "    </SiteConnected>"
          + "</Event>";

    public static final String XML_EVENT_WITH_FIRMWARE = 
            "<Event name='SiteConnected' qualifier='ZIOS'>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <SiteConnected>"
          + "       <BridgeInstance>" + BRIDGE_INSTANCE + "</BridgeInstance>"
          + "       <IpAddress>" + IP_ADDRESS + "</IpAddress>"
          + "       <TimestampUtc>" + TIMESTAMP_STRING + "</TimestampUtc>"
          + "       <FirmwareVersion>" + FIRMWARE_VERSION + "</FirmwareVersion>"
          + "    </SiteConnected>"
          + "</Event>";

    @Test
    public void shouldFormatDate() throws Exception {
        Date timestamp = SiteConnectionEvent.dateFormat.parse(TIMESTAMP_STRING);
        SiteConnectedEvent event = new SiteConnectedEvent(SITE_ADDRESS, BRIDGE_INSTANCE, IP_ADDRESS, timestamp, null);
        String xmlString = event.asXmlString();
        assertIsValidXml(xmlString);
        Element element = JDomUtilities.elementForXmlString(xmlString);
        String timestampString = element.getChild(ZiosEventName.SiteConnected.getName()).getChildText(SiteConnectionEvent.TIMESTAMP_TAG);
        assertEquals(TIMESTAMP_STRING, timestampString);
    }
    
    @Test
    public void shouldBeValidStaticXMLWithoutFirmware() throws Exception {
        assertIsValidXml(XML_EVENT_WITHOUT_FIRMWARE);
    }
    
    @Test
    public void shouldBeValidStaticXMLWithFirmware() throws Exception {
        assertIsValidXml(XML_EVENT_WITH_FIRMWARE);
    }

    @Test
    public void testAsXmlStringWithoutFirmware() throws Exception {
        SiteConnectedEvent event = new SiteConnectedEvent(SITE_ADDRESS, BRIDGE_INSTANCE, IP_ADDRESS, null);
        assertIsValidXml(event.asXmlString());
        Document document = JDomUtilities.jdomDocumentForXmlString(event.asXmlString());
        Element root = document.getRootElement();
        String version = root.getChild(ZiosEventName.SiteConnected.getName()).getChildText(SiteConnectedEvent.FIRMWARE_VERSION_TAG);
        assertNull(version);
    }

    @Test
    public void testAsXmlStringWithFirmware() throws Exception {
        SiteConnectedEvent event = new SiteConnectedEvent(SITE_ADDRESS, BRIDGE_INSTANCE, IP_ADDRESS, FIRMWARE_VERSION);
        assertIsValidXml(event.asXmlString());
        System.out.println("XML: " + event.asXmlString());
        Document document = JDomUtilities.jdomDocumentForXmlString(event.asXmlString());
        Element root = document.getRootElement();
        Element element = root.getChild(ZiosEventName.SiteConnected.getName()).getChild(SiteConnectedEvent.FIRMWARE_VERSION_TAG);
        assertNotNull(element);
        assertEquals(FIRMWARE_VERSION, element.getText());
    }

    @SampleEventXML
    public static String sampleXml() {
        return XML_EVENT_WITHOUT_FIRMWARE;
    }
}
