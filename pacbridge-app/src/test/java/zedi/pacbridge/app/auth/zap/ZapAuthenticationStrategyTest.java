package zedi.pacbridge.app.auth.zap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static zedi.pacbridge.utl.ByteArrayArgumentMatcher.matchesByteArrayArgument;
import static zedi.pacbridge.utl.ByteArrayMatcher.matchesArrayOfBytes;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.auth.DefaultSessionKeyGenerator;
import zedi.pacbridge.app.auth.SessionKeyGenerator;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.auth.AuthenticationContext;
import zedi.pacbridge.net.auth.CompressionContext;
import zedi.pacbridge.net.auth.EncryptionContext;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AuthenticationResponseMessage;
import zedi.pacbridge.zap.messages.ChallengeResponseMessageV1;
import zedi.pacbridge.zap.messages.ServerChallenge;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapPacketHeader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZapAuthenticationStrategy.class, ChallengeResponseMessageV1.class, ZapPacket.class})
public class ZapAuthenticationStrategyTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 12;
    private static final byte[] SERVER_SALT = new byte[ZapAuthenticationStrategy.SALT_SIZE];
    private static final byte[] CLIENT_SALT = new byte[]{0x01, 0x02, 0x03};
    private static final byte[] CLIENT_HASH = new byte[]{0x04, 0x05, 0x06, 0x07};
    private static final byte[] SECRET_KEY = new byte[]{0x08, 0X09, 0X0A, 0X0B, 0X0C};
    private static final byte[] SESSION_KEY = new byte[]{0x0D, 0X0E, 0X0F, 0X10, 0X11};
    private static final String USERNAME = "spooge";
    private static final String NON_MATCHING_USERNAME = "Bob";
    private static final String MATCHING_RE = "spo.*";
    private static final String NUID = "1234";
    private static final String FIRMWARE_VERSION = "V1.2.3";
    
    static {
        for (int i = 0; i < SERVER_SALT.length; i++)
            SERVER_SALT[i] = (byte)i;
    }
    
    @Mock
    private Hasher hasher;
    @Mock
    private AuthenticationDelegate delegate;
    @Mock
    private TraceLogger traceLogger;

    
    @Test
    public void shouldAuthenticateDeviceFromDatabaseIfPromiscuousModeEnabledWithMatchingRegularExpressionAndUsernameDoesNotMatchesRE() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);
        SessionKeyGenerator sessionKeyGenerator = mock(DefaultSessionKeyGenerator.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        given(packet.getHeader()).willReturn(header);
        given(delegate.deviceForNuid(NON_MATCHING_USERNAME)).willReturn(null);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getUsername()).willReturn(NON_MATCHING_USERNAME);
        given(sessionKeyGenerator.newSessionKeyForSecretKey(SECRET_KEY)).willReturn(SESSION_KEY);
        given(delegate.serverName()).willReturn(InetAddress.getLocalHost().getHostName());

        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);

        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, sessionKeyGenerator, null, traceLogger);
        strategy.enablePromiscuousMode(MATCHING_RE);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);
        
        Packet nextPacket = strategy.nextPacket();
        assertTrue(strategy.isFinished());
        assertFalse(strategy.isAuthenticated());
        assertNull(strategy.authenticationContext());
        verify(delegate).deviceForNuid(NON_MATCHING_USERNAME);

        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertNotNull(nextMessage);
        assertFalse(nextMessage.getConnectionFlags().isAuthorized());
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);        
    }
       
    @Test
    public void shouldAuthenticateDeviceIfPromiscuousModeEnabledWithMatchingRegularExpressionAndUsernameMatchesREAndNotHitTheDatabase() throws Exception {
        EncryptionContext encryptionContext = mock(EncryptionContext.class);
        CompressionContext compressionContext = mock(CompressionContext.class);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);
        SessionKeyGenerator sessionKeyGenerator = mock(DefaultSessionKeyGenerator.class);
        NuidSiteAddress siteAddress = mock(NuidSiteAddress.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        given(packet.getHeader()).willReturn(header);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getUsername()).willReturn(USERNAME);
        given(sessionKeyGenerator.newSessionKeyForSecretKey(SECRET_KEY)).willReturn(SESSION_KEY);
        given(delegate.serverName()).willReturn(InetAddress.getLocalHost().getHostName());

        whenNew(EncryptionContext.class).withArguments(EncryptionType.NONE, SECRET_KEY, SESSION_KEY).thenReturn(encryptionContext);
        whenNew(CompressionContext.class).withArguments(CompressionType.NONE).thenReturn(compressionContext);
        whenNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER).thenReturn(siteAddress);

        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);

        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, sessionKeyGenerator, null, traceLogger);
        strategy.enablePromiscuousMode(MATCHING_RE);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);
        
        Packet nextPacket = strategy.nextPacket();
        assertTrue(strategy.isFinished());
        assertTrue(strategy.isAuthenticated());
        assertNotNull(strategy.authenticationContext());
        verify(delegate, times(0)).deviceForNuid(USERNAME);

        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage.getConnectionFlags().isAuthorized());
        assertFalse(nextMessage.getConnectionFlags().isOutBoundDataPending());
        assertEquals(InetAddress.getLocalHost().getHostName(), nextMessage.getServerName());
        assertNull(nextMessage.getSessionKey());
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);        
    }
    
    @Test
    public void shouldAuthenticateAnyDeviceIfPromiscuousModeEnabledAndNotHitTheDatabase() throws Exception {
        EncryptionContext encryptionContext = mock(EncryptionContext.class);
        CompressionContext compressionContext = mock(CompressionContext.class);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);
        SessionKeyGenerator sessionKeyGenerator = mock(DefaultSessionKeyGenerator.class);
        NuidSiteAddress siteAddress = mock(NuidSiteAddress.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        given(packet.getHeader()).willReturn(header);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getUsername()).willReturn(USERNAME);
        given(sessionKeyGenerator.newSessionKeyForSecretKey(SECRET_KEY)).willReturn(SESSION_KEY);
        given(delegate.serverName()).willReturn(InetAddress.getLocalHost().getHostName());

        whenNew(EncryptionContext.class).withArguments(EncryptionType.NONE, SECRET_KEY, SESSION_KEY).thenReturn(encryptionContext);
        whenNew(CompressionContext.class).withArguments(CompressionType.NONE).thenReturn(compressionContext);
        whenNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER).thenReturn(siteAddress);

        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);

        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, sessionKeyGenerator, null, traceLogger);
        strategy.enablePromiscuousMode(null);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);
        
        Packet nextPacket = strategy.nextPacket();
        assertTrue(strategy.isFinished());
        assertTrue(strategy.isAuthenticated());
        assertNotNull(strategy.authenticationContext());
        verify(delegate, times(0)).deviceForNuid(USERNAME);

        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage.getConnectionFlags().isAuthorized());
        assertFalse(nextMessage.getConnectionFlags().isOutBoundDataPending());
        assertEquals(InetAddress.getLocalHost().getHostName(), nextMessage.getServerName());
        assertNull(nextMessage.getSessionKey());
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);        
    }
    
    @Test
    public void shouldHandleClientResponseAndNotAuthenticateIfDeviceDoesNotExist() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        given(packet.getHeader()).willReturn(header);
        given(delegate.deviceForNuid(USERNAME)).willReturn(null);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getUsername()).willReturn(USERNAME);
        
        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);

        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, null, null, traceLogger);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);
        
        Packet nextPacket = strategy.nextPacket();
        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertFalse(nextMessage.isAuthenticated());
                
        assertTrue(strategy.isFinished());
        assertFalse(strategy.isAuthenticated());
        assertNull(strategy.authenticationContext());

        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);
    }
    
    @Test
    public void shouldHandleClientResponseAndAuthenticateWhenPromiscuousModeEnabled() throws Exception {
        InetSocketAddress address = new InetSocketAddress(0);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        EncryptionContext encryptionContext = mock(EncryptionContext.class);
        CompressionContext compressionContext = mock(CompressionContext.class);
        NuidSiteAddress siteAddress = mock(NuidSiteAddress.class);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        SessionKeyGenerator sessionKeyGenerator = mock(DefaultSessionKeyGenerator.class);

        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        
        given(delegate.serverName()).willReturn(InetAddress.getLocalHost().getHostName());
        given(delegate.hasOutgoingDataRequests(siteAddress)).willReturn(true);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getUsername()).willReturn(USERNAME);
        given(response.getEncryptionType()).willReturn(EncryptionType.NONE);
        given(response.getCompressionType()).willReturn(CompressionType.NONE);
        given(response.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        given(sessionKeyGenerator.newSessionKeyForSecretKey(null)).willReturn(SESSION_KEY);
        
        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        whenNew(EncryptionContext.class).withArguments(EncryptionType.NONE, null, SESSION_KEY).thenReturn(encryptionContext);
        whenNew(CompressionContext.class).withArguments(CompressionType.NONE).thenReturn(compressionContext);
        whenNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER, address).thenReturn(siteAddress);
        whenNew(AuthenticationContext.class)
            .withArguments(siteAddress, encryptionContext, compressionContext, address, FIRMWARE_VERSION)
            .thenReturn(authenticationContext);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);
        random.returnBytes.add(SECRET_KEY);
        
        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, sessionKeyGenerator, address, traceLogger);
        strategy.enablePromiscuousMode(null);
        
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);

        verifyNew(EncryptionContext.class).withArguments(EncryptionType.NONE, null, SESSION_KEY);
        verifyNew(CompressionContext.class).withArguments(CompressionType.NONE);
        verifyNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER, address);
        verifyNew(AuthenticationContext.class).withArguments(siteAddress, encryptionContext, compressionContext, address, FIRMWARE_VERSION);

        assertFalse(strategy.isFinished());
        assertFalse(strategy.isAuthenticated());
        
        Packet nextPacket = strategy.nextPacket();
        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage.getConnectionFlags().isAuthorized());
        assertTrue(nextMessage.getConnectionFlags().isOutBoundDataPending());
        assertEquals(InetAddress.getLocalHost().getHostName(), nextMessage.getServerName());
        assertTrue(Arrays.equals(SESSION_KEY, nextMessage.getSessionKey()));
        
        assertTrue(strategy.isFinished());
        assertTrue(strategy.isAuthenticated());
        assertSame(authenticationContext, strategy.authenticationContext());
        verify(hasher, times(0)).update(any(byte[].class));
        verify(delegate, times(0)).deviceForNuid(any(String.class));
        verify(response, times(0)).getClientHash();
        verify(response, times(0)).getClientSalt();
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);
        
    }
    
    
    @Test
    public void shouldHandleClientResponseAndNotAuthenticateIfInvalid() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        Device device = mock(Device.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        given(hasher.hashedValue(16)).willReturn(CLIENT_SALT);
        given(device.getNuid()).willReturn(NUID);
        given(device.getSecretKey()).willReturn(SECRET_KEY);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(delegate.deviceForNuid(USERNAME)).willReturn(device);
        given(response.getClientHash()).willReturn(CLIENT_HASH);
        given(response.getUsername()).willReturn(USERNAME);
        given(response.getClientSalt()).willReturn(CLIENT_SALT);
        given(response.getEncryptionType()).willReturn(EncryptionType.NONE);
        given(response.getCompressionType()).willReturn(CompressionType.NONE);
        
        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        InOrder inOrder = inOrder(hasher);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);

        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, null, null, traceLogger);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);
        
        Packet nextPacket = strategy.nextPacket();
        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertFalse(nextMessage.isAuthenticated());
        
        assertTrue(strategy.isFinished());
        assertFalse(strategy.isAuthenticated());
        assertNull(strategy.authenticationContext());
        
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(USERNAME.getBytes())));
        inOrder.verify(hasher).update(SECRET_KEY);
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(CLIENT_SALT)));
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(SERVER_SALT)));
        
        verify(hasher).update(argThat(matchesByteArrayArgument(SERVER_SALT)));
        verify(hasher).update(argThat(matchesByteArrayArgument(CLIENT_SALT)));
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);
    }    
    
    @Test
    public void shouldHandleClientResponseAndAuthenticateIfValidWithOutboundDataPending() throws Exception {
        InetSocketAddress address = new InetSocketAddress(0);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        EncryptionContext encryptionContext = mock(EncryptionContext.class);
        CompressionContext compressionContext = mock(CompressionContext.class);
        Device device = mock(Device.class);
        NuidSiteAddress siteAddress = mock(NuidSiteAddress.class);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        SessionKeyGenerator sessionKeyGenerator = mock(DefaultSessionKeyGenerator.class);

        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        
        InOrder inOrder = inOrder(hasher);
        
        given(hasher.hashedValue(16)).willReturn(CLIENT_HASH);
        given(device.getNuid()).willReturn(NUID);
        given(device.getSecretKey()).willReturn(SECRET_KEY);
        given(delegate.serverName()).willReturn(InetAddress.getLocalHost().getHostName());
        given(delegate.deviceForNuid(USERNAME)).willReturn(device);
        given(delegate.hasOutgoingDataRequests(siteAddress)).willReturn(true);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getClientHash()).willReturn(CLIENT_HASH);
        given(response.getUsername()).willReturn(USERNAME);
        given(response.getClientSalt()).willReturn(CLIENT_SALT);
        given(response.getEncryptionType()).willReturn(EncryptionType.NONE);
        given(response.getCompressionType()).willReturn(CompressionType.NONE);
        given(sessionKeyGenerator.newSessionKeyForSecretKey(SECRET_KEY)).willReturn(SESSION_KEY);
        
        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        whenNew(EncryptionContext.class).withArguments(EncryptionType.NONE, SECRET_KEY, SESSION_KEY).thenReturn(encryptionContext);
        whenNew(CompressionContext.class).withArguments(CompressionType.NONE).thenReturn(compressionContext);
        whenNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER, address).thenReturn(siteAddress);
        whenNew(AuthenticationContext.class)
            .withArguments(siteAddress, encryptionContext, compressionContext, address, null)
            .thenReturn(authenticationContext);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);
        random.returnBytes.add(SECRET_KEY);
        
        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, sessionKeyGenerator, address, traceLogger);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);

        verifyNew(EncryptionContext.class).withArguments(EncryptionType.NONE, SECRET_KEY, SESSION_KEY);
        verifyNew(CompressionContext.class).withArguments(CompressionType.NONE);
        verifyNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER, address);
        verifyNew(AuthenticationContext.class).withArguments(siteAddress, encryptionContext, compressionContext, address, null);

        assertFalse(strategy.isFinished());
        assertFalse(strategy.isAuthenticated());
        
        Packet nextPacket = strategy.nextPacket();
        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage.getConnectionFlags().isAuthorized());
        assertTrue(nextMessage.getConnectionFlags().isOutBoundDataPending());
        assertEquals(InetAddress.getLocalHost().getHostName(), nextMessage.getServerName());
        assertTrue(Arrays.equals(SESSION_KEY, nextMessage.getSessionKey()));
        
        assertTrue(strategy.isFinished());
        assertTrue(strategy.isAuthenticated());
        assertSame(authenticationContext, strategy.authenticationContext());
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(USERNAME.getBytes())));
        inOrder.verify(hasher).update(SECRET_KEY);
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(CLIENT_SALT)));
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(SERVER_SALT)));
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);
    }

    @Test
    public void shouldHandleClientResponseAndAuthenticateIfValidWithNoOutboundDataPendering() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        InetSocketAddress address = new InetSocketAddress(0);
        EncryptionContext encryptionContext = mock(EncryptionContext.class);
        CompressionContext compressionContext = mock(CompressionContext.class);
        Device device = mock(Device.class);
        NuidSiteAddress siteAddress = mock(NuidSiteAddress.class);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);
        ChallengeResponseMessageV1 response = mock(ChallengeResponseMessageV1.class);
        SessionKeyGenerator sessionKeyGenerator = mock(DefaultSessionKeyGenerator.class);

        ZapPacket packet = mock(ZapPacket.class);
        ZapPacketHeader header = mock(ZapPacketHeader.class);

        mockStatic(ZapPacket.class);
        
        given(header.messageType()).willReturn(ZapMessageType.ClientChallengeResponse);
        given(packet.getMessage()).willReturn(response);
        
        InOrder inOrder = inOrder(hasher);
        
        given(hasher.hashedValue(16)).willReturn(CLIENT_HASH);
        given(device.getNuid()).willReturn(NUID);
        given(device.getSecretKey()).willReturn(SECRET_KEY);
        given(delegate.serverName()).willReturn(InetAddress.getLocalHost().getHostName());
        given(delegate.deviceForNuid(USERNAME)).willReturn(device);
        given(ZapPacket.packetFromByteBuffer(byteBuffer)).willReturn(packet);
        given(response.getClientHash()).willReturn(CLIENT_HASH);
        given(response.getUsername()).willReturn(USERNAME);
        given(response.getClientSalt()).willReturn(CLIENT_SALT);
        given(response.getEncryptionType()).willReturn(EncryptionType.NONE);
        given(response.getCompressionType()).willReturn(CompressionType.NONE);
        given(sessionKeyGenerator.newSessionKeyForSecretKey(SECRET_KEY)).willReturn(SESSION_KEY);
        
        ServerChallenge message = mock(ServerChallenge.class);
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);
        
        whenNew(EncryptionContext.class).withArguments(EncryptionType.NONE, SECRET_KEY, SESSION_KEY).thenReturn(encryptionContext);
        whenNew(CompressionContext.class).withArguments(CompressionType.NONE).thenReturn(compressionContext);
        whenNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER, address).thenReturn(siteAddress);
        whenNew(AuthenticationContext.class)
            .withArguments(siteAddress, encryptionContext, compressionContext, address, null)
            .thenReturn(authenticationContext);
        
        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);
        random.returnBytes.add(SECRET_KEY);
        
        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, sessionKeyGenerator, address, traceLogger);
        strategy.nextPacket();
        strategy.handleBytesFromClient(byteBuffer);

        verifyNew(EncryptionContext.class).withArguments(EncryptionType.NONE, SECRET_KEY, SESSION_KEY);
        verifyNew(CompressionContext.class).withArguments(CompressionType.NONE);
        verifyNew(NuidSiteAddress.class).withArguments(USERNAME, NETWORK_NUMBER, address);
        verifyNew(AuthenticationContext.class).withArguments(siteAddress, encryptionContext, compressionContext, address, null);

        assertFalse(strategy.isFinished());
        assertFalse(strategy.isAuthenticated());
        
        Packet nextPacket = strategy.nextPacket();
        AuthenticationResponseMessage nextMessage = (AuthenticationResponseMessage)nextPacket.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage.getConnectionFlags().isAuthorized());
        assertFalse(nextMessage.getConnectionFlags().isOutBoundDataPending());
        assertEquals(InetAddress.getLocalHost().getHostName(), nextMessage.getServerName());
        assertTrue(Arrays.equals(SESSION_KEY, nextMessage.getSessionKey()));
        
        assertTrue(strategy.isFinished());
        assertTrue(strategy.isAuthenticated());
        assertSame(authenticationContext, strategy.authenticationContext());
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(USERNAME.getBytes())));
        inOrder.verify(hasher).update(SECRET_KEY);
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(CLIENT_SALT)));
        inOrder.verify(hasher).update(argThat(matchesByteArrayArgument(SERVER_SALT)));
        
        verifyStatic(ZapPacket.class);
        ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);
    }

    @Test
    public void shouldCreateInitialChallengeMessage() throws Exception {
        ServerChallenge message = mock(ServerChallenge.class);
        
        whenNew(ServerChallenge.class)
            .withArguments(matchesArrayOfBytes(SERVER_SALT))
            .thenReturn(message);

        MyRandom random = new MyRandom();
        random.returnBytes.add(SERVER_SALT);
        
        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(NETWORK_NUMBER, hasher, delegate, random, null, null, traceLogger);
        
        assertSame(message, strategy.nextPacket().getMessage());
        assertNull(strategy.nextPacket());
        assertFalse(strategy.isFinished());
    }
    
    private class MyRandom extends Random {
        public List<byte[]> returnBytes = new ArrayList<>();
        
        @Override
        public void nextBytes(byte[] bytes) {
            byte[] nextBytes = returnBytes.remove(0);
            System.arraycopy(nextBytes, 0, bytes, 0, nextBytes.length);
        }
    }
}