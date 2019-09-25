package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.utl.io.Unsigned;

public class StandardAlarmValue implements AlarmValue, Serializable {
    public static final int FIXED_SIZE = 3;
    
    private AlarmType alarmType;
    private GdnValue<?> limitValue;
    private boolean enabled;
        
    public StandardAlarmValue(AlarmType alarmType, GdnValue<?> limitValue, boolean enabled) {
        this.alarmType = alarmType;
        this.limitValue = limitValue;
        this.enabled = enabled;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(size().byteValue());
        byteBuffer.put(getAlarmType().getTypeNumber().byteValue());
        byteBuffer.put((byte)(isEnabled() ? 1 : 0));
        if (shouldIncludeLimitValue())
            getLimitValue().deserialize(byteBuffer);
        
    }
    
    public AlarmType getAlarmType() {
        return alarmType;
    }
    
    public GdnValue<?> getLimitValue() {
        return limitValue;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public Integer size() {
        return FIXED_SIZE + (shouldIncludeLimitValue() ? limitValue.serializedSize() : 0);
    }

    private boolean shouldIncludeLimitValue() {
        return isDataUnavailable() == false && enabled;
    }
    
    private boolean isDataUnavailable() {
        return getAlarmType().equals(AlarmType.DataUnavailable);
    }
    
    public static final StandardAlarmValue standardAlarmValueFromByteBuffer(ByteBuffer byteBuffer, GdnDataType dataType) {
        byteBuffer.get();
        AlarmType alarmType = AlarmType.alarmTypeForTypeNumber(Unsigned.getUnsignedByte(byteBuffer));
        boolean enabled = Unsigned.getUnsignedByte(byteBuffer) == 0 ? false : true;
        GdnValue<?> value = null;
        if (alarmType != AlarmType.DataUnavailable && enabled)
            value = GdnValue.valueFromByteBuffer(byteBuffer, dataType);
        return new StandardAlarmValue(alarmType, value, enabled);
    }
    
}
