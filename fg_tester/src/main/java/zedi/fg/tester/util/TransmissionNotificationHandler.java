package zedi.fg.tester.util;

import org.apache.log4j.Logger;

import zedi.fg.tester.ui.ConsoleTextPane;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;

public class TransmissionNotificationHandler implements Notifiable
{
	private static final Logger logger = Logger.getLogger(TransmissionNotificationHandler.class);
	private ZapMessageDecoder messageDecoder = new ZapMessageDecoder();
	private ConsoleTextPane consoleTextPane;
	
	
	public TransmissionNotificationHandler(ConsoleTextPane consoleTextPane)
	{
		this.consoleTextPane = consoleTextPane;
	}

	@Override
	public void handleNotification(Notification notification)
	{
		TransmissionPackage transmissionPackage = notification.getAttachment();
		String prefix = transmissionPackage.getType() == TransmissionPackage.TYPE.BYTES_TRX ? "TRX>" : "RCV>";
		logger.info(prefix + HexStringEncoder.bytesAsHexString(transmissionPackage.getBytes(), transmissionPackage.getOffset(), transmissionPackage.getLength()));

		try {
			String textMessage = messageDecoder.decodePacketBytes(transmissionPackage.getBytes(), transmissionPackage.getOffset()+2, transmissionPackage.getLength()-2);
			consoleTextPane.addTraceOutput(prefix+"\n");
			consoleTextPane.addTraceOutput(textMessage+"\n");
		}
		catch (Exception e)
		{
			logger.error("Unable to translate message", e);
		}	
	}
}
