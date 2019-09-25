package zedi.figdevice.emulator.utl;


public class EventIdGenerator {
    private long nextEventId = 1;
        
    public Long nextEventId() {
        if (nextEventId >= Long.MAX_VALUE)
            nextEventId = 1;
        return nextEventId++;
    }
}
