package zedi.pacbridge.app.net;

public interface ConnectionInfoCollector<T> {
    public T collectInfo(Connection connection);
}