package zedi.figdevice.emulator.utl;

import java.util.Random;

public class RandomIntervalReportTimeGenerator implements ReportTimeGenerator {
    private static Random random = new Random(System.currentTimeMillis());
    
    private Integer maxIntervalSeconds;
    
    public RandomIntervalReportTimeGenerator(Integer maxIntervalSeconds) {
        this.maxIntervalSeconds = maxIntervalSeconds;
    }

    @Override
    public Integer secondsUntilNextReport() {
        return Math.abs(random.nextInt() % maxIntervalSeconds);
    }

}
