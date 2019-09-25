package zedi.pacbridge.app.messaging;

import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;

public class SiteReportItemBuilder {

    public SiteReportItem siteReportItemForTemplateAndReading(IoPointTemplate template, IoPointReading reading) {
        SiteReportItem siteReportItem = null;
        if (reading.isEmptyValue() == false) {
            if (reading.isNullValue())
                siteReportItem = new SiteReportItem(template.dataType(), template.index(), reading.alarmStatus());
            else 
                siteReportItem = new SiteReportItem(template.dataType(), template.index(), reading.value().toString(), reading.alarmStatus());
        }
        return siteReportItem;
    }
}
