package zedi.pacbridge.app.publishers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.junit.Test;
import org.mockito.InOrder;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.ReflectionHelper;

public class EventPublisherTest extends BaseTestCase {

    private static final String XML_STRING = "<Hello>World</Hello>";
    private static final Long TTL = 100L;
    private static final Long EVENT_ID = 1234L;

    @Test
    public void shouldPublishMessageWithCorrectPriorityWithEventId() throws Exception {
        Topic topic = mock(Topic.class);
        Event event = mock(Event.class);
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        MessageProducer messageProducer = mock(MessageProducer.class);
        TextMessage textMessage = mock(TextMessage.class);
        
        given(connectionFactory.createConnection()).willReturn(connection);
        given(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).willReturn(session);
        given(session.createTextMessage()).willReturn(textMessage);
        given(session.createProducer(topic)).willReturn(messageProducer);
        given(event.getEventQualifier()).willReturn(EventQualifier.ZIOS);
        given(event.getEventName()).willReturn(ZiosEventName.DemandPoll);
        given(event.asXmlString()).willReturn(XML_STRING);
        given(messageProducer.getTimeToLive()).willReturn(TTL);
        given(event.getEventId()).willReturn(EVENT_ID);
     
        InOrder order = inOrder(textMessage, messageProducer, event, connectionFactory, session, connection);
        EventHandler publisher = new EventPublisher();
        ReflectionHelper.setObjectsFieldWithValue(publisher, "connectionFactory", connectionFactory);
        ReflectionHelper.setObjectsFieldWithValue(publisher, "topic", topic);
        
        publisher.publishEvent(event);
        
        order.verify(connectionFactory).createConnection();
        order.verify(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        order.verify(session).createProducer(topic);
        order.verify(session).createTextMessage();
        order.verify(textMessage).clearBody();
        order.verify(textMessage).clearProperties();
        order.verify(textMessage).setStringProperty(EventPublisher.EVENT_QUALIFIER_KEY_NAME, EventQualifier.ZIOS.getName());
        order.verify(textMessage).setStringProperty(EventPublisher.EVENT_NAME_KEY_NAME, ZiosEventName.DemandPoll.getName());
        order.verify(event, times(2)).getEventId();
        order.verify(textMessage).setJMSCorrelationID(EVENT_ID.toString());
        order.verify(event).asXmlString();
        order.verify(textMessage).setText(XML_STRING);
        order.verify(messageProducer).send(textMessage, DeliveryMode.PERSISTENT, EventPublisher.HIGH_PRIORITY, TTL);
        order.verify(messageProducer).close();
        order.verify(session).close();
        order.verify(connection).close();
    }

    @Test
    public void shouldPublishMessageWithCorrectPriorityWithoutEventId() throws Exception {
        Topic topic = mock(Topic.class);
        Event event = mock(Event.class);
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        MessageProducer messageProducer = mock(MessageProducer.class);
        TextMessage textMessage = mock(TextMessage.class);
        
        given(connectionFactory.createConnection()).willReturn(connection);
        given(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).willReturn(session);
        given(session.createTextMessage()).willReturn(textMessage);
        given(session.createProducer(topic)).willReturn(messageProducer);
        given(event.getEventQualifier()).willReturn(EventQualifier.ZIOS);
        given(event.getEventName()).willReturn(ZiosEventName.DemandPoll);
        given(event.asXmlString()).willReturn(XML_STRING);
        given(messageProducer.getTimeToLive()).willReturn(TTL);
        given(event.getEventId()).willReturn(null);
     
        InOrder order = inOrder(textMessage, messageProducer, event, connectionFactory, session, connection);
        EventHandler publisher = new EventPublisher();
        ReflectionHelper.setObjectsFieldWithValue(publisher, "connectionFactory", connectionFactory);
        ReflectionHelper.setObjectsFieldWithValue(publisher, "topic", topic);
        
        publisher.publishEvent(event);
        
        
        order.verify(connectionFactory).createConnection();
        order.verify(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        order.verify(session).createProducer(topic);
        order.verify(session).createTextMessage();
        order.verify(textMessage).clearBody();
        order.verify(textMessage).clearProperties();
        order.verify(textMessage).setStringProperty(EventPublisher.EVENT_QUALIFIER_KEY_NAME, EventQualifier.ZIOS.getName());
        order.verify(textMessage).setStringProperty(EventPublisher.EVENT_NAME_KEY_NAME, ZiosEventName.DemandPoll.getName());
        order.verify(event).getEventId();
        order.verify(event).asXmlString();
        order.verify(textMessage).setText(XML_STRING);
        order.verify(messageProducer).send(textMessage, DeliveryMode.PERSISTENT, EventPublisher.HIGH_PRIORITY, TTL);
        order.verify(messageProducer).close();
        order.verify(session).close();
        order.verify(connection).close();
    }
}
