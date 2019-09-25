package zedi.pacbridge.app.events.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.utl.JDomUtilities;

public class ConnectEventResponseEventTest extends EventTestCase {
    
    @Test
    public void shouldCreateXml() throws Exception {
        ConnectEventResponseEvent event = new ConnectEventResponseEvent(EVENT_ID, EventStatus.Success, "StatusMessage", "ExtraData");
        assertTrue(isValidXml(event.asXmlString()));
        Element element = JDomUtilities.elementForXmlString(event.asXmlString());
        assertEquals(EVENT_ID.toString(), element.getChildText(ConnectEvent.EVENT_ID_TAG));
        Element responseElement = element.getChild(ConnectEventResponseEvent.ROOT_ELEMENT_NAME);
        assertEquals("Success", responseElement.getChildText(ConnectEventResponseEvent.STATUS_TAG));
        assertEquals("StatusMessage", responseElement.getChildText(ConnectEventResponseEvent.MESSAGE_TAG));
        assertEquals("ExtraData", responseElement.getChildText(ConnectEventResponseEvent.EVENT_DATA_TAG));
    }
}
