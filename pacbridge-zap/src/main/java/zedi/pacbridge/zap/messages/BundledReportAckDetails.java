package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.reporting.ResponseStatus;


public class BundledReportAckDetails extends AckDetails {
    public static final Integer ACK_SIZE = 6; // 4 byte report id, 2 byte status. 
    
    private Map<Integer, ResponseStatus> statusMap;
    
    private BundledReportAckDetails(Map<Integer, ResponseStatus> statusMap) {
        super(AckDetailsType.BundledReportAck);
        this.statusMap = statusMap;
    }
    
    public BundledReportAckDetails() {
        this(new HashMap<Integer, ResponseStatus>());
    }

    public void addReportStatus(Integer reportId, ResponseStatus reportStatus) {
        statusMap.put(reportId, reportStatus);
    }
    
    public Map<Integer, ResponseStatus> getStatusMap() {
        return Collections.unmodifiableMap(statusMap);
    }
    
    @Override
    public byte[] asBytes() {
        int size = (Short.SIZE/Byte.SIZE) + (ACK_SIZE * statusMap.size());
        byte[] bytes = new byte[size];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.putShort((short)statusMap.size());
        for (Iterator<Integer> iter = statusMap.keySet().iterator(); iter.hasNext(); ) {
            Integer reportId = iter.next();
            ResponseStatus status = statusMap.get(reportId);
            byteBuffer.putInt(reportId);
            byteBuffer.putShort(status.getNumber().shortValue());
        }
        return bytes;
    }
    
    @Override
    public JSONObject asJSONObject() {
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), new JSONObject(statusMap));
        return json;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }

    public static BundledReportAckDetails bundledReportAckFromByteBuffer(ByteBuffer byteBuffer) {
        Map<Integer, ResponseStatus> statusMap = new HashMap<>();
        int count = byteBuffer.getShort();
        while (count-- > 0) {
            Integer reportId = (int)Unsigned.getUnsignedInt(byteBuffer);
            ResponseStatus reportStatus = ResponseStatus.reportStatusForNumber((int)Unsigned.getUnsignedShort(byteBuffer));
            statusMap.put(reportId, reportStatus);
        }
        return new BundledReportAckDetails(statusMap);
    }
}
