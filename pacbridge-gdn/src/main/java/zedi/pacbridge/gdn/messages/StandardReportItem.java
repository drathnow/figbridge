package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnSerializable;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.utl.io.Unsigned;


public class StandardReportItem extends IoPointReportItem implements GdnSerializable {
    static final long serialVersionUID = 1001;

    private StandardReportItem() {
    }

    public StandardReportItem(Integer index, GdnValue<?> value) {
        this.index = index;
        this.gdnValue = value;
    }

    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setIndex(Unsigned.getUnsignedShort(byteBuffer));
        GdnDataType dataType = GdnDataType.dataTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        gdnValue = GdnValue.valueForDataType(dataType);
        gdnValue.deserialize(byteBuffer);
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)getIndex());
        byteBuffer.put(getValue().dataType().getNumber().byteValue());
        gdnValue.serialize(byteBuffer);
    }
    
    public static StandardReportItem standardReportItemFromByteBuffer(ByteBuffer byteBuffer) {
        StandardReportItem item = new StandardReportItem();
        item.deserialize(byteBuffer);
        return item;
    }
}
