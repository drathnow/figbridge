package zedi.pacbridge.app.messaging;

import java.io.IOException;
import java.io.Serializable;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.xmlpull.v1.XmlSerializer;

import zedi.pacbridge.net.AlarmStatus;
import zedi.pacbridge.net.DataType;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.values.ZapDataType;

public class SiteReportItem implements Serializable {

    public static final String TAG_REPORT_ITEM = "ReportItem";
    public static final String TAG_INDEX = "index";
    public static final String TAG_DATA_TYPE = "dataType";
    public static final String TAG_ALARM_STATUS = "alarmStatus";
    public static final String TAG_VALUE = "Value";
    
    private Long index;
    private DataType dataType;
    private String value;
    private AlarmStatus alarmStatus;

    public SiteReportItem(DataType dataType, Long index, String value, AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
        this.dataType = dataType;
        this.index = index;
        this.value = value;
    }

    public SiteReportItem(DataType dataType, Long index, AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
        this.dataType = dataType;
        this.index = index;
        this.value = null;
    }

    public Long getIndex() {
        return index;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getValue() {
        return value;
    }

    public AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(ZapAlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    void serialize(XmlSerializer serializer) throws IOException {
        serializer.startTag(SiteReport.NAMESPACE, TAG_REPORT_ITEM);
        serializer.attribute(SiteReport.NAMESPACE, TAG_INDEX, Long.toString(index));
        serializer.attribute(SiteReport.NAMESPACE, TAG_DATA_TYPE, dataType.getName());
        if (getAlarmStatus() != null)
            serializer.attribute(SiteReport.NAMESPACE, TAG_ALARM_STATUS, alarmStatus.getName());
        serializeValue(serializer);
    }

    private void serializeValue(XmlSerializer serializer) throws IOException {
        serializer.startTag(SiteReport.NAMESPACE, TAG_VALUE);
        if (value != null)
            serializer.text(value);
        serializer.endTag(SiteReport.NAMESPACE, TAG_VALUE);            
        serializer.endTag(SiteReport.NAMESPACE, TAG_REPORT_ITEM);
    }

    public static SiteReportItem reportItemForElement(Element element) {
        Long index = Long.decode(element.getAttribute(TAG_INDEX).getValue());
        String attrValue = element.getAttribute(TAG_DATA_TYPE).getValue();
        ZapDataType dataType = ZapDataType.dataTypeForTypeNumber(Integer.parseInt(attrValue));
        String value = null;
        if (ZapDataType.EmptyValue.equals(dataType) == false)
            value = element.getChild(TAG_VALUE).getText();
        Attribute alarmStatusAttribute = element.getAttribute(TAG_ALARM_STATUS);
        ZapAlarmStatus alarmStatus = ZapAlarmStatus.alarmStatusForName(alarmStatusAttribute.getValue());
        return new SiteReportItem(dataType, index, value, alarmStatus);
    }
}
