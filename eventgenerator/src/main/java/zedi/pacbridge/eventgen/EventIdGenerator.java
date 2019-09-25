package zedi.pacbridge.eventgen;

public class EventIdGenerator {

    private static int nextEventId = 1;
    
    public static Integer nextEventId() {
        return nextEventId++;
    }
}
