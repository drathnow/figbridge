package zedi.pacbridge.app.messaging;

import java.util.Iterator;
import java.util.List;

import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ZapReport;

public class ReportToSiteReportConverter {
    private String nuid;
    private ZapReport report;
    private SiteReportItemBuilder itemBuilder;
    private List<IoPointTemplate> ioPointTemplate;
    private Iterator<ReadingCollection> readingCollectionIterator;
    
    public ReportToSiteReportConverter() {
    }
    
    protected void init(String nuid, ZapReport report, SiteReportItemBuilder itemBuilder) {
        this.nuid = nuid;
        this.itemBuilder = itemBuilder;
        this.report = report;
        this.ioPointTemplate = report.ioPointTemplate();
        this.readingCollectionIterator = report.readingCollections().iterator();
    }
    
    public void init(String nuid, ZapReport report) {
        init(nuid, report, new SiteReportItemBuilder());
    }
    
    public SiteReport nextReport() {
        SiteReport siteReport = null;
        if (readingCollectionIterator.hasNext()) {
            ReadingCollection readingCollection = readingCollectionIterator.next();
            siteReport = builtSiteReport(ioPointTemplate, readingCollection);
        }
        return siteReport;
    }
    
    private SiteReport builtSiteReport(List<IoPointTemplate> ioPointTemplate, ReadingCollection readingCollection) {
        SiteReport siteReport = new SiteReport(EventQualifier.ZIOS, 
                                               nuid, 
                                               report.pollsetNumber(), 
                                               readingCollection.timestamp(), 
                                               report.reasonCode(), 
                                               report.getEventId());
        addReportItemsToSiteReport(ioPointTemplate, readingCollection.ioPointReadings(), siteReport);
        return siteReport;
    }

    private void addReportItemsToSiteReport(List<IoPointTemplate> ioPointTemplates, List<IoPointReading> ioPointReadings, SiteReport siteReport) {
        Iterator<IoPointReading> readingsIterator = ioPointReadings.iterator();
        for (Iterator<IoPointTemplate> templateIterator = ioPointTemplates.iterator();
                templateIterator.hasNext(); ) {
            IoPointTemplate template = templateIterator.next();
            IoPointReading reading = readingsIterator.next();
            SiteReportItem item = itemBuilder.siteReportItemForTemplateAndReading(template, reading);
            if (item != null)
                siteReport.addReportItem(item);
        }
    }
}
