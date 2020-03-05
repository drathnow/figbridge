package zedi.fg.tester.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import zedi.fg.tester.util.BundledReportMessageHandler;
import zedi.fg.tester.util.Constants;
import zedi.fg.tester.util.FgMessageSender;
import zedi.fg.tester.util.HeartbeatMessageHandler;
import zedi.fg.tester.util.ZapMessageHandler;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.ZapMessage;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapPacketHeader;

public class ZapMessageOrchastrator implements Notifiable
{
	private static final Logger logger = Logger.getLogger(ZapMessageOrchastrator.class);
	private FgMessageSender messageSender;
	
	private Map<ZapMessageType, ZapMessageHandler> handlerMap = new HashMap<ZapMessageType, ZapMessageHandler>();
	
	private ZapMessageOrchastrator(FgMessageSender messageSender)
	{
		this.messageSender = messageSender;
	}

	@Override
	public void handleNotification(Notification notification)
	{
		if (notification.getName().equals(Constants.ZAP_MSG_RECEVIED))
		{
			ZapPacket packet = notification.<ZapPacket>getAttachment();
			
			ZapMessageHandler messageHandler = handlerMap.get(packet.getHeader().messageType());
			if (messageHandler != null)
				messageHandler.handleMessageWithFgMessageSender((ZapMessage)packet.getMessage(), messageSender);
		}
	}
	
	public void registerHandler(ZapMessageType messageType, ZapMessageHandler messageHandler)
	{
		handlerMap.put(messageType, messageHandler);
	}
	
	public static ZapMessageOrchastrator buildZapMessageOrchastratorWithFgMessageSender(FgMessageSender messageSender)
	{
		ZapMessageOrchastrator messageOrchastrator = new ZapMessageOrchastrator(messageSender);
		messageOrchastrator.registerHandler(ZapMessageType.HeartBeat, new HeartbeatMessageHandler());
		messageOrchastrator.registerHandler(ZapMessageType.BundledReport, new BundledReportMessageHandler());
		return messageOrchastrator;
	}
}
