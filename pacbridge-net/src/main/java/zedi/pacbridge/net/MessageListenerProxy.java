package zedi.pacbridge.net;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import zedi.pacbridge.net.annotations.Async;
import zedi.pacbridge.utl.ReflectionHelper;

public class MessageListenerProxy implements MessageListener, Serializable {
    private static Executor classExecutor = Executors.newSingleThreadExecutor(); 
    
    private MessageListener messageListener;
    private boolean callAsync;
    
    public MessageListenerProxy(MessageListener messageListener) {
        callAsync = ReflectionHelper.classHasTypeAnnotation(messageListener.getClass(), Async.class);
        this.messageListener = messageListener;
    }
    
    @Override
    public void handleMessage(final Message message) {
        if (callAsync)
            classExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    messageListener.handleMessage(message);
                }
            });
        else
            messageListener.handleMessage(message);
    }
}
