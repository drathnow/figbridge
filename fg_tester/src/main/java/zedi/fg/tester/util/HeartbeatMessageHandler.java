package zedi.fg.tester.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.HeartBeatMessage;
import zedi.pacbridge.zap.messages.HeartBeatResponseMessage;
import zedi.pacbridge.zap.messages.ZapMessage;

public class HeartbeatMessageHandler implements ZapMessageHandler
{
	private static final Logger logger = Logger.getLogger(HeartbeatMessageHandler.class);

	@Override
	public void handleMessageWithFgMessageSender(ZapMessage message, FgMessageSender messageSender)
	{
		if (message.messageType().getNumber() == ZapMessageType.HEART_BEAT_MESSAGE_NUMBER)
		{
			HeartBeatMessage heartBeatMessage = (HeartBeatMessage)message;
			HeartBeatResponseMessage response = new HeartBeatResponseMessage(heartBeatMessage.getDeviceTime(), (int)System.currentTimeMillis()/1000);
			try
			{
				messageSender.sendMessageWithoutSession(response);
			} catch (IOException e)
			{
				logger.error("Unable to send Heartbeat response");
			}
		}
	}
}
