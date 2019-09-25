package zedi.pacbridge.eventgen;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import zedi.pacbridge.eventgen.util.Configuration;
import zedi.pacbridge.eventgen.zios.ui.ConsoleTextPane;
import zedi.pacbridge.eventgen.zios.ui.StaticPublisher;
import zedi.pacbridge.eventgen.zios.ui.TextPaneAppender;
import zedi.pacbridge.msg.JmsCenter;
import zedi.pacbridge.utl.CommandLineParser;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.Utilities;
import zedi.swingutl.beans.utl.SwingUtl;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());
    
    public static final String TRACE_JMS_PROPERTIES_PROPERTY_NAME = "traceProperties";
    public static final String SEVER_CHANGED_NOTIFICATION = "ServerChanged";
    
    private static ConsoleAppender consoleApender = new ConsoleAppender(new PatternLayout("%m%n"));
    private static Configuration configuration;
    private static MessageListenerCoordinator coordinator;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            Properties properties = CommandLineParser.argumentsFromCommandLine(args);
            SwingUtl.initLookAndFeel();
            org.apache.log4j.Logger.getRootLogger().addAppender(consoleApender);
            loadConfig(properties.getProperty("config"));
            final Injector injector = Guice.createInjector(new InjectModel());
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        StaticPublisher window = injector.getInstance(StaticPublisher.class);
                        TextPaneAppender appender = new TextPaneAppender(window.getTheLoggingTextPane(), new PatternLayout("%d{yyyy-MMM-dd HH:mm:ss} %m%n"));
                        registerListener(injector, window.getTheEventsTextPane());
                        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
                        org.apache.log4j.Logger.getRootLogger().removeAppender(consoleApender);
                        NotificationCenter center = injector.getInstance(NotificationCenter.class);
                        center.addObserver(configuration, NotificationNames.DEFAULT_USERNAME_CHANGED);
                        center.addObserver(coordinator, NotificationNames.TURN_TRACE_OFF);
                        center.addObserver(coordinator, NotificationNames.TURN_TRACE_ON);
                        window.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
    
    public static void loadConfig(String filename) throws Exception {
        File file;
        if (filename == null)
            file = new File("eventgen.xml");
        else
            file = new File(filename);
        configuration = new Configuration(file);
    }


    private static void registerListener(Injector injector, final ConsoleTextPane consoleTextPane) {
        JmsCenter jmsCenter = injector.getInstance(JmsCenter.class);
        MessageListener messageListener = new MessageListener() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            @Override
            public void onMessage(Message message) {
                try {
                    consoleTextPane.addWarnOutput(dateFormat.format(new Date()) + " " + traceMessage(message));
                } catch (JMSException e) {
                    logger.error("Unable to decode message", e);
                }
            }
        };
        jmsCenter.registerMessageListener(messageListener, getConfiguration().getEventTopicName(), false);
        coordinator = new MessageListenerCoordinator(jmsCenter, messageListener);
    }

    
    @SuppressWarnings("unchecked")
    private static String traceMessage(Message message) throws JMSException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Recieved Event\n");
        if (Boolean.getBoolean(TRACE_JMS_PROPERTIES_PROPERTY_NAME)) {
            stringBuilder.append("JMS Properties: \n");
            stringBuilder.append("    JMSCorrelationID: ").append(message.getJMSCorrelationID()).append('\n');
            stringBuilder.append("    JMSDeliveryMode : ").append(message.getJMSDeliveryMode()).append('\n');
            stringBuilder.append("    JMSDestination  : ").append(message.getJMSDestination()).append('\n');
            stringBuilder.append("    JMSRedelivered  : ").append(message.getJMSRedelivered()).append('\n');
            stringBuilder.append("    JMSExpiration   : ").append(message.getJMSExpiration()).append('\n');
            stringBuilder.append("    JMSMessageID    : ").append(message.getJMSMessageID()).append('\n');
            stringBuilder.append("    JMSTimestamp    : ").append(message.getJMSTimestamp()).append('\n');
            stringBuilder.append("    JMSPriority     : ").append(message.getJMSPriority()).append('\n');
            stringBuilder.append("    JMSReplyTo      : ").append(message.getJMSReplyTo()).append('\n');
            stringBuilder.append("    JMSType         : ").append(message.getJMSType()).append('\n');
            stringBuilder.append("Message Header: \n");
            for (Enumeration<String> enumeration = message.getPropertyNames(); enumeration.hasMoreElements(); ) {
                String name = enumeration.nextElement();
                stringBuilder.append("    ")
                    .append(name)
                    .append(" = ")
                    .append(message.getObjectProperty(name))
                    .append('\n');
             }
        }
        stringBuilder.append(((TextMessage)message).getText());
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
