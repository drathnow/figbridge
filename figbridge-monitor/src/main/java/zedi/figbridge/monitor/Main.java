package zedi.figbridge.monitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.log4j.Level;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figbridge.monitor.utl.BundledReportMessageGenerator;
import zedi.figbridge.monitor.utl.ChallengeResponseMessageBuilder;
import zedi.figbridge.monitor.utl.ClientAuthenticator;
import zedi.figbridge.monitor.utl.Configuration;
import zedi.figbridge.monitor.utl.CountedBytePacketReader;
import zedi.figbridge.monitor.utl.CountedBytePacketWriter;
import zedi.figbridge.monitor.utl.JmsImplementation;
import zedi.figbridge.monitor.utl.ReportMessageListener;
import zedi.figbridge.monitor.utl.ReportSender;
import zedi.pacbridge.app.devices.KeyDecoder;
import zedi.pacbridge.utl.CommandLineParser;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.io.Sha1Hasher;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());
    
    private static final String MQ_WAIT_SECONDS_PROPERTY_NAME = "figbridgeMonitor.mqWaitSeconds";
    private static final String DEFAULT_CONFIG = "bridgemonitor.xml";
    private static final String CONFIG_ARG_KEY = "config";
    private static final String DEBUG_ARG_KEY = "debug";
    private static final String HELP_KEY = "help";
    
    private static final Integer REPORT_ID = 12345;
    private static final Integer IO_ID = 7893;

    private static final Number DEFAULT_MQ_WAIT_SECONDS = 5;
   
    public static void main(String[] args) {
        Status finalStatus = Status.SUCCESS;
        try {
            Properties properties = CommandLineParser.argumentsFromCommandLine(args);
            String configFile = properties.getProperty(CONFIG_ARG_KEY, DEFAULT_CONFIG);
            boolean showHelp = properties.containsKey(HELP_KEY);
            if (showHelp)
                CommandLineParser.displayUsageAndExitWithStatus("usage.txt", 0);
            boolean debug = properties.containsKey(DEBUG_ARG_KEY);
            if (debug)
                org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
            Configuration configuration = loadConfiguration(configFile);
            BridgeMonitor bridgeMonitor = bridgeMonitorForConfiguration(configuration);
            finalStatus = bridgeMonitor.statusOfBridge();
        } catch (Exception e) {
            logger.debug("Error!", e);
            finalStatus = Status.fatalErrorWithMessage(e.toString());
        }
        logger.info(finalStatus.toString());
        System.exit(finalStatus.getNumber());
    }
   
    private static BridgeMonitor bridgeMonitorForConfiguration(Configuration configuration) throws Exception {
        Socket socket = null;
        KeyDecoder keyDecoder = new KeyDecoder();
        socket = new Socket(configuration.getBridgeAddress(), configuration.getBridgePortNumber());
        Long timestamp = System.currentTimeMillis();
        String username = configuration.getNuid();
        ReportMessageListener listener = messageListenerForConfiguration(configuration, timestamp, IO_ID);
        CountedBytePacketReader packetReader = new CountedBytePacketReader(socket);
        CountedBytePacketWriter packetWriter = new CountedBytePacketWriter(socket);
        byte[] secretKey = keyDecoder.decodedBytesForBase64EncodedBytes(configuration.getBase64SecretKey().getBytes());
        ChallengeResponseMessageBuilder messageBuilder = new ChallengeResponseMessageBuilder(new Sha1Hasher(1024), username, secretKey);
        ClientAuthenticator authenticator = new ClientAuthenticator(messageBuilder, packetReader, packetWriter, 2048);
        BundledReportMessageGenerator messageGenerator = new BundledReportMessageGenerator(REPORT_ID, IO_ID, timestamp);
        ReportSender reportSender = new ReportSender(packetReader, packetWriter);
        IntegerSystemProperty mqWaitProperty = new IntegerSystemProperty(MQ_WAIT_SECONDS_PROPERTY_NAME, DEFAULT_MQ_WAIT_SECONDS);
        return new BridgeMonitor(authenticator, reportSender, listener, messageGenerator, mqWaitProperty.currentValue());
    }
    
    private static ReportMessageListener messageListenerForConfiguration(Configuration configuration, Long timestamp, Integer ioId) throws JMSException, NamingException {
        JmsImplementation implementation = new JmsImplementation(configuration.getJmsHostName(), configuration.getJmsQueueManagerName());
        implementation.setClientId("Foo");
        Destination destination = implementation.createDestination(configuration.getJmsEventTopicName());
        Connection connection = implementation.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        ReportMessageListener reportMessageListener = new ReportMessageListener(configuration.getNuid(), (int)(timestamp/1000L), ioId);
        consumer.setMessageListener(reportMessageListener);
        connection.start();
        return reportMessageListener;
    }
    
    private static Configuration loadConfiguration(String configFile) throws IOException, JDOMException {
        FileInputStream fis = new FileInputStream(configFile);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        fis.close();
        return new Configuration(new String(bytes));
    }
}