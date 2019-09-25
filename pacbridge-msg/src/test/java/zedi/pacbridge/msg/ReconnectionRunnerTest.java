package zedi.pacbridge.msg;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;

import javax.jms.JMSException;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.ThreadSleeper;

public class ReconnectionRunnerTest extends BaseTestCase {

    @Mock
    private NotificationCenter notificationCenter;
    @Mock
    private Lock lock;
    @Mock
    private JMSException exception;
    @Mock
    private ThreadSleeper threadSleeper;
    @Mock
    private ThreadFactory threadFactory;
    @Mock
    private Thread thread;
    @Mock
    private JmsServerReconnector reconnector; 

    private ExpectedJMSException expectedException = new ExpectedJMSException();
    
    @Test
    public void shouldStartThread() throws Exception {
        ReconnectionRunner runner = new ReconnectionRunner(expectedException, threadSleeper, threadFactory, reconnector, lock, notificationCenter);

        given(threadFactory.newThread(runner)).willReturn(thread);
        
        runner.start();
        
        verify(threadFactory).newThread(runner);
        assertEquals(ReconnectionRunner.THREAD_NAME, thread.getName());
        verify(thread).start();
    }
    
    @Test
    public void shouldDoNothingIfLockCannotBeAquired() throws Exception {
        given(lock.tryLock()).willReturn(false);
        ReconnectionRunner runner = new ReconnectionRunner(expectedException, threadSleeper, threadFactory, reconnector, lock, notificationCenter);
        
        runner.run();
        verify(lock).tryLock();
        verify(notificationCenter, never()).postNotification(JmsCenter.CONNECTION_LOST_NOTIFICATION);
        verify(reconnector, never()).isConnectionReestabilshed();
        verify(threadSleeper, never()).sleep(ReconnectionRunner.RECONNECTION_INTERVAL_SECONDS*1000);
        verify(notificationCenter, never()).postNotification(JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION);
        verify(lock, never()).unlock();
    }
    
    @Test
    public void shouldCompeteForLockWhenRun() throws Exception {
        given(lock.tryLock()).willReturn(true);
        given(reconnector.isConnectionReestabilshed())
            .willReturn(false)
            .willReturn(true);
        ReconnectionRunner runner = new ReconnectionRunner(expectedException, threadSleeper, threadFactory, reconnector, lock, notificationCenter);
        
        InOrder inOrder = inOrder(reconnector, notificationCenter, threadSleeper, lock);
        
        runner.run();
        inOrder.verify(lock).tryLock();
        inOrder.verify(notificationCenter).postNotification(JmsCenter.CONNECTION_LOST_NOTIFICATION);
        inOrder.verify(reconnector).isConnectionReestabilshed();
        inOrder.verify(threadSleeper).sleep(ReconnectionRunner.RECONNECTION_INTERVAL_SECONDS*1000);
        inOrder.verify(reconnector).isConnectionReestabilshed();
        inOrder.verify(notificationCenter).postNotification(JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION);
        inOrder.verify(lock).unlock();
    }
}
