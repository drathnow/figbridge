package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;

public class ConfigureExtendedAlarmsControl extends GdnMessageBase implements GdnMessage, Control, Serializable {
    private static final long serialVersionUID = 1001;
    public static final Integer FIXED_SIZE = 5;
    
    private Integer version = 0;
    private Integer index;
    private GdnDataType dataType;
    private List<ExtendedAlarmValue> alarmValues;
    
    public ConfigureExtendedAlarmsControl(Integer index, GdnDataType gdnDataType, List<ExtendedAlarmValue> alarmValues) {
        super(GdnMessageType.ConfigureExtendedAlarms);
        this.index = index;
        this.dataType = gdnDataType;
        this.alarmValues = alarmValues;
        for (ExtendedAlarmValue alarmValue : alarmValues) {
            if (alarmValue.isEnabled()) {
                if (alarmValue.getLimitValue().dataType() != gdnDataType) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Cannot add ExtendedAlarmValue with different data type than message. Expecting '");
                    stringBuilder.append(dataType);
                    stringBuilder.append("' but was '");
                    stringBuilder.append(alarmValue.getLimitValue().dataType());
                    stringBuilder.append('\'');
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
        }
    }

    public Long getEventId() {
        return 0L;
    }

    public List<ExtendedAlarmValue> getExtendedAlarmValues() {
        return new ArrayList<ExtendedAlarmValue>(alarmValues);
    }
    
    public Integer getIndex() {
        return index;
    }
    
    public GdnDataType getDataType() {
        return dataType;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    @Override   
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(version.byteValue());
        byteBuffer.putShort(index.shortValue());
        byteBuffer.put(dataType.getNumber().byteValue());
        byteBuffer.put((byte)alarmValues.size());
        
        for (ExtendedAlarmValue alarmValue : alarmValues)
            alarmValue.serialize(byteBuffer);
    }

    @Override
    public Integer size() {
        return FIXED_SIZE + sizeOfAllAlarmValues();
    }

    public static ConfigureExtendedAlarmsControl configureExtendedAlarmsControlFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get(); // Version is not used.
        Integer index = Unsigned.getUnsignedShort(byteBuffer);
        GdnDataType dataType = GdnDataType.dataTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        int count = Unsigned.getUnsignedByte(byteBuffer);
        List<ExtendedAlarmValue> localValues = new ArrayList<ExtendedAlarmValue>();
        while (count-- > 0)
            localValues.add(ExtendedAlarmValue.alarmValueFromByteBuffer(byteBuffer, dataType));
        return new ConfigureExtendedAlarmsControl(index, dataType, localValues);
    }

    private int sizeOfAllAlarmValues() {
        int totalSize = 0;
        for (ExtendedAlarmValue alarmValue : alarmValues)
            totalSize += alarmValue.size();
        return totalSize;
    }

}