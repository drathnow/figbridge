package zedi.figdevice.emulator.net;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.net.ReceiveProtocolPacket;
import zedi.pacbridge.net.TransmitProtocolPacket;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.utl.io.SaltGenerator;
import zedi.pacbridge.zap.messages.AuthenticationResponseMessage;
import zedi.pacbridge.zap.messages.ChallengeResponseMessageV1;
import zedi.pacbridge.zap.messages.ServerChallenge;
import zedi.pacbridge.zap.messages.SessionHeader;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapPacketHeader;

public class AuthenticationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationStrategy.class.getName());

    public static final Integer CLIENT_SALT_LENGTH = 16;
    
    private enum State {
        ExpectingServerChallenge,
        SendDeviceResponseToChallenge,
        ExpectingServerAuthentication        
    };
    
    private Hasher hasher;
    private String username;
    private State currentState;
    private byte[] serverSalt;
    private byte[] secretKey;
    private AuthenticationResponseMessage response;
    
    public AuthenticationStrategy(Hasher hasher, String username) {
        this.hasher = hasher;
        this.username = username;
        this.currentState = State.ExpectingServerChallenge;
    }

    public boolean isAuthenticated() {
        return response != null && response.isAuthenticated();
    }
    
    public Integer lengthOfNextPacket(TransmitProtocolPacket protocolPacket) {
        switch (currentState) {
            case ExpectingServerChallenge : {
                break;
            }

            case SendDeviceResponseToChallenge : {
                byte[] clientSalt = SaltGenerator.generateSalt(CLIENT_SALT_LENGTH);
                
                hasher.reset();
                hasher.update(username.getBytes());
                if (secretKey != null)
                    hasher.update(secretKey);
                hasher.update(clientSalt);
                hasher.update(serverSalt);

                byte[] clientHash = hasher.hashedValue(16);
                logger.debug("Sending ClientSalt: " + HexStringEncoder.bytesAsHexString(clientSalt));
                logger.debug("Sending ClientHash: " + HexStringEncoder.bytesAsHexString(clientHash));
                logger.debug("Sending Username  : " + HexStringEncoder.bytesAsHexString(username.getBytes()));
                ChallengeResponseMessageV1 message = new ChallengeResponseMessageV1(EncryptionType.NONE,
                                                                                CompressionType.NONE,
                                                                                (int)(System.currentTimeMillis()/1000),
                                                                                clientSalt,
                                                                                clientHash,
                                                                                username);
                ZapPacketHeader header = new SessionHeader(message.messageType());
                ZapPacket packet = new ZapPacket(header, message);
                ByteBuffer byteBuffer = protocolPacket.bodyByteBuffer();
                int pos = byteBuffer.position();
                packet.serialize(byteBuffer);
                currentState = State.ExpectingServerAuthentication;
                return byteBuffer.position() - pos;
            }

            case ExpectingServerAuthentication : {
                throw new UnsupportedOperationException("This should never happen");
            }

        }
        return 0;            
    }

    public void handleRecievedPacket(ReceiveProtocolPacket protocolPacket) {
        switch (currentState) {
            case ExpectingServerChallenge : {
                ZapPacket packet = ZapPacket.packetFromByteBuffer(protocolPacket.bodyByteBuffer());
                ServerChallenge challenge = (ServerChallenge)packet.getMessage();
                serverSalt = challenge.getServerSaltValue();
                currentState = State.SendDeviceResponseToChallenge;
                logger.debug("Rcvd ServerSalt: " + HexStringEncoder.bytesAsHexString(serverSalt));
                break;
            }
            
            case SendDeviceResponseToChallenge : {
                throw new UnsupportedOperationException("This should never happen");
            }
            
            case ExpectingServerAuthentication : {
                ZapPacket packet = ZapPacket.packetFromByteBuffer(protocolPacket.bodyByteBuffer());
                response = (AuthenticationResponseMessage)packet.getMessage();
                break;
            }
        }        
    }
    
    public void reset() {
        currentState = State.ExpectingServerChallenge;
        serverSalt = null;
        response = null;
    }
}
