package zedi.fg.tester.util;

import java.io.IOException;

import zedi.pacbridge.zap.messages.ZapMessage;

public interface FgMessageSender
{
	void sendMessageWithoutSession(ZapMessage message) throws IOException;
	void sendMessageWithSession(ZapMessage message) throws IOException;
}