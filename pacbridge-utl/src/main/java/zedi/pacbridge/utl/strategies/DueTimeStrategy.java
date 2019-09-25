package zedi.pacbridge.utl.strategies;

public interface DueTimeStrategy {
    
    /**
     * Calculates the next event time from a given base time
     *
     * @return long - the next due time in milliseconds. Utilities.DISTANT_FUTURE_INMILLIS
     * if no future event is due.
     */
    public long nextDueTimeFromTime(long baseTime);
}
