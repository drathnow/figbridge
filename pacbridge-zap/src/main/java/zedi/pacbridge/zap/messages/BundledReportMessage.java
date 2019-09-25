package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.reporting.ZapReport;

public class BundledReportMessage extends ZapMessage {
    public static final Integer HEADER_ITEM_SIZE = 6;
    public static final Integer VERSION1 = 1;
    
    private Map<Integer, ZapReport> reportMap;
    private Set<Integer> reportIds;
    
    public BundledReportMessage(Integer sequenceNumber, Set<Integer> reportIds, Map<Integer, ZapReport> reportMap) {
        super(ZapMessageType.BundledReport, sequenceNumber);
        this.reportMap = reportMap;
        this.reportIds = reportIds;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(VERSION1.byteValue());
        byteBuffer.putShort(sequenceNumber().shortValue());
        byteBuffer.putShort((short)reportMap.size());
        for (Integer reportId : reportIds)
            byteBuffer.putInt(reportId.intValue());
        for (Integer reportId : reportIds) {
            ZapReport report = reportMap.get(reportId);
            report.serialize(byteBuffer);
        }
    }

    @Override
    public Integer size() {
        throw new UnsupportedOperationException("Method not implemented");
    }
    
    public Map<Integer, ZapReport> reportsMap() {
        return Collections.unmodifiableMap(reportMap);
    }

    public Set<Integer> reportIds() {
        return Collections.unmodifiableSet(reportIds);
    }

    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        JSONArray idArray = new JSONArray();
        for (Integer id : reportIds)
            idArray.put(id);
        obj.put("ReportIds", idArray);
        JSONArray mapArray = new JSONArray();
        for (Integer id : reportMap.keySet())
            mapArray.put(reportMap.get(id).asJsonObject());
        obj.put("ReportMap", mapArray);
        return obj;
    }
    
    public static BundledReportMessage bundledReportMessageFromByteBuffer(ByteBuffer byteBuffer) {
        Set<Integer> reportIds = new TreeSet<>();
        Map<Integer, ZapReport> reportMap = new TreeMap<>();
        byteBuffer.get(); // Skip version number. Only support one right now
        Integer sequenceNumber = Unsigned.getUnsignedShort(byteBuffer);
        int count = Unsigned.getUnsignedShort(byteBuffer);
        for (int i = 0; i < count; i++) {
            Long reportId = Unsigned.getUnsignedInt(byteBuffer);
            reportIds.add(reportId.intValue());
        }
            
        for (int i = 0; i < count; i++) {
            ZapReport report = ZapReport.reportFromByteBuffer(byteBuffer);
            reportMap.put(report.reportId(), report);
        }
        
        return new BundledReportMessage(sequenceNumber, reportIds, reportMap);
    }
    
}
