package zedi.figbridge.monitor.utl;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.zap.messages.AuthenticationResponseMessage;
import zedi.pacbridge.zap.messages.ChallengeResponseMessageV1;
import zedi.pacbridge.zap.messages.ServerChallenge;
import zedi.pacbridge.zap.messages.SessionHeader;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapPacketHeader;

public class ClientAuthenticator {
    private static final Logger logger = LoggerFactory.getLogger(ClientAuthenticator.class.getName());
    private ChallengeResponseMessageBuilder messageBuilder;
    private CountedBytePacketReader packetReader;
    private CountedBytePacketWriter packetWriter;
    private byte[] buffer;
    private byte[] serverSalt;
    
    public ClientAuthenticator(ChallengeResponseMessageBuilder messageBuilder, CountedBytePacketReader packetReader, CountedBytePacketWriter packetWriter, int bufferSize) {
        this.messageBuilder = messageBuilder;
        this.packetReader = packetReader;
        this.packetWriter = packetWriter;
        this.buffer = new byte[bufferSize];
    }

    public boolean didAuthenticate() throws IOException {
        int length = packetReader.lengthOfNextPacket(buffer);
        if (length == 0) {
            logger.error("No Challenge message returned from server");
            return false;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        ZapPacket packet = ZapPacket.packetFromByteBuffer(byteBuffer);
        ServerChallenge challenge = (ServerChallenge)packet.getMessage();
        serverSalt = challenge.getServerSaltValue();
        ChallengeResponseMessageV1 responseMessage = messageBuilder.newMessageWithServerSalt(serverSalt);
        ZapPacketHeader header = new SessionHeader(responseMessage.messageType());
        packet = new ZapPacket(header, responseMessage);
        byteBuffer.clear();
        packet.serialize(byteBuffer);
        byteBuffer.flip();
        packetWriter.sendBytes(buffer, byteBuffer.limit());
        length = packetReader.lengthOfNextPacket(buffer);
        if (length == 0) {
            logger.error("No AuthenticationResponseMessage returned from server");
            return false;
        }
        byteBuffer = ByteBuffer.wrap(buffer);
        packet = ZapPacket.packetFromByteBuffer(byteBuffer);
        AuthenticationResponseMessage authenticationResponse = (AuthenticationResponseMessage)packet.getMessage();
        return authenticationResponse.isAuthenticated();
    }
}