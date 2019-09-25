package zedi.pacbridge.app.publishers;

import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.clustering.ClusterIndex;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.net.ReasonCode;

@Stateless
public class SiteReportMessageFormatter {
    public static final String EVENT_NAME_KEY_NAME = "EventName";
    public static final String EVENT_QUALIFIER_KEY_NAME = "EventQualifier";
    public static final String CLUSTER_INDEX_KEY = "clusterIndex";
    public TextMessage formatMessageWithSiteReport(TextMessage textMessage, SiteReport siteReport, InterestingSitesCache interestingSitesCache) throws JMSException {
        int clusterIndex = (int)ClusterIndex.newIndex();
        textMessage.clearBody();
        textMessage.clearProperties();
        textMessage.setStringProperty(EVENT_NAME_KEY_NAME, ZiosEventName.SiteReport.getName());
        textMessage.setIntProperty(CLUSTER_INDEX_KEY, clusterIndex);
        textMessage.setJMSCorrelationID(siteReport.getMessageId());
        textMessage.setJMSPriority(priorityForSiteReport(siteReport, interestingSitesCache));
        textMessage.setText(siteReport.asXmlString());
        return textMessage;
    }

    private Integer priorityForSiteReport(SiteReport siteReport, InterestingSitesCache interestingSitesCache) {
        Integer priority = interestingSitesCache.isSiteIneresting(siteReport.getNuid()) ? EventPublisher.HIGH_PRIORITY : EventPublisher.NORMAL_PRIORITY;
        return Math.max(priority, priorityForReasonCode(siteReport.getReasonCode()));
    }
    
    private static int priorityForReasonCode(ReasonCode reasonCode) {
        if (reasonCode != null && reasonCode.isHighPriority())
            return EventPublisher.HIGH_PRIORITY;
        return EventPublisher.NORMAL_PRIORITY;
    }

}
