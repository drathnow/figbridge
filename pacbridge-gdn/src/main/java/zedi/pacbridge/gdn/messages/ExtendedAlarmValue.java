package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.AlarmType;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.utl.io.Unsigned;


public abstract class ExtendedAlarmValue implements AlarmValue {
    public static final Integer FIXED_SIZE = 3;
    public static final Integer VARIABLE_FIXED_SIZE = 6;

    private AlarmType alarmType;
    private GdnValue<?> limitValue;
    private boolean enabled;
    private Integer setTimeSeconds;
    private Integer clearTimeSeconds;
    private GdnValue<?> hysteresisValue;

    protected ExtendedAlarmValue(AlarmType alarmType, 
                                 boolean enabled, 
                                 GdnValue<?> limitValue, 
                                 GdnValue<?> hysteresisValue, 
                                 Integer setTimeSeconds, 
                                 Integer clearTimeSeconds) {
        if (hysteresisValue != null && (hysteresisValue.dataType() == GdnDataType.Binary || hysteresisValue.dataType() == GdnDataType.EmptyValue))
            throw new IllegalArgumentException("Hysteresis value cannot be Binary or empty");
        this.alarmType = alarmType;
        this.limitValue = limitValue;
        this.enabled = enabled;
        this.setTimeSeconds = setTimeSeconds;
        this.clearTimeSeconds = clearTimeSeconds;
        this.hysteresisValue = hysteresisValue;
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

    public Integer getSetTimeSeconds() {
        return setTimeSeconds;
    }

    public Integer getClearTimeSeconds() {
        return clearTimeSeconds;
    }

    public GdnValue<?> getHysteresisValue() {
        return hysteresisValue;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        int totalSize = size();
        byteBuffer.put((byte)totalSize);
        byteBuffer.put(alarmType.getTypeNumber().byteValue());
        byteBuffer.put((byte)(enabled ? 1 : 0));
        if (enabled && isDataUnavailable() == false) {
            limitValue.serialize(byteBuffer);
            hysteresisValue.serialize(byteBuffer);
            byteBuffer.putShort(setTimeSeconds.shortValue());
            byteBuffer.putShort(clearTimeSeconds.shortValue());
            byteBuffer.putShort((short)0);
        }
    }

    @Override
    public Integer size() {
        return FIXED_SIZE + variableSize();
    }

    /**
     * The variable length portion of the value depends on if it if enable or if
     * it's data unavailable. If it's disabled or it's data unavailable, then
     * the variable size is zero. If not, then the size is made up of 6 bytes
     * for the set time, clear time and action id, plus the size of the limit
     * value, times 2. We multiply by two to include the size of the hysteresis
     * value, which will be the same size as the limit value.
     * 
     * @return
     */
    private int variableSize() {
        if (enabled == false || isDataUnavailable())
            return 0;
        return VARIABLE_FIXED_SIZE + (limitValue == null ? 0 : (limitValue.serializedSize() * 2));
    }

    private boolean isDataUnavailable() {
        return alarmType == AlarmType.DataUnavailable;
    }

    public static ExtendedAlarmValue alarmValueFromByteBuffer(ByteBuffer byteBuffer, GdnDataType dataType) {
        Unsigned.getUnsignedByte(byteBuffer); // Size not used.
        AlarmType alarmType = AlarmType.alarmTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        Boolean enabled = (int)Unsigned.getUnsignedByte(byteBuffer) == 0 ? false : true;
        if (enabled) {
            GdnValue<?> limitValue = GdnValue.valueFromByteBuffer(byteBuffer, dataType);
            GdnValue<?> hysteresisValue = GdnValue.valueFromByteBuffer(byteBuffer, dataType);

            Integer setTimeSeconds = Unsigned.getUnsignedShort(byteBuffer);
            Integer clearTimeSeconds = Unsigned.getUnsignedShort(byteBuffer);
            Unsigned.getUnsignedShort(byteBuffer); // AlarmActionID is currently not used.
            return new EnabledAlarmValue(alarmType, limitValue, hysteresisValue, setTimeSeconds, clearTimeSeconds);
        } else {
            return new DisabledAlarmValue(alarmType);
        }
    }

}
