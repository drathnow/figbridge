package zedi.figdevice.emulator.utl;

public class ReportIdGenerator {
    private int nextReportId = 1;
    
    public Integer nextReportId() {
        return nextReportId++;
    }
}