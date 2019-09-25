package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

import zedi.pacbridge.utl.MD5;
import zedi.pacbridge.utl.io.Unsigned;


public class ExtendedReportMessage extends IoPointReportMessage<ExtendedReportItem> implements GdnMessage, Serializable {
    static final long serialVersionUID = 1001;

    private GdnReasonCode reasonCode;
    private ValueType valueType;
    
    private ExtendedReportMessage() {
        super(GdnMessageType.ExtendedReport);
    }
    
    public ExtendedReportMessage(List<ExtendedReportItem> reportItems, GdnReasonCode reasonCode, Integer pollSetNumber, Date timestamp) {
        super(GdnMessageType.ExtendedReport, reportItems, pollSetNumber, timestamp);
        this.reasonCode = reasonCode;
        this.valueType = ValueType.Extended;
    }
    
    @Override
    public GdnMessageType messageType() {
        return GdnMessageType.ExtendedReport;
    }
    
    public GdnReasonCode getReasonCode() {
        return reasonCode;
    }

    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)getPollSetNumber());
        byteBuffer.putInt((int)(getTimeStamp().getTime()/1000));
        byteBuffer.put((byte)getReportItems().size());
        ReportAttributes reportAttributes = new ReportAttributes(reasonCode, valueType);
        reportAttributes.serialize(byteBuffer);
        serializeReportItems(byteBuffer);
    }
    
    @Override
    public String toString() {
        return new GdnMessageDecoder().decodeGdnMessage(this);
    }

    private void deserialize(ByteBuffer byteBuffer) {
        MD5 md5 = new MD5();
        int length = byteBuffer.limit() - byteBuffer.position();
        md5.update(byteBuffer.array(), byteBuffer.position(), length);
        uniqueId = md5.asHex();
        int reportItemCount;
        setPollSetNumber(Unsigned.getUnsignedByte(byteBuffer));
        setTimestamp(new Date((long)Unsigned.getUnsignedInt(byteBuffer)*1000));
        reportItemCount = Unsigned.getUnsignedByte(byteBuffer);
        ReportAttributes reportAttributes = ReportAttributes.reportAttributesFromByteBuffer(byteBuffer);
        reasonCode = reportAttributes.getReasonCode();
        valueType = reportAttributes.getValueType();
        deserializeReportItems(byteBuffer, reportItemCount);
    }

    
    private void deserializeReportItems(ByteBuffer byteBuffer, int reportItemCount) {
        for (int i = 0; i < reportItemCount; i++) {
            ExtendedReportItem item = ExtendedReportItem.extendedReportItemFromByteBuffer(byteBuffer);
            addReportItem(item);
        }
    }
    
    public static ExtendedReportMessage extendedReportMessageFromByteBuffer(ByteBuffer byteBuffer) {
        ExtendedReportMessage message = new ExtendedReportMessage();
        message.deserialize(byteBuffer);
        return message;
    }
}
