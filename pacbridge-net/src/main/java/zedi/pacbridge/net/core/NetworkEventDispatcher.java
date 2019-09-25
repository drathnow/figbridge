package zedi.pacbridge.net.core;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ConnectEventHandler;
import zedi.pacbridge.net.NetworkEventHandler;
import zedi.pacbridge.net.ReadEventHandler;
import zedi.pacbridge.net.WriteEventHandler;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.annotations.AfterTaskFinishes;
import zedi.pacbridge.utl.annotations.BeforeTaskStarts;
import zedi.pacbridge.utl.concurrent.DetachedTask;
import zedi.pacbridge.utl.stats.MovingAverage;

class NetworkEventDispatcher implements DetachedTask, ContextCommandProcessor {

    private static Logger logger = LoggerFactory.getLogger(NetworkEventDispatcher.class);

    // Sets the limit on the amount of time that will be dedicated to processing context commands (in milliseconds)
    public static final String CONTEXT_COMMAND_PROCTIME_LIMIT_PROPERTY_NAME = "eventDispatcher.contextCommandProcTimeLimitMilliseconds";
    public static final int DEFAULT_CONTEXT_COMMAND_PROCTIME_MILLIS = 100;
    public static final int MIN_CONTEXT_COMMAND_PROCTIME_MILLIS = 10;
    public static final int MAX_CONTEXT_COMMAND_PROCTIME_MILLIS = 1000;
    
    public static final String SHUTDOWN_DELAY_PROPERTY_NAME = "eventDispatcher.shutdowDelaySeconds";
    public static final int DEFAULT_SHUTDOWN_DELAY_SECONDS = 60;
    public static final int MIN_SHUTDOWN_DELAY_SECONDS = 30;
    public static final int MAX_SHUTDOWN_DELAY_SECONDS = 300;
    
    private static IntegerSystemProperty shutdownDelaySeconds
            = new IntegerSystemProperty(SHUTDOWN_DELAY_PROPERTY_NAME, 
                                            DEFAULT_SHUTDOWN_DELAY_SECONDS, 
                                            MIN_SHUTDOWN_DELAY_SECONDS, 
                                            MAX_SHUTDOWN_DELAY_SECONDS);
    
    private static IntegerSystemProperty contextCommandTimeLimitMilliseconds
        = new IntegerSystemProperty(CONTEXT_COMMAND_PROCTIME_LIMIT_PROPERTY_NAME, 
                                        DEFAULT_CONTEXT_COMMAND_PROCTIME_MILLIS, 
                                        MIN_CONTEXT_COMMAND_PROCTIME_MILLIS, 
                                        MAX_CONTEXT_COMMAND_PROCTIME_MILLIS);

    private Selector selector;
    private ChannelHelper channelHelper;
    private MovingAverage movingAverageScanTime;
    private RequestQueue<DispatcherRequest> dispatcherRequestQueue;
    private ContextCommandQueue contextCommandQueue;
    private boolean refuseNewChannelRequests;
    private long shutdownRequestTime;
    private SystemTime systemTime;
    private Thread myThread;

    public NetworkEventDispatcher(RequestQueue<DispatcherRequest> dispatcherRequestQueue) throws IOException {
        this(Selector.open(), 
                new ChannelHelperFactory(), 
                dispatcherRequestQueue, 
                new ContextCommandQueue());
    }

    protected NetworkEventDispatcher(Selector selector, 
            ChannelHelperFactory channelHelperFactory, 
            RequestQueue<DispatcherRequest> dispatcherRequestQueue, 
            ContextCommandQueue contextCommandQueue) {
        this.selector = selector;
        this.channelHelper = channelHelperFactory.newChannelHelperWithSelector(selector);
        this.movingAverageScanTime = new MovingAverage(1000);
        this.dispatcherRequestQueue = dispatcherRequestQueue;
        this.systemTime = new SystemTime();
        this.contextCommandQueue = contextCommandQueue;
    }
    
    public Integer getRegisteredSocketCount() {
        return selector.keys().size();
    }
    
    public Double getAverageScanTime() {
        return movingAverageScanTime.getAverage();
    }
    
    public void queueContextCommand(ContextCommand contextCommand) {
        contextCommandQueue.queueCommand(contextCommand);
    }
    
    public FutureTimer queueContextCommand(ContextCommand contextCommand, long delayTime, TimeUnit timeUnit) {
        return contextCommandQueue.queueCommand(contextCommand, delayTime, timeUnit);
    }
    
    public void setSystemTime(SystemTime systemTime) {
        this.systemTime = systemTime;
    }
        
    public String getName() {
        return myThread == null ? "No Started" : myThread.getName();
    }
    
    public void shutdown() {
        this.refuseNewChannelRequests = true;
        this.selector.wakeup();
        shutdownRequestTime = systemTime.getCurrentTime();
    }

    @BeforeTaskStarts
    public void executionStarting() {
        logger.info("Network event dispatcher " + Thread.currentThread().getName() + " starting");
        this.myThread = Thread.currentThread();
    }
    
    @AfterTaskFinishes
    public void executionTerminating() {
        logger.info("Network event dispatcher " + Thread.currentThread().getName() + " exiting");
        runClean();
    }

    public boolean isCurrentContext() {
        return Thread.currentThread() == myThread;
    }
    
    public boolean shouldExitAfterMainLoop() {
        doSelect();
        checkForDispatcherRequests();
        processContextCommands();
        boolean shouldExit = shouldExit();
        if (shouldExit)
            runClean();
        return shouldExit;
    }
    
    private boolean shouldExit() {
        if (refuseNewChannelRequests)
            return selector.keys().size() == 0 || hasShutdownDelayTimePassedSinceShutdownRequest();
        return false;
    }
    
    private boolean hasShutdownDelayTimePassedSinceShutdownRequest() {
        return (systemTime.getCurrentTime() - shutdownRequestTime) > (shutdownDelaySeconds.currentValue()*1000L);
    }

    private void checkForDispatcherRequests() {
        if (refuseNewChannelRequests == false) {
            DispatcherRequest request = null;
            while ((request = dispatcherRequestQueue.nextRequest()) != null) {
                DispatcherKey key = new ChannelManagerDispatcherKey(channelHelper);
                NetworkEventDispatcherThreadContext requester = new NetworkEventDispatcherThreadContext(this);
                request.handleRequest(key, requester);
            }
        }
    }

    private void doSelect() {
        try {
            if (selector.select(100) > 0) {
                long now = System.currentTimeMillis();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                if (selectedKeys.isEmpty() == false)
                    processPendingEvents(selectedKeys);
                long time = System.currentTimeMillis() - now;
                movingAverageScanTime.addSample(time);
            }
        } catch (Throwable ioe) {
            logger.error("Unexpected exception during selector.select()", ioe);
        }
    }

    private void processPendingEvents(Set<SelectionKey> selectedKeys) {
        synchronized (selectedKeys) {
            for (Iterator<SelectionKey> iter = selectedKeys.iterator(); iter.hasNext(); ) {
                SelectionKey selectionKey = iter.next();
                iter.remove();
                try {
                    if (selectionKey.isValid())
                        dispatchEventForSelectionKey(selectionKey);
                } catch (Exception e) {
                    logger.error("Unexpected exception encounterd while processing event", e);
                }
            }
        }
    }
    
    private void processContextCommands() {
        ContextCommand contextCommand;
        if ((contextCommand = contextCommandQueue.nextDueCommand()) != null)
            try {
                contextCommand.execute();
            } catch (Exception e) {
                logger.warn("Error processing context command", e);
            }
    }
    
    private void dispatchEventForSelectionKey(SelectionKey selectionKey) {
        cancelReadyOperationsOnSelectionKey(selectionKey);
        NetworkEventHandler eventHandler = (NetworkEventHandler)selectionKey.attachment();
        LoggingContext loggingContext = eventHandler.loggingContext();
        if (loggingContext != null)
            loggingContext.setupContext();
        try {
            if (selectionKey.isValid() && selectionKey.isConnectable())
                ((ConnectEventHandler)eventHandler).handleConnect();
    
            if (selectionKey.isValid() && selectionKey.isReadable())
                ((ReadEventHandler)eventHandler).handleRead();
    
            if (selectionKey.isValid() && selectionKey.isWritable())
                ((WriteEventHandler)eventHandler).handleWrite();
            
        } catch (Throwable e) {
            logger.error("Unhandled exception during event dispatching", e);
        } finally {
            if (loggingContext != null)
                loggingContext.clearContext();
        }
    }

    private void cancelReadyOperationsOnSelectionKey(SelectionKey selectionKey) {
        int readyOperations = selectionKey.readyOps();
        selectionKey.interestOps(selectionKey.interestOps() & ~readyOperations);
    }
    
    private void runClean() {
        if (selector.isOpen()) {
            synchronized (selector.keys()) {
                for (SelectionKey key : selector.keys()) {
                    if (key.isValid()) {
                        try {
                            key.channel().close();
                        } catch (IOException eatIt) {
                        }
                        key.cancel();
                    }
                    key.attach(null);
                }
                cleanUpSelector();
            }
        }
    }

    private void cleanUpSelector() {
        try {
            selector.selectNow();
        } catch (IOException eatIt) {
        }
        
        try {
            selector.close();
        } catch (IOException eatIt) {
        }
    }
    
    boolean isCurrentThread() {
        return Thread.currentThread().equals(myThread);
    }
}
