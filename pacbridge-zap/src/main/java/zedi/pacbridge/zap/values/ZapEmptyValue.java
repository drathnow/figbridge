package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ZapEmptyValue extends ZapValueBase implements ZapValue, Serializable {

    protected ZapEmptyValue() {
        super(ZapDataType.EmptyValue);
    }

    @Override
    public Integer serializedSize() {
        return 0;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
    }

}
