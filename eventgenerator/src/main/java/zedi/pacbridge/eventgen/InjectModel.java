package zedi.pacbridge.eventgen;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.eventgen.util.SimpleFieldTypeLibrary;
import zedi.pacbridge.eventgen.util.StaticDeviceCache;
import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.msg.JmsImplementor;
import zedi.pacbridge.msg.annotations.JmsImplParam;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.wsmq.WsmqJmsImplementator;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;


public class InjectModel extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(InjectModel.class.getName());
    private static final String QUEUE_MANAGER_NAME = "QM_csmqdev1";
    private static final String HOST_NAME = "csmqdev1";
    private static final String PREPROD_QUEUE_MANAGER_NAME = "QM_esvppmq1";
    private static final String PREPROD_HOST_NAME = "esvppfe1.zediprod.net";
    
    private static WsmqJmsImplementator jmsImplementor;

    @Override
    protected void configure() {
        bind(JmsImplementor.class)
            .annotatedWith(JmsImplParam.class)
            .to(WsmqJmsImplementator.class)
            .in(Scopes.SINGLETON);
        bind(FieldTypeLibrary.class)
            .to(SimpleFieldTypeLibrary.class)
            .in(Scopes.SINGLETON);
        bind(EventPublisher.class)
            .toInstance(eventPublisher());
        bind(StaticEventGenerator.class)
            .in(Scopes.SINGLETON);
        bind(DeviceCache.class)
            .to(StaticDeviceCache.class);
        bind(NotificationCenter.class)
            .in(Scopes.SINGLETON);
    }

    private EventPublisher eventPublisher() {
        try {
            JmsImplementor implementor = jmsImplementor();
            Connection connection = implementor.createConnection();
            String destinationName = "lclp.eventQueue";
            logger.info("Creating destination: " + destinationName);
            Destination destination = implementor.createDestination(destinationName);
            return new EventPublisher(connection, destination);
        } catch (JMSException ex) {
            logger.error("ErrorCode   : " + ex.getErrorCode()); 
            logger.error("Linked Cause: " + ex.getLinkedException().getCause()); 
            logger.error("Message     : " + ex.getMessage()); 
            throw new RuntimeException("ACK!!!", ex);
        } catch (Exception ex) {
            throw new RuntimeException("ACK!!!", ex);
        }
    }

    @Provides
    private WsmqJmsImplementator jmsImplementor() {
        if (jmsImplementor != null)
            return jmsImplementor;
        jmsImplementor = new WsmqJmsImplementator();
        jmsImplementor.setQueueManagerName(Main.getConfiguration().getJmsQManager());
        jmsImplementor.setHostName(Main.getConfiguration().getJmsHost());
        jmsImplementor.setClientId("eventgen");
        try {
            jmsImplementor.initialize();
        } catch (JMSException ex) {
            logger.error("ErrorCode   : " + ex.getErrorCode()); 
            logger.error("Linked Cause: " + ex.getLinkedException().getCause()); 
            logger.error("Message     : " + ex.getMessage()); 
            throw new RuntimeException("ACK!!!", ex);
        } catch (Exception ex) {
            throw new RuntimeException("ACK!!!", ex);
        }
        return jmsImplementor;
    }
}

