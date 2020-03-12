package zedi.pacbridge.app.auth.zap;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.auth.SessionKeyGenerator;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.auth.AuthenticationContext;
import zedi.pacbridge.net.auth.AuthenticationStrategy;
import zedi.pacbridge.net.auth.CompressionContext;
import zedi.pacbridge.net.auth.EncryptionContext;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.zap.messages.AuthenticationResponseMessage;
import zedi.pacbridge.zap.messages.ChallengeResponseMessage;
import zedi.pacbridge.zap.messages.ConnectionFlags;
import zedi.pacbridge.zap.messages.ServerChallenge;
import zedi.pacbridge.zap.messages.ZapSessionHeader;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapPacketHeader;

public class ZapAuthenticationStrategy implements AuthenticationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ZapAuthenticationStrategy.class.getName());

    public static final Integer SALT_SIZE = 16;
    
    private Hasher hasher;
    private Random random;
    private byte[] serverSalt;
    private State currentState;
    private Message nextMessage;
    private Integer networkNumber;
    private AuthenticationContext authenticationContext;
    private AuthenticationDelegate authenticationDelegate;
    private SessionKeyGenerator sessionKeyGenerator;
    private InetSocketAddress remoteAddress;
    private TraceLogger traceLogger;
    
    private AuthenticationMode authenticationMode;
    
    ZapAuthenticationStrategy(Integer networkNumber, Hasher hasher, AuthenticationDelegate authenticationDelegate, Random random, SessionKeyGenerator sessionKeyGenerator, InetSocketAddress remoteAddress, TraceLogger traceLogger) {
        this.networkNumber = networkNumber;
        this.hasher = hasher;
        this.random = random;
        this.sessionKeyGenerator = sessionKeyGenerator;
        this.authenticationDelegate = authenticationDelegate;
        this.remoteAddress = remoteAddress;
        this.traceLogger = traceLogger;
        this.authenticationMode = new DefaultAuthenticationMode(authenticationDelegate, hasher, traceLogger);
        reset();
    }
    
    public ZapAuthenticationStrategy(Integer networkNumber, Hasher hasher, AuthenticationDelegate authenticationDelegate, SessionKeyGenerator sessionKeyGenerator, InetSocketAddress remoteAddress, TraceLogger traceLogger) {
        this(networkNumber, hasher, authenticationDelegate, new Random(System.currentTimeMillis()), sessionKeyGenerator, remoteAddress, traceLogger);
    }
    
    public void enablePromiscuousMode(String usernameMatchingRe) {
        this.authenticationMode = new PromiscuousAuthenticationMode(authenticationDelegate, usernameMatchingRe, hasher, traceLogger);
    }
    
    @Override
    public void reset() {
        this.serverSalt = new byte[SALT_SIZE];
        this.random.nextBytes(serverSalt);
        this.nextMessage = new ServerChallenge(serverSalt);
        this.currentState = new WaitingForDeviceResponseState();
    }
    
    @Override
    public void handleBytesFromClient(ByteBuffer byteBuffer) {
        currentState.handleBytesFromClient(byteBuffer);
    }

    @Override
    public AuthenticationContext authenticationContext() {
        return authenticationContext;
    }

    @Override
    public boolean isFinished() {
        return currentState.isFinished() && nextMessage == null;
    }

    @Override
    public Packet nextPacket() {
        try {
            if (nextMessage == null)
                return null;
            ZapPacketHeader header = new ZapSessionHeader(nextMessage.messageType());
            return new ZapPacket(header, nextMessage);
        } finally {
            nextMessage = null;
        }
    }

    @Override
    public boolean isAuthenticated() {
        return currentState.isAuthenticate();
    }

    private void createAuthenticationResponseMessage(ChallengeResponseMessage response, byte[] secretKey) {
        byte[] sessionKey = sessionKeyGenerator.newSessionKeyForSecretKey(secretKey);
        EncryptionContext encryptionContext = new EncryptionContext(response.getEncryptionType(), secretKey, sessionKey);
        CompressionContext compressionContext = new CompressionContext(response.getCompressionType());
        SiteAddress siteAddress = new NuidSiteAddress(response.getUsername(), networkNumber, remoteAddress);
        authenticationContext = new AuthenticationContext(siteAddress, encryptionContext, compressionContext, remoteAddress, response.getFirmwareVersion());
        ConnectionFlags connectionFlags = new ConnectionFlags();
        connectionFlags.setAuthorized(true);
        connectionFlags.setOutBoundDataPending(authenticationDelegate.hasOutgoingDataRequests(siteAddress));
        String serverName = authenticationDelegate.serverName();
        Integer serverTime = (int)(System.currentTimeMillis()/1000L);
        nextMessage = new AuthenticationResponseMessage(connectionFlags, response.getDeviceTime(), serverTime, serverName, serverSalt, sessionKey);
    }
    
    private void failWithUnknownDeviceError(String username) {
        nextMessage = new AuthenticationResponseMessage();
        logger.warn("Authentication request recieved for unknown device: '" + username + "' - Authentication failed!");
    }
    
    private interface State {
        boolean isFinished();
        boolean isAuthenticate();
        public void handleBytesFromClient(ByteBuffer byteBuffer);
    }
    
    private class WaitingForDeviceResponseState implements State {

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isAuthenticate() {
            return false;
        }

        @Override
        public void handleBytesFromClient(ByteBuffer byteBuffer) {
            Packet packet = ZapPacket.packetFromByteBuffer(byteBuffer);
            ChallengeResponseMessage response = (ChallengeResponseMessage)packet.getMessage();

            if (authenticationMode.isAuthorized(response, serverSalt))
                createAuthenticationResponseMessage(response, authenticationMode.getSecretKey());
            else 
                failWithUnknownDeviceError(response.getUsername());
            currentState = new FinishedState();
        }
    }

    private class FinishedState implements State {

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isAuthenticate() {
            return authenticationContext != null && nextMessage == null;
        }

        @Override
        public void handleBytesFromClient(ByteBuffer byteBuffer) {
        }
    }
    
}
