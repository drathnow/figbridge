package zedi.pacbridge.net.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.ThreadContextHandler;

public class TimeSliceContextCommandTest extends BaseTestCase {

    @Test
    public void shouldCallHaveYourTimeSlice() {
        ThreadContextHandler handler = mock(ThreadContextHandler.class);
        ThreadContextCommand command = new ThreadContextCommand(handler);
        command.execute();
        verify(handler).handleSyncTrap();
    }

}
