package zedi.pacbridge.net.core;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.core.ContextCommandQueue.ContextCommandContainer;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextCommandQueue.class)
public class ContextCommandQueueTest extends BaseTestCase {

    @Test
    public void shouldReturnDelayedCommandsBeforeRegularQueuedCommands() throws Exception {
        ContextCommand command1 = mock(ContextCommand.class);
        ContextCommand command2 = mock(ContextCommand.class);
        ContextCommand command3 = mock(ContextCommand.class);
     
        given(command1.toString()).willReturn("Command1");
        given(command2.toString()).willReturn("Command2");
        given(command3.toString()).willReturn("Command3");
        
        ContextCommandQueue queue = new ContextCommandQueue();

        queue.queueCommand(command1);
        queue.queueCommand(command2);
        queue.queueCommand(command3, 10, TimeUnit.MILLISECONDS);
        
        Thread.sleep(100);
        
        assertSame(command3, queue.nextDueCommand());
        assertSame(command1, queue.nextDueCommand());
        assertSame(command2, queue.nextDueCommand());
        assertNull(queue.nextDueCommand());
    }
    
    @Test
    public void shouldDiscardCancelledCommands() throws Exception {
        SystemTime systemTime = mock(SystemTime.class);
        
        ContextCommand command1 = mock(ContextCommand.class);
        ContextCommand command2 = mock(ContextCommand.class);
        ContextCommand command3 = mock(ContextCommand.class);
        
        ContextCommandContainer container = mock(ContextCommandContainer.class);
        whenNew(ContextCommandContainer.class).withArguments(command3, 20L).thenReturn(container);
        given(systemTime.getCurrentTime()).willReturn(0L);
        given(container.isCancelled()).willReturn(true);

        ContextCommandQueue queue = new ContextCommandQueue();
        queue.setSystemTime(systemTime);

        queue.queueCommand(command1);
        queue.queueCommand(command2);
        queue.queueCommand(command3, 20, TimeUnit.MILLISECONDS);
        
        Thread.sleep(100);
        
        assertSame(command1, queue.nextDueCommand());
        assertSame(command2, queue.nextDueCommand());
        assertNull(queue.nextDueCommand());
    }
    
    @Test
    public void shouldReturnCommandsInOrder() throws Exception {
        ContextCommand command1 = mock(ContextCommand.class);
        ContextCommand command2 = mock(ContextCommand.class);
        
        ContextCommandQueue queue = new ContextCommandQueue();

        queue.queueCommand(command1);
        queue.queueCommand(command2);
        
        assertSame(command1, queue.nextDueCommand());
        assertSame(command2, queue.nextDueCommand());
        assertNull(queue.nextDueCommand());
    }
    
    @Test
    public void shouldOrderTimedCommands() throws Exception {
        long now = System.currentTimeMillis();
        SystemTime systemTime = mock(SystemTime.class);
        
        ContextCommand command1 = mock(ContextCommand.class);
        ContextCommand command2 = mock(ContextCommand.class);
        ContextCommand command3 = mock(ContextCommand.class);
        
        given(systemTime.getCurrentTime()).willReturn(now);
        given(command1.toString()).willReturn("Command1");
        given(command2.toString()).willReturn("Command2");
        given(command3.toString()).willReturn("Command3");
        
        ContextCommandQueue queue = new ContextCommandQueue(systemTime);
        
        queue.queueCommand(command3, 200, TimeUnit.MILLISECONDS);
        queue.queueCommand(command2, 100, TimeUnit.MILLISECONDS);
        queue.queueCommand(command1, 0, TimeUnit.MILLISECONDS);
        
        Thread.sleep(300);
        
        ContextCommand nextDueCommand = queue.nextDueCommand();
        assertSame(command1, nextDueCommand);
        nextDueCommand = queue.nextDueCommand();
        assertSame(command2, nextDueCommand);
        nextDueCommand = queue.nextDueCommand();
        assertSame(command3, nextDueCommand);
    }
}
