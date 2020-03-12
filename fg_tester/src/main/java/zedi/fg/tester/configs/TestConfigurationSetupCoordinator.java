package zedi.fg.tester.configs;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import zedi.fg.tester.util.Constants;
import zedi.fg.tester.util.FgMessageSender;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;
import zedi.pacbridge.zap.messages.ZapPacket;

public class TestConfigurationSetupCoordinator implements Notifiable
{
	private static final Logger logger = Logger.getLogger(TestConfigurationSetupCoordinator.class);

	private FgMessageSender messageSender;
	private Deque<ConfigurationSetup> configurationSetups;
	private ConfigurationSetup currentConfigurationSetup;
	private boolean shutdown;
	private final Lock syncLock;
	private final Condition condition;

	public TestConfigurationSetupCoordinator(FgMessageSender messageSender)
	{
		this.messageSender = messageSender;
		this.configurationSetups = new ArrayDeque<ConfigurationSetup>();
		this.syncLock = new ReentrantLock();
		this.condition = syncLock.newCondition();
		this.shutdown = true;
		this.currentConfigurationSetup = null;
	}

	public void start()
	{
		Thread thread = new Thread(new CoordinatorRunner(), "TestConfigurationSetupCoordinator");
		thread.setDaemon(true);
		shutdown = false;
		thread.start();
	}

	public void stop()
	{
		try
		{
			syncLock.lock();
			shutdown = true;
			condition.signal();
		} finally
		{
			syncLock.unlock();
		}
	}

	public void submitConfigurationSetup(ConfigurationSetup configurationSetup)
	{
		try
		{
			syncLock.lock();
			configurationSetups.offer(configurationSetup);
			condition.signal();
		} finally
		{
			syncLock.unlock();
		}
	}


	@Override
	public void handleNotification(Notification notification)
	{
		if (Constants.ZAP_MSG_RECEVIED.equals(notification.getName()))
		{
			ZapPacket packet = notification.getAttachment();
			if (packet.getMessage().messageType().getNumber() == ZapMessageType.ACK_MESSAGE_NUMBER)
			{
				AckMessage ackMessage = (AckMessage)packet.getMessage();
				if (ackMessage.getAckedMessageType().getNumber() == ZapMessageType.CONFIGURE_NUMBER)
				{
					ConfigureResponseAckDetails ackDetails = ackMessage.additionalDetails();
					currentConfigurationSetup.handleConfigurationResponse(ackDetails);
					syncLock.lock();
					condition.signal();
					syncLock.unlock();
				}
			}
		}
	}

	private void processCurrentConfigurationSetups() throws InterruptedException
	{
		try
		{
			ConfigureControl nextControl = null;
			while ((nextControl = currentConfigurationSetup.nextConfigureControl()) != null) 
			{
				messageSender.sendMessageWithSession(nextControl);
				if (!condition.await(5, TimeUnit.SECONDS))
					logger.error("Respone timeout exceeded.  Configure control not ACKed.");
			}
		} catch (IOException e)
		{
			logger.error("Unable to send control", e);
		}
	}

	class CoordinatorRunner implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				syncLock.lock();
				while (!shutdown)
				{
					if (configurationSetups.size() == 0)
						condition.await();
					else
					{
						currentConfigurationSetup = configurationSetups.getFirst();
						configurationSetups.removeFirst();
						processCurrentConfigurationSetups();
					}
				}
			} catch (Exception e)
			{
			    logger.error("Unhandled exception", e);
			} finally
			{
				syncLock.unlock();
			}
		}

	}
}
