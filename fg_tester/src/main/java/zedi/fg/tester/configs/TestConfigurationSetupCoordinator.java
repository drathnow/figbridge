package zedi.fg.tester.configs;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
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
	private Deque<ConfigureControl> controls;
	private ConfigureControl pendingControl;
	private boolean shutdown;
	private final Lock syncLock;
	private final Condition condition;

	public TestConfigurationSetupCoordinator(FgMessageSender messageSender)
	{
		this.messageSender = messageSender;
		this.controls = new ArrayDeque<ConfigureControl>();
		this.syncLock = new ReentrantLock();
		this.condition = syncLock.newCondition();
		this.shutdown = true;
		this.pendingControl = null;
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

	public void addConfigureControl(ConfigureControl control)
	{
		try
		{
			syncLock.lock();
			controls.offer(control);
			condition.signal();
		} finally
		{
			syncLock.unlock();
		}
	}

	public void addConfigureControls(List<ConfigureControl> controls)
	{
		for (ConfigureControl control : controls)
			addConfigureControl(control);
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
					ConfigureResponseAckDetails details = ackMessage.additionalDetails();
					syncLock.lock();
					try
					{
						if (pendingControl != null && pendingControl.getObjectType().getNumber() == details.getObjectType().getNumber())
							condition.signal();
					} finally
					{
						syncLock.unlock();
					}
				}
			}
		}
	}

	private void processPendingControl() throws InterruptedException
	{
		try
		{
			messageSender.sendMessageWithSession(pendingControl);
			if (!condition.await(5, TimeUnit.SECONDS))
				logger.error("Respone timeout exceeded.  Configure control not ACKed.");
		} catch (IOException e)
		{
			logger.error("Unable to send control", e);
		} finally
		{
			pendingControl = null;
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
					if (controls.size() == 0)
						condition.await();
					else
					{
						pendingControl = controls.getFirst();
						controls.removeFirst();
						processPendingControl();
					}
				}
			} catch (Exception e)
			{
			} finally
			{
				syncLock.unlock();
			}
		}

	}
}
