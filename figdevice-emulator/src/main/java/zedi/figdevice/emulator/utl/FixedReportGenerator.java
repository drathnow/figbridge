package zedi.figdevice.emulator.utl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.messages.ZapReasonCode;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ReadingFlags;
import zedi.pacbridge.zap.reporting.ReportHeader;
import zedi.pacbridge.zap.reporting.ZapReport;
import zedi.pacbridge.zap.values.ZapValue;

public class FixedReportGenerator implements ReportGenerator  {
    private static final Integer VERSION = 1;

    private static final Integer POLLSET_NUMBER = 1;
    private Integer readingCount;
    private ReportIdGenerator reportIdGenerator;
    private List<IoPointTemplate> templates;
    private List<IoPointReading> readings;
    private ReportTimeGenerator reportTimeGenerator;
    private EventIdGenerator eventIdGenerator;
    
    public FixedReportGenerator(Integer readingCount, ValueGenerator valueGenerator, ReportIdGenerator reportIdGenerator, ReportTimeGenerator reportTimeGenerator) {
        this.reportIdGenerator = reportIdGenerator;
        this.readingCount = readingCount;
        this.readings = new ArrayList<>();
        this.templates = new ArrayList<>();
        this.reportTimeGenerator = reportTimeGenerator;
        this.eventIdGenerator = new EventIdGenerator();
        init(valueGenerator);
    }

    @Override
    public ZapReport nextReport() {
        Long timestamp = System.currentTimeMillis();
        Date reportDate = new Date(timestamp);
        List<ReadingCollection> collections = new ArrayList<ReadingCollection>();
        collections.add(new ReadingCollection(reportDate, readings));
        ReportHeader header = reportHeader(POLLSET_NUMBER, templates);
        return new ZapReport(header, collections, UUID.randomUUID().toString());
    }
    
    public Integer secondsUntilNextReport() {
        return reportTimeGenerator.secondsUntilNextReport();
    }
    
    private void init(ValueGenerator valueGenerator) {
        int ioId = 1;
        ReadingFlags flags = new ReadingFlags(false, false, ZapAlarmStatus.OK);
        for (int i = 0; i < readingCount; i++)
            generateReading(valueGenerator, ioId++, flags);
    }
    
    private void generateReading(ValueGenerator valueGenerator, Integer ioId, ReadingFlags flags) {
        ZapValue value = valueGenerator.nextValue();
        readings.add(new IoPointReading(flags, value));
        templates.add(new IoPointTemplate(ioId.longValue(), value.dataType()));
    }

    private ReportHeader reportHeader(Integer pollsetNumber, List<IoPointTemplate> ioPointTemplates) {
        return new ReportHeader(VERSION, 
                                reportIdGenerator.nextReportId(), 
                                new Date(), 
                                1, // Only one set of readings
                                ZapReasonCode.Scheduled, 
                                pollsetNumber, 
                                ioPointTemplates,
                                eventIdGenerator.nextEventId());

    }
    
}
