package zedi.pacbridge.net.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.FigBridgeThreadFactory;
import zedi.pacbridge.utl.concurrent.DetachedTaskRunner;

@ApplicationScoped
public class NetworkEventDispatcherManager implements DispatcherExecutionListener, DispatcherManager, DispatcherRequestQueue {

    private static Logger logger = LoggerFactory.getLogger(NetworkEventDispatcherManager.class);
    
    private HashSet<NetworkEventDispatcher> coreDispatchers = new HashSet<NetworkEventDispatcher>();
    private RequestQueue<DispatcherRequest> dispatcherRequestQueue = new RequestQueue<DispatcherRequest>();
    private NetworkEventDispatcherFactory dispatcherFactory;
    private DispatcherThreadPool dispatcherThreadPool;
    private EventWorkerThreadPool eventWorkerThreadPool;
    private boolean started = false;
    private int totalDispatcherCount;

    public NetworkEventDispatcherManager() {
        this(null, new NetworkEventDispatcherFactory(), new EventWorkerThreadPool());
    }
    
    protected NetworkEventDispatcherManager(DispatcherThreadPool threadPool, NetworkEventDispatcherFactory dispatcherFactory, EventWorkerThreadPool eventWorkerThreadPool) {
        this.dispatcherThreadPool = threadPool == null ? new DispatcherThreadPool(this) : threadPool;
        this.dispatcherFactory = dispatcherFactory;
        this.eventWorkerThreadPool = eventWorkerThreadPool;
    }

    @Override
    public void queueDispatcherRequest(DispatcherRequest dispatcherRequest) {
        dispatcherRequestQueue.queueRequest(dispatcherRequest);
    }

    public Map<String, Double> getAverageScanTimes() {
        Map<String, Double> map = new HashMap<>();
        synchronized (coreDispatchers) {
            for (NetworkEventDispatcher ned : coreDispatchers)
                map.put(ned.getName(), ned.getAverageScanTime());
        }
        return map;
    }
    
    public Integer getRequestQueueDepth() {
        return dispatcherRequestQueue.size();
    }
    
    @Override
    public void dispatcherTerminated(NetworkEventDispatcher dispatcher, Throwable exception) {
        synchronized (coreDispatchers) {
            if (coreDispatchers.contains(dispatcher)) {
                coreDispatchers.remove(dispatcher);
                if (exception == null && dispatcherThreadPool.isShutdown() == false)
                    logger.warn("Network event dispatcher failed with exception. Restarting another.", exception);
                
                if (dispatcherThreadPool.isShutdown() == false)
                   startNewDispatcher();
            }
        }
    }

    public void startNewDispatcher() {
        try {
            if (canAddMoreDispatchers()) {
                NetworkEventDispatcher dispatcher = dispatcherFactory.newNetworkEventDispatcher(dispatcherRequestQueue, eventWorkerThreadPool);
                synchronized (coreDispatchers) {
                    coreDispatchers.add(dispatcher);
                    dispatcherThreadPool.startDispatcher(dispatcher);
                    totalDispatcherCount++;
                }
            } else
                logger.warn("Request for additional Network Event Dispatcher fail.  Maximum number of dispatchers reached.");
        } catch (IOException e) {
            logger.error("Unable to create new NetworkEventDispatcher", e);
        }
    }

    public void shutdown() {
        dispatcherThreadPool.shutdown();
        synchronized (coreDispatchers) {
            for (NetworkEventDispatcher dispatcher : coreDispatchers)
                dispatcher.shutdown(); 
        }
    }
    
    public void start() {
        if (started == false) {
            for (int i = 0; i < dispatcherThreadPool.getCorePoolSize(); i++)
                startNewDispatcher();
            totalDispatcherCount = 0;
            FigBridgeThreadFactory threadFactory = new FigBridgeThreadFactory();
            RequestQueueMonitorHelper dispatcherFacade = new DispatcherQueueMonitorHelper(this, dispatcherThreadPool);
            RequestQueueMonitor queueMonitor = new RequestQueueMonitor(dispatcherRequestQueue, dispatcherFacade);
            Thread thread = threadFactory.newThread(new DetachedTaskRunner<RequestQueueMonitor>(queueMonitor), "Request Queue Monitor");
            thread.setDaemon(true);
            thread.start();
        }

        started = true;
    }

    public int getNumberOfDispatchers() {
        return getAddedDispatcherCount() + getCoreDispatcherCount();
    }

    public int getCoreDispatcherCount() {
        return dispatcherThreadPool.getPoolSize();
    }
    
    public int getAddedDispatcherCount() {
        return totalDispatcherCount - dispatcherThreadPool.getCorePoolSize();
    }
    
    private boolean canAddMoreDispatchers() {
        return dispatcherThreadPool.getPoolSize() < dispatcherThreadPool.getMaximumPoolSize();
    }
}
