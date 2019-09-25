package zedi.pacbridge.zap;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.test.BaseTestCase;
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

public class ZapUnsolicitedMessageHandlerTest extends BaseTestCase {
    private static final Integer DEVICE_TIME = 199;

    @Mock
    private SiteAddress siteAddress;
    @Mock
    private ResponseSender messageSender;

    
    @Test
    public void shouldHandleRequestTimeMessage() throws Exception {
        RequestTimeMessage message = mock(RequestTimeMessage.class);
        RequestTimeMessageHandler messageHandler = mock(RequestTimeMessageHandler.class);
        
        given(message.getDeviceTime()).willReturn(DEVICE_TIME);
        given(message.messageType()).willReturn(ZapMessageType.RequestTime);
        
        ZapUnsolicitedMessageHandler handler = new ZapUnsolicitedMessageHandler(null, null, messageHandler, null, null);
        handler.handleUnsolicitedMessage(siteAddress, message, messageSender);
        
        verify(messageHandler).handleMessageForSiteAddress(message, siteAddress, messageSender);
    }
    
    @Test
    public void shouldHandleHeartBeatMessage() throws Exception {
        HeartBeatMessage message = mock(HeartBeatMessage.class);
        HeartBeatMessageHandler messageHandler = mock(HeartBeatMessageHandler.class);
        
        given(message.getDeviceTime()).willReturn(DEVICE_TIME);
        given(message.messageType()).willReturn(ZapMessageType.HeartBeat);
        
        ZapUnsolicitedMessageHandler handler = new ZapUnsolicitedMessageHandler(null, messageHandler, null, null, null);
        handler.handleUnsolicitedMessage(siteAddress, message, messageSender);
        
        verify(messageHandler).handleMessageForSiteAddress(message, siteAddress, messageSender);
    }
    
    @Test
    public void shouldPassBundledReportMessageToMessageHandler() throws Exception {
        BundledReportMessageHandler bundledReportMessageHandler = mock(BundledReportMessageHandler.class);
        BundledReportMessage reportMessage = mock(BundledReportMessage.class);
        
        given(reportMessage.messageType()).willReturn(ZapMessageType.BundledReport);
        
        ZapUnsolicitedMessageHandler handler = new ZapUnsolicitedMessageHandler(bundledReportMessageHandler, null, null, null, null);
        
        handler.handleUnsolicitedMessage(siteAddress, reportMessage, messageSender);
        
        verify(bundledReportMessageHandler).handleMessageForSiteAddress(reportMessage, siteAddress, messageSender);
    }

    @Test
    public void shouldPassConfigureUpdateMessageToMessageHandler() throws Exception {
        ConfigureUpdateMessageHandler messageHandler = mock(ConfigureUpdateMessageHandler.class);
        ConfigureUpdateMessage updateMessage = mock(ConfigureUpdateMessage.class);
        
        given(updateMessage.messageType()).willReturn(ZapMessageType.ConfigureUpdate);
        
        ZapUnsolicitedMessageHandler handler = new ZapUnsolicitedMessageHandler(null, null, null, messageHandler, null);
        
        handler.handleUnsolicitedMessage(siteAddress, updateMessage, messageSender);
        
        verify(messageHandler).handleMessageForSiteAddress(updateMessage, siteAddress, messageSender);
    }
    
    @Test
    public void shouldPassOtadStatusUpdateMessageToMessageHandler() throws Exception {
        OtadStatusMessageHandler messageHandler = mock(OtadStatusMessageHandler.class);
        OtadStatusMessage message = mock(OtadStatusMessage.class);
        
        given(message.messageType()).willReturn(ZapMessageType.OtadStatusUpdate);
        
        ZapUnsolicitedMessageHandler handler = new ZapUnsolicitedMessageHandler(null, null, null, null, messageHandler);
        
        handler.handleUnsolicitedMessage(siteAddress, message, messageSender);
        
        verify(messageHandler).handleMessageForSiteAddress(message, siteAddress, messageSender);
    }

}
