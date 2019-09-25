package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.ChannelFactory;
import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.net.core.SelectorSyncObject;
import zedi.pacbridge.utl.FigBridgeThreadFactory;
import zedi.pacbridge.utl.annotations.AfterTaskFinishes;
import zedi.pacbridge.utl.annotations.BeforeTaskStarts;
import zedi.pacbridge.utl.concurrent.DetachedTask;
import zedi.pacbridge.utl.concurrent.DetachedTaskRunner;

public class ServerTask implements DetachedTask {
    private static Logger logger = LoggerFactory.getLogger(ServerTask.class);
    
    public static final String TASK_NAME = "TCP Server";
    
    public static final int SELECT_TIMEOUT_MILLISECONDS = 1000;
    public static final int ERROR_COUNT_THRESHOLD = 10;
    
    private RequestQueue<ServerRequest> requestQueue;
    private ServerHelper serverHelper;
    private ServerProxy serverProxy;
    private boolean started;
    
    private int selectErrorCount;
    private ThreadFactory threadFactory;

    ServerTask(ServerHelper serverHelper, ThreadFactory threadFactory, RequestQueue<ServerRequest> requestQueue) throws IOException {
        this.threadFactory = threadFactory;
        this.requestQueue = requestQueue;
        this.serverHelper = serverHelper;
    }
    
    public ServerTask() {
        Selector selector;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create ServerTask: Can't open seletor", e);
        }
        this.serverHelper = new ServerHelper(selector, new ChannelFactory());
        this.threadFactory = new FigBridgeThreadFactory();
        this.requestQueue = new RequestQueue<ServerRequest>(new SelectorSyncObject(selector));
    }
    
    public boolean shouldExitAfterMainLoop() {
        doSelect();
        checkForAsynRequests();
        return isShutdown() || isSelectErrorCountBeyondThreshold(); 
    }

    public void start() {
        if (started == false) {
            DetachedTaskRunner<ServerTask> tcpTaskRunner = new DetachedTaskRunner<ServerTask>(this);
            Thread serverThread = threadFactory.newThread(tcpTaskRunner);
            serverThread.setName(TASK_NAME);
            serverThread.start();
            started = true;
        }
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public ServerProxy getProxy() {
        if (serverProxy == null) {
            TcpServerRequestFactory requestFactory = new TcpServerRequestFactory(requestQueue);
            serverProxy = new ServerProxy(requestFactory);
        }
        return serverProxy;
    }    
    
    @BeforeTaskStarts
    public void beforeTaskStart() {
        logger.info("TCP Server task is starting");
    }
    
    @AfterTaskFinishes
    public void afterTaskFinishes() {
        logger.info("TCP Server task is shutting down");
    }
    
    private boolean isSelectErrorCountBeyondThreshold() {
        if (selectErrorCount > ERROR_COUNT_THRESHOLD) {
            logger.error("Select error count is beyond threshold.  Listener is shutting down");
            return true;
        }
        return false;
    }
    
    private void doSelect() {
        try {
            serverHelper.doSelect(SELECT_TIMEOUT_MILLISECONDS);
            selectErrorCount = 0;
        } catch (IOException ioe) {
            selectErrorCount++;
            logger.error("Unexpected exception during selector.select()", ioe);
        }
    }

    private boolean isShutdown() {
        return serverHelper.isShutdown();
    }

    private void checkForAsynRequests() {
        ServerRequest request;
        while ((request = requestQueue.nextRequest()) != null)
            request.handleRequestWithServer(serverHelper);
    }
}
