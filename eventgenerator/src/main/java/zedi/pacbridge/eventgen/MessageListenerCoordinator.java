package zedi.pacbridge.eventgen;

import javax.jms.MessageListener;

import zedi.pacbridge.eventgen.zios.ui.TextPaneAppender;
import zedi.pacbridge.msg.JmsCenter;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;

public class MessageListenerCoordinator implements Notifiable {
    private JmsCenter jmsCenter;
    MessageListener messageListener;
    private boolean added;
    
    public MessageListenerCoordinator(JmsCenter jmsCenter, MessageListener messageListener) {
        this.jmsCenter = jmsCenter;
        this.messageListener = messageListener;
        this.added = true;
    }
    
    @Override
    public void handleNotification(Notification notification) {
        if (notification.getName().equals(NotificationNames.TURN_TRACE_OFF)) {
            if (added)
                jmsCenter.unregisterMessageListener(messageListener);
            added = false;
        } else if (notification.getName().equals(NotificationNames.TURN_TRACE_ON)) {
            if (added == false)
                jmsCenter.registerMessageListener(messageListener, "topic://scada/system/events", false);
            added = true;
        }
    }

}
