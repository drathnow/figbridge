package zedi.figdevice.emulator.utl;


public class FixedIntervalReportTimeGenerator implements ReportTimeGenerator {

    private Integer intervalSeconds;

    public FixedIntervalReportTimeGenerator(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }
    
    @Override
    public Integer secondsUntilNextReport() {
        return intervalSeconds;
    }
}