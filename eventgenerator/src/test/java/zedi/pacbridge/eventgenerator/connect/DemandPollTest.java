package zedi.pacbridge.eventgenerator.connect;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;

import zedi.pacbridge.app.events.connect.DemandPollEvent;
import zedi.pacbridge.eventgenerator.JmsPublisherBaseTest;
import zedi.pacbridge.utl.IpSiteAddress;

public class DemandPollTest extends JmsPublisherBaseTest {
    
    private static final Integer INDEX = 0;
    private static final Integer POLLSET = 1;
    private static final Long EVENT_ID = 123L;
    private static final String ADDRESS = "172.16.10.2";
    private static final Integer NETWORK_NUMBER = 17;
    private static final Integer FIRMWARE_VERSION = 401;
    private static final String SERIAL_NUMBER = "8764";

    @Test
    public void shouldPublishEvent() throws Exception {
        Connection connection = jmsCenter.getConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = jmsCenter.getDestination(EVENTS_TOPIC);
        MessageProducer messageProducer = session.createProducer(destination);
        
        DemandPollEvent event = new DemandPollEvent(INDEX, POLLSET, EVENT_ID, new IpSiteAddress(ADDRESS, NETWORK_NUMBER), FIRMWARE_VERSION, SERIAL_NUMBER);
        TextMessage message = session.createTextMessage();
        message.setText(event.asXmlString());
        messageProducer.send(message);
    }
}
