package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public class ByteBufferManager {

    public ByteBuffer allocateByteBufferWithSize(int size) {
        return ByteBuffer.allocate(size);
    }
}
