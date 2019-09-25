package zedi.pacbridge.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringEncoder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CountedByteFramingLayer.class, ByteBuffer.class})
public class CountedByteFramingLayerTest extends BaseTestCase {

    private static final int CAPACITY = 10;
    private static final byte[] PACKET1 = {// Packet 1
                               0x00, 0x23, //count
                               0x01, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x1b, (byte)0xc0, 0x07, 0x43, 0x57, 0x2d, 0x52, 0x44, 0x34, 0x38, 0x10, 0x4c, 0x02, (byte)0x8b, (byte)0x94, (byte)0xbe, (byte)0xa6, (byte)0xa7, 0x61, (byte)0x9b, 0x62, 0x3d, (byte)0x9c, 0x31, (byte)0xd7, (byte)0xc1, 0x00, 0x00};
    private static final byte[] PACKET2 = {// Packet 2
                               0x00, 0x13, // count
                               0x01, 0x00, 0x05, (byte)0xff, (byte)0xfe, 0x00, 0x00, 0x00, 0x0a, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x3f, (byte)0x99, (byte)0x99, (byte)0x9a};
    @Mock
    private TraceLogger traceLogger;
    
    private void assertArraysRangeEquals(byte[] expected, byte[] actuals, int offset, int length) {
        assertTrue("Actual's length is too short", actuals.length >= (length - offset));
        assertTrue("Expect's length is too short", expected.length >= (length - offset));
        String expectedString = HexStringEncoder.bytesAsHexString(expected, offset, (length - offset));
        String actualString = HexStringEncoder.bytesAsHexString(expected, offset, (length - offset));
        assertEquals(expectedString, actualString);
    }
    
    @Test
    public void shouldExpandRcvBufferIfTooSmallToHoldLoad() throws Exception {
        ByteBuffer smallByteBuffer = ByteBuffer.allocate(10);
        ByteBuffer rightSizeByteBuffer = ByteBuffer.allocate(PACKET1.length-2);
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET1.length);
        TransportLayer transportLayer = mock(TransportLayer.class);
        ArgumentCaptor<ReceiveProtocolPacket> argument = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        mockStatic(ByteBuffer.class);
        given(ByteBuffer.allocate(10)).willReturn(smallByteBuffer);
        given(ByteBuffer.allocate(35)).willReturn(rightSizeByteBuffer);
        
        CountedByteFramingLayer framingLayer = new CountedByteFramingLayer(traceLogger, 10);
        framingLayer.setTransportLayer(transportLayer);
        
        byteBuffer.put(PACKET1);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        
        verifyStatic(ByteBuffer.class, times(2));
        ByteBuffer.allocate(10);
        ByteBuffer.allocate(35);
        verify(transportLayer).receive(argument.capture());
        List<ReceiveProtocolPacket> packets = argument.getAllValues();
        assertArraysRangeEquals(PACKET1, packets.get(0).bodyByteBuffer().array(), 2, PACKET1.length);
    }
    
    @Test
    public void shouldConsumeBytes() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET1.length+PACKET2.length);
        TransportLayer transportLayer = mock(TransportLayer.class);

        ArgumentCaptor<ReceiveProtocolPacket> argument = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        CountedByteFramingLayer framingLayer = new CountedByteFramingLayer(traceLogger);
        framingLayer.setTransportLayer(transportLayer);
        
        for (int i = 0; i < PACKET1.length-1; i++) {
            byteBuffer.put(PACKET1[i]);
            byteBuffer.flip();
            framingLayer.receive(byteBuffer);
            verify(transportLayer, never()).receive(argument.capture());
        }
        byteBuffer.put(PACKET1[PACKET1.length-1]);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        verify(transportLayer).receive(argument.capture());

        for (int i = 0; i < PACKET2.length-1; i++) {
            byteBuffer.put(PACKET2[i]);
            byteBuffer.flip();
            framingLayer.receive(byteBuffer);
            verify(transportLayer, times(1)).receive(argument.capture());
        }
        byteBuffer.put(PACKET2[PACKET2.length-1]);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        
        verify(transportLayer, times(2)).receive(argument.capture());
        List<ReceiveProtocolPacket> packets = argument.getAllValues();
        assertArraysRangeEquals(PACKET1, packets.get(0).bodyByteBuffer().array(), 2, PACKET1.length);
        assertArraysRangeEquals(PACKET2, packets.get(1).bodyByteBuffer().array(), 2, PACKET2.length);

    }
    
    @Test
    public void shouldHandleSingleBufferWithTwoPackets() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET1.length+PACKET2.length);
        TransportLayer transportLayer = mock(TransportLayer.class);

        ArgumentCaptor<ReceiveProtocolPacket> argument = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        CountedByteFramingLayer framingLayer = new CountedByteFramingLayer(traceLogger);
        framingLayer.setTransportLayer(transportLayer);
        
        byteBuffer.put(PACKET1);
        byteBuffer.put(PACKET2);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        
        verify(transportLayer, times(2)).receive(argument.capture());
        List<ReceiveProtocolPacket> packets = argument.getAllValues();
        assertArraysRangeEquals(PACKET1, packets.get(0).bodyByteBuffer().array(), 2, PACKET1.length);
        assertArraysRangeEquals(PACKET2, packets.get(1).bodyByteBuffer().array(), 2, PACKET2.length);
    }
    
    @Test
    public void shouldHandleSinglePacketWithSplitCount2() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET1.length);
        TransportLayer transportLayer = mock(TransportLayer.class);

        ArgumentCaptor<ReceiveProtocolPacket> argument = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        CountedByteFramingLayer framingLayer = new CountedByteFramingLayer(traceLogger);
        framingLayer.setTransportLayer(transportLayer);
        byteBuffer.put(PACKET1, 0, 2);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        verify(transportLayer, never()).receive(argument.capture());
        
        byteBuffer.put(PACKET1, 2, PACKET1.length-2);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        
        verify(transportLayer).receive(argument.capture());
        ReceiveProtocolPacket packet = argument.getValue();
        ByteBuffer resultBuffer = packet.bodyByteBuffer();
        assertArraysRangeEquals(PACKET1, resultBuffer.array(), 2, PACKET1.length);
    }
    
    @Test
    public void shouldHandleSinglePacketWithSplitCount1() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET1.length);
        TransportLayer transportLayer = mock(TransportLayer.class);

        ArgumentCaptor<ReceiveProtocolPacket> argument = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        CountedByteFramingLayer framingLayer = new CountedByteFramingLayer(traceLogger);
        framingLayer.setTransportLayer(transportLayer);
        byteBuffer.put(PACKET1[0]);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        verify(transportLayer, never()).receive(argument.capture());

        byteBuffer.put(PACKET1, 1, PACKET1.length-1);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        
        verify(transportLayer).receive(argument.capture());
        ReceiveProtocolPacket packet = argument.getValue();
        ByteBuffer resultBuffer = packet.bodyByteBuffer();
        assertArraysRangeEquals(PACKET1, resultBuffer.array(), 2, PACKET1.length);
    }
    
    @Test
    public void shouldHandleSinglePacket() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET1.length);
        TransportLayer transportLayer = mock(TransportLayer.class);

        ArgumentCaptor<ReceiveProtocolPacket> argument = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        CountedByteFramingLayer framingLayer = new CountedByteFramingLayer(traceLogger);
        framingLayer.setTransportLayer(transportLayer);
        
        byteBuffer.put(PACKET1);
        byteBuffer.flip();
        framingLayer.receive(byteBuffer);
        
        verify(transportLayer).receive(argument.capture());
        ReceiveProtocolPacket packet = argument.getValue();
        ByteBuffer resultBuffer = packet.bodyByteBuffer();
        assertArraysRangeEquals(PACKET1, resultBuffer.array(), 2, PACKET1.length);
    }
    
    @Test
    public void shouldTransmitProtocolPacket() throws Exception {
        TransmitProtocolPacket packet = mock(TransmitProtocolPacket.class);
        NetworkAdapter networkAdapter = mock(NetworkAdapter.class);
        ByteBuffer headerBuffer = ByteBuffer.allocate(2);

        given(packet.headerByteBuffer()).willReturn(headerBuffer);
        given(packet.bodyLength()).willReturn(10);
        
        CountedByteFramingLayer layer = new CountedByteFramingLayer(traceLogger);
        layer.setNetworkAdapter(networkAdapter);
        layer.transmit(packet);

        verify(packet).addHeader(CountedByteFramingLayer.SIZE_BYTE_COUNT);
        verify(packet).headerByteBuffer();
        verify(networkAdapter).transmit(packet);
        
        assertEquals(2, headerBuffer.position());
        headerBuffer.flip();
        assertEquals(10, headerBuffer.getShort());
    }
    
    @Test
    public void shouldConsumeAllBytesIn2Buffers() throws Exception {
        
        TransportLayer transportLayer = new TransportLayer() {

            int index = 0;
            
            @Override
            public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
            }
            @Override
            public void start() {
            }
            @Override
            public void setFramingLayer(FramingLayer framingLayer) {
            }
            @Override
            public void setSecurityLayer(SecurityLayer compressionLayer) {
            }
            
            @Override
            public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
                ByteBuffer bb = protocolPacket.bodyByteBuffer();
                if (index == 0) {
                    index++;
                    byte[] expected = new byte[]{0x0a, 0x0b, 0x0c};
                    for (int i = 0; i < expected.length; i++)
                        if (bb.get() != expected[i])
                            Assert.fail();
                } else {
                    byte[] expected = new byte[]{0x0d, 0x0e};
                    for (int i = 0; i < expected.length; i++)
                        if (bb.get() != expected[i])
                            Assert.fail();
                    
                }
            }
            @Override
            public void close() {
            }
            @Override
            public void reset() {
            }
            @Override
            public boolean isActive() {
                return false;
            }
        };
        
        byte[] buffer1 = new byte[]{0x00, 0x03, 0x0a, 0x0b, 0x0c, 0x00, 0x02};
        byte[] buffer2 = new byte[]{0x0d, 0x0e};
        ByteBuffer bb = ByteBuffer.allocate(buffer1.length);
        bb.put(buffer1);
        bb.flip();
        CountedByteFramingLayer layer = new CountedByteFramingLayer(traceLogger);
        layer.setTransportLayer(transportLayer);
        layer.receive(bb);
        assertEquals(CountedByteFramingLayer.State.WaitingForBytes, layer.currentState());
        bb.put(buffer2);
        bb.flip();
        layer.receive(bb);
        assertEquals(CountedByteFramingLayer.State.WaitingForCountBytes, layer.currentState());
    }
    
    @Test
    public void shouldConsumeAllBytesInBuffer() throws Exception {
        
        TransportLayer transportLayer = new TransportLayer() {

            int index = 0;
            
            @Override
            public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
            }
            @Override
            public void start() {
            }
            @Override
            public void setFramingLayer(FramingLayer framingLayer) {
            }
            @Override
            public void setSecurityLayer(SecurityLayer compressionLayer) {
            }
            @Override
            public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
                ByteBuffer bb = protocolPacket.bodyByteBuffer();
                if (index == 0) {
                    index++;
                    byte[] expected = new byte[]{0x0a, 0x0b, 0x0c};
                    for (int i = 0; i < expected.length; i++)
                        if (bb.get() != expected[i])
                            Assert.fail();
                } else {
                    byte[] expected = new byte[]{0x0d, 0x0e};
                    for (int i = 0; i < expected.length; i++)
                        if (bb.get() != expected[i])
                            Assert.fail();
                    
                }
            }
            @Override
            public void close() {
            }
            @Override
            public void reset() {
            }
            @Override
            public boolean isActive() {
                return false;
            }
        };
        
        byte[] buffer = new byte[]{0x00, 0x03, 0x0a, 0x0b, 0x0c, 0x00, 0x02, 0x0d, 0x0e};
        ByteBuffer bb = ByteBuffer.allocate(buffer.length);
        bb.put(buffer);
        CountedByteFramingLayer layer = new CountedByteFramingLayer(traceLogger);
        layer.setTransportLayer(transportLayer);
        layer.receive(bb);
        assertEquals(CountedByteFramingLayer.State.WaitingForCountBytes, layer.currentState());
    }
    
    @Test
    public void shouldWaitUntilEnoughBytesArrive() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(CAPACITY);
        byteBuffer.putShort((byte)5);
        byteBuffer.flip();
        
        TransportLayer transportLayer = mock(TransportLayer.class);
        CountedByteFramingLayer layer = new CountedByteFramingLayer(traceLogger);
        layer.setTransportLayer(transportLayer);
        layer.receive(byteBuffer);
        
        assertEquals(CountedByteFramingLayer.State.WaitingForBytes, layer.currentState());
        byteBuffer.putInt(42);
        byteBuffer.flip();
        layer.receive(byteBuffer);
        assertEquals(CountedByteFramingLayer.State.WaitingForBytes, layer.currentState());
        byteBuffer.put((byte)1);
        byteBuffer.flip();
        layer.receive(byteBuffer);
        
        ArgumentCaptor<ReceiveProtocolPacket> arg = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);
        
        assertEquals(CountedByteFramingLayer.State.WaitingForCountBytes, layer.currentState());
        verify(transportLayer).receive(arg.capture());
        ReceiveProtocolPacket packet = arg.getValue();
        
        ByteBuffer bb = packet.bodyByteBuffer();
        
        assertEquals(5, packet.bodyLength().intValue());
        assertEquals(bb.limit(), packet.bodyLength().intValue());
        assertEquals(42, bb.getInt());
        assertEquals(1, bb.get());
    }
    
    @Test
    public void shouldWaitForCountBytes() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(CAPACITY);
        byteBuffer.put((byte)0);
        byteBuffer.flip();
        
        CountedByteFramingLayer layer = new CountedByteFramingLayer(traceLogger);
        layer.receive(byteBuffer);
        
        assertEquals(CAPACITY, byteBuffer.limit());
        assertEquals(0, byteBuffer.position());
        assertEquals(CAPACITY, byteBuffer.capacity());
        assertEquals(CountedByteFramingLayer.State.WaitingForCountBytes, layer.currentState());
        
        byteBuffer.put((byte)5);
        byteBuffer.flip();
        layer.receive(byteBuffer);
        
        assertEquals(CAPACITY, byteBuffer.limit());
        assertEquals(0, byteBuffer.position());
        assertEquals(CAPACITY, byteBuffer.capacity());
        assertEquals(CountedByteFramingLayer.State.WaitingForBytes, layer.currentState());
    }
    

    @Test
    public void shouldConsumeAllBytesWhenFullPacketArrive() throws Exception {
        byte[] buffer = new byte[]{0x00, 0x03, 0x0a, 0x0b, 0x0c};
        ByteBuffer bb = ByteBuffer.allocate(buffer.length);
        bb.put(buffer);
        bb.flip();
        TransportLayer transportLayer = mock(TransportLayer.class);
        ArgumentCaptor<ReceiveProtocolPacket> arg = ArgumentCaptor.forClass(ReceiveProtocolPacket.class);

        CountedByteFramingLayer layer = new CountedByteFramingLayer(traceLogger);
        layer.setTransportLayer(transportLayer);
        layer.receive(bb);
        assertEquals(CountedByteFramingLayer.State.WaitingForCountBytes, layer.currentState());

        verify(transportLayer).receive(arg.capture());
        ReceiveProtocolPacket packet = arg.getValue();
        
        bb = packet.bodyByteBuffer();
        
        assertEquals(3, packet.bodyLength().intValue());
        assertEquals(0x0a, bb.get());
        assertEquals(0x0b, bb.get());
        assertEquals(0x0c, bb.get());

    }
}
