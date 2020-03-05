package zedi.pacbridge.gdn;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.gdn.messages.GdnMessage;
import zedi.pacbridge.gdn.messages.GdnMessageFactory;
import zedi.pacbridge.gdn.messages.GdnMessageType;
import zedi.pacbridge.gdn.messages.SwtHeader;
import zedi.pacbridge.gdn.messages.SwtHeaderFactory;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.test.BaseTestCase;

public class GdnPacketDecoderTest extends BaseTestCase {
    @Mock
    private SwtHeaderFactory headerFactory;
    @Mock
    private GdnMessageFactory messageFactory;
    @Mock
    private ByteBuffer byteBuffer;


    @Test
    public void shouldDecodePacket() throws Exception {
        SwtHeader header = mock(SwtHeader.class);
        GdnMessage message = mock(GdnMessage.class);
        
        given(header.messageType()).willReturn(GdnMessageType.WriteIoPoint);
        given(headerFactory.headerFromByteBuffer(any(ByteBuffer.class))).willReturn(header);
        given(messageFactory.messageFromByteBuffer(GdnMessageType.WriteIoPoint.getNumber(), byteBuffer)).willReturn(message);

        GdnPacketDecoder packetDecoder = new GdnPacketDecoder(headerFactory, messageFactory);
        Packet resultPacket = packetDecoder.packetForByteBuffer(byteBuffer);
        
        verify(headerFactory).headerFromByteBuffer(byteBuffer);
        verify(messageFactory).messageFromByteBuffer(GdnMessageType.WriteIoPoint.getNumber(), byteBuffer);
        
        assertNotNull(resultPacket);
        assertSame(message, resultPacket.getMessage());
        assertSame(header, resultPacket.getHeader());
    }
}