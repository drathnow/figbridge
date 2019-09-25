package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public class ExpandableByteBuffer {

    private int initialSize;
    private int expansionSize;
    private ByteBuffer byteBuffer;
    
    public ExpandableByteBuffer(int initialSize, int expansionSize) {
        this.initialSize = initialSize;
        this.expansionSize = expansionSize;
        this.byteBuffer = ByteBuffer.allocate(initialSize);
    }
    
    public void write(byte aByte) {
        if (byteBuffer.position() == byteBuffer.capacity()) {
            byte[] bytes = byteBuffer.array();
            byteBuffer = ByteBuffer.allocate(byteBuffer.capacity() + expansionSize);
            byteBuffer.put(bytes);
        }
        byteBuffer.put(aByte);
    }
    
    public int getSize() {
        return byteBuffer.position();
    }
    
    public void copyBytesToDestinationByteArray(byte[] dstBytes) {
        byteBuffer.flip();
        byteBuffer.get(dstBytes);
    }
    
    public void clear() {
        byteBuffer.clear();
    }
    
    public void reset() {
        if (byteBuffer.capacity() > initialSize)
            byteBuffer = ByteBuffer.allocate(initialSize);
        else
            clear();
    }
}
