package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import zedi.pacbridge.gdn.messages.GdnMessage;
import zedi.pacbridge.gdn.messages.GdnMessageType;
import zedi.pacbridge.gdn.messages.GdnPacket;
import zedi.pacbridge.gdn.messages.SwtHeader;
import zedi.pacbridge.gdn.messages.SwtHeaderFactory;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.test.BaseTestCase;


public class GdnPacketEncoderTest extends BaseTestCase {
    private static final Integer SESSION_ID = 2;
    
    @Test
    public void shouldEncodePacketWithoutSession() throws Exception {
        GdnMessage message = mock(GdnMessage.class);
        SwtHeaderFactory headerFactory = mock(SwtHeaderFactory.class);
        SwtHeader header = mock(SwtHeader.class);
        GdnMessageType messageType = mock(GdnMessageType.class);
                
        given(message.messageType()).willReturn(messageType);
        given(headerFactory.newSessionlessHeaderWithMessageType(messageType)).willReturn(header);
        
        GdnPacketEncoder encoder = new GdnPacketEncoder(headerFactory);
        Packet result = encoder.packetForMessage(message);

        assertEquals(GdnPacket.class, result.getClass());
        assertSame(header, ((GdnPacket)result).getHeader());
        assertSame(message, ((GdnPacket)result).getMessage());
    }
    
    @Test
    public void shouldEncodePacketWithSessionId() throws Exception {
        Session session = mock(Session.class);
        GdnMessage message = mock(GdnMessage.class);
        SwtHeaderFactory headerFactory = mock(SwtHeaderFactory.class);
        SwtHeader header = mock(SwtHeader.class);
        GdnMessageType messageType = mock(GdnMessageType.class);
        
        given(message.messageType()).willReturn(messageType);
        given(session.getSessionId()).willReturn(SESSION_ID);
        given(headerFactory.newSessionHeaderWithSessionIdAndMessageType(SESSION_ID, messageType)).willReturn(header);
        
        GdnPacketEncoder encoder = new GdnPacketEncoder(headerFactory);
        Packet result = encoder.packetForMessageAndSession(message, session);
        
        assertEquals(GdnPacket.class, result.getClass());
        assertSame(header, ((GdnPacket)result).getHeader());
        assertSame(message, ((GdnPacket)result).getMessage());
    }
}
