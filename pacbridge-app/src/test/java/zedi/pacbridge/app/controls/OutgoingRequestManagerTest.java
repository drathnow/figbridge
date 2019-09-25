package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OutgoingRequestManager.class)
@SuppressWarnings("unchecked")
public class OutgoingRequestManagerTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 42;

    @Mock
    private OutgoingRequest outgoingRequest;
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private OutgoingRequestSession outgoingRequestSession;
    @Mock
    private OutgoingRequestCache requestCache;
    
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(outgoingRequestSession.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
    }
    
    @Test
    public void shouldRemoveSessionWhenSessionClosedAndStartNextSession() throws Exception {
        OutgoingRequestManagerDelegateFactory delegateFactory = mock(OutgoingRequestManagerDelegateFactory.class);
        ThreadFactory threadFactory = mock(ThreadFactory.class);
        OutgoingRequestManagerDelegate delegate = mock(OutgoingRequestManagerDelegate.class);
        BlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<Runnable>();
        OutgoingRequest otherRequest = mock(OutgoingRequest.class);
        List<OutgoingRequest> requests = new ArrayList<>();

        requests.add(otherRequest);
        given(delegateFactory.newOutgoingRequestManagerDelegate(any(OutgoingRequestManager.class)))
            .willReturn(delegate);
        given(requestCache.outgoingRequestsForSiteAddress(siteAddress)).willReturn(requests);

        OutgoingRequestManager manager = new OutgoingRequestManager(requestCache, delegateFactory, requestQueue, threadFactory);
        manager.sessionClosed(outgoingRequestSession);
        
        assertEquals(1, requestQueue.size());
        requestQueue.take().run();
        verify(delegate).removeOutgoingRequestSession(outgoingRequestSession);
        verify(requestCache).outgoingRequestsForSiteAddress(siteAddress);
        verify(delegate).startOutgoingRequest(otherRequest);
    }
    
    @Test
    public void shouldConstructDelegate() throws Exception {
        ThreadFactory threadFactory = mock(ThreadFactory.class);
        BlockingQueue<Runnable> requestQueue = mock(BlockingQueue.class);
        OutgoingRequestManagerDelegate delegate = mock(OutgoingRequestManagerDelegate.class);
        OutgoingRequestManagerDelegateFactory delegateFactory = mock(OutgoingRequestManagerDelegateFactory.class);

        given(delegateFactory.newOutgoingRequestManagerDelegate(any(OutgoingRequestManager.class)))
            .willReturn(delegate);

        OutgoingRequestManager manager = new OutgoingRequestManager(requestCache, delegateFactory, requestQueue, threadFactory);
        verify(delegateFactory).newOutgoingRequestManagerDelegate(manager);
    }
    
    @Test
    public void shouldStartWorkerThread() throws Exception {
        OutgoingRequestManagerDelegateFactory delegateFactory = mock(OutgoingRequestManagerDelegateFactory.class);
        ThreadFactory threadFactory = mock(ThreadFactory.class);
        Thread thread = mock(Thread.class);
        BlockingQueue<Runnable> requestQueue = mock(BlockingQueue.class);

        given(threadFactory.newThread(any(OutgoingRequestManager.WorkerRunner.class))).willReturn(thread);
        
        OutgoingRequestManager outgoingRequestManager = new OutgoingRequestManager(requestCache, delegateFactory, requestQueue, threadFactory);
        outgoingRequestManager.start();
        
        verify(threadFactory).newThread(any(OutgoingRequestManager.WorkerRunner.class));
        assertEquals(OutgoingRequestManager.WORKER_NAME, thread.getName());
        verify(thread).start();
    }
    
    @Test
    public void shouldPassQueueRequestToDelegate() throws Exception {
        ThreadFactory threadFactory = mock(ThreadFactory.class);
        BlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<Runnable>();
        OutgoingRequestManagerDelegate delegate = mock(OutgoingRequestManagerDelegate.class);
        OutgoingRequestManagerDelegateFactory delegateFactory = mock(OutgoingRequestManagerDelegateFactory.class);

        given(delegateFactory.newOutgoingRequestManagerDelegate(any(OutgoingRequestManager.class)))
            .willReturn(delegate);

        OutgoingRequestManager manager = new OutgoingRequestManager(requestCache, delegateFactory, requestQueue, threadFactory);
        manager.queueOutgoingRequest(outgoingRequest);
        
        assertEquals(1, requestQueue.size());
        requestQueue.poll().run();
        verify(delegate).startOutgoingRequest(outgoingRequest);
        verify(delegateFactory).newOutgoingRequestManagerDelegate(manager);
    }
    
    @Test
    public void shouldShutdownWorkerThread() throws Exception {
        ThreadFactory threadFactory = mock(ThreadFactory.class);
        BlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<Runnable>();
        OutgoingRequestManagerDelegate delegate = mock(OutgoingRequestManagerDelegate.class);
        OutgoingRequestManagerDelegateFactory delegateFactory = mock(OutgoingRequestManagerDelegateFactory.class);
        Thread thread = mock(Thread.class);

        given(delegateFactory.newOutgoingRequestManagerDelegate(any(OutgoingRequestManager.class)))
            .willReturn(delegate);
        given(threadFactory.newThread(any(OutgoingRequestManager.WorkerRunner.class))).willReturn(thread);
        
        OutgoingRequestManager manager = new OutgoingRequestManager(requestCache, delegateFactory, requestQueue, threadFactory);
        manager.start();
        
        ArgumentCaptor<OutgoingRequestManager.WorkerRunner> arg = ArgumentCaptor.forClass(OutgoingRequestManager.WorkerRunner.class);
        verify(threadFactory).newThread(arg.capture());
        assertEquals(OutgoingRequestManager.WORKER_NAME, thread.getName());
        verify(thread).start();
        
        OutgoingRequestManager.WorkerRunner runner = arg.getValue();
        assertEquals(0, requestQueue.size());
        assertFalse(runner.shutdown);
        manager.shutdown();
        assertTrue(runner.shutdown);
        assertEquals(1, requestQueue.size());
    }
    
}