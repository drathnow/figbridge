package zedi.pacbridge.net;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionManager.class)
public class SessionManagerTest extends BaseTestCase {
    private static final Integer MSG_NO = 300;
    private static final Integer SEQ_NO = 200;
    private static final Integer SESSION_ID = 100;
    private static final String ADDRESS = "1.2.3.4/12";
    
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private LoggingContext loggingContext;
    @Mock
    private PacketLayer packetLayer;
    @Mock
    private UnsolicitedMessageHandler unsolicitedMessageHandler;
    @Mock
    private SessionIdGenerator sessionIdGenerator;
    @Mock
    private ThreadContext astRequester;
    @Mock
    private Lock queueLock;
    @Mock
    private Condition condition;
    @Mock
    private DeviceSession session;
    @Mock
    private SequencedMessage message;
    @Mock
    private LinkedList<SessionManager.AsyncCommand> commandQueue;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Map<Integer, DeviceSession> sessionMap;
    @Mock
    private TransmitProtocolPacket trxProtocolPacket;
    @Mock
    private TransmitProtocolPacketFactory protocolPacketFactory;
    @Mock
    private ReceiveProtocolPacket rcvProtocolPacket;
    @SuppressWarnings("rawtypes")
    @Mock
    private MessageFactory messageFactory;
    @Mock
    private TraceLogger traceLogger;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        given(session.getSessionId()).willReturn(SESSION_ID);
        given(loggingContext.siteAddress()).willReturn(siteAddress);
    }
    
    @Test
    public void shouldPassMessageToSessionLessMessageHandlerIfSet() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        SessionlessMessageHandler sessionlessMessageHandler = mock(SessionlessMessageHandler.class);
        SessionlessMessageHandlerAdapter sessionlessMessageHandlerAdapter = mock(SessionlessMessageHandlerAdapter.class);

        given(sessionIdGenerator.nextSessionId()).willReturn(SESSION_ID);
        given(rcvProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(messageFactory.messageFromByteBuffer(MSG_NO, byteBuffer)).willReturn(message);

        whenNew(SessionlessMessageHandlerAdapter.class)
            .withParameterTypes(SessionlessMessageHandler.class)
            .withArguments(sessionlessMessageHandler)
            .thenReturn(sessionlessMessageHandlerAdapter);
        
        SessionManager sessionManager = new SessionManager(siteAddress, 
                                                           unsolicitedMessageHandler, 
                                                           sessionlessMessageHandler, 
                                                           sessionIdGenerator, 
                                                           trxProtocolPacket,
                                                           astRequester, 
                                                           messageFactory,
                                                           traceLogger,
                                                           queueLock, 
                                                           commandQueue, 
                                                           sessionMap, 
                                                           sessionFactory);

        verifyNew(SessionlessMessageHandlerAdapter.class).withArguments(sessionlessMessageHandler);
        given(sessionFactory.newSession(SESSION_ID, sessionManager)).willReturn(session);
        
        sessionManager.receive(rcvProtocolPacket, MSG_NO, SEQ_NO, SESSION_ID);

        verify(sessionFactory).newSession(SESSION_ID, sessionManager);
        verify(sessionlessMessageHandlerAdapter).invoke(siteAddress, message, session);
    }
    
    @Test
    public void shouldCreateNewSession() throws Exception {
        given(sessionIdGenerator.maxSessionId()).willReturn(SESSION_ID);
        given(sessionIdGenerator.nextSessionId()).willReturn(SESSION_ID);
        given(session.getSessionId()).willReturn(SESSION_ID);
                
        SessionManager sessionLayer = new SessionManager(siteAddress, 
                                                         unsolicitedMessageHandler, 
                                                         null, 
                                                         sessionIdGenerator, 
                                                         trxProtocolPacket,
                                                         astRequester, 
                                                         messageFactory, 
                                                         traceLogger,
                                                         queueLock, 
                                                         commandQueue, 
                                                         sessionMap,
                                                         sessionFactory);
        given(sessionFactory.newSession(SESSION_ID, sessionLayer)).willReturn(session);

        Session newSession = sessionLayer.newSession();
        
        assertSame(session, newSession);
        verify(sessionFactory).newSession(SESSION_ID, sessionLayer);
        verify(sessionMap).put(SESSION_ID, session);
    }
    
    @Test
    public void shouldPassMessageToSession() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        MessageType messageType = mock(MessageType.class);

        given(sessionIdGenerator.nextSessionId()).willReturn(SESSION_ID);
        given(sessionMap.get(SESSION_ID)).willReturn(session);
        given(messageType.getNumber()).willReturn(MSG_NO);
        given(message.messageType()).willReturn(messageType);
        given(message.sequenceNumber()).willReturn(SEQ_NO);
        given(rcvProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(messageFactory.messageFromByteBuffer(MSG_NO, byteBuffer)).willReturn(message);

        SessionManager sessionLayer = new SessionManager(siteAddress, 
                                                         unsolicitedMessageHandler, 
                                                         null, 
                                                         sessionIdGenerator, 
                                                         trxProtocolPacket,
                                                         astRequester, 
                                                         messageFactory, 
                                                         traceLogger,
                                                         queueLock, 
                                                         commandQueue, 
                                                         sessionMap, 
                                                         sessionFactory);
        
        sessionLayer.receive(rcvProtocolPacket, MSG_NO, SEQ_NO, SESSION_ID);

        verify(session).handleRecievedMessage(message);
    }
    
    @Test
    public void shouldPassUnsolicitedMessageToUnsolicitedMessageHandler() throws Exception {
        SessionlessMessageHandler sessionlessMessageHandler = mock(SessionlessMessageHandler.class);
        UnsolicitedMessageHandlerAdapter adapter = mock(UnsolicitedMessageHandlerAdapter.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        MessageType messageType = mock(MessageType.class);

        given(sessionMap.get(SESSION_ID)).willReturn(null);
        given(messageType.getNumber()).willReturn(MSG_NO);
        given(message.messageType()).willReturn(messageType);
        given(message.sequenceNumber()).willReturn(SEQ_NO);
        given(rcvProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(messageFactory.messageFromByteBuffer(MSG_NO, byteBuffer)).willReturn(message);
        
        whenNew(UnsolicitedMessageHandlerAdapter.class)
            .withParameterTypes(UnsolicitedMessageHandler.class)
            .withArguments(unsolicitedMessageHandler)
            .thenReturn(adapter);

        SessionManager sessionLayer = new SessionManager(siteAddress, 
                                                         unsolicitedMessageHandler, 
                                                         sessionlessMessageHandler,
                                                         sessionIdGenerator, 
                                                         trxProtocolPacket,
                                                         astRequester, 
                                                         messageFactory, 
                                                         traceLogger,
                                                         queueLock, 
                                                         commandQueue, 
                                                         sessionMap, 
                                                         sessionFactory);
        verifyNew(UnsolicitedMessageHandlerAdapter.class).withArguments(unsolicitedMessageHandler);

        sessionLayer.receive(rcvProtocolPacket, MSG_NO, SEQ_NO, 0);
        
        verify(adapter).invoke(eq(siteAddress), eq(message), any(ResponseSender.class));
    }
    
    
    @Test
    public void shouldSendMessage() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        MessageType messageType = mock(MessageType.class);
        
        given(trxProtocolPacket.bodyByteBuffer()).willReturn(byteBuffer);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(session.getSessionId()).willReturn(SESSION_ID);
        given(messageType.getNumber()).willReturn(MSG_NO);
        given(message.messageType()).willReturn(messageType);
        given(message.sequenceNumber()).willReturn(SEQ_NO);

        SessionManager sessionLayer = new SessionManager(siteAddress, 
                                                         unsolicitedMessageHandler, 
                                                         null, 
                                                         sessionIdGenerator, 
                                                         trxProtocolPacket,
                                                         astRequester, 
                                                         messageFactory, 
                                                         traceLogger,
                                                         queueLock, 
                                                         commandQueue, 
                                                         sessionMap, 
                                                         sessionFactory);
        sessionLayer.setPacketLayer(packetLayer);
        sessionLayer.sendMessageForSession(message, session);
        
        verify(trxProtocolPacket).reset();
        verify(message).serialize(byteBuffer);
        verify(packetLayer).transmit(trxProtocolPacket, MSG_NO, SEQ_NO, SESSION_ID);
    }
    
    @Test
    public void shouldResetAfterClose() {
        given(sessionIdGenerator.nextSessionId()).willReturn(SESSION_ID);
        given(sessionMap.values()).willReturn(Arrays.asList(session) );
        
        ArgumentCaptor<SessionManager.CloseSessionManagerCommmand> arg = ArgumentCaptor.forClass(SessionManager.CloseSessionManagerCommmand.class);
        
        SessionManager sessionLayer = new SessionManager(siteAddress, 
                                                         unsolicitedMessageHandler, 
                                                         null, 
                                                         sessionIdGenerator, 
                                                         trxProtocolPacket,
                                                         astRequester, 
                                                         messageFactory, 
                                                         traceLogger,
                                                         queueLock, 
                                                         commandQueue, 
                                                         sessionMap, 
                                                         sessionFactory);
        sessionLayer.setPacketLayer(packetLayer);
        
        sessionLayer.close();
        
        verify(commandQueue).addLast(arg.capture());
        
        SessionManager.CloseSessionManagerCommmand command = arg.getValue();
        command.execute();
        
        verify(packetLayer).close();
        verify(session).close();
        verify(sessionMap).clear();
    }
}
