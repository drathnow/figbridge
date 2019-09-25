package zedi.pacbridge.msg;

import javax.jms.Connection;

class ConnectionWrapperFactory {
    public ConnectionWrapper newConnectionWrapper(Connection connection, ConnectionPool connectionPool) {
        return new ConnectionWrapper(connection, connectionPool);
    }
}
