package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;


public class WriteIoPointControl extends IoPointControl implements GdnMessage, Control, Serializable {
    static final long serialVersionUID = 1001;

    private static final int FIXED_SIZE = 3;
    
    protected GdnValue<?> gdnValue;

    private WriteIoPointControl() {
        super(GdnMessageType.WriteIoPoint,0, 0);
    }
    
    public WriteIoPointControl(Integer index, GdnValue<?> gdnValue) {
        super(GdnMessageType.WriteIoPoint, gdnValue.dataType(), index, 0);
        this.index = index;
        this.gdnValue = gdnValue;
    }

    @Override
    public GdnMessageType messageType() {
        return GdnMessageType.WriteIoPoint;
    }
    
    public GdnValue<?> getValue() {
        return gdnValue;
    }

    @Override
    public Integer size() {
        return FIXED_SIZE + dataType.getSize();
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)typeNumberForSerialization());
        byteBuffer.putShort(getIndex().shortValue());
        gdnValue.serialize(byteBuffer);
    }
    
    private void deserialize(ByteBuffer byteBuffer) {
        dataType = GdnDataType.dataTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        index = Unsigned.getUnsignedShort(byteBuffer);
        gdnValue = GdnValue.valueForDataType(dataType);
        gdnValue.deserialize(byteBuffer);
    }
    
    public static WriteIoPointControl writeIoPointControlFromByteBuffer(ByteBuffer byteBuffer) {
        WriteIoPointControl control = new WriteIoPointControl();
        control.deserialize(byteBuffer);
        return control;
    }

}
