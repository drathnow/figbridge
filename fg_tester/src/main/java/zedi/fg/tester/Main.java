package zedi.fg.tester;

import javax.swing.UIManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.varia.LevelMatchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import zedi.fg.tester.configs.TestConfigurationSetupCoordinator;
import zedi.fg.tester.configs.TestConfigurator;
import zedi.fg.tester.net.ConnectionListener;
import zedi.fg.tester.net.ZapMessageOrchastrator;
import zedi.fg.tester.ui.MainFrame;
import zedi.fg.tester.ui.TextPaneAppender;
import zedi.fg.tester.util.AckDecoderNotificationHandler;
import zedi.fg.tester.util.AppController;
import zedi.fg.tester.util.Configuration;
import zedi.fg.tester.util.ConfigurationSerializer;
import zedi.fg.tester.util.Constants;
import zedi.fg.tester.util.TransmissionNotificationHandler;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;

public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class.getName());
    private static ConsoleAppender consoleApender = new ConsoleAppender(new PatternLayout("%m%n"));
    private static NotificationCenter notificationCenter;
    private static ZapMessageOrchastrator messageOrchastrator;
    private static TransmissionNotificationHandler notificationHandler;
    private static AckDecoderNotificationHandler ackDecoderNotificationHandler;
    private static FieldTypeLibrary fieldTypeLibrary;

    public static void main(String[] args)
    {
        Injector injector = Guice.createInjector(new InjectorModel());
        org.apache.log4j.Logger.getRootLogger().addAppender(consoleApender);
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            notificationCenter = injector.getInstance(NotificationCenter.class);
            fieldTypeLibrary = injector.getInstance(FieldTypeLibrary.class);
            
            Configuration configuration = injector.getInstance(Configuration.class);
            configuration.load();
            
            ConnectionListener connectionListener = new ConnectionListener(notificationCenter, configuration.getListeningAddress(), configuration.getPort(), fieldTypeLibrary);
            TestConfigurationSetupCoordinator coordinator = new TestConfigurationSetupCoordinator(connectionListener);
            coordinator.start();
            TestConfigurator configurationFactory = new TestConfigurator(fieldTypeLibrary, coordinator);

            AppController appController = new AppController(notificationCenter, configuration, connectionListener, configurationFactory);
            messageOrchastrator = ZapMessageOrchastrator.buildZapMessageOrchastratorWithFgMessageSender(connectionListener);
            notificationCenter.addObserver(messageOrchastrator, Constants.ZAP_MSG_RECEVIED);
            notificationCenter.addObserver(coordinator, Constants.ZAP_MSG_RECEVIED);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainFrame window = new MainFrame(appController);
            TextPaneAppender appender = new TextPaneAppender(window.getLoggingPane(), new PatternLayout("%d{yyyy-MMM-dd HH:mm:ss} %m%n"));
            org.apache.log4j.Logger.getRootLogger().addAppender(appender);

            TextPaneAppender traceAppender = new TextPaneAppender(window.getTracePane(), new PatternLayout("%m%n"));
            LevelMatchFilter acceptFilter = new LevelMatchFilter();
            acceptFilter.setAcceptOnMatch(true);
            acceptFilter.setLevelToMatch("TRACE");
            traceAppender.addFilter(acceptFilter);
            org.apache.log4j.Logger.getLogger(Constants.TRACE_LOGGER_NAME).addAppender(traceAppender);
            org.apache.log4j.Logger.getLogger(Constants.TRACE_LOGGER_NAME).setLevel(Level.TRACE);
            org.apache.log4j.Logger.getLogger(Constants.TRACE_LOGGER_NAME).setAdditivity(false);

//            org.apache.log4j.Logger.getRootLogger().removeAppender(consoleApender);

            notificationHandler = new TransmissionNotificationHandler(new ZapMessageDecoder(fieldTypeLibrary));
            notificationCenter.addObserver(notificationHandler, Constants.TRANSMISSION_NOTIFICATION_NAME);
            
            ackDecoderNotificationHandler = new AckDecoderNotificationHandler(fieldTypeLibrary);
            notificationCenter.addObserver(ackDecoderNotificationHandler, Constants.TRANSMISSION_NOTIFICATION_NAME);

            window.setVisible(true);
            appController.startConnectionListener();
        } catch (Exception e)
        {
            logger.error("ACK!!!", e);
            System.exit(1);
        }
    }
}
