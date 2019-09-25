package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.zap.ZapMessageType;

public class RequestTimeMessage extends ZapMessage {
    public static final Integer SIZE = 4;
    
    private Integer deviceTime;
    
    public RequestTimeMessage(Integer deviceTime) {
        super(ZapMessageType.RequestTime);
        this.deviceTime = deviceTime;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort(SIZE.shortValue());
        byteBuffer.putInt(deviceTime);
    }

    @Override
    public Integer size() {
        return SIZE;
    }

    public Integer getDeviceTime() {
        return deviceTime;
    }
    
    public static RequestTimeMessage messageFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get();
        Integer deviceTime = byteBuffer.getInt();
        return new RequestTimeMessage(deviceTime);
    }
    
}
