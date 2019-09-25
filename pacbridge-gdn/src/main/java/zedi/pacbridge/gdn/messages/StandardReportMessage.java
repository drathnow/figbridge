package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

import zedi.pacbridge.utl.MD5;
import zedi.pacbridge.utl.io.Unsigned;


public class StandardReportMessage extends IoPointReportMessage<StandardReportItem> implements GdnMessage, Serializable {
    static final long serialVersionUID = 1001;
    
    public static final int MESSAGE_NUMBER = 9;

    private StandardReportMessage() {
        super(GdnMessageType.StandardReport);
    }
    
    public StandardReportMessage(List<StandardReportItem> reportItems, Integer pollSetNumber, Date timestamp) {
        super(GdnMessageType.StandardReport, reportItems, pollSetNumber, timestamp);
    }
    
    @Override
    public GdnReasonCode getReasonCode() {
        return GdnReasonCode.Unknown;
    }
    
    @Override
    public GdnMessageType messageType() {
        return GdnMessageType.StandardReport;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)pollSetNumber);
        byteBuffer.putInt((int)(timestamp.getTime() / 1000));
        byteBuffer.put((byte)reportItems.size());
        for (StandardReportItem item : reportItems)
            item.serialize(byteBuffer);
    }
  
    private void deserialize(ByteBuffer byteBuffer) {
        MD5 md5 = new MD5();
        int length = byteBuffer.limit() - byteBuffer.position();
        md5.update(byteBuffer.array(), byteBuffer.position(), length);
        uniqueId = md5.asHex();
        pollSetNumber = Unsigned.getUnsignedByte(byteBuffer);
        timestamp = new Date(((long)Unsigned.getUnsignedInt(byteBuffer)) * 1000);
        int count = Unsigned.getUnsignedByte(byteBuffer);
        for (int i = 0; i < count; i++) {
            StandardReportItem reportItem = StandardReportItem.standardReportItemFromByteBuffer(byteBuffer);
            reportItems.add(reportItem);
        }
    }

    public static StandardReportMessage standardReportMessageFromByteBuffer(ByteBuffer byteBuffer) {
        StandardReportMessage message = new StandardReportMessage();
        message.deserialize(byteBuffer);
        return message;
    }
}
