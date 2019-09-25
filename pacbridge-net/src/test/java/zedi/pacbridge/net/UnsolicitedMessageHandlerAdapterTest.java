package zedi.pacbridge.net;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;

public class UnsolicitedMessageHandlerAdapterTest extends BaseTestCase {
    private boolean called;
    private Message calledArg;
    private SiteAddress calledSiteAddress;
    private ResponseSender calledResponseSender;

    @Test
    public void shouldUseInterfaceImplementation() throws Exception {
        Message message = mock(Message.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        ResponseSender responseSender = mock(ResponseSender.class);
        UnsolicitedMessageHandler handler = new UnsolicitedMessageHandler() {
            @Override
            public void handleUnsolicitedMessage(SiteAddress siteAddress, Message message, ResponseSender responseSender) {
                called = true;
                calledArg = message;
                calledSiteAddress = siteAddress;
                calledResponseSender = responseSender;
            }
        };
        
        UnsolicitedMessageHandlerAdapter adapter = new UnsolicitedMessageHandlerAdapter(handler);
        adapter.invoke(siteAddress, message, responseSender);
        
        assertTrue(called);
        assertSame(message, calledArg);
        assertSame(siteAddress, calledSiteAddress);
        assertSame(responseSender, calledResponseSender);
    }
    
    @Test
    public void shouldUseReflectedObjects() throws Exception {
        Message message = mock(Message.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        ResponseSender responseSender = mock(ResponseSender.class);
        UnsolicitedMessageHandlerAdapter adapter = new UnsolicitedMessageHandlerAdapter(new GoodAnnotationAndSignature());
        adapter.invoke(siteAddress, message, responseSender);
        
        assertTrue(called);
        assertSame(message, calledArg);
        assertSame(siteAddress, calledSiteAddress);
        assertSame(responseSender, calledResponseSender);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlIfMethodHasIncorrectSignature() throws Exception {
        new UnsolicitedMessageHandlerAdapter(new BadSignature());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlIfMethodHasMissingAnnotation() throws Exception {
        new UnsolicitedMessageHandlerAdapter(new MissingAnnotation());
    }

    private class GoodAnnotationAndSignature {
        @zedi.pacbridge.net.annotations.HandleUnsolicitedMessage
        public void handleMessage(SiteAddress siteAddress, Message message, ResponseSender responseSender) {
            called = true;
            calledArg = message;
            calledSiteAddress = siteAddress;
            calledResponseSender = responseSender;
        }
    }

    private class BadSignature {
        @zedi.pacbridge.net.annotations.HandleUnsolicitedMessage
        public void handleMessage() {
        }
    }

    private class MissingAnnotation {
        public void handleMessage(Message message) {
        }
    }
}
