package zedi.pacbridge.app.events;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.util.EventDocumentFactory;
import zedi.pacbridge.test.BaseTestCase;

public class EventParserTest extends BaseTestCase {
    private static final String VALIDATION_ERROR = "Its wrong";
    private static final String VALID_XML_EVENT = 
            "<Event name='SetIOPointValue' qualifier='Connect'>"
          + "    <EventId>342</EventId>"
          + "    <IpAddress>1.2.3.4</IpAddress>"
          + "    <NetworkNumber>12</NetworkNumber>"
          + "    <SerialNumber>TEST_89433894934</SerialNumber>"
          + "    <FirmwareVersion>400</FirmwareVersion>"
          + "    <SetIOPointValue>"
          + "        <WriteValue>"
          + "            <Index>42</Index>"
          + "            <DataType>Float</DataType>"
          + "            <Value>4.5</Value>"
          + "        </WriteValue>"
          + "    </SetIOPointValue>"
          + "</Event>";

    private static final String INVALID_XML_EVENT = 
            "<Event name='SetIOPointValue' qualifier='Connect'>"
          + "    <EventId>342</EventId>"
          + "    <NetworkNumber>12</NetworkNumber>"
          + "    <SerialNumber>TEST_89433894934</SerialNumber>"
          + "    <FirmwareVersion>400</FirmwareVersion>"
          + "    <SetIOPointValue>"
          + "        <WriteValue>"
          + "            <Index>42</Index>"
          + "            <DataType>Float</DataType>"
          + "            <Value>4.5</Value>"
          + "        </WriteValue>"
          + "    </SetIOPointValue>"
          + "</Event>";

    @Test
    public void shouldParseInvalidXmlStringAndReturnNull() throws Exception {
        EventFactoryFacade eventFactory = mock(EventFactoryFacade.class);
        EventDocumentFactory documentFactory = mock(EventDocumentFactory.class);
        EventXmlValidator eventXmlValidator = mock(EventXmlValidator.class);
        given(eventXmlValidator.isValidXml(INVALID_XML_EVENT)).willReturn(false);
        given(eventXmlValidator.getLastError()).willReturn(VALIDATION_ERROR);
        
        EventParser eventParser = new EventParser(eventFactory, eventXmlValidator, documentFactory);
        HandleableEvent result = eventParser.eventForXmlEventString(VALID_XML_EVENT);
        
        assertNull(result);
        verify(eventXmlValidator).isValidXml(VALID_XML_EVENT);
        verify(documentFactory, never()).documentForXmlString(VALID_XML_EVENT);
        verify(eventFactory, never()).eventForElement(any(Element.class));
        verify(eventXmlValidator).getLastError();
    }
    
    @Test
    public void shouldParseValidXmlString() throws Exception {
        EventFactoryFacade eventFactory = mock(EventFactoryFacade.class);
        EventDocumentFactory documentFactory = mock(EventDocumentFactory.class);
        EventXmlValidator eventXmlValidator = mock(EventXmlValidator.class);
        Document document = mock(Document.class);
        Element element = mock(Element.class);
        HandleableEvent event = mock(HandleableEvent.class);
        
        given(eventFactory.eventForElement(element)).willReturn(event);
        given(documentFactory.documentForXmlString(VALID_XML_EVENT)).willReturn(document);
        given(document.getRootElement()).willReturn(element);
        given(eventXmlValidator.isValidXml(VALID_XML_EVENT)).willReturn(true);
        
        EventParser eventParser = new EventParser(eventFactory, eventXmlValidator, documentFactory);
        HandleableEvent result = eventParser.eventForXmlEventString(VALID_XML_EVENT);
        
        verify(eventXmlValidator).isValidXml(VALID_XML_EVENT);
        verify(documentFactory).documentForXmlString(VALID_XML_EVENT);
        verify(eventFactory).eventForElement(element);
        assertSame(result, event);
    }
}
