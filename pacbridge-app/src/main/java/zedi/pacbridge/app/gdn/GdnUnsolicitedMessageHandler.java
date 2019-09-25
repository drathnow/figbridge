package zedi.pacbridge.app.gdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.SessionManager;
import zedi.pacbridge.net.annotations.HandleUnsolicitedMessage;
import zedi.pacbridge.net.annotations.UnsolicitedMessageHandler;
import zedi.pacbridge.utl.SiteAddress;

@UnsolicitedMessageHandler(forNetworkType="GDN")
public class GdnUnsolicitedMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(GdnUnsolicitedMessageHandler.class.getName());
    
    @HandleUnsolicitedMessage
    public void handleUnsolicitedMessage(SiteAddress siteAddress, Message message, SessionManager sessionManager) {
        logger.info("=============================================");
        logger.info("Received Message From: " + siteAddress.toString());
        logger.info(message.toString());
        logger.info("=============================================");
    }
}
