package zedi.pacbridge.app.events;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jdom2.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.events.connect.ConnectEventParser;
import zedi.pacbridge.app.events.zios.ZiosEventFactory;
import zedi.pacbridge.test.BaseTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EventFactoryFacade.class, ConnectEventParser.class, ZiosEventFactory.class})
public class EventFactoryFacadeTest extends BaseTestCase {

    @Test
    public void shouldNeverHappendButIfItDoesShouldReturnNullIfQualifierIsNotSet() throws Exception {
        Element element = mock(Element.class);
        HandleableEvent event = mock(HandleableEvent.class);
        ZiosEventFactory ziosEventParser = mock(ZiosEventFactory.class);
        
        given(element.getAttributeValue(Event.QUALIFIER_TAG)).willReturn(null);
        given(ziosEventParser.eventForElement(element)).willReturn(event);
        
        EventFactoryFacade facade = new EventFactoryFacade(ziosEventParser);
        assertNull(facade.eventForElement(element));
        verify(element).getAttributeValue(Event.QUALIFIER_TAG);
    }

    @Test
    public void shouldReturnNullForNonExistentEventFactoryForEventQualifier() throws Exception {
        Element element = mock(Element.class);
        HandleableEvent event = mock(HandleableEvent.class);
        ZiosEventFactory ziosEventParser = mock(ZiosEventFactory.class);
        
        given(element.getAttributeValue(Event.QUALIFIER_TAG)).willReturn("FOO");
        given(ziosEventParser.eventForElement(element)).willReturn(event);
        
        EventFactoryFacade facade = new EventFactoryFacade(ziosEventParser);
        assertNull(facade.eventForElement(element));
        verify(element).getAttributeValue(Event.QUALIFIER_TAG);
    }
    
    @Test
    public void shouldReturnEventForEventElement() throws Exception {
        Element element = mock(Element.class);
        HandleableEvent event = mock(HandleableEvent.class);
        ZiosEventFactory ziosEventParser = mock(ZiosEventFactory.class);
        
        given(element.getAttributeValue(Event.QUALIFIER_TAG)).willReturn(EventQualifier.ZIOS.getName());
        given(ziosEventParser.eventForElement(element)).willReturn(event);
        
        EventFactoryFacade facade = new EventFactoryFacade(ziosEventParser);
        HandleableEvent result = facade.eventForElement(element);
        
        assertSame(result, event);
        verify(element).getAttributeValue(Event.QUALIFIER_TAG);
        verify(ziosEventParser).eventForElement(element);
    }
}
