package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;

public class DeviceEventTest extends ZiosEventTestCase {

    public static final String XML_EVENT_WITHOUT_FIRMWARE = 
            "<Event name='WriteIOPoints' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "</Event>";
    
    public static final String XML_EVENT_WITH_FIRMWARE = 
            "<Event name='WriteIOPoints' qualifier='ZIOS'>"
          + "<FirmwareVersion>" + FIRMWARE_VERSION + "</FirmwareVersion>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "</Event>";

    @Test
    public void shouldConstructFromElementWithFirmware() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_EVENT_WITH_FIRMWARE);
        TestableDeviceEvent event = new TestableDeviceEvent(ZiosEventName.WriteIOPoints, element);
        assertEquals(FIRMWARE_VERSION, event.getFirmwareVersion());
        assertEquals(EVENT_ID.longValue(), event.getEventId().longValue());
        assertEquals(NUID, event.getSiteAddress().getAddress());
    }

    @Test
    public void shouldConstructFromElementWithNoFirmware() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_EVENT_WITHOUT_FIRMWARE);
        TestableDeviceEvent event = new TestableDeviceEvent(ZiosEventName.WriteIOPoints, element);
        assertNull(event.getFirmwareVersion());
        assertEquals(EVENT_ID.longValue(), event.getEventId().longValue());
        assertEquals(NUID, event.getSiteAddress().getAddress());
    }

    private class TestableDeviceEvent extends DeviceEvent {
        
        public TestableDeviceEvent(ZiosEventName eventName, Element element) {
            super(eventName, element);
        }

        @Override
        public void handle(OutgoingRequestService outgoingRequestService) {
        }

        @Override
        public String asXmlString() {
            return null;
        }

    }
}
