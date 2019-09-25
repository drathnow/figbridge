package zedi.pacbridge.app.events.zios;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.OtadStatus;

public class OtadStatusUpdateEventTest extends ZiosEventTestCase {
    private static final String ERROR_MESSAGE = "Something went wrong";
    
    public static final String XML_FULL_EVENT = 
            "<Event name='OtadStatus' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <OtadStatus>"
          + "        <Status>" + OtadStatus.FAILED.getName() + "</Status>"
          + "        <OptionalData>" + ERROR_MESSAGE + "</OptionalData>"
          + "    </OtadStatus>"
          + "</Event>";

    private Element eventElement;
    
    @Override
    public void setUp() throws Exception {
        eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
    }
    
    @Test
    public void shouldCreateValidXmlWithOptionalData() throws Exception {
        OtadStatusUpdateEvent event = new OtadStatusUpdateEvent(EVENT_ID, SITE_ADDRESS, OtadStatus.FAILED, ERROR_MESSAGE);
        assertIsValidXml(event.asXmlString());
    }

    @Test
    public void shouldCreateValidXmlWithNoOptionalData() throws Exception {
        OtadStatusUpdateEvent event = new OtadStatusUpdateEvent(EVENT_ID, SITE_ADDRESS, OtadStatus.INSTALLING, null);
        assertIsValidXml(event.asXmlString());
    }
}
