package zedi.figbridge.monitor.utl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.messages.ZapReasonCode;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ReadingFlags;
import zedi.pacbridge.zap.reporting.ReportHeader;
import zedi.pacbridge.zap.reporting.ZapReport;
import zedi.pacbridge.zap.values.ZapFloat;
import zedi.pacbridge.zap.values.ZapValue;

public class BundledReportMessageGenerator {
    private static final Float FLOAT_VALUE = 1.2F;
    private static final Integer POLLSET_NUMBER = 1;
    private static final Integer VERSION = 1;
    
    private Integer reportId;
    private Integer ioId;
    private Long timestamp;

    public BundledReportMessageGenerator(Integer reportId, Integer ioId, Long timestamp) {
        this.reportId = reportId;
        this.ioId = ioId;
        this.timestamp = timestamp;
    }
    
    public Integer getReportId() {
        return reportId;
    }
    
    public BundledReportMessage buildBundledReportMessage() {
        Date reportDate = new Date(timestamp);
        List<ReadingCollection> collections = new ArrayList<ReadingCollection>();
        List<IoPointReading> readings = new ArrayList<>();
        List<IoPointTemplate> templates = new ArrayList<>();
        ZapValue value = new ZapFloat(FLOAT_VALUE);
        ReadingFlags flags = new ReadingFlags(false, false, ZapAlarmStatus.OK);
        readings.add(new IoPointReading(flags, value));
        templates.add(new IoPointTemplate(ioId.longValue(), value.dataType()));
        collections.add(new ReadingCollection(reportDate, readings));
        ReportHeader header = new ReportHeader(VERSION, reportId, new Date(), 1, ZapReasonCode.Scheduled, POLLSET_NUMBER, templates, 0L);
        ZapReport report = new ZapReport(header, collections, UUID.randomUUID().toString());
        Set<Integer> reportIds = new TreeSet<>();
        Map<Integer, ZapReport> reportMap = new TreeMap<>();
        reportIds.add(reportId);
        reportMap.put(reportId, report);
        return new BundledReportMessage(ioId, reportIds, reportMap);
    }
}
