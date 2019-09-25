package zedi.pacbridge.msg;

import javax.jms.MessageListener;


public interface TopicMessageListener extends MessageListener {
    public String getTopicName();
    public boolean isDuarable();
    public String getSubscriptionName();
}
