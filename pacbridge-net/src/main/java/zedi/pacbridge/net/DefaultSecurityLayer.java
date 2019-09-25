package zedi.pacbridge.net;

import java.io.IOException;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.auth.AuthenticationContext;
import zedi.pacbridge.net.auth.AuthenticationListener;
import zedi.pacbridge.net.auth.AuthenticationStrategy;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.HexStringEncoder;


public class DefaultSecurityLayer implements SecurityLayer {
    enum State {
        Authenticated, NotAuthenticated
    }
    
    private AuthenticationStrategy authenticationStrategy;
    private CompressionLayer compressionLayer;
    private FramingLayer framingLayer;
    private State currentState;
    private TransmitProtocolPacket secureProtocolPacket;
    private AuthenticationListener authenticationListener;
    private TraceLogger traceLogger;
    private boolean firstPacketSent;
    
    DefaultSecurityLayer(AuthenticationStrategy authenticationStrategy, int bufferSize, TransmitProtocolPacket secureProtocolPacket, TraceLogger traceLogger) {
        this.secureProtocolPacket = secureProtocolPacket;
        this.traceLogger = traceLogger;
        this.authenticationStrategy = authenticationStrategy;
        this.currentState = State.NotAuthenticated;
        this.firstPacketSent = false;
    }
    
    public DefaultSecurityLayer(AuthenticationStrategy authenticationStrategy, int bufferSize, TraceLogger traceLogger) {
        this(authenticationStrategy, bufferSize, new TransmitProtocolPacket(new byte[bufferSize], 2, bufferSize-2), traceLogger);
    }
    
    public void setAuthenticationListener(AuthenticationListener authenticationListener) {
        this.authenticationListener = authenticationListener;
    }
    
    public boolean isAuthenticated() {
        return currentState == State.Authenticated;
    }
    
    @Override
    public void start() throws IOException {
        framingLayer.start();
        sendNextPacket();
    }
    
    @Override
    public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
        if (traceLogger.isEnabled())
            traceLogger.trace("Rcv: " + HexStringEncoder.bytesAsHexString(protocolPacket.bodyByteBuffer()));
        if (currentState == State.NotAuthenticated) {
            authenticationStrategy.handleBytesFromClient(protocolPacket.bodyByteBuffer());
            try {
                sendNextPacket();
            } catch (IOException e) {
                throw new ProtocolException("Unable to send next authentication message: ", e);
            }
            if (authenticationStrategy.isFinished()) {
                if (authenticationStrategy.isAuthenticated()) {
                    AuthenticationContext context = authenticationStrategy.authenticationContext();
                    if (authenticationListener != null)
                        authenticationListener.deviceAuthenticated(context);
                    currentState = State.Authenticated;
                } else
                    authenticationListener.authenticationFailed();
            }
        } else
            compressionLayer.receive(protocolPacket);
    }
    
    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        if (currentState == State.NotAuthenticated)
            throw new IllegalStateException("Unable to transmit packet.  Layer not authenticated!");
        privateTransmit(protocolPacket);
    }
    
    @Override
    public void setCompressionLayer(CompressionLayer compressionLayer) {
        this.compressionLayer = compressionLayer;
    }

    @Override
    public void setFramingLayer(FramingLayer framingLayer) {
        this.framingLayer = framingLayer;
    }
    
    private void sendNextPacket() throws IOException {
        Packet packet = authenticationStrategy.nextPacket();
        if (packet != null) {
            if (firstPacketSent == false) {
                firstPacketSent = true;
                if (authenticationListener != null)
                    authenticationListener.authenticationStarted();
            }
            secureProtocolPacket.reset();
            ByteBuffer byteBuffer = secureProtocolPacket.bodyByteBuffer();
            int savedPos = byteBuffer.position();
            packet.serialize(byteBuffer);
            int len = byteBuffer.position()-savedPos;
            secureProtocolPacket.setBodyLength(len);
            privateTransmit(secureProtocolPacket);
        }
    }

    @Override
    public void close() {
        framingLayer.close();
        authenticationListener = null;
    }

    @Override
    public void reset() {
        framingLayer.reset();
        authenticationStrategy.reset();
        this.currentState = State.NotAuthenticated;
        this.firstPacketSent = false;
    }
    
    private void privateTransmit(TransmitProtocolPacket protocolPacket) throws IOException {
        if (traceLogger.isEnabled())
            traceLogger.trace("Trx: " + HexStringEncoder.bytesAsHexString(protocolPacket.bodyByteBuffer()));
        framingLayer.transmit(protocolPacket);
    }
}
