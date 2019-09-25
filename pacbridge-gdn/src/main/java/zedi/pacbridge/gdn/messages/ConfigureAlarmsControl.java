package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;

public class ConfigureAlarmsControl extends GdnMessageBase implements GdnMessage, Control, Serializable {
    private static final long serialVersionUID = 1001L;
    private static final int FIXED_SIZE = 4;
    
    protected Integer index;
    protected GdnDataType dataType;
    private List<StandardAlarmValue> alarmValues;
    
    public ConfigureAlarmsControl(Integer index, GdnDataType dataType, List<StandardAlarmValue> alarmValues) {
        super(GdnMessageType.ConfigureAlarms);
        this.index = index;
        this.dataType = dataType;
        this.alarmValues = alarmValues;
    }

    public Integer getIndex() {
        return index;
    }

    public GdnDataType getDataType() {
        return dataType;
    }
    
    public Long getEventId() {
        return 0L;
    }

    @Override
    public Integer size() {
        int totalSize = FIXED_SIZE;
        for (StandardAlarmValue alarmValue : alarmValues)
            totalSize += alarmValue.size();
        return totalSize;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort(index.shortValue());
        byteBuffer.put(dataType.getNumber().byteValue());
        byteBuffer.put((byte)alarmValues.size());
        for (AlarmValue alarmValue : alarmValues)
            alarmValue.serialize(byteBuffer);
    }

    public List<StandardAlarmValue> getAlarmValues() {
        return new ArrayList<StandardAlarmValue>(alarmValues);
    }

    public static ConfigureAlarmsControl configureAlarmsControlFromByteBuffer(ByteBuffer byteBuffer) {
        Integer index = Unsigned.getUnsignedShort(byteBuffer);
        GdnDataType dataType = GdnDataType.dataTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        Integer count = (int)Unsigned.getUnsignedByte(byteBuffer);
        List<StandardAlarmValue> alarmValues = new ArrayList<>();
        for (int i = 0; i < count; i++)
            alarmValues.add(StandardAlarmValue.standardAlarmValueFromByteBuffer(byteBuffer, dataType));
        return new ConfigureAlarmsControl(index, dataType, alarmValues);
    }
}
