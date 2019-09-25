package zedi.pacbridge.net.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.ThreadContextHandler;

public class NetworkEventDispatcherThreadContextTest extends BaseTestCase {

    @Test
    public void shouldQueueCommandToDispatcher() {
        ArgumentCaptor<ThreadContextCommand> argument = ArgumentCaptor.forClass(ThreadContextCommand.class);
        ThreadContextHandler handler = mock(ThreadContextHandler.class);
        NetworkEventDispatcher dispatcher = mock(NetworkEventDispatcher.class);
        
        NetworkEventDispatcherThreadContext requester = new NetworkEventDispatcherThreadContext(dispatcher);
        requester.requestTrap(handler);
        
        verify(dispatcher).queueContextCommand(argument.capture());
        argument.getValue().execute();
        verify(handler).handleSyncTrap();
    }

}
