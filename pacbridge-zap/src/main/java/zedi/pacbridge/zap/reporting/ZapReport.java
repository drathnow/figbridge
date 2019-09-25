package zedi.pacbridge.zap.reporting;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.MD5;
import zedi.pacbridge.zap.messages.ZapReasonCode;

public class ZapReport {

    private ReportHeader reportHeader;
    private List<ReadingCollection> readingCollections;
    private String uniqueId;
    
    public ZapReport(ReportHeader reportHeader, List<ReadingCollection> readingCollections, String uniqueId) {
        this.reportHeader = reportHeader;
        this.readingCollections = readingCollections;
        this.uniqueId = uniqueId;
    }

    public Integer pollsetNumber() {
        return reportHeader.pollsetNumber();
    }
    
    public Long getEventId() {
        return reportHeader.getEventId();
    }
    
    public String uniqueId() {
        return uniqueId;
    }
    
    public ZapReasonCode reasonCode() {
        return reportHeader.reasonCode();
    }
    
    public List<IoPointTemplate> ioPointTemplate() {
        return reportHeader.ioPointTemplates();
    }
    
    public List<ReadingCollection> readingCollections() {
        return readingCollections;
    }
    
    public Integer reportId() {
        return reportHeader.uniqueId();
    }
    
    public Integer numberOfReadings() {
        return reportHeader.numberOfReadings();
    }

    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("ReportHeader", reportHeader.asJsonObject());
        JSONArray array = new JSONArray();
        for (ReadingCollection collection : readingCollections)
            array.put(collection.asJsonObject());
        obj.put("ReadingCollections", array);
        obj.put("UniqueId", uniqueId);
        return obj;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        reportHeader.serialize(byteBuffer);
        for (Iterator<ReadingCollection> iter = readingCollections.iterator(); iter.hasNext(); )
            iter.next().serialize(byteBuffer);
    }
    
    public static ZapReport reportFromByteBuffer(ByteBuffer byteBuffer) {
        MD5 md5 = new MD5();
        int length = byteBuffer.limit() - byteBuffer.position();
        md5.update(byteBuffer.array(), byteBuffer.position(), length);
        String uniqueId = md5.asHex();
        ReportHeader reportHeader = ReportHeader.reportHeaderFromByteBuffer(byteBuffer);
        List<ReadingCollection> readingCollections = new ArrayList<>();
        List<IoPointTemplate> ioPointTemplates = reportHeader.ioPointTemplates();
        for (int i = 0; i < reportHeader.readingCollectionCount(); i++)
            readingCollections.add(ReadingCollection.readingCollectionFromByteBuffer(ioPointTemplates, byteBuffer));
        return new ZapReport(reportHeader, readingCollections, uniqueId);
    }

    public static void analyze(byte[] hexStringAsBytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(hexStringAsBytes);
        ReportHeader.analyze(byteBuffer);
        System.out.println();
    }
}
