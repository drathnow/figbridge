package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.Zap;
import zedi.pacbridge.zap.ZapMessageType;

public class DemandPollControl extends ZapMessage implements Control, Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer SIZE = 14;
    
    private Long eventId;
    private Long index;
    private Integer pollsetNumber;
    
    public DemandPollControl(Long eventId, Long index, Integer pollsetNumber) {
        super(ZapMessageType.DemandPoll);
        if (isNumberNegative(index.longValue()) || isNumberNegative(pollsetNumber.longValue())) {
            throw new RuntimeException("Invalid value for index or pollset. Both must be greater than zero");
        }
        if (index > Zap.MAX_INDEX)
            throw new RuntimeException("Invalid value for index.  Index must be less than " + Zap.MAX_INDEX);
        if (pollsetNumber> Zap.MAX_POLLSET_NUMBER)
            throw new RuntimeException("Invalid value for pollset.  Pollset must be less than " + Zap.MAX_POLLSET_NUMBER);
        this.eventId = eventId;
        this.index = index;
        this.pollsetNumber = pollsetNumber;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putLong(eventId.longValue());
        byteBuffer.putShort(pollsetNumber.shortValue());
        byteBuffer.putInt(index.intValue());
    }

    public Long getIndex() {
        return index;
    }
    
    public Integer getPollSetNumber() {
        return pollsetNumber;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public JSONObject asJSONObject() {
        JSONObject json = baseJSONObject();
        json.put("IOID", index);
        json.put("PID", pollsetNumber.toString());
        return new JSONObject().put(messageType().getName(), json);
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }
    
    @Override
    public Integer size() {
        return SIZE;
    }
    
    private boolean isNumberNegative(Long number) {
        return number < 0;
    }

    public static DemandPollControl messageFromByteBuffer(ByteBuffer byteBuffer) {
        Long eventId = byteBuffer.getLong();
        Integer pollsetNumber = (int)Unsigned.getUnsignedShort(byteBuffer);
        Long index = (long)Unsigned.getUnsignedInt(byteBuffer);
        return new DemandPollControl(eventId, index, pollsetNumber);
    }
}
