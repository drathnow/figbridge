package zedi.pacbridge.app.events.connect;

import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.test.BaseTestCase;

public class ConnectEventParserTest extends BaseTestCase {

    
    @Test
    public void shouldHurlIfUnknownEventNamePassed() throws Exception {
        Element element = mock(Element.class);
        
        ConnectEventParser eventParser = new ConnectEventParser();
        given(element.getAttributeValue(Event.NAME_TAG)).willReturn("FOO");
        assertNull(eventParser.eventForElement(element));
    }
}
