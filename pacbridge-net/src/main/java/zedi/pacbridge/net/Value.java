package zedi.pacbridge.net;

import java.nio.ByteBuffer;


public interface Value {
    public DataType dataType();
    public Integer serializedSize();
    public String toString();
    public void serialize(ByteBuffer byteBuffer);
}
