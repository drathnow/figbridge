package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.zap.ZapMessageType;

public class HeartBeatMessage extends ZapMessage {
    public static final Integer SIZE = 32;
    
    private Integer deviceTime; // Unix time (seconds since Jan 1, 1970
    
    public HeartBeatMessage(Integer deviceTime) {
        super(ZapMessageType.HeartBeat);
        this.deviceTime = deviceTime;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)0);
    }

    public Integer getDeviceTime() {
        return deviceTime;
    }
    
    @Override
    public Integer size() {
        return 32;
    }

    public static ZapMessage heartBeatMessageFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get();
        Integer deviceTime = byteBuffer.getInt();
        return new HeartBeatMessage(deviceTime);
    }
}