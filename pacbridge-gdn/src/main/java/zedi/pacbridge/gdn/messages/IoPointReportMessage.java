package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zedi.pacbridge.gdn.IoPointIndexes;
import zedi.pacbridge.net.Report;


public abstract class IoPointReportMessage<TReportItem extends IoPointReportItem> extends GdnMessageBase implements Serializable, Report {
    static final long serialVersionUID = 1001;

    protected Date timestamp;
    protected int pollSetNumber;
    protected List<TReportItem> reportItems;
    protected String uniqueId;

    protected IoPointReportMessage(GdnMessageType messageType) {
        super(messageType);
        reportItems = new ArrayList<TReportItem>();
    }

    protected IoPointReportMessage(GdnMessageType messageType, List<TReportItem> items, Integer pollSetNumber, Date timestamp) {
        super(messageType);
        this.reportItems = items;
        this.pollSetNumber = pollSetNumber;
        this.timestamp = timestamp;
    }

    public abstract GdnReasonCode getReasonCode();
    
    public String typeString() {
        return "IoPointReportMessage";
    }
    
    @Override
    public String uniqueId() {
        return uniqueId;
    }

    public boolean isDiagnosticReport() {
        return pollSetNumber == IoPointIndexes.DIAGNOSTIC_POLLSET_NUMBER;
    }

    public boolean isDiagnosticErrorReport() {
        return (isDiagnosticReport() && reportItems.size() == 1 && containsItemWithIndex(IoPointIndexes.INDEX_FOR_ERROR_CODE));
    }

    public int getNumberOfReportItems() {
        return reportItems.size();
    }

    public boolean containsItemWithIndex(int index) {
        for (IoPointReportItem item : reportItems)
            if (item.getIndex() == index)
                return true;
        return false;
    }

    public IoPointReportItem reportItemWithIndex(int index) {
        for (IoPointReportItem item : reportItems)
            if (item.getIndex() == index)
                return item;
        return null;
    }

    public int getPollSetNumber() {
        return pollSetNumber;
    }

    public List<TReportItem> getReportItems() {
        return new ArrayList<TReportItem>(reportItems);
    }

    public Date getTimeStamp() {
        return timestamp;
    }

    protected void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    protected void setPollSetNumber(int pollSetNumber) {
        this.pollSetNumber = pollSetNumber;
    }
    
    protected void addReportItem(TReportItem item) {
        reportItems.add(item);
    }
    
    protected void serializeReportItems(ByteBuffer byteBuffer) {
        for (TReportItem item : reportItems)
            item.serialize(byteBuffer);
    }
}
