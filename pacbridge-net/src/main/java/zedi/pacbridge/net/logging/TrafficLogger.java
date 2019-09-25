package zedi.pacbridge.net.logging;


public interface TrafficLogger {
    public void logOutgoingData(byte[] bytes, int startPosition, int length);
    public void logIncomingData(byte[] bytes, int startPosition, int length);
}