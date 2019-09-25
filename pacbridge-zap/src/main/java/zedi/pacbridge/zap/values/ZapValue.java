package zedi.pacbridge.zap.values;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.Value;

public interface ZapValue extends Value {
    
    /**
     * Converts the value to a string representation
     */
    public String toString();
    
    /**
     * Returns the data type of the object
     */
    public ZapDataType dataType();
    
    /**
     * Returns the serialized size of the value object.
     */
    public Integer serializedSize();
    
    /**
     * Serializes the value into the ByteBuffer.
     */
    public void serialize(ByteBuffer byteBuffer);
}
