package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class DeviceCapabilitiesMessage {
    public static final Integer VERSION1 = 1;
    
    private Integer maxSessions;
    private Integer deviceCharacteristics;
    private Integer maxPacketSize;
    
    
    public DeviceCapabilitiesMessage(Integer deviceCharacteristics, Integer maxSessions, Integer maxPacketSize) {
        this.maxSessions = maxSessions;
        this.deviceCharacteristics = deviceCharacteristics;
    }

    public Integer getMaxSessions() {
        return maxSessions;
    }
    
    public Integer getMaxPacketSize() {
        return maxPacketSize;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(VERSION1.byteValue());
        byteBuffer.putInt(deviceCharacteristics.intValue());
        byteBuffer.putShort(maxSessions.shortValue());
        byteBuffer.putShort(maxPacketSize.shortValue());
    }
    
    public static DeviceCapabilitiesMessage DeviceCapabilitiesMessageFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get(); // Only one version right now
        Integer maxSessions = (int)Unsigned.getUnsignedInt(byteBuffer);
        Integer deviceCharacteristics = (int)Unsigned.getUnsignedShort(byteBuffer);
        Integer maxPacketSize = (int)Unsigned.getUnsignedShort(byteBuffer);
        return new DeviceCapabilitiesMessage(deviceCharacteristics, maxSessions, maxPacketSize);
    }
}
