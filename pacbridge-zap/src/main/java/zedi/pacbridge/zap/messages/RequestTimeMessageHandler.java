package zedi.pacbridge.zap.messages;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

public class RequestTimeMessageHandler {

    private SystemTime systemTime;
    
    public RequestTimeMessageHandler() {
        this(new SystemTime());
    }
    
    public RequestTimeMessageHandler(SystemTime systemTime) {
        this.systemTime = systemTime;
    }
    
    public void handleMessageForSiteAddress(RequestTimeMessage requestTimeMessage, SiteAddress siteAddress, ResponseSender responseSender) {
        Integer serverTime = (int)(systemTime.getCurrentTime()/1000L);
        ServerTimeMessage serverTimeMessage = new ServerTimeMessage(requestTimeMessage.getDeviceTime(), serverTime);
        responseSender.sendResponse(serverTimeMessage);
    }
}
