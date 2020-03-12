package zedi.fg.tester.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.ConfigureUpdateAckDetails;
import zedi.pacbridge.zap.messages.ZapMessage;
import zedi.pacbridge.zap.reporting.ResponseStatus;

public class ConfigureUpdateHandler implements ZapMessageHandler
{
	private static final Logger logger = Logger.getLogger(ConfigureUpdateHandler.class);

	@Override
	public void handleMessageWithFgMessageSender(ZapMessage message, FgMessageSender messageSender)
	{
		ConfigureUpdateAckDetails details = new ConfigureUpdateAckDetails(ResponseStatus.OK);
		AckMessage ackMessage = new AckMessage(message.sequenceNumber(), message.messageType(), details);
		try
		{
			messageSender.sendMessageWithoutSession(ackMessage);
		} catch (IOException e)
		{
			logger.error("Unable to send ACK message", e);
		}
	}
}
