package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;


public class NotificationCenterTest extends BaseTestCase {

    private static final String NOTIFICATION_NAME = "foo";
    
    @Mock
    private Notifiable notifiable;
    @Mock
    private ExecutorService threadPool;
    
    @Test
    public void testFoo() throws Exception {
		
	}
    
    @Test
    public void shouldNotificationRunnerShouldPostNotification() throws Exception {
        Object attachment = new Object();
        NotificationCenter center = new NotificationCenter(threadPool);
        center.addObserver(notifiable, "foo");
        
        NotificationRunner runner = new NotificationRunner(center, NOTIFICATION_NAME, attachment);
        runner.run();
        
        ArgumentCaptor<Notification> argument = ArgumentCaptor.forClass(Notification.class);
        
        verify(notifiable).handleNotification(argument.capture());
        Notification notification = argument.getValue();
        assertEquals("foo", notification.getName());
        assertSame(attachment, notification.getAttachment());
    }
    
    @Test
    public void shouldSubmitRunnableToThreadPoolWhenAsyncNotificationRequestedWithAttachement() throws Exception {        
        Object attachment = new Object();
        NotificationCenter center = new NotificationCenter(threadPool);
        ArgumentCaptor<NotificationRunner> argument = ArgumentCaptor.forClass(NotificationRunner.class);
       
        center.postNotificationAsync("foo", attachment);
                
        verify(threadPool).execute(argument.capture());
        
        NotificationRunner notificationRunner = argument.getValue();
        assertEquals("foo", notificationRunner.getNotificationName());
        assertSame(attachment, notificationRunner.getAttachment());
        assertSame(center, notificationRunner.getNotificationCenter());
    }
    
    @Test
    public void shouldSubmitRunnableToThreadPoolWhenAsyncNotificationRequested() throws Exception {
        NotificationCenter center = new NotificationCenter(threadPool);
        ArgumentCaptor<NotificationRunner> argument = ArgumentCaptor.forClass(NotificationRunner.class);
       
        center.postNotificationAsync("foo");
                
        verify(threadPool).execute(argument.capture());
        
        NotificationRunner notificationRunner = argument.getValue();
        assertEquals("foo", notificationRunner.getNotificationName());
        assertNull(notificationRunner.getAttachment());
        assertSame(center, notificationRunner.getNotificationCenter());
    }
    
    @Test
    public void shouldPostNotificationWithAttachement() throws Exception {
        Object attachment = new Object();
        NotificationCenter center = new NotificationCenter(threadPool);
        center.addObserver(notifiable, "foo");
        center.postNotification("foo", attachment);
        
        ArgumentCaptor<Notification> argument = ArgumentCaptor.forClass(Notification.class);
        
        verify(notifiable).handleNotification(argument.capture());
        Notification notification = argument.getValue();
        assertEquals("foo", notification.getName());
        assertSame(attachment, notification.getAttachment());
    }
    
    @Test
    public void shouldPostNotification() throws Exception {
        NotificationCenter center = new NotificationCenter(threadPool);
        center.addObserver(notifiable, "foo");
        center.postNotification("foo");
        
        ArgumentCaptor<Notification> argument = ArgumentCaptor.forClass(Notification.class);

        verify(notifiable).handleNotification(argument.capture());
        Notification notification = argument.getValue();
        assertEquals("foo", notification.getName());
        assertNull(notification.getAttachment());
    }

    @Test
    public void shouldRemoveObserver() throws Exception {
        NotificationCenter center = new NotificationCenter(threadPool);
        center.addObserver(notifiable, "foo");
        center.postNotification("foo");
        verify(notifiable).handleNotification(any(Notification.class));
        center.removeObserver(notifiable);
        center.postNotification("foo");
        verify(notifiable, times(1)).handleNotification(any(Notification.class));
    }
}
