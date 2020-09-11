package zedi.fg.tester.util;

import org.apache.log4j.Logger;

import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;

public class TransmissionNotificationHandler implements Notifiable
{
	private static final Logger logger = Logger.getLogger(TransmissionNotificationHandler.class);
	private static final Logger traceLogger = Logger.getLogger(Constants.TRACE_LOGGER_NAME);
	private ZapMessageDecoder messageDecoder;
	
	
	public TransmissionNotificationHandler(ZapMessageDecoder messageDecoder)
	{
		this.messageDecoder = messageDecoder;
	}

	@Override
	public void handleNotification(Notification notification)
	{
		TransmissionPackage transmissionPackage = notification.getAttachment();
		String prefix = transmissionPackage.getType() == TransmissionPackage.TYPE.BYTES_TRX ? "TRX>" : "RCV>";
        String hexMsg = HexStringEncoder.bytesAsHexString(transmissionPackage.getBytes(), transmissionPackage.getOffset(), transmissionPackage.getLength());
		logger.info(prefix + hexMsg);

		try 
		{
		    String textMessage = messageDecoder.decodePacketBytes(transmissionPackage.getBytes(), transmissionPackage.getOffset()+2, transmissionPackage.getLength()-2);
		    traceLogger.trace(prefix+'\n'+textMessage);
		} catch (Exception e)
		{
		    logger.error("Unable to translate message\nMSG: " + hexMsg, e);
		}
	}	
}
