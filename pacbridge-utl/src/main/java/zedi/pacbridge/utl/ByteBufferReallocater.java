package zedi.pacbridge.utl;

import java.nio.ByteBuffer;

public class ByteBufferReallocater {

    private ByteBufferReallocater() {
    }
    
    public static ByteBuffer reallocatedBufferWithAddedSize(ByteBuffer byteBuffer, int addedSize) {
        int newSize;
        if (byteBuffer.position() > 0)
            newSize = addedSize + byteBuffer.position();
        else
            newSize = addedSize;
        ByteBuffer anotherBuffer = ByteBuffer.allocate(newSize);
        byteBuffer.flip();
        anotherBuffer.put(byteBuffer);
        return anotherBuffer;
    }
}
