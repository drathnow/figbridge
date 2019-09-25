package zedi.pacbridge.msg;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.concurrent.locks.Lock;

import javax.jms.JMSException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExceptionListener.class)
public class ExceptionListenerTest extends BaseTestCase {

    @Mock
    private NotificationCenter notificationCenter;
    @Mock
    private Lock lock;
    @Mock
    private JMSException exception;
    @Mock
    private JmsServerReconnector reconnector;
    
    @Test
    public void shouldTryToLockAndStartRunnerIfSuccessful() throws Exception {
        ReconnectionRunner runner = mock(ReconnectionRunner.class);
        
        whenNew(ReconnectionRunner.class).withArguments(exception, reconnector, lock, notificationCenter).thenReturn(runner);
        
        ExceptionListener exceptionListener = new ExceptionListener(reconnector, lock, notificationCenter);
        exceptionListener.onException(exception);
        
        verifyNew(ReconnectionRunner.class).withArguments(exception, reconnector, lock, notificationCenter);
        verify(runner).start();
    }
}
