package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;

public class WriteIoPointsControlAckDetails extends AckDetails {
    
    private Map<Long, WriteValueAck> ackMap;
    
    public WriteIoPointsControlAckDetails(Map<Long, WriteValueAck> ackMap) {
        super(AckDetailsType.WriteIoPoints);
        this.ackMap = ackMap;
    }

    public Map<Long, WriteValueAck> ackMap() {
        return new HashMap<Long, WriteValueAck>(ackMap);
    }
    
    public static WriteIoPointsControlAckDetails writeIoPointsMessageAckDetailsFromByteBuffer(ByteBuffer byteBuffer) {
        Map<Long, WriteValueAck> ackMap = new TreeMap<>();
        int count = Unsigned.getUnsignedByte(byteBuffer);
        while (count-- > 0) {
            WriteValueAck ack = WriteValueAck.writeValueAckFromByteBuffer(byteBuffer);
            ackMap.put(ack.iodId(), ack);
        }
        return new WriteIoPointsControlAckDetails(ackMap);
    }

    @Override
    public byte[] asBytes() {
        return null;
    }
    
    @Override
    public JSONObject asJSONObject() {
        JSONArray array = new JSONArray();
        for (Iterator<WriteValueAck> iter = ackMap.values().iterator(); iter.hasNext(); )
            array.put(iter.next().asJSONObject());
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), new JSONObject(array));
        return json;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }
    
}
