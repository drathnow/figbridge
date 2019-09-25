package zedi.pacbridge.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.auth.AuthenticationStrategy;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.test.BaseTestCase;

public class DefaultSecurityLayerTest extends BaseTestCase {

    @Mock
    private TraceLogger traceLogger;
    
    @Test
    public void shouldHandlePassPacketWhenAuthenticated() throws Exception {
        CompressionLayer compressionLayer = mock(CompressionLayer.class);
        FramingLayer framingLayer = mock(FramingLayer.class);
        AuthenticationStrategy strategy = mock(AuthenticationStrategy.class);
        ReceiveProtocolPacket outProtocolPacket = mock(ReceiveProtocolPacket.class);
        TransmitProtocolPacket innerProtocolPacket = mock(TransmitProtocolPacket.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);

        given(outProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(innerProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(strategy.isFinished()).willReturn(true);
        given(strategy.isAuthenticated()).willReturn(true);
        
        DefaultSecurityLayer securityLayer = new DefaultSecurityLayer(strategy, 10, innerProtocolPacket, traceLogger);
        securityLayer.setCompressionLayer(compressionLayer);
        securityLayer.setFramingLayer(framingLayer);
        
        securityLayer.receive(outProtocolPacket);
        verify(compressionLayer, never()).receive(outProtocolPacket);
        
        securityLayer.receive(outProtocolPacket);
        verify(compressionLayer).receive(outProtocolPacket);
    }

    @Test
    public void shouldHandleReceivedPacketAndHandItToAuthenticationStrategyWhenNotFinished() throws Exception {
        CompressionLayer compressionLayer = mock(CompressionLayer.class);
        FramingLayer framingLayer = mock(FramingLayer.class);
        AuthenticationStrategy strategy = mock(AuthenticationStrategy.class);
        Packet packet = mock(Packet.class);
        ReceiveProtocolPacket outProtocolPacket = mock(ReceiveProtocolPacket.class);
        TransmitProtocolPacket innerProtocolPacket = mock(TransmitProtocolPacket.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);

        given(outProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(innerProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(strategy.isAuthenticated()).willReturn(false);
        given(strategy.nextPacket()).willReturn(packet);
        
        DefaultSecurityLayer securityLayer = new DefaultSecurityLayer(strategy, 10, innerProtocolPacket, traceLogger);
        securityLayer.setCompressionLayer(compressionLayer);
        securityLayer.setFramingLayer(framingLayer);
        
        securityLayer.receive(outProtocolPacket);
        
        verify(strategy).handleBytesFromClient(byteBuffer);
        verify(innerProtocolPacket).reset();
        verify(strategy).nextPacket();
        verify(innerProtocolPacket).bodyByteBuffer();
        verify(packet).serialize(byteBuffer);
        verify(innerProtocolPacket).setBodyLength(0);
        verify(framingLayer).transmit(innerProtocolPacket);
    }
    
    @Test
    public void shouldSendFirstPacket() throws Exception {
        FramingLayer framingLayer = mock(FramingLayer.class);
        AuthenticationStrategy strategy = mock(AuthenticationStrategy.class);
        Packet packet = mock(Packet.class);
        TransmitProtocolPacket protocolPacket = mock(TransmitProtocolPacket.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);

        byteBuffer.position(5);
        given(strategy.nextPacket()).willReturn(packet);
        given(protocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        
        DefaultSecurityLayer securityLayer = new DefaultSecurityLayer(strategy, 10, protocolPacket, traceLogger);
        securityLayer.setFramingLayer(framingLayer);
        securityLayer.start();
        
        verify(protocolPacket).reset();
        verify(strategy).nextPacket();
        verify(protocolPacket).bodyByteBuffer();
        verify(packet).serialize(byteBuffer);
        verify(protocolPacket).setBodyLength(0);
        verify(framingLayer).transmit(protocolPacket);
    }
}
