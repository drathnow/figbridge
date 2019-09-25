package zedi.pacbridge.net;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Unsigned;

public class CountedByteFramingLayer implements FramingLayer {

    private static final Logger logger = LoggerFactory.getLogger(CountedByteFramingLayer.class.getName());
    
    enum State {
        WaitingForBytes,
        WaitingForCountBytes;
    }
    
    public static final int SIZE_BYTE_COUNT = 2;
    
    private NetworkAdapter networkAdapter;
    private TransportLayer transportLayer;
    private State currentState;
    private int expectedNumberOfBytes;
    private TraceLogger traceLogger;
    private ByteBuffer rcvByteBuffer;
    private Integer initialBufferSize;
    
    public CountedByteFramingLayer(TraceLogger traceLogger, Integer initialBufferSize) {
        this.traceLogger = traceLogger;
        this.currentState = State.WaitingForCountBytes;
        this.expectedNumberOfBytes = 0;
        this.initialBufferSize = initialBufferSize;
        this.rcvByteBuffer = ByteBuffer.allocate(initialBufferSize);
    }

    public CountedByteFramingLayer(TraceLogger traceLogger) {
        this(traceLogger, 1024);
    }
    
    @Override
    public void setTransportLayer(TransportLayer transportLayer) {
        this.transportLayer = transportLayer;
    }

    @Override
    public void setNetworkAdapter(NetworkAdapter networkAdapter) {
        this.networkAdapter = networkAdapter; 
    }

    public boolean isActive() {
        return currentState == State.WaitingForBytes;
    }

    private void processReceivedBytesFromByteBuffer(ByteBuffer byteBuffer) throws ProtocolException {
        switch (currentState) {
            case WaitingForBytes:
                while (rcvByteBuffer.position() < expectedNumberOfBytes && byteBuffer.position() < byteBuffer.limit())
                    rcvByteBuffer.put(byteBuffer.get());
                if (rcvByteBuffer.position() == expectedNumberOfBytes) {
                    if (traceLogger.isEnabled())
                        traceLogger.trace("Rcv: " + HexStringEncoder.bytesAsHexString(rcvByteBuffer.array(), 0, expectedNumberOfBytes));
                    ReceiveProtocolPacket protocolPacket = new ReceiveProtocolPacket(rcvByteBuffer.array(), 0, expectedNumberOfBytes);
                    protocolPacket.trim();
                    transportLayer.receive(protocolPacket);
                    byteBuffer.compact();
                    rcvByteBuffer.clear();
                    currentState = State.WaitingForCountBytes;
                    if (rcvByteBuffer.capacity() > initialBufferSize) {
                        logger.debug("Resizing buffer to original size: " + initialBufferSize + " bytes.");
                        rcvByteBuffer = ByteBuffer.allocate(initialBufferSize);
                    }
                }
                break;
                
            case WaitingForCountBytes:
                while (byteBuffer.hasRemaining() && rcvByteBuffer.position() < SIZE_BYTE_COUNT) 
                    rcvByteBuffer.put(byteBuffer.get());
                if (rcvByteBuffer.position() >= SIZE_BYTE_COUNT) {
                    rcvByteBuffer.flip();
                    expectedNumberOfBytes = Unsigned.getUnsignedShort(rcvByteBuffer);
                    if (rcvByteBuffer.capacity() < expectedNumberOfBytes) {
                        logger.debug("Insufficient space for packet.  Increasing size of buffer to " + expectedNumberOfBytes + " bytes.");
                        rcvByteBuffer = ByteBuffer.allocate(expectedNumberOfBytes);
                    }
                    rcvByteBuffer.clear();
                    currentState = State.WaitingForBytes;
                    processReceivedBytesFromByteBuffer(byteBuffer);
                }
                break;
        }
    }
    
    @Override
    public void receive(ByteBuffer byteBuffer) throws ProtocolException {
        while (byteBuffer.hasRemaining()) {
            processReceivedBytesFromByteBuffer(byteBuffer);
            if (byteBuffer.hasRemaining())
                byteBuffer.flip();
        }
        byteBuffer.clear();
    }  
    
    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        short length = protocolPacket.bodyLength().shortValue();
        protocolPacket.addHeader(SIZE_BYTE_COUNT);
        ByteBuffer byteBuffer = protocolPacket.headerByteBuffer();
        byteBuffer.putShort(length);
        if (traceLogger.isEnabled())
            traceLogger.trace("Trx: " + HexStringEncoder.bytesAsHexString(protocolPacket.bodyByteBuffer()));
        protocolPacket.merge();
        networkAdapter.transmit(protocolPacket);
    }

    @Override
    public void close() {
        networkAdapter.close();
    }
    
    /**
     * For Testing
     * @return
     */
    State currentState() {
        return currentState;
    }

    @Override
    public void start() throws IOException {
        networkAdapter.start();
    }

    @Override
    public void reset() {
        networkAdapter.reset();
    }

}
