package zedi.pacbridge.net;

public class IncrementingSessionIdGenerator implements SessionIdGenerator {
    private int currentSessionId;
    private int maxSessionId;
    private int upperLimitSessionId;
    
    public IncrementingSessionIdGenerator(int maxSessionId) {
        this.maxSessionId = maxSessionId;
        this.currentSessionId = 1;
        this.upperLimitSessionId = maxSessionId/2;
    }

    @Override
    public Integer nextSessionId() {
        if (currentSessionId == upperLimitSessionId)
            currentSessionId = 1;
        return currentSessionId++;
    }

    @Override
    public Integer maxSessionId() {
        return maxSessionId;
    }
}
