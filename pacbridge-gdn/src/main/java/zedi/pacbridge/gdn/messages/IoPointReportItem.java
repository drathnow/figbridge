package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.DataTypeFactory;
import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnSerializable;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.gdn.GdnValueFactory;


public abstract class IoPointReportItem implements GdnSerializable {
    static final long serialVersionUID = 1001;
    
    protected int index;
    protected GdnValue<?> gdnValue;
    protected GdnAlarmStatus alarmStatus;
    protected GdnValueFactory valueFactory;
    protected DataTypeFactory dataTypeFactory;
    protected AlarmStatusFactory alarmStatusFactory;
    
    IoPointReportItem(GdnValueFactory valueFactory, DataTypeFactory dataTypeFactory, AlarmStatusFactory alarmStatusFactory) {
        this.valueFactory = valueFactory;
        this.dataTypeFactory = dataTypeFactory;
        this.alarmStatusFactory = alarmStatusFactory;
    }
    
    public IoPointReportItem() {
        this(new GdnValueFactory(), new DataTypeFactory(), new AlarmStatusFactory());
    }
    
    public abstract void deserialize(ByteBuffer byteBuffer);
    public abstract void serialize(ByteBuffer byteBuffer);
    
    public int getIndex() {
        return index;
    }

    public GdnValue<?> getValue() {
        return gdnValue;
    }

    protected void setValue(GdnValue<?> value) {
        this.gdnValue = value;
    }

    protected void setIndex(int anIndex) {
        index = anIndex;
    }

    protected void setAlarmStatus(GdnAlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
    
    public GdnAlarmStatus getAlarmStatus() {
        return alarmStatus;
    }
}