package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;


public interface AlarmValue {
    public Integer size();
    public void serialize(ByteBuffer byteBuffer);
}
