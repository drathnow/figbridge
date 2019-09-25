package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;

public class ZiosEventTest extends BaseTestCase {

    class TestableZiosEvent extends ZiosEvent {

        protected TestableZiosEvent(ZiosEventName eventName, Long eventId) {
            super(eventName, eventId);
        }

        protected TestableZiosEvent(ZiosEventName eventName) {
            super(eventName);
        }
        
        Element getRootElement() {
            return rootElement();
        }
        
        @Override
        public String asXmlString() {
            return JDomUtilities.xmlStringForElement(rootElement());
        }
    }

    private static final Long EVENT_ID = 42L;
    
    @Test
    public void shouldConstructRootElementWithEventIdContainingValue() throws Exception {
        TestableZiosEvent event = new TestableZiosEvent(ZiosEventName.DemandPoll, EVENT_ID);
        Element rootElement = event.getRootElement();
        
        assertEquals(ZiosEventName.DemandPoll.getName(), rootElement.getAttributeValue(ZiosEvent.NAME_TAG));
        assertEquals(EventQualifier.ZIOS.getName(), rootElement.getAttributeValue(ZiosEvent.QUALIFIER_TAG));
        Element eventIdElement = rootElement.getChild(ZiosEvent.EVENT_ID_TAG);
        assertNotNull(eventIdElement);
        assertEquals(EVENT_ID.toString(), eventIdElement.getText());
    }

    @Test
    public void shouldConstructRootElementWitheEmptyEventIdWhenNoEventIdProvided() throws Exception {
        TestableZiosEvent event = new TestableZiosEvent(ZiosEventName.DemandPoll);
        Element rootElement = event.getRootElement();
        
        assertEquals(ZiosEventName.DemandPoll.getName(), rootElement.getAttributeValue(ZiosEvent.NAME_TAG));
        assertEquals(EventQualifier.ZIOS.getName(), rootElement.getAttributeValue(ZiosEvent.QUALIFIER_TAG));
        Element eventIdElement = rootElement.getChild(ZiosEvent.EVENT_ID_TAG);
        assertNotNull(eventIdElement);
        assertEquals("", eventIdElement.getText());
        System.out.println("XML: " + event.asXmlString());
    }
}
