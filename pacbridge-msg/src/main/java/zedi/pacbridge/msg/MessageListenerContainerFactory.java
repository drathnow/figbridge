package zedi.pacbridge.msg;

import javax.jms.Destination;
import javax.jms.MessageListener;

public class MessageListenerContainerFactory {
    public MessageListenerContainer newMessageListenerContainer(MessageListener messageListener, Destination destination, String subscriptionName, boolean isTransacted, String messageSelector) {
        return new MessageListenerContainer(messageListener, destination, subscriptionName, isTransacted, null);
    }

    public MessageListenerContainer newMessageListenerContainer(MessageListener messageListener, Destination destination, boolean isTransacted, String messageSelector) {
        return new MessageListenerContainer(messageListener, destination, isTransacted, null);
    }
}
