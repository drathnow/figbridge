package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.utl.JDomUtilities;


public class ZiosEventResponseEventTest extends ZiosEventTestCase {
    private static final String NUID = "DavesNotHere";
    
    private static final String XML_EVENT_WITHOUT_RESPONSE_TO =
            "<Event name=\"EventResponse\" qualifier=\"ZIOS\">"
            + "    <EventId>12</EventId>"
            + "    <Nuid>12345DavesNotHere</Nuid>"
            + "    <EventResponse>"
            + "        <Status>Success</Status>"
            + "        <Message>StatusMessage</Message>"
            + "        <EventData></EventData>"
            + "    </EventResponse>"
            + "</Event>";    
    
    private static final String XML_EVENT_WITH_RESPONSE_TO =
            "<Event name=\"EventResponse\" qualifier=\"ZIOS\">"
            + "    <EventId>12</EventId>"
            + "    <Nuid>12345DavesNotHere</Nuid>"
            + "    <EventResponse>"
            + "        <ResponseTo>" + ZiosEventName.Scrub.getName() + "</ResponseTo>"
            + "        <Status>Success</Status>"
            + "        <Message>StatusMessage</Message>"
            + "        <EventData>"
            + "            <Property name='Percent' value='75'/>"
            + "            <Property name='Step' value='Downloading'/>"
            + "        </EventData>"
            + "    </EventResponse>"
            + "</Event>";    

    @Test
    public void shouldIsValidXml() throws Exception {
        assertIsValidXml(XML_EVENT_WITHOUT_RESPONSE_TO);       
        assertIsValidXml(XML_EVENT_WITH_RESPONSE_TO);       
    }
    
    @Test
    public void shouldCreateXmlWithoutResponseTo() throws Exception {
        ZiosEventResponseEvent event = new ZiosEventResponseEvent(EVENT_ID, EventStatus.Success, NUID, "StatusMessage", null);
        Element element = JDomUtilities.elementForXmlString(event.asXmlString());
        assertEquals(EVENT_ID.toString(), element.getChildText(ZiosEvent.EVENT_ID_TAG));
        assertEquals(NUID, element.getChildText(ZiosEvent.NUID_TAG));
        Element responseElement = element.getChild(ZiosEventResponseEvent.ROOT_ELEMENT_NAME);
        assertNull(responseElement.getChild(ZiosEventResponseEvent.RESPONSE_TO_TAG));
        assertEquals("Success", responseElement.getChildText(ZiosEventResponseEvent.STATUS_TAG));
        assertEquals("StatusMessage", responseElement.getChildText(ZiosEventResponseEvent.MESSAGE_TAG));
        assertNull(responseElement.getChildText(EventData.ROOT_ELEMENT_NAME));
    }
    
    @Test
    public void shouldCreateXmlWithResponseTo() throws Exception {
        EventData eventData = new EventData();
        eventData.addProperty("Step", "Downloading");
        eventData.addProperty("Percent", "75");
        ZiosEventResponseEvent event = new ZiosEventResponseEvent(EVENT_ID, EventStatus.Success, NUID, ZiosEventName.Scrub, "StatusMessage", eventData);
        System.out.println(event.asXmlString());
        assertIsValidXml(event.asXmlString());
        Element element = JDomUtilities.elementForXmlString(event.asXmlString());
        assertEquals(EVENT_ID.toString(), element.getChildText(ZiosEvent.EVENT_ID_TAG));
        assertEquals(NUID, element.getChildText(ZiosEvent.NUID_TAG));
        Element responseElement = element.getChild(ZiosEventResponseEvent.ROOT_ELEMENT_NAME);
        assertNotNull(responseElement.getChild(ZiosEventResponseEvent.RESPONSE_TO_TAG));
        assertEquals(ZiosEventName.Scrub.getName(), responseElement.getChildText(ZiosEventResponseEvent.RESPONSE_TO_TAG));
        assertEquals("Success", responseElement.getChildText(ZiosEventResponseEvent.STATUS_TAG));
        assertEquals("StatusMessage", responseElement.getChildText(ZiosEventResponseEvent.MESSAGE_TAG));
        Element eventElement = responseElement.getChild(EventData.ROOT_ELEMENT_NAME); 
        assertNotNull(eventElement);
        List<Element> children = eventElement.getChildren(EventData.PROPERTY_TAG);
        assertEquals(2, children.size());
        assertEquals("Percent", children.get(0).getAttributeValue(EventData.NAME_TAG));
        assertEquals("75", children.get(0).getAttributeValue(EventData.VALUE_TAG));
        assertEquals("Step", children.get(1).getAttributeValue(EventData.NAME_TAG));
        assertEquals("Downloading", children.get(1).getAttributeValue(EventData.VALUE_TAG));
    }
}
