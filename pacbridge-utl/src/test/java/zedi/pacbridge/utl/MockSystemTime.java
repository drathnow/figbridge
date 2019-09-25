package zedi.pacbridge.utl;

public class MockSystemTime extends SystemTime {

    public long currentTime;

    public long getCurrentTime() {
        return currentTime;
    }

}