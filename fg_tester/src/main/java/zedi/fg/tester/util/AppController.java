package zedi.fg.tester.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import zedi.fg.tester.net.ConnectionListener;
import zedi.fg.tester.net.ZapMessageOrchastrator;
import zedi.fg.tester.ui.MainFrame;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.DemandPollControl;

public class AppController implements Notifiable
{ 
	private static final Logger logger = Logger.getLogger(AppController.class);

	private ConnectionListener connectionListener;
	private NotificationCenter notificationCenter;
	private Configuration configuration;
	private MainFrame mainFrame;
	
	public AppController(NotificationCenter notificationCenter, 
					     Configuration configuration,
					     ConnectionListener connectionListener)
	{
		this.connectionListener = connectionListener;
		this.notificationCenter = notificationCenter;
		this.configuration = configuration;
		
		this.notificationCenter.addObserver(this, Constants.FG_CONNECTED);
		this.notificationCenter.addObserver(this, Constants.FG_DISCONNECTED);
	}
	
	public void setMainFrame(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
	}
	
	public void startConnectionListener()
	{
		try {
			connectionListener.start();			
		} catch (Exception e) 
		{
			logger.error("Unable to start listener", e);
			connectionListener = null;
		}
	}
	
	public void stopConnectionListener() 
	{
		if (connectionListener != null)
			connectionListener.close();
		connectionListener = null;
	}
	
	public void shutdown()
	{
		if (connectionListener != null)
			stopConnectionListener();
	}

	@Override
	public void handleNotification(Notification notification)
	{
		if (notification.getName().equals(Constants.FG_CONNECTED))
			mainFrame.enableSendMenu();
		if (notification.getName().equals(Constants.FG_DISCONNECTED))
			mainFrame.enableSendMenu();
	}

	public void sendDemanPoll(Long index, Integer pollset)
	{
		DemandPollControl demandPoll = new DemandPollControl(0L, index, pollset);
		try
		{
			connectionListener.sendMessageWithSession(demandPoll);
		} catch (IOException e)
		{
			logger.error("Unable to send demand poll", e);
		}
	}
}
