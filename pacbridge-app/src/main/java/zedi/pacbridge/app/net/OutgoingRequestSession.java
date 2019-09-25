package zedi.pacbridge.app.net;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestProcessor;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.MessageListener;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;

public class OutgoingRequestSession {
    private static final Logger logger = LoggerFactory.getLogger(OutgoingRequestSession.class.getName());

    public static final String CLOSED_MSG = "Outgoing session closed unexpectedly";
    public static final String TIMEOUT_ERROR_MSG = "Timed out waiting for response";
    
    private Session currentSession;
    private OutgoingRequest currentOutgoingRequest;
    private OutgoingRequestProcessor currentRequestProcessor;
    private Connection connection;
    private State currentState;
    private ThreadContext astRequester;
    private FutureTimer futureTimer;
    private ThreadContextHandler defaultHandler;
    private Long responseTimeoutSeconds;
    private LoggingContext loggingContext;
    private OutgoingRequestSessionListener listener;
    private final Lock lock;

    private State startRequestState = new StartRequestState();
    private State sendNextMessageState = new SendNextMessageState();
    private State waitingForResponseState = new WaitingForResponseState();
    private State finishProcessingRequestState = new FinishProcessingRequestState();
    private State closedState = new ClosedState();
    
    OutgoingRequestSession(OutgoingRequest outgoingRequest, Connection connection, ThreadContext astRequester) {
        this.lock = new ReentrantLock();
        this.astRequester = astRequester;
        this.connection = connection;
        this.currentOutgoingRequest = outgoingRequest;
        this.loggingContext = new LoggingContext(connection.getSiteAddress());
        this.defaultHandler = new ThreadContextHandler() {
            @Override
            public void handleSyncTrap() {
                loggingContext.setupContext();
                currentState.doAction();
                loggingContext.clearContext();
            }
        };
    }

    public void start() {
        dispatch(startRequestState);
        logger.info("OutgoingRequestSession for " + connection.getSiteAddress() + " started");
    }
        
    public void setOutgoingRequestSessionListener(OutgoingRequestSessionListener listener) {
        this.listener = listener;
    }
    
    public SiteAddress getSiteAddress() {
        return connection.getSiteAddress();
    }
    
    void close() {
        dispatch(closedState);
    }
    
    private void closeCurrentSession() {
        if (currentSession != null)
            currentSession.close();
        currentSession = null;
        currentOutgoingRequest = null;
        currentRequestProcessor = null;
    }
    
    private void dispatch(State nextState) {
        currentState = nextState;
        astRequester.requestTrap(defaultHandler);
    }
    
    private void dispatchFuture(State nextState, long delay, TimeUnit timeUnit) {
        currentState = nextState;
        futureTimer = astRequester.getTimer().schedule(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                currentState.doAction();
                                                            }
                                                        }, delay, timeUnit);
    }
    
    interface State {
        void doAction();
        void handleResponseMessage(Message message);
    }
    
    private abstract class BaseState {
        
        public void handleResponseMessage(Message message) {
            logger.error("Message recieved in wrong state. Current state = " + currentState.getClass().getName());
        }        
    }
    
    class StartRequestState extends BaseState implements State {

        @Override
        public void doAction() {
            lock.lock();
            try {
                if (currentOutgoingRequest.isCancelled()) {
                    currentOutgoingRequest = null;
                    dispatch(closedState);
                    return;
                }
                responseTimeoutSeconds = currentOutgoingRequest.getResponseTimeoutSeconds().longValue();
            } finally {
                lock.unlock();
            }
            
            logger.info("Starting request: " + currentOutgoingRequest.shortDescription());
            if (currentOutgoingRequest != null) {
                currentRequestProcessor = currentOutgoingRequest.outgoingRequestProcessor();
                currentSession = connection.newSession();
                currentSession.setMessageListener(new MessageListener() {
                    
                    @Override
                    public void handleMessage(Message message) {
                        currentState.handleResponseMessage(message);
                    }
                });
                currentRequestProcessor.starting();
                dispatch(new SendNextMessageState());
            }
        }
         
    }
       
    class SendNextMessageState extends BaseState implements State {

        @Override
        public void doAction() {
            try {
                Message message = currentRequestProcessor.nextMessageWithSequenceNumber(currentSession.nextSequenceNumber());
                currentSession.sendMessage(message, 0);
                dispatchFuture(waitingForResponseState, responseTimeoutSeconds, TimeUnit.SECONDS);
            } catch (Exception e) {
                currentRequestProcessor.forceFinished(ControlStatus.FAILURE, "Unable to send message for request: " + e.toString());
                logger.error("Unable to send request message", e);
                dispatch(finishProcessingRequestState);
            }
        }
    }
    
    class FinishProcessingRequestState extends BaseState implements State {
                
        @Override
        public void doAction() {
            currentRequestProcessor.doFinalProcessing();
            currentOutgoingRequest = null;
            currentRequestProcessor = null;
            closeCurrentSession();
            dispatch(closedState);
        }
    }
    
    class WaitingForResponseState extends BaseState implements State {

        @Override
        public void handleResponseMessage(Message message) {
            if (currentRequestProcessor.isExpected(message)) {
                futureTimer.cancel();
                if (currentRequestProcessor.hasMoreMessages() == false)
                    dispatch(finishProcessingRequestState);
                else
                    dispatch(sendNextMessageState);
            }
        }
        
        @Override
        public void doAction() {
            logger.error("Outgoing request session timed out waiting for response to " + currentOutgoingRequest.shortDescription());
            currentRequestProcessor.forceFinished(ControlStatus.TIMED_OUT, TIMEOUT_ERROR_MSG);
            dispatch(finishProcessingRequestState);
        }
    }
    
    class ClosedState extends BaseState implements State {

        @Override
        public void doAction() {
            if (currentSession != null)
                currentSession.close();
            currentSession = null;
            if (currentRequestProcessor != null)
                currentRequestProcessor.forceFinished(ControlStatus.FAILURE, CLOSED_MSG);
            currentRequestProcessor = null;
            currentOutgoingRequest = null;
            if (listener != null)
                listener.sessionClosed(OutgoingRequestSession.this);
        }
    }
}
