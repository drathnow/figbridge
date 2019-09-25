package zedi.pacbridge.zap.reporting;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.Utilities;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.messages.ZapReasonCode;

public class ReportHeader {
    private Integer version;
    private Integer reportId;
    private Date creationTime;
    private Integer readingCollectionCount;
    private ZapReasonCode reasonCode;
    private Integer pollsetNumber;
    private List<IoPointTemplate> ioPointTemplates;
    private Long eventId;
    
    public ReportHeader(Integer version, 
                        Integer reportId, 
                        Date creationTime, 
                        Integer readingCollectionCount, 
                        ZapReasonCode reasonCode, 
                        Integer pollsetNumber, 
                        List<IoPointTemplate> ioPointTemplates,
                        Long eventId) {
        this.version = version;
        this.reportId = reportId;
        this.creationTime = creationTime;
        this.readingCollectionCount = readingCollectionCount;
        this.reasonCode = reasonCode;
        this.pollsetNumber = pollsetNumber;
        this.ioPointTemplates = ioPointTemplates;
        this.eventId = eventId == null ? 0L : eventId;
    }

    public ReportHeader(Integer reportId, 
                        Date creationTime, 
                        Integer readingCollectionCount, 
                        ZapReasonCode reasonCode, 
                        Integer pollsetNumber, 
                        List<IoPointTemplate> ioPointTemplates,
                        Long eventId) {
        this(1, reportId, creationTime, readingCollectionCount, reasonCode, pollsetNumber, ioPointTemplates, eventId);
    }
  
    public ZapReasonCode reasonCode() {
        return reasonCode;
    }
    
    public Integer uniqueId() {
        return reportId;
    }
    
    public Date creationTime() {
        return creationTime;
    }
    
    public Integer readingCollectionCount() {
        return readingCollectionCount;
    }
    
    public Integer pollsetNumber() {
        return pollsetNumber;
    }
    
    public Integer numberOfReadings() {
        return readingCollectionCount*ioPointTemplates.size();
    }
    
    public List<IoPointTemplate> ioPointTemplates() {
        return ioPointTemplates;
    }
    
    public Long getEventId() {
        return eventId;
    }

    public JSONObject asJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("Version", version.toString());
        jsonObj.put("EventId", eventId.toString());
        jsonObj.put("ReportId", reportId.toString());
        jsonObj.put("CreationTime", Utilities.ISO8601DateFormatterUtc.format(creationTime));
        jsonObj.put("NoOfTemplates", (short)ioPointTemplates.size());
        jsonObj.put("ReadingCollectionCount", readingCollectionCount.shortValue());
        jsonObj.put("ReasonCode", reasonCode.getNumber().byteValue());
        jsonObj.put("PollsetNumber", pollsetNumber.shortValue());
        JSONArray array = new JSONArray();
        for (Iterator<IoPointTemplate> iter = ioPointTemplates.iterator(); iter.hasNext(); )
            array.put(iter.next().asJsonObject());
        jsonObj.put("IoPointTemplates", array);
        return jsonObj;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(version.byteValue());
        byteBuffer.putLong(eventId);
        byteBuffer.putInt(reportId.intValue());
        byteBuffer.putInt((int)(creationTime.getTime()/1000L));
        byteBuffer.putShort((short)ioPointTemplates.size());
        byteBuffer.putShort(readingCollectionCount.shortValue());
        byteBuffer.put(reasonCode.getNumber().byteValue());
        byteBuffer.putShort(pollsetNumber.shortValue());
        for (Iterator<IoPointTemplate> iter = ioPointTemplates.iterator(); iter.hasNext(); )
            iter.next().serialize(byteBuffer);
    }
    
    public static ReportHeader reportHeaderFromByteBuffer(ByteBuffer byteBuffer) {
        Unsigned.getUnsignedByte(byteBuffer); // Don't need the version for now.
        Long eventId = byteBuffer.getLong();
        Integer reportId = (int)Unsigned.getUnsignedInt(byteBuffer);
        Date date = new Date(Unsigned.getUnsignedInt(byteBuffer)*1000L);
        Integer templateCount = Unsigned.getUnsignedShort(byteBuffer);
        Integer readingCollectionCount = Unsigned.getUnsignedShort(byteBuffer);
        ZapReasonCode reasonCode = ZapReasonCode.reasonCodeForReasonNumber(Unsigned.getUnsignedByte(byteBuffer));
        Integer pollsetNumber = Unsigned.getUnsignedShort(byteBuffer);
        List<IoPointTemplate> templates = new ArrayList<>();
        for (int i = 0; i < templateCount; i++)
            templates.add(IoPointTemplate.templateFromByteBuffer(byteBuffer));
        return new ReportHeader(reportId, date, readingCollectionCount, reasonCode, pollsetNumber, templates, eventId);
    }

    public static void analyze(ByteBuffer byteBuffer) {
        System.out.println("Version: " + Unsigned.getUnsignedByte(byteBuffer));
        System.out.println("EventId: " + byteBuffer.getLong());
        System.out.println("ReportId: " + (int)Unsigned.getUnsignedInt(byteBuffer));
        System.out.println("Date: " + Unsigned.getUnsignedInt(byteBuffer));
        System.out.println("Template Count: " + Unsigned.getUnsignedShort(byteBuffer));
        System.out.println("Reading Collection Count: " + Unsigned.getUnsignedShort(byteBuffer));
        System.out.println("Reason Code: " +  Unsigned.getUnsignedByte(byteBuffer));
        System.out.println("Pollset Number: " + Unsigned.getUnsignedShort(byteBuffer));
    }
}
