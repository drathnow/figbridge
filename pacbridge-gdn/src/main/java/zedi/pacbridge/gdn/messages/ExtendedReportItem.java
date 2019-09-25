package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.DataTypeFactory;
import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnSerializable;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.gdn.GdnValueFactory;
import zedi.pacbridge.utl.io.Unsigned;


public class ExtendedReportItem extends IoPointReportItem implements GdnSerializable {
    static final long serialVersionUID = 1001;

    private ExtendedReportItem() {
        this(new GdnValueFactory(), new DataTypeFactory(), new AlarmStatusFactory());
    }
    
    ExtendedReportItem(GdnValueFactory valueFactory, DataTypeFactory dataTypeFactory, AlarmStatusFactory alarmStatusFactory) {
        super(valueFactory, dataTypeFactory, alarmStatusFactory);
    }
    
    public ExtendedReportItem(Integer index, GdnValue<?> value, GdnAlarmStatus alarmStatus) {
        this.index = index;
        this.gdnValue = value;
        this.alarmStatus = alarmStatus;
    }
    
    public void deserialize(ByteBuffer byteBuffer) {
        index = Unsigned.getUnsignedShort(byteBuffer);
        GdnDataType dataType = dataTypeFactory.dataTypeForTypeNumber(Unsigned.getUnsignedByte(byteBuffer));
        alarmStatus = alarmStatusFactory.alarmStatusForAlarmStatusNumber(Unsigned.getUnsignedByte(byteBuffer));
        gdnValue = valueFactory.valueForDataType(dataType);
        gdnValue.deserialize(byteBuffer);
    }

    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)index);
        byteBuffer.put(gdnValue.dataType().getNumber().byteValue());
        byteBuffer.put(alarmStatus.getTypeNumber().byteValue());
        gdnValue.serialize(byteBuffer);
    }
    
    public static ExtendedReportItem extendedReportItemFromByteBuffer(ByteBuffer byteBuffer) {
        ExtendedReportItem item = new ExtendedReportItem();
        item.deserialize(byteBuffer);
        return item;
    }
}
