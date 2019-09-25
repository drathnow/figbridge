package zedi.figdevice.emulator.utl;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.reporting.ZapReport;

public class RandomBundledReportMessageGenerator implements BundledReportMessageGenerator  {
    private Integer maxReportsPerMessage;
    private ReportGenerator reportGenerator;
    private Random random;
    
    public RandomBundledReportMessageGenerator(Integer maxReportsPerMessage, ReportGenerator reportGenerator) {
        this.maxReportsPerMessage = maxReportsPerMessage;
        this.reportGenerator = reportGenerator;
        this.random = new Random(System.currentTimeMillis());
    }
    
    public Integer secondsUntilNextReportIsDue() {
        return reportGenerator.secondsUntilNextReport();
    }

    @Override
    public BundledReportMessage nextBundledReportMessage(Integer sequenceNumber) {
        Set<Integer> reportIdSet = new TreeSet<Integer>();
        Map<Integer, ZapReport> reportMap = new TreeMap<Integer, ZapReport>();
        int numberOfReport = (random.nextInt()%maxReportsPerMessage) + 1; 
        for (int i = 0; i < numberOfReport; i++) {
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
