package zedi.pacbridge.zap.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapConfigurationUpdateHandler;
import zedi.pacbridge.zap.reporting.ResponseStatus;

public class ConfigureUpdateMessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigureUpdateMessageHandler.class.getName());
    
    public void handleMessageForSiteAddress(ConfigureUpdateMessage updateMessage, SiteAddress siteAddress, ResponseSender responseSender) {
        ZapConfigurationUpdateHandler updateHandler = DependencyResolver.Implementation.sharedInstance().getImplementationOf(ZapConfigurationUpdateHandler.class);
        AckDetails details = null;
        
        logger.debug("Hanlding ConfigureUpdateMessage");
        if (updateHandler.didProcessConfigurationUpdate(siteAddress, updateMessage))
            details = new ConfigureUpdateAckDetails(ResponseStatus.OK);
        else
            details = new ConfigureUpdateAckDetails(ResponseStatus.TransientError);
                
        AckMessage ackMessage = new AckMessage(updateMessage.sequenceNumber(), updateMessage.messageType(), details);
        responseSender.sendResponse(ackMessage);
    }

}
