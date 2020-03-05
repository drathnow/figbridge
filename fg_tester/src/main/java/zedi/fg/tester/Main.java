package zedi.fg.tester;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.UIManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.fg.tester.net.ConnectionListener;
import zedi.fg.tester.net.ZapMessageOrchastrator;
import zedi.fg.tester.ui.MainFrame;
import zedi.fg.tester.ui.TextPaneAppender;
import zedi.fg.tester.util.AppController;
import zedi.fg.tester.util.Configuration;
import zedi.fg.tester.util.Constants;
import zedi.fg.tester.util.TransmissionNotificationHandler;
import zedi.pacbridge.utl.NotificationCenter;

public class Main
{
	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class.getName());
	private static ConsoleAppender consoleApender = new ConsoleAppender(new PatternLayout("%m%n"));
	private static NotificationCenter notificationCenter = new NotificationCenter();
    private static ZapMessageOrchastrator messageOrchastrator;
    private static TransmissionNotificationHandler notificationHandler;
    
	public static void main(String[] args)
	{
		org.apache.log4j.Logger.getRootLogger().addAppender(consoleApender);
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			Configuration configuration = loadConfiguration();
			ConnectionListener connectionListener = new ConnectionListener(notificationCenter, configuration.getListeningAddress(), configuration.getPort());
			AppController appController = new AppController(notificationCenter, configuration, connectionListener);
			messageOrchastrator = ZapMessageOrchastrator.buildZapMessageOrchastratorWithFgMessageSender(connectionListener);
			notificationCenter.addObserver(messageOrchastrator, Constants.ZAP_MSG_RECEVIED);
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			MainFrame window = new MainFrame(appController);
			TextPaneAppender appender = new TextPaneAppender(window.getLoggingPane(), new PatternLayout("%d{yyyy-MMM-dd HH:mm:ss} %m%n"));
			org.apache.log4j.Logger.getRootLogger().addAppender(appender);
			appender = new TextPaneAppender(window.getTracePane(), new PatternLayout("%m%n"));
			org.apache.log4j.Logger.getRootLogger().removeAppender(consoleApender);

			notificationHandler = new TransmissionNotificationHandler(window.getTracePane());
			notificationCenter.addObserver(notificationHandler, Constants.TRANSMISSION_NOTIFICATION_NAME);

			window.setVisible(true);
			appController.startConnectionListener();
		} catch (Exception e)
		{
			logger.error("ACK!!!", e);
			System.exit(1);
		}
	}
	
	private static Configuration loadConfiguration() throws Exception
	{
		FileInputStream fileInputStream = new FileInputStream(new File("fgtester.xml"));
		Configuration configuration = new Configuration();
		configuration.serialize(fileInputStream);
		return configuration;

	}
}
