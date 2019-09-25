package zedi.pacbridge.app.events;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;

public class EventListenerMDBTest extends BaseTestCase {

    private static final String XML_EVENT = 
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
    
    @Mock
    private DependencyResolver dependencyResolver;
    @Mock
    private EventProcessor eventProcessor;
    @Mock
    private TextMessage message;
    @Mock
    private EventParser eventParser;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }
    
    
    @Test
    public void shouldHandleJmsError() throws Exception {
        JMSException jmsException = mock(JMSException.class);
        
        doThrow(jmsException).when(message).getText();
        
        EventListenerMDB eventListener = new EventListenerMDB(eventProcessor, eventParser);
        eventListener.onMessage(message);
    }
    
    @Test
    public void shouldValidateXmlString() throws Exception {
        HandleableEvent event = mock(HandleableEvent.class);
        
        EventListenerMDB eventListener = new EventListenerMDB(eventProcessor, eventParser);
        
        given(message.getText()).willReturn(XML_EVENT);
        given(eventParser.eventForXmlEventString(XML_EVENT)).willReturn(event);
        
        eventListener.onMessage(message);

        verify(message).getText();
        verify(eventParser).eventForXmlEventString(XML_EVENT);
        verify(eventProcessor).processEvent(event);
    }
}
