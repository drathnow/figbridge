package zedi.pacbridge.zap.messages;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapReportProcessor;
import zedi.pacbridge.zap.reporting.ResponseStatus;
import zedi.pacbridge.zap.reporting.ZapReport;

public class BundledReportMessageHandler {

    public void handleMessageForSiteAddress(BundledReportMessage reportMessage, SiteAddress siteAddress, ResponseSender responseSender) {
        ZapReportProcessor reportProcessor = DependencyResolver.Implementation.sharedInstance().getImplementationOf(ZapReportProcessor.class);
        
        Set<Integer> reportIds = reportMessage.reportIds();
        Map<Integer, ZapReport> reports = reportMessage.reportsMap();
        BundledReportAckDetails reportAck = new BundledReportAckDetails();
        for (Iterator<Integer> iter = reportIds.iterator(); iter.hasNext(); ) {
            Integer nextReportId = iter.next();
            ZapReport report = reports.get(nextReportId);
            if (report != null) {
                if (reportProcessor.didProcessReport(siteAddress, report))
                    reportAck.addReportStatus(nextReportId, ResponseStatus.OK);
                else
                    reportAck.addReportStatus(nextReportId, ResponseStatus.TransientError);
            } else
                reportAck.addReportStatus(nextReportId, ResponseStatus.PermanentError);
        }
        AckMessage ackMessage = new AckMessage(reportMessage.sequenceNumber(), reportMessage.messageType(), reportAck);
        responseSender.sendResponse(ackMessage);
    }

}
