package zedi.pacbridge.zap;

import java.nio.ByteBuffer;

public interface ZapSerializable {
    public void serialize(ByteBuffer byteBuffer);
    public Integer size();
}
