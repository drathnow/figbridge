package zedi.tools.topicreader;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsoleMessageListener implements MessageListener {
    boolean dumpHeader;

    public ConsoleMessageListener(boolean dumpHeader) {
        this.dumpHeader = dumpHeader;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (dumpHeader)
                dumpHeader(message);
            System.out.println(((TextMessage)message).getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void dumpHeader(Message message) throws JMSException {
        Enumeration<String> enumeration = message.getPropertyNames();
        if (enumeration.hasMoreElements()) {
            System.out.println("Properties: ");
            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement();
                Object o = message.getObjectProperty(name);
                System.out.println("   " + name + " = " + o.toString());
            }
        }
    }

}
