package zedi.pacbridge.zap.messages;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

public class HeartBeatMessageHandler {

    private SystemTime systemTime;

    public HeartBeatMessageHandler() {
        this(SystemTime.SHARED_INSTANCE);
    }
    
    public HeartBeatMessageHandler(SystemTime systemTime) {
        this.systemTime = systemTime;
    }

    public void handleMessageForSiteAddress(HeartBeatMessage heartBeatMessage, SiteAddress siteAddress, ResponseSender responseSender) {
        Integer serverTime = (int)(systemTime.getCurrentTime()/1000L);
        Integer deviceTime = heartBeatMessage.getDeviceTime();
        HeartBeatResponseMessage response = new HeartBeatResponseMessage(deviceTime, serverTime);
        responseSender.sendResponse(response);
    }
}