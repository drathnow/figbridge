package zedi.pacbridge.zap;

import java.io.IOException;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.CompressionLayer;
import zedi.pacbridge.net.PacketLayer;
import zedi.pacbridge.net.ProtocolException;
import zedi.pacbridge.net.ReceiveProtocolPacket;
import zedi.pacbridge.net.SessionLayer;
import zedi.pacbridge.net.TransmitProtocolPacket;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Unsigned;

public class ZapPacketLayer implements PacketLayer {
    public static final Integer HEADER_BYTE_COUNT = 7;
    
    private CompressionLayer compressionLayer;
    private SessionLayer sessionManager;
    private TraceLogger traceLogger;
    
    public ZapPacketLayer(TraceLogger traceLogger) {
        this.traceLogger = traceLogger;
    }

    public void transmit(TransmitProtocolPacket protocolPacket, Integer messageType, Integer sequenceNumber, Integer sessionId) throws IOException {
        protocolPacket.addHeader(HEADER_BYTE_COUNT);
        ByteBuffer byteBuffer = protocolPacket.headerByteBuffer();
        byteBuffer.put((byte)1);
        byteBuffer.putShort(messageType.shortValue());
        byteBuffer.putShort(sessionId.shortValue());
        byteBuffer.putShort(sequenceNumber.shortValue());
        protocolPacket.merge();
        if (traceLogger.isEnabled())
            traceLogger.trace("Trx: " + HexStringEncoder.bytesAsHexString(protocolPacket.bodyByteBuffer()));
        compressionLayer.transmit(protocolPacket);
    }
    
    @Override
    public void setSessionManager(SessionLayer sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void setCompressionLayer(CompressionLayer compressionLayer) {
        this.compressionLayer = compressionLayer;
    }

    @Override
    public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
        if (traceLogger.isEnabled())
            traceLogger.trace("Rcv: " + HexStringEncoder.bytesAsHexString(protocolPacket.bodyByteBuffer()));
        protocolPacket.extractHeader(HEADER_BYTE_COUNT);
        ByteBuffer byteBuffer = protocolPacket.headerByteBuffer();
        byteBuffer.get();
        Integer messageType = Unsigned.getUnsignedShort(byteBuffer);
        Integer sessionId = Unsigned.getUnsignedShort(byteBuffer);
        Integer sequenceNumber = Unsigned.getUnsignedShort(byteBuffer);
        protocolPacket.trim();
        sessionManager.receive(protocolPacket, messageType, sequenceNumber, sessionId);
    }

    @Override
    public void close() {
        compressionLayer.close();
    }

    @Override
    public void start() throws IOException {
        compressionLayer.start();
    }

    @Override
    public void reset() {
        compressionLayer.reset();
    }
}
