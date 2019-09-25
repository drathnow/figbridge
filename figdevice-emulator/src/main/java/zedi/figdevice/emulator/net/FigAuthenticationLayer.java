package zedi.figdevice.emulator.net;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.CompressionLayer;
import zedi.pacbridge.net.FramingLayer;
import zedi.pacbridge.net.ProtocolException;
import zedi.pacbridge.net.ReceiveProtocolPacket;
import zedi.pacbridge.net.SecurityLayer;
import zedi.pacbridge.net.TransmitProtocolPacket;

public class FigAuthenticationLayer implements SecurityLayer {
    private static final Logger logger = LoggerFactory.getLogger(FigAuthenticationLayer.class.getName());
    
    enum State {Authenticated, NotAuthenticated}
    
    private State currentState;
    private FramingLayer framingLayer;
    private CompressionLayer compressionLayer;
    private AuthenticationStrategy authenticationStrategy;
    private AuthenticationListener authenticationListener;
    
    public FigAuthenticationLayer(AuthenticationStrategy authenticationStrategy, AuthenticationListener authenticationListener) {
        this.authenticationStrategy = authenticationStrategy;
        this.currentState = State.NotAuthenticated;
        this.authenticationListener = authenticationListener;
    }
    
    @Override
    public void setAuthenticationListener(zedi.pacbridge.net.auth.AuthenticationListener listener) {
        throw new UnsupportedOperationException("Sorry, you can't use this!!");
    }

    @Override
    public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
        switch (currentState) {
            case Authenticated : {
                compressionLayer.receive(protocolPacket);
                break;
            }
            
            case NotAuthenticated : {
                authenticationStrategy.handleRecievedPacket(protocolPacket);
                if (authenticationStrategy.isAuthenticated()) {
                    currentState = State.Authenticated;
                    authenticationListener.authenticate();
                } else {
                    byte[] bytes = new byte[100];
                    TransmitProtocolPacket packet = new TransmitProtocolPacket(bytes, 20, 80);
                    Integer len = 0;
                    if ((len = authenticationStrategy.lengthOfNextPacket(packet)) > 0) {
                        packet.setBodyLength(len);
                        packet.merge();
                        try {
                            framingLayer.transmit(packet);
                            if (authenticationStrategy.isAuthenticated()) {
                                currentState = State.Authenticated;
                                authenticationListener.authenticate();
                            }
                        } catch (IOException e) {
                            logger.error("Unable to authenticate", e);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        switch (currentState) {
            case Authenticated : {
                framingLayer.transmit(protocolPacket);
                break;
            }
            
            case NotAuthenticated : {
                throw new IOException("Not authenticated");
            }
        }
    }

    @Override
    public void setCompressionLayer(CompressionLayer compressionLayer) {
        this.compressionLayer = compressionLayer;
    }

    @Override
    public void setFramingLayer(FramingLayer framingLayer) {
        this.framingLayer = framingLayer;
    }

    @Override
    public void start() throws IOException {
        framingLayer.start();
    }

    @Override
    public void close() {
        framingLayer.close();
    }

    @Override
    public void reset() {
        currentState = State.NotAuthenticated;
        authenticationStrategy.reset();
        framingLayer.reset();
    }

}
