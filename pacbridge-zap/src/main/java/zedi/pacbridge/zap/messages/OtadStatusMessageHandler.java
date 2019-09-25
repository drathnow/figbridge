package zedi.pacbridge.zap.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;

public class OtadStatusMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConfigureUpdateMessageHandler.class.getName());
    
    public void handleMessageForSiteAddress(OtadStatusMessage statusMessage, SiteAddress siteAddress, ResponseSender responseSender) {
        ZapOtadStatusMessageHandler statusHandler = DependencyResolver.Implementation.sharedInstance().getImplementationOf(ZapOtadStatusMessageHandler.class);
        
        if (logger.isDebugEnabled())
            logger.debug("Hanlding OtadStatusMessage");
        if (statusHandler.didProcessStatusUpdateMessage(siteAddress, statusMessage)) {
            AckMessage ackMessage = new AckMessage(statusMessage.sequenceNumber(), statusMessage.messageType());
            responseSender.sendResponse(ackMessage);
        }
    }

}
