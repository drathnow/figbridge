package zedi.fg.tester.util;

import zedi.pacbridge.zap.messages.ZapMessage;

public interface ZapMessageHandler
{
	void handleMessageWithFgMessageSender(ZapMessage message, FgMessageSender messageSender);
}
