package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.zap.ZapMessageType;

public class HeartBeatResponseMessage extends ZapMessage {
    public static final Integer SIZE = 8;
    
    private Integer deviceTime; // Unix time (seconds since Jan 1, 1970)
    private Integer serverTime; // Unix time (seconds since Jan 1, 1970)
    
    public HeartBeatResponseMessage(Integer deviceTime, Integer serverTime) {
        super(ZapMessageType.HeartBeatResponse);
        this.deviceTime = deviceTime;
        this.serverTime = serverTime;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(SIZE.byteValue());
        byteBuffer.putInt(deviceTime);
        byteBuffer.putInt(serverTime);
    }

    public Integer getDeviceTime() {
        return deviceTime;
    }
    
    public Integer getServerTime() {
        return serverTime;
    }
    
    @Override
    public Integer size() {
        return SIZE;
    }

    public static ZapMessage heartBeatMessageFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get();
        Integer deviceTime = byteBuffer.getInt();
        Integer serverTime = byteBuffer.getInt();
        return new HeartBeatResponseMessage(deviceTime, serverTime);
    }
}