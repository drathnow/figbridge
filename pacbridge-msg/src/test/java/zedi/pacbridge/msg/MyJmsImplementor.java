package zedi.pacbridge.msg;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.naming.NamingException;

import zedi.pacbridge.msg.annotations.JmsImplementation;

@JmsImplementation(name = "JmsImplementationBuilderTestImplementation")
public class MyJmsImplementor implements JmsImplementor {

    private String foo;
    private Integer bar;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public Integer getBar() {
        return bar;
    }

    public void setBar(Integer bar) {
        this.bar = bar;
    }

    @Override
    public void setClientId(String cliendId) {
    }

    @Override
    public Connection createConnection() throws JMSException, NamingException {
        return null;
    }

    @Override
    public Topic createTopic(String topicName) throws NamingException, JMSException {
        return null;
    }

    @Override
    public Destination createDestination(String destinationName) throws NamingException, JMSException {
        return null;
    }

    @Override
    public void initialize() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public JmsServerReconnector serverReconnector() {
        // TODO Auto-generated method stub
        return null;
    }
}