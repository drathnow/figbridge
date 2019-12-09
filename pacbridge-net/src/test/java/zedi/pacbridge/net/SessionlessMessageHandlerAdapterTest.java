package zedi.pacbridge.net;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;

public class SessionlessMessageHandlerAdapterTest extends BaseTestCase {
    private boolean called;
    @SuppressWarnings("unused")
	private SiteAddress calledSiteAddressArg;
    private Message calledMessageArg;
    private Session calledSessionArg;

    @Test
    public void shouldUseInterfaceImplementation() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        Message message = mock(Message.class);
        Session session = mock(Session.class); 
                
        SessionlessMessageHandler handler = new SessionlessMessageHandler() {
            @Override
            public void handleMessageForSession(SiteAddress siteAddress, Message message, Session session) {
                called = true;
                calledSiteAddressArg = siteAddress;
                calledMessageArg = message;
                calledSessionArg = session;
            }
        };
        
        SessionlessMessageHandlerAdapter adapter = new SessionlessMessageHandlerAdapter(handler);
        adapter.invoke(siteAddress, message, session);
        
        assertTrue(called);
        assertSame(message, calledMessageArg);
        assertSame(siteAddress, siteAddress);
        assertSame(session, calledSessionArg);
    }
    
    @Test
    public void shouldUseReflectedObjects() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        Message message = mock(Message.class);
        Session session = mock(Session.class); 

        SessionlessMessageHandlerAdapter adapter = new SessionlessMessageHandlerAdapter(new GoodAnnotationAndSignature());
        adapter.invoke(siteAddress, message, session);
        
        assertTrue(called);
        assertSame(message, calledMessageArg);
        assertSame(session, calledSessionArg);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlIfMethodHasIncorrectSignature1() throws Exception {
        new SessionlessMessageHandlerAdapter(new BadSignature1());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlIfMethodHasIncorrectSignature2() throws Exception {
        new SessionlessMessageHandlerAdapter(new BadSignature2());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlIfMethodHasIncorrectSignature3() throws Exception {
        new SessionlessMessageHandlerAdapter(new BadSignature3());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlIfMethodHasMissingAnnotation() throws Exception {
        new SessionlessMessageHandlerAdapter(new MissingAnnotation());
    }

    private class GoodAnnotationAndSignature {
        @zedi.pacbridge.net.annotations.HandleMessageForSession
        public void handleMessage(SiteAddress siteAddress, Message message, Session session) {
            called = true;
            calledSiteAddressArg = siteAddress;
            calledMessageArg = message;
            calledSessionArg = session;
        }
    }

    private class BadSignature1 {
        @zedi.pacbridge.net.annotations.HandleMessageForSession
        public void handleMessage() {
        }
    }

    private class BadSignature2 {
        @zedi.pacbridge.net.annotations.HandleMessageForSession
        public void handleMessage(Message message) {
        }
    }

    private class BadSignature3 {
        @zedi.pacbridge.net.annotations.HandleMessageForSession
        public void handleMessage(Session session) {
        }
    }

    private class MissingAnnotation {
        @SuppressWarnings("unused")
		public void handleMessage(Message message) {
        }
    }
}
