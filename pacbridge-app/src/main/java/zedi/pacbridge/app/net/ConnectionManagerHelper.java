package zedi.pacbridge.app.net;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.controls.OutgoingRequestManager;
import zedi.pacbridge.app.util.LookupHelper;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.SiteAddress;

/**
 * The ConnectionManagerHelper is used by the ConnectionManager to initiate asynchronous activites
 * that could take too long to perform.  ConnectionManager are accessed by dispatch threads that need
 * to do thing quickly in order to maintain performace within the bridge.
 * 
 * @author daver
 *
 */
class ConnectionManagerHelper {
    private static final String CORE_POOL_SIZE_PROPERTY_NAME = "pacbridge.conManHelper.corePoolSize";
    private static final String MAX_POOL_SIZE_PROPERTY_NAME = "pacbridge.conManHelper.maxPoolSize";
    private static final String POOL_KEEPALIVE_SECONDS_PROPERTY_NAME = "pacbridge.conManHelper.poolKeepAliveSeconds";
    
    private static final Integer DEFAULT_CORE_POOL_SIZE = 4;
    private static final Integer DEFAULT_MAX_POOL_SIZE = 8;
    private static final Integer DEFAULT_POOL_KEEPALIVE_SECONDS = 5;

    private int corePoolSize = IntegerSystemProperty.valueOf(CORE_POOL_SIZE_PROPERTY_NAME, DEFAULT_CORE_POOL_SIZE); 
    private int maximumPoolSize = IntegerSystemProperty.valueOf(MAX_POOL_SIZE_PROPERTY_NAME, DEFAULT_MAX_POOL_SIZE); 
    private long keepAliveTime = IntegerSystemProperty.valueOf(POOL_KEEPALIVE_SECONDS_PROPERTY_NAME, DEFAULT_POOL_KEEPALIVE_SECONDS); 
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    private ThreadPoolExecutor executor;
    
    public ConnectionManagerHelper() {
        executor = new ThreadPoolExecutor(corePoolSize, 
                                          maximumPoolSize, 
                                          keepAliveTime, 
                                          TimeUnit.SECONDS, 
                                          workQueue);
    }
    
    /**
     * Launches a thread to search the OutgoingRequestCache to see if there are any outgoing requests
     * queued for the specific site.
     * 
     * @param siteAddress
     */
    public void queueAnyOutgoingRequestForSite(SiteAddress siteAddress) {
        executor.execute(new WorkerRunner(siteAddress));
    }
    
    private class WorkerRunner implements Runnable {
        private SiteAddress siteAddress;
        
        
        public WorkerRunner(SiteAddress siteAddress) {
            this.siteAddress = siteAddress;
        }

        @Override
        public void run() {
            LookupHelper helper = DependencyResolver.Implementation.sharedInstance().getImplementationOf(LookupHelper.class);
            OutgoingRequestCache cache = helper.getOutgoingRequestCache();
            OutgoingRequestManager manager = helper.getOutgoingRequestManager();
            Collection<OutgoingRequest> requests = cache.outgoingRequestsForSiteAddress(siteAddress);
            for (Iterator<OutgoingRequest> iter = requests.iterator(); iter.hasNext(); )
                manager.queueOutgoingRequest(iter.next());
        }
    }
}
