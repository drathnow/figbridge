package zedi.fg.tester.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.BundledReportAckDetails;
import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.messages.ZapMessage;
import zedi.pacbridge.zap.reporting.ResponseStatus;
import zedi.pacbridge.zap.reporting.ZapReport;

public class BundledReportMessageHandler implements ZapMessageHandler
{
	private static final Logger logger = Logger.getLogger(HeartbeatMessageHandler.class);

	@Override
	public void handleMessageWithFgMessageSender(ZapMessage message, FgMessageSender messageSender)
	{
		if (message.messageType().getNumber() == ZapMessageType.BUNDLED_REPORT_NUMBER)
		{
			BundledReportMessage reportMessage = (BundledReportMessage)message;
	        Set<Integer> reportIds = reportMessage.reportIds();
	        Map<Integer, ZapReport> reports = reportMessage.reportsMap();
	        BundledReportAckDetails reportAck = new BundledReportAckDetails();
	        for (Iterator<Integer> iter = reportIds.iterator(); iter.hasNext(); ) {
	            Integer nextReportId = iter.next();
	            ZapReport report = reports.get(nextReportId);
	            if (report != null) {
	            	reportAck.addReportStatus(nextReportId, ResponseStatus.OK);
	            } else
	                reportAck.addReportStatus(nextReportId, ResponseStatus.PermanentError);
	        }
	        
	        AckMessage ackMessage = new AckMessage(reportMessage.sequenceNumber(), reportMessage.messageType(), reportAck);

			try
			{
				messageSender.sendMessageWithoutSession(ackMessage);
			} catch (IOException e)
			{
				logger.error("Unable to send Heartbeat response");
			}
		}
	}
}
