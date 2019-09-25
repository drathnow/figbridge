package zedi.pacbridge.gdn;

import java.nio.ByteBuffer;

public interface GdnSerializable {
    public void deserialize(ByteBuffer byteBuffer);
    public void serialize(ByteBuffer byteBuffer);
}
