package zedi.pacbridge.utl.strategies;

import java.io.Serializable;

import zedi.pacbridge.utl.Utilities;

public class NextDueTimeStrategy implements Serializable, DueTimeStrategy {
    private static final long serialVersionUID = 1001L;
    private Long startTime;
    private Integer intervalSeconds;
    private Long dueWindowMilliseconds;
    
    public NextDueTimeStrategy(Long startTime, Integer intervalSeconds, Long dueWindowMilliseconds) {
        this.startTime = startTime;
        this.intervalSeconds = intervalSeconds;
        this.dueWindowMilliseconds = dueWindowMilliseconds;
    }

    @Override
    public long nextDueTimeFromTime(long baseTime) {
        if (startTime != 0) {
            if (baseTime <= startTime + dueWindowMilliseconds)
                return startTime;
            else if (intervalSeconds != 0) {
                long intervalMilliseconds = (intervalSeconds * 1000L);
                long numberOfIntervals = ((baseTime - startTime - 1) / intervalMilliseconds) + 1;
                long nextTime = (numberOfIntervals * intervalMilliseconds) + startTime;
                if (baseTime <= nextTime - intervalMilliseconds + dueWindowMilliseconds)
                    return baseTime;
                else
                    return nextTime;
            }
        }
        return Utilities.DISTANT_FUTURE_INMILLIS;
    }
}
