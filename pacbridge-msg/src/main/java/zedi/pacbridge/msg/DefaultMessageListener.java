package zedi.pacbridge.msg;

import javax.jms.Message;
import javax.jms.MessageListener;

import zedi.pacbridge.utl.DependencyResolver;

class DefaultMessageListener implements MessageListener {

    private String lookupName;
    
    
    public DefaultMessageListener(String lookupName) {
        this.lookupName = lookupName;
    }

    @Override
    public void onMessage(Message message) {
        MessageListener messageListerner = DependencyResolver.Implementation.sharedInstance().getImplementationOf(lookupName);
        messageListerner.onMessage(message);
    }
}