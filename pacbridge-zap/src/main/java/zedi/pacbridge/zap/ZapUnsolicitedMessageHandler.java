package zedi.pacbridge.zap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.net.UnsolicitedMessageHandler;
import zedi.pacbridge.net.annotations.HandleUnsolicitedMessage;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.messages.BundledReportMessageHandler;
import zedi.pacbridge.zap.messages.ConfigureUpdateMessage;
import zedi.pacbridge.zap.messages.ConfigureUpdateMessageHandler;
import zedi.pacbridge.zap.messages.HeartBeatMessage;
import zedi.pacbridge.zap.messages.HeartBeatMessageHandler;
import zedi.pacbridge.zap.messages.OtadStatusMessage;
import zedi.pacbridge.zap.messages.OtadStatusMessageHandler;
import zedi.pacbridge.zap.messages.RequestTimeMessage;
import zedi.pacbridge.zap.messages.RequestTimeMessageHandler;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;

@zedi.pacbridge.net.annotations.UnsolicitedMessageHandler(forNetworkType="zap")
public class ZapUnsolicitedMessageHandler implements UnsolicitedMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZapUnsolicitedMessageHandler.class.getName());
    private ZapMessageDecoder decoder = new ZapMessageDecoder();

    private BundledReportMessageHandler bundledReportMessageHandler;
    private RequestTimeMessageHandler requestTimeMessageHandler;
    private HeartBeatMessageHandler heartBeatHandler;
    private ConfigureUpdateMessageHandler configureUpdateMessageHandler;
    private OtadStatusMessageHandler otadStatusMessageHandler;
    
    public ZapUnsolicitedMessageHandler() {
        this(new BundledReportMessageHandler(), 
             new HeartBeatMessageHandler(), 
             new RequestTimeMessageHandler(), 
             new ConfigureUpdateMessageHandler(),
             new OtadStatusMessageHandler());
    }
    
    public ZapUnsolicitedMessageHandler(BundledReportMessageHandler bundledReportMessageHandler, 
                                        HeartBeatMessageHandler heartBeatHandler, 
                                        RequestTimeMessageHandler requestTimeMessageHandler, 
                                        ConfigureUpdateMessageHandler configureUpdateMessageHandler,
                                        OtadStatusMessageHandler otadStatusMessageHandler) {
        this.bundledReportMessageHandler = bundledReportMessageHandler;
        this.heartBeatHandler = heartBeatHandler;
        this.requestTimeMessageHandler = requestTimeMessageHandler;
        this.configureUpdateMessageHandler = configureUpdateMessageHandler;
        this.otadStatusMessageHandler = otadStatusMessageHandler;
    }

    @Override
    @HandleUnsolicitedMessage
    public void handleUnsolicitedMessage(SiteAddress siteAddress, Message message, ResponseSender responseSender) {
        if (logger.isTraceEnabled())
            logger.trace("Rcvd - " + decoder.formattedMessage(message));
        switch (message.messageType().getNumber()) {
            case ZapMessageType.HEART_BEAT_MESSAGE_NUMBER : 
                logger.debug("Received Heart Beat message");
                heartBeatHandler.handleMessageForSiteAddress((HeartBeatMessage)message, siteAddress, responseSender);
                break;
                
            case ZapMessageType.BUNDLED_REPORT_NUMBER :
                logger.debug("Received Bundled Report message");
                bundledReportMessageHandler.handleMessageForSiteAddress((BundledReportMessage)message, siteAddress, responseSender);
                break;
                
            case ZapMessageType.REQUEST_TIME_MESSAGE_NUMBER :
                requestTimeMessageHandler.handleMessageForSiteAddress((RequestTimeMessage)message, siteAddress, responseSender);
                break;
                
            case ZapMessageType.CONFIGURE_UPDATE_MESSAGE_NUMBER :
                configureUpdateMessageHandler.handleMessageForSiteAddress((ConfigureUpdateMessage)message, siteAddress, responseSender);
                break;
                
            case ZapMessageType.OTAD_STATUS_UPDATE_NUMBER :
                otadStatusMessageHandler.handleMessageForSiteAddress((OtadStatusMessage)message, siteAddress, responseSender);
                break;

            default : 
                logger.warn("Received unsolicited message but we cannot handle it!  MessageType: " + message.messageType());
        }
    }
}
