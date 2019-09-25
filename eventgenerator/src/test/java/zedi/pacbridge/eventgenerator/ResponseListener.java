package zedi.pacbridge.eventgenerator;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;

public class ResponseListener implements MessageListener {
    private String xml;
    private Lock lock;
    private Condition condition;
    private Long forEventId;
    
    public ResponseListener() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }
    
    public void lock() {
        lock.lock();
    }
    
    public boolean waitForResponse(long waitSeconds, Long forEventId) throws InterruptedException {
        this.forEventId = forEventId;
        return condition.await(waitSeconds, TimeUnit.SECONDS);
    }
    
    public void unlock() {
        lock.unlock();
    }

    public String result() {
        return xml;
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            xml = ((TextMessage)message).getText();
            try {
                lock.lock();
                Element element = JDomUtilities.elementForXmlString(xml);
                if (forEventId != null 
                        && element.getAttributeValue("name").equals("ConfigureResponse")
                        && element.getChild("EventId").getText().equals(forEventId.toString())) {
                    condition.signal();
                } else
                    xml = null;
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
