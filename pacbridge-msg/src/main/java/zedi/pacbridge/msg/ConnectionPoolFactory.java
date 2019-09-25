package zedi.pacbridge.msg;

public class ConnectionPoolFactory {
    public ConnectionPool newConnectionPool(JmsImplementor jmsImplementation) {
        return new ConnectionPool(jmsImplementation);
    }
}
