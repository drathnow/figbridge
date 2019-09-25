package zedi.pacbridge.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;

public class SessionManager implements SessionLayer {
    private static Logger logger = LoggerFactory.getLogger(SessionManager.class.getName());
    
    private PacketLayer packetLayer;
    private UnsolicitedMessageHandlerAdapter unsolicitedMessageHandlerAdapter;
    private Map<Integer, DeviceSession> sessionMap;
    private SessionIdGenerator sessionIdGenerator;
    private SessionlessMessageHandlerAdapter sessionlessMessageHandlerAdapter;
    private ThreadContext threadContext;
    private ThreadContextHandler contextHandler;
    private SessionFactory sessionFactory;
    private SiteAddress siteAddress;
    private LoggingContext loggingContext;
    private TransmitProtocolPacket protocolPacket;
    private MessageFactory<?> messageFactory;
    private Lock queueLock;
    private LinkedList<AsyncCommand> commandQueue;
    private TraceLogger traceLogger;
    
    SessionManager(SiteAddress siteAddress, 
                   UnsolicitedMessageHandler unsolicitedMessageHandler, 
                   SessionlessMessageHandler sessionlessMessageHandler, 
                   SessionIdGenerator sessionIdGenerator, 
                   TransmitProtocolPacket protocolPacket,
                   ThreadContext threadContext,
                   MessageFactory<?> messageFactory, 
                   TraceLogger traceLogger,
                   Lock queueLock, 
                   LinkedList<AsyncCommand> commandQueue, 
                   Map<Integer, DeviceSession> sessionMap, SessionFactory sessionFactory) {
        setSiteAddress(siteAddress);
        this.unsolicitedMessageHandlerAdapter = (unsolicitedMessageHandler == null) ? null : new UnsolicitedMessageHandlerAdapter(unsolicitedMessageHandler);
        this.sessionlessMessageHandlerAdapter = (sessionlessMessageHandler == null) ? null : new SessionlessMessageHandlerAdapter(sessionlessMessageHandler);
        this.sessionMap = Collections.synchronizedMap(sessionMap);
        this.protocolPacket = protocolPacket;
        this.sessionFactory = sessionFactory;
        this.sessionIdGenerator = sessionIdGenerator;
        this.threadContext = threadContext;
        this.messageFactory = messageFactory;
        this.traceLogger = traceLogger;
        this.queueLock = new ReentrantLock();
        this.commandQueue = commandQueue;
        this.contextHandler = new ThreadContextHandler() {
            @Override
            public void handleSyncTrap() {
                SessionManager.this.queueLock.lock();
                try {
                    if (SessionManager.this.commandQueue.isEmpty() == false)
                        SessionManager.this.commandQueue.removeFirst().execute();
                } finally {
                    SessionManager.this.queueLock.unlock();
                }
            }
        };             
    }
    
    public SessionManager(SiteAddress siteAddress,
                          UnsolicitedMessageHandler unsolicitedMessageHandler, 
                          SessionlessMessageHandler sessionlessMessageHandler, 
                          SessionIdGenerator sessionIdGenerator,
                          TransmitProtocolPacket protocolPacket, 
                          ThreadContext astRequester, 
                          MessageFactory<?> messageFactory, 
                          TraceLogger traceLogger) {
        this(siteAddress, 
                unsolicitedMessageHandler, 
                sessionlessMessageHandler, 
                sessionIdGenerator, 
                protocolPacket,
                astRequester,
                messageFactory,
                traceLogger,
                new ReentrantLock(), 
                new LinkedList<AsyncCommand>(), 
                new TreeMap<Integer, DeviceSession>(), 
                new SessionFactory());
    }

    @Override
    public void start() throws IOException {
        packetLayer.start();
    }
    
    public void sendMessageForSession(Message message, Session session, long timeoutMilliseconds) throws IOException, InterruptedException {
        queueLock.lock();
        try {
            Condition condition = queueLock.newCondition();
            SendMessageWithWaitCommand command = new SendMessageWithWaitCommand(session, message, condition, timeoutMilliseconds);
            commandQueue.addLast(command);
            threadContext.requestTrap(contextHandler);
            command.waitForCompletion();
        } finally {
            queueLock.unlock();
        }
    }

    private void sendMessage(Message message) {
        try {
            sendMessageForSession(message, null);
        } catch (IOException e) {
            logger.error("Unable to send message", e);
        }
    }

    public void setSiteAddress(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
        this.loggingContext = new LoggingContext(siteAddress);
    }
    
    public Session newSession() {
        synchronized (sessionMap) {
            DeviceSession session = sessionFactory.newSession(sessionIdGenerator.nextSessionId(), this);
            sessionMap.put(session.getSessionId(), session);
            return session;
        }
    }
    
    public void closeSession(Session session) {
        sessionMap.remove(session.getSessionId());
    }
    
    public Integer getSessionCount() {
        return sessionMap.size();
    }
    
    @Override
    public void setPacketLayer(PacketLayer packetLayer) {
        this.packetLayer = packetLayer;
    }
    
    @Override
    public void receive(ReceiveProtocolPacket protocolPacket, Integer messageNumber, Integer sequenceNumber, Integer sessionId) {
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        if (traceLogger.isEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MsgNo: ")
                         .append(messageNumber)
                         .append(", ")
                         .append("SeqNo: ")
                         .append(sequenceNumber)
                         .append(", SesId: ")
                         .append(sessionId)
                         .append(", Pkt: ")
                         .append(HexStringEncoder.bytesAsHexString(bodyByteBuffer));
            traceLogger.trace("Rcv: " + stringBuilder.toString());
        }

        // TODO: Need to pass back some kind of status to the client to indicate the packet is bad
        Message message = messageFactory.messageFromByteBuffer(messageNumber, bodyByteBuffer);
        if (message == null) {
            logger.error("Received unknown message type: " + messageNumber);
        } else if (sessionId == 0) {
            message.setSequenceNumber(sequenceNumber);
            ResponseSender responseSender = new ResponseSender() {
                private Thread thread = Thread.currentThread();
                @Override
                public void sendResponse(Message responseMessage) {
                    if (thread != Thread.currentThread())
                        throw new UnsupportedOperationException("You cannot call this method from another thread!");
                    sendMessage(responseMessage);
                }
            };
            unsolicitedMessageHandlerAdapter.invoke(siteAddress, message, responseSender);
        } else {
            DeviceSession session = sessionMap.get(sessionId);
            if (session == null) {
                if (sessionlessMessageHandlerAdapter == null)
                    logger.warn("Received packet for non-existant session '" + sessionId + "'");
                else {
                    session = (DeviceSession)newSession();
                    sessionlessMessageHandlerAdapter.invoke(siteAddress, message, session);
                }
            } else
                session.handleRecievedMessage(message);
        }
    }
    
    public void close() {
        if (threadContext != null) {
            CloseSessionManagerCommmand command = new CloseSessionManagerCommmand();
            commandQueue.addLast(command);
            threadContext.requestTrap(contextHandler);
        }
    }
    
    void setUnsolicitedMessageHandler(Object handler) {
        this.unsolicitedMessageHandlerAdapter = new UnsolicitedMessageHandlerAdapter(handler);
    }
    
    void setSessionlessMessageHandler(Object handler) {
        this.sessionlessMessageHandlerAdapter = new SessionlessMessageHandlerAdapter(handler);
    }
    
    private void privateClose() {
        packetLayer.close();
        clearAllSessions();
        threadContext = null;
    }
    
    @Override
    public void reset() {
        clearAllSessions();
        packetLayer.reset();
    }

    private void clearAllSessions() {
        synchronized (sessionMap) {
            for (Session session : sessionMap.values())
                session.close();
            sessionMap.clear();
        }
    }
    
    /**
     * This method should only be called by the context thread that is tied to this object.
     * 
     * @param message
     * @param session
     * @throws IOException
     */
    void sendMessageForSession(Message message, Session session) throws IOException {
        Integer sessionId = session == null ? 0 : session.getSessionId();
        protocolPacket.reset();
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        message.serialize(bodyByteBuffer);
        protocolPacket.setBodyLength(bodyByteBuffer.position());
        protocolPacket.merge();
        
        if (traceLogger.isEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MsgNo: ")
                         .append(message.messageType().getNumber())
                         .append(", ")
                         .append("SeqNo: ")
                         .append(message.sequenceNumber())
                         .append(", SesId: ")
                         .append(sessionId)
                         .append(", Pkt: ")
                         .append(HexStringEncoder.bytesAsHexString(protocolPacket.bodyByteBuffer()));
            traceLogger.trace("Trx: " + stringBuilder.toString());
        }
        packetLayer.transmit(protocolPacket, message.messageType().getNumber(), message.sequenceNumber(), sessionId);
    }

    interface AsyncCommand {
        void execute();
    }
    
    abstract class BaseAsyncCommand {
        private Long waitTimeInMilliseconds;
        protected Condition condition;
        protected IOException exception;
        
        abstract void doCommand() throws IOException;
        abstract void handleTimeout();
        
        public BaseAsyncCommand(Condition condition, Long waitTimeInMilliseconds) {
            this.waitTimeInMilliseconds = waitTimeInMilliseconds;
            this.condition = condition;
        }        
        
        public void waitForCompletion() throws InterruptedException, IOException {
            if (condition.await(waitTimeInMilliseconds, TimeUnit.MILLISECONDS) == false)
                handleTimeout();
            if (exception != null)
                throw exception;
        }
        
        public void execute() {
            queueLock.lock();
            try {
                loggingContext.setupContext();
                doCommand();
            } catch (IOException e) {
                exception = e;
            } finally {
                condition.signal();
                queueLock.unlock();
                loggingContext.clearContext();
            }
        }
    }

    /**
     * This command will be executed in the context of the thread that owns this SessionManager.
     * 
     */
    class CloseSessionManagerCommmand implements AsyncCommand {

        public CloseSessionManagerCommmand() {
        }

        @Override
        public void execute() {
            privateClose();
        };
        
    }
    
    class SendMessageWithWaitCommand extends BaseAsyncCommand implements AsyncCommand {
        private Session session;
        private Message message;
        
        public SendMessageWithWaitCommand(Session session, Message message, Condition condition, Long waitTimeInMilliseconds) {
            super(condition, waitTimeInMilliseconds);
            this.session = session;
            this.message = message;
        }
        
        @Override
        void doCommand() throws IOException {
            sendMessageForSession(message, session);
        }
        
        @Override
        void handleTimeout() {
            logger.warn("Wait time elapsed before transmitData completed");
        }
        
    }    
}
