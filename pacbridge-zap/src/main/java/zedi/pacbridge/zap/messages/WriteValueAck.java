package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;

public class WriteValueAck {
    public static final Integer SUCCESS = 0;

    private Long ioId;
    private Integer status;
    
    public WriteValueAck(Long ioId, Integer status) {
        this.ioId = ioId;
        this.status = status;
    }

    public Long iodId() {
        return ioId;
    }
    
    public boolean isSuccess() {
        return status == SUCCESS;
    }
    
    public JSONObject asJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("ioId", ioId);
        obj.put("status", status);
        return obj;
    }    
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }
    
    public static WriteValueAck writeValueAckFromByteBuffer(ByteBuffer byteBuffer) {
        Long ioId = Unsigned.getUnsignedInt(byteBuffer);
        Integer status = (int)byteBuffer.get();
        return new WriteValueAck(ioId, status);
    }
}
