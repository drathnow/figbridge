package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapConfigurationUpdateHandler;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.ZapReportProcessor;
import zedi.pacbridge.zap.reporting.ResponseStatus;

public class ConfigureUpdateMessageHandlerTest extends BaseTestCase {
    @Mock
    private DependencyResolver dependencyResolver;
    
    @Mock
    private ZapConfigurationUpdateHandler updateHandler;

    @Mock
    private SiteAddress siteAddress;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
        given(dependencyResolver.getImplementationOf(ZapConfigurationUpdateHandler.class)).willReturn(updateHandler);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }

    @Test
    public void shouldHandleConfigureUpdateMessageWhenNoErrorReturns() throws Exception {
        ConfigureUpdateMessage configureUpdateMessage = mock(ConfigureUpdateMessage.class);
        ResponseSender responseSender = mock(ResponseSender.class);

        ArgumentCaptor<Message> arg = ArgumentCaptor.forClass(Message.class);
        given(configureUpdateMessage.messageType()).willReturn(ZapMessageType.ConfigureUpdate);
        given(updateHandler.didProcessConfigurationUpdate(siteAddress, configureUpdateMessage)).willReturn(true);
        
        ConfigureUpdateMessageHandler handler = new ConfigureUpdateMessageHandler();
        handler.handleMessageForSiteAddress(configureUpdateMessage, siteAddress, responseSender);
        
        verify(responseSender).sendResponse(arg.capture());
        
        AckMessage ackMessage = (AckMessage)arg.getValue();
        assertEquals(ZapMessageType.ConfigureUpdate, ackMessage.getAckedMessageType());
        ConfigureUpdateAckDetails details = ackMessage.additionalDetails();
        assertEquals(ResponseStatus.OK, details.getResponseStatus());
    }
    

    @Test
    public void shouldHandleConfigureUpdateMessageWhenErrorReturns() throws Exception {
        ConfigureUpdateMessage configureUpdateMessage = mock(ConfigureUpdateMessage.class);
        ResponseSender responseSender = mock(ResponseSender.class);

        ArgumentCaptor<Message> arg = ArgumentCaptor.forClass(Message.class);
        given(configureUpdateMessage.messageType()).willReturn(ZapMessageType.ConfigureUpdate);
        given(updateHandler.didProcessConfigurationUpdate(siteAddress, configureUpdateMessage)).willReturn(false);
        
        ConfigureUpdateMessageHandler handler = new ConfigureUpdateMessageHandler();
        handler.handleMessageForSiteAddress(configureUpdateMessage, siteAddress, responseSender);
        
        verify(responseSender).sendResponse(arg.capture());
        
        AckMessage ackMessage = (AckMessage)arg.getValue();
        assertEquals(ZapMessageType.ConfigureUpdate, ackMessage.getAckedMessageType());
        ConfigureUpdateAckDetails details = ackMessage.additionalDetails();
        assertEquals(ResponseStatus.TransientError, details.getResponseStatus());
    }
}
