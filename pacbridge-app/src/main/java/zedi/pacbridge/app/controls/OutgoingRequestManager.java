package zedi.pacbridge.app.controls;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import zedi.pacbridge.app.net.Network;
import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.utl.SiteAddress;

/**
 * The <code>OutgoingRequestManager</code> manages all outgoing requests for the bridge. It maintains a list of
 * outgoing sessions for each network. {@link Network}s can specify a maximum number of outgoing sessions allowed.  If
 * that limit is exceeded, the <code>OutgoingRequestManager</code> will queue requests for the network and start them
 * as existing sessions close.  If the maximum number of sessions allowed by a Network is zero, that is taken to be 
 * no limit.
 */
@Singleton
@Startup
@EJB(name = OutgoingRequestManager.JNDI_NAME, beanInterface = OutgoingRequestManager.class)
public class OutgoingRequestManager {
    public static final String JNDI_NAME = "java:global/OutgoingRequestManager";
    public static final String WORKER_NAME = "OutgoingRequestManager.Worker";

    /*
     * Queue that hold works requests. 
     */
    private BlockingQueue<Runnable> requestQueue;
    private ThreadFactory threadFactory;
    private WorkerRunner workerRunner;
    private OutgoingRequestManagerDelegate delegate;
    private OutgoingRequestCache outgoingRequestCache;
    
    OutgoingRequestManager(OutgoingRequestCache outgoingRequestCache, OutgoingRequestManagerDelegateFactory delegateFactory, BlockingQueue<Runnable> requestQueue, ThreadFactory threadFactory) {
        this.outgoingRequestCache = outgoingRequestCache;
        this.delegate = delegateFactory.newOutgoingRequestManagerDelegate(this);
        this.threadFactory = threadFactory;
        this.requestQueue = requestQueue;
    }

    @Inject
    public OutgoingRequestManager(OutgoingRequestCache outgoingRequestCache, 
                                  OutgoingRequestManagerDelegateFactory delegateFactory, 
                                  ThreadFactory threadFactory) {
        this(outgoingRequestCache, delegateFactory, new LinkedBlockingDeque<Runnable>(), threadFactory);
    }

    public OutgoingRequestManager() {
    }

    public void queueOutgoingRequest(final OutgoingRequest outgoingRequest) {
        requestQueue.offer(new Runnable() {
            @Override
            public void run() {
                delegate.startOutgoingRequest(outgoingRequest);
            }
        });
    }
    
    public void sessionClosed(final OutgoingRequestSession requestSession) {
        requestQueue.offer(new Runnable() {
            @Override
            public void run() {
                SiteAddress siteAddress = requestSession.getSiteAddress();
                delegate.removeOutgoingRequestSession(requestSession);
                Collection<OutgoingRequest> requests = outgoingRequestCache.outgoingRequestsForSiteAddress(siteAddress);
                if (requests.isEmpty() == false)
                    delegate.startOutgoingRequest(requests.iterator().next());
            }
        });
    }    
    
    @PostConstruct
    void start() {
        workerRunner = new WorkerRunner();
        Thread thread = threadFactory.newThread(workerRunner);
        thread.setName(WORKER_NAME);
        thread.start();
    }
    
    @PreDestroy
    void shutdown() {
        workerRunner.shutdown = true;
        requestQueue.offer(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    class WorkerRunner implements Runnable {
        boolean shutdown;
        
        @Override
        public void run() {
            while (shutdown == false) {
                try {
                    requestQueue.take().run();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}