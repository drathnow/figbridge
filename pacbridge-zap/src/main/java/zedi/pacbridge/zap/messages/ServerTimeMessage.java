package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.zap.ZapMessageType;

public class ServerTimeMessage extends ZapMessage {
    public static Integer VERSION1 = 1;
    public static Integer VERSION1_SIZE = 9;
    
    private Integer deviceTime;
    private Integer serverTime;
        
    public ServerTimeMessage(Integer deviceTime, Integer serverTime) {
        super(ZapMessageType.ServerTime);
        this.deviceTime = deviceTime;
        this.serverTime = serverTime;
    }

    public Integer getDeviceTime() {
        return deviceTime;
    }
    
    public Integer getServerTime() {
        return serverTime;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(VERSION1.byteValue()); // Skip version
        byteBuffer.putInt(deviceTime);
        byteBuffer.putInt(serverTime);
    }

    @Override
    public Integer size() {
        return VERSION1_SIZE;
    }

}
