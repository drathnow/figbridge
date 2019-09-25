package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class WriteIoPointsControl extends ZapMessage implements Control, Serializable {
    public static final Integer FIXED_SIZE = 1;
    
    private List<WriteValue> writeValues;
    private Long eventId;
    
    public WriteIoPointsControl() {
        super(ZapMessageType.WriteIOPoints);
        this.eventId = 0L;
    }
    
    public WriteIoPointsControl(List<WriteValue> writeValues, Long eventId) {
        super(ZapMessageType.WriteIOPoints);
        this.writeValues = writeValues;
        this.eventId = eventId;
    }

    public List<WriteValue> getWriteValues() {
        return Collections.unmodifiableList(writeValues);
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        short size = FIXED_SIZE.shortValue();
        for (Iterator<WriteValue> iter = writeValues.iterator(); iter.hasNext(); )
            size += iter.next().size();

        byteBuffer.putShort(size);
        byteBuffer.put((byte)(writeValues.size()));
        for (Iterator<WriteValue> iter = writeValues.iterator(); iter.hasNext(); )
            iter.next().serialize(byteBuffer);
    }

    @Override
    public Integer size() {
        int total = FIXED_SIZE;
        for (Iterator<WriteValue> iter = writeValues.iterator(); iter.hasNext(); )
            total += iter.next().size();
        return total;
    }
    
    public JSONObject asJSONObject() {
        JSONArray array = new JSONArray();
        for (Iterator<WriteValue> iter = writeValues.iterator(); iter.hasNext(); ) {
            WriteValue writeValue = iter.next();
            array.put(new JSONObject(writeValue.toJSONString()));
        }
        JSONObject json = baseJSONObject();
        json.put("WriteValues", array);
        return new JSONObject().put(messageType().getName(), json);
    }
    
    public String toJSONString() {
        return toString();
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }

    public static WriteIoPointsControl messageFromByteBuffer(ByteBuffer byteBuffer) {
        List<WriteValue> writeValues = new ArrayList<>();
        byteBuffer.getShort();
        int count = Unsigned.getUnsignedByte(byteBuffer);
        while (count-- > 0)
            writeValues.add(WriteValue.writeValueFromByteBuffer(byteBuffer));
        return new WriteIoPointsControl(writeValues, 0L);
    }
}
