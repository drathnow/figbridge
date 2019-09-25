package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

public class HeartBeatMessageHandlerTest extends BaseTestCase {

    private static final Integer DEVICE_TIME = 112;

    @Mock
    private ResponseSender messageSender;
    @Mock
    private SiteAddress siteAddress;
    
    @Test
    public void shouldSendHeartBeatResponseMessageWhenHeartBeatMessageReceiveved() throws Exception {
        Integer now = (int)(System.currentTimeMillis()/1000L);
        SystemTime systemTime = mock(SystemTime.class);
        HeartBeatMessage message = mock(HeartBeatMessage.class);
        
        given(systemTime.getCurrentTime()).willReturn(now*1000L);
        given(message.getDeviceTime()).willReturn(DEVICE_TIME);
        ArgumentCaptor<HeartBeatResponseMessage> arg = ArgumentCaptor.forClass(HeartBeatResponseMessage.class);
        
        HeartBeatMessageHandler handler = new HeartBeatMessageHandler(systemTime);
        
        handler.handleMessageForSiteAddress(message, siteAddress, messageSender);
        
        verify(messageSender).sendResponse(arg.capture());
        assertEquals(DEVICE_TIME.intValue(), arg.getValue().getDeviceTime().intValue());
        assertEquals(now.intValue(), arg.getValue().getServerTime().intValue());
    }
    
}
