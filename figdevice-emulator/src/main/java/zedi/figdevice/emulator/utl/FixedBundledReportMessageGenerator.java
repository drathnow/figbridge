package zedi.figdevice.emulator.utl;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.reporting.ZapReport;

public class FixedBundledReportMessageGenerator implements BundledReportMessageGenerator {

    private Integer numberOfReportsPerMessage;
    private ReportGenerator reportGenerator;
    
    public FixedBundledReportMessageGenerator(Integer numberOfReportsPerMessage, ReportGenerator reportGenerator) {
        this.numberOfReportsPerMessage = numberOfReportsPerMessage;
        this.reportGenerator = reportGenerator;
    }

    public Integer secondsUntilNextReportIsDue() {
        return reportGenerator.secondsUntilNextReport();
    }
    
    @Override
    public BundledReportMessage nextBundledReportMessage(Integer sequenceNumber) {
        Set<Integer> reportIdSet = new TreeSet<Integer>();
        Map<Integer, ZapReport> reportMap = new TreeMap<Integer, ZapReport>();
        for (int i = 0; i < numberOfReportsPerMessage; i++) {
            ZapReport report = reportGenerator.nextReport();
            reportIdSet.add(report.reportId());
            reportMap.put(report.reportId(), report);
        }
        return new BundledReportMessage(sequenceNumber, reportIdSet, reportMap);
    }
    
    @Override
    public boolean isFinished() {
        return false;
    }
}
